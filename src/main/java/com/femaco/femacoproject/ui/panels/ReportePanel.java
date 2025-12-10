package com.femaco.femacoproject.ui.panels;

import com.femaco.femacoproject.model.MovimientoInventario;
import com.femaco.femacoproject.model.Producto;
import com.femaco.femacoproject.model.enums.CategoriaProducto;
import com.femaco.femacoproject.model.enums.TipoMovimiento;
import com.femaco.femacoproject.service.GestionInventarioService;
import com.femaco.femacoproject.service.ReporteService;
import com.femaco.femacoproject.ui.components.CustomButton;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ReportePanel extends JPanel {
    private ReporteService reporteService;
    private GestionInventarioService inventarioService;

    private JComboBox<String> cmbTipoReporte;
    private JDateChooser dateInicio;
    private JDateChooser dateFin;
    private JTable reporteTable;
    private DefaultTableModel tableModel;
    private JTextArea txtResumen;

    private CustomButton btnGenerar;
    private CustomButton btnExportar;
    private CustomButton btnLimpiar;

    public ReportePanel(ReporteService reporteService, GestionInventarioService inventarioService) {
        this.reporteService = reporteService;
        this.inventarioService = inventarioService;
        initComponents();
        setupLayout();
        configurarEventos();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Panel de controles
        JPanel controlsPanel = crearControlsPanel();

        // Panel de tabla
        JPanel tablePanel = crearTablePanel();

        // Panel de resumen
        JPanel summaryPanel = crearSummaryPanel();

        add(controlsPanel, BorderLayout.NORTH);
        add(tablePanel, BorderLayout.CENTER);
        add(summaryPanel, BorderLayout.SOUTH);
    }

    private JPanel crearControlsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Parámetros del Reporte"));
        panel.setPreferredSize(new Dimension(800, 120));

        // Panel de parámetros en una línea
        JPanel paramsPanel = new JPanel(new GridLayout(1, 6, 8, 5));
        paramsPanel.setPreferredSize(new Dimension(600, 35));

        // Tipo de reporte
        JLabel lblTipo = new JLabel("Tipo de Reporte:");
        paramsPanel.add(lblTipo);

        String[] tiposReporte = {
                "Stock Actual",
                "Stock Bajo Mínimo",
                "Stock Crítico",
                "Movimientos por Período",
                "Productos Más Vendidos",
                "Valor de Inventario por Categoría",
                "Resumen Ejecutivo"
        };
        cmbTipoReporte = new JComboBox<>(tiposReporte);
        cmbTipoReporte.setPreferredSize(new Dimension(180, 18));
        paramsPanel.add(cmbTipoReporte);

        // Fecha Inicio
        JLabel lblInicio = new JLabel("Fecha Inicio:");
        paramsPanel.add(lblInicio);

        dateInicio = new JDateChooser();
        dateInicio.setDateFormatString("dd/MM/yyyy");
        dateInicio.setPreferredSize(new Dimension(120, 18));
        paramsPanel.add(dateInicio);

        // Fecha Fin
        JLabel lblFin = new JLabel("Fecha Fin:");
        paramsPanel.add(lblFin);

        dateFin = new JDateChooser();
        dateFin.setDateFormatString("dd/MM/yyyy");
        dateFin.setPreferredSize(new Dimension(120, 18));
        paramsPanel.add(dateFin);

        // Panel de botones con alineación centrada
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        btnGenerar = CustomButton.createPrimaryButton("Generar Reporte");
        btnExportar = CustomButton.createSuccessButton("Exportar a CSV");
        btnLimpiar = CustomButton.createSecondaryButton("Limpiar");
        
        // Establecer altura de botones a 18px
        btnGenerar.setPreferredSize(new Dimension(150, 28));
        btnExportar.setPreferredSize(new Dimension(130, 28));
        btnLimpiar.setPreferredSize(new Dimension(90, 28));
        
        buttonsPanel.add(btnGenerar);
        buttonsPanel.add(btnExportar);
        buttonsPanel.add(btnLimpiar);

        panel.add(paramsPanel, BorderLayout.NORTH);
        panel.add(buttonsPanel, BorderLayout.SOUTH);

        return panel;
    }    private JPanel crearTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Resultados del Reporte"));

        tableModel = new DefaultTableModel();
        reporteTable = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(reporteTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearSummaryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Resumen"));
        panel.setPreferredSize(new Dimension(800, 150));

        txtResumen = new JTextArea(6, 80);
        txtResumen.setEditable(false);
        txtResumen.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtResumen.setText("Seleccione un tipo de reporte y haga clic en 'Generar Reporte'");

        JScrollPane scrollPane = new JScrollPane(txtResumen);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void setupLayout() {
        setPreferredSize(new Dimension(1000, 700));
    }

    private void configurarEventos() {
        btnGenerar.addActionListener(e -> generarReporte());
        btnExportar.addActionListener(e -> exportarReporte());
        btnLimpiar.addActionListener(e -> limpiarReporte());

        // Actualizar interfaz cuando cambie el tipo de reporte
        cmbTipoReporte.addActionListener(e -> actualizarInterfazPorTipoReporte());
    }

    private void actualizarInterfazPorTipoReporte() {
        String tipoReporte = (String) cmbTipoReporte.getSelectedItem();
        boolean requiereFechas = tipoReporte.equals("Movimientos por Período") ||
                tipoReporte.equals("Productos Más Vendidos");

        dateInicio.setEnabled(requiereFechas);
        dateFin.setEnabled(requiereFechas);
    }

    private void generarReporte() {
        try {
            String tipoReporte = (String) cmbTipoReporte.getSelectedItem();

            switch (tipoReporte) {
                case "Stock Actual":
                    generarReporteStockActual();
                    break;
                case "Stock Bajo Mínimo":
                    generarReporteStockBajo();
                    break;
                case "Stock Crítico":
                    generarReporteStockCritico();
                    break;
                case "Movimientos por Período":
                    generarReporteMovimientos();
                    break;
                case "Productos Más Vendidos":
                    generarReporteProductosMasVendidos();
                    break;
                case "Valor de Inventario por Categoría":
                    generarReporteValorPorCategoria();
                    break;
                case "Resumen Ejecutivo":
                    generarResumenEjecutivo();
                    break;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al generar reporte: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void generarReporteStockActual() {
        List<Producto> productos = reporteService.generarReporteStockActual();

        String[] columnNames = { "ID", "Nombre", "Categoría", "Stock Actual", "Stock Mínimo", "Precio", "Valor Stock" };
        tableModel.setColumnIdentifiers(columnNames);
        tableModel.setRowCount(0);

        double valorTotal = 0;
        for (Producto producto : productos) {
            double valorStock = producto.calcularValorTotalStock();
            valorTotal += valorStock;

            Object[] row = {
                    producto.getId(),
                    producto.getNombre(),
                    producto.getCategoria().getDescripcion(),
                    producto.getStockActual(),
                    producto.getStockMinimo(),
                    String.format("S/. %.2f", producto.getPrecio()),
                    String.format("S/. %.2f", valorStock)
            };
            tableModel.addRow(row);
        }

        txtResumen.setText(String.format(
                "REPORTE DE STOCK ACTUAL\n" +
                        "Total de Productos: %d\n" +
                        "Valor Total del Inventario: S/. %.2f\n" +
                        "Productos con Stock Bajo: %d\n" +
                        "Productos con Stock Crítico: %d",
                productos.size(), valorTotal,
                reporteService.generarReporteStockBajo().size(),
                reporteService.generarReporteStockCritico().size()));
    }

    private void generarReporteStockBajo() {
        List<Producto> productos = reporteService.generarReporteStockBajo();

        String[] columnNames = { "ID", "Nombre", "Categoría", "Stock Actual", "Stock Mínimo", "Diferencia", "Estado" };
        tableModel.setColumnIdentifiers(columnNames);
        tableModel.setRowCount(0);

        for (Producto producto : productos) {
            int diferencia = producto.getStockActual() - producto.getStockMinimo();
            String estado = diferencia < 0 ? "CRÍTICO" : "BAJO";

            Object[] row = {
                    producto.getId(),
                    producto.getNombre(),
                    producto.getCategoria().getDescripcion(),
                    producto.getStockActual(),
                    producto.getStockMinimo(),
                    diferencia,
                    estado
            };
            tableModel.addRow(row);
        }

        txtResumen.setText(String.format(
                "REPORTE DE STOCK BAJO MÍNIMO\n" +
                        "Total de Productos con Stock Bajo: %d\n" +
                        "Productos en Estado Crítico: %d\n" +
                        "Recomendación: Realizar pedidos de reposición para estos productos.",
                productos.size(),
                productos.stream().filter(p -> p.getStockActual() == 0).count()));
    }

    private void generarReporteStockCritico() {
        List<Producto> productos = reporteService.generarReporteStockCritico();

        String[] columnNames = { "ID", "Nombre", "Categoría", "Stock Actual", "Stock Mínimo", "Diferencia", "Urgencia" };
        tableModel.setColumnIdentifiers(columnNames);
        tableModel.setRowCount(0);

        int stockCero = 0;
        int stockMenor = 0;

        for (Producto producto : productos) {
            int diferencia = producto.getStockActual() - producto.getStockMinimo();
            String urgencia;
            
            if (producto.getStockActual() == 0) {
                urgencia = "CRÍTICO - SIN STOCK";
                stockCero++;
            } else if (diferencia < 0) {
                urgencia = "CRÍTICO - POR DEBAJO";
                stockMenor++;
            } else {
                urgencia = "CRÍTICO";
            }

            Object[] row = {
                    producto.getId(),
                    producto.getNombre(),
                    producto.getCategoria().getDescripcion(),
                    producto.getStockActual(),
                    producto.getStockMinimo(),
                    diferencia,
                    urgencia
            };
            tableModel.addRow(row);
        }

        txtResumen.setText(String.format(
                "REPORTE DE STOCK CRÍTICO\n" +
                        "Total de Productos en Estado Crítico: %d\n" +
                        "Productos sin Stock: %d\n" +
                        "Productos por Debajo del Mínimo: %d\n" +
                        "ACCIÓN REQUERIDA: Realizar compras urgentes para estos productos.",
                productos.size(), stockCero, stockMenor));
    }

    private void generarReporteMovimientos() {
        if (!validarFechas())
            return;

        Date fechaInicio = dateInicio.getDate();
        Date fechaFin = dateFin.getDate();

        List<MovimientoInventario> movimientos = reporteService.generarReporteMovimientos(fechaInicio, fechaFin);

        String[] columnNames = { "Fecha", "Producto", "Tipo", "Cantidad", "Motivo", "Usuario" };
        tableModel.setColumnIdentifiers(columnNames);
        tableModel.setRowCount(0);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        int totalEntradas = 0;
        int totalSalidas = 0;

        for (MovimientoInventario movimiento : movimientos) {
            if (movimiento.getTipo() == TipoMovimiento.ENTRADA) {
                totalEntradas += movimiento.getCantidad();
            } else {
                totalSalidas += movimiento.getCantidad();
            }

            Object[] row = {
                    dateFormat.format(movimiento.getFecha()),
                    movimiento.getProductoId(),
                    movimiento.getTipo().getDescripcion(),
                    movimiento.getCantidad(),
                    movimiento.getMotivo(),
                    movimiento.getUsuarioId()
            };
            tableModel.addRow(row);
        }

        txtResumen.setText(String.format(
                "REPORTE DE MOVIMIENTOS\n" +
                        "Período: %s a %s\n" +
                        "Total de Movimientos: %d\n" +
                        "Total Entradas: %d unidades\n" +
                        "Total Salidas: %d unidades\n" +
                        "Balance Neto: %+d unidades",
                new SimpleDateFormat("dd/MM/yyyy").format(fechaInicio),
                new SimpleDateFormat("dd/MM/yyyy").format(fechaFin),
                movimientos.size(), totalEntradas, totalSalidas, totalEntradas - totalSalidas));
    }

    private void generarReporteProductosMasVendidos() {
        if (!validarFechas())
            return;

        Date fechaInicio = dateInicio.getDate();
        Date fechaFin = dateFin.getDate();

        Map<Producto, Integer> productosVendidos = reporteService.generarReporteProductosMasVendidos(fechaInicio,
                fechaFin, 10);

        String[] columnNames = { "Producto", "Categoría", "Unidades Vendidas", "Precio Unitario", "Total Vendido" };
        tableModel.setColumnIdentifiers(columnNames);
        tableModel.setRowCount(0);

        double totalVendido = 0;
        for (Map.Entry<Producto, Integer> entry : productosVendidos.entrySet()) {
            Producto producto = entry.getKey();
            int cantidad = entry.getValue();
            double totalProducto = cantidad * producto.getPrecio();
            totalVendido += totalProducto;

            Object[] row = {
                    producto.getNombre(),
                    producto.getCategoria().getDescripcion(),
                    cantidad,
                    String.format("S/. %.2f", producto.getPrecio()),
                    String.format("S/. %.2f", totalProducto)
            };
            tableModel.addRow(row);
        }

        txtResumen.setText(String.format(
                "REPORTE DE PRODUCTOS MÁS VENDIDOS\n" +
                        "Período: %s a %s\n" +
                        "Total de Productos: %d\n" +
                        "Ingresos Totales por Ventas: S/. %.2f",
                new SimpleDateFormat("dd/MM/yyyy").format(fechaInicio),
                new SimpleDateFormat("dd/MM/yyyy").format(fechaFin),
                productosVendidos.size(), totalVendido));
    }

    private void generarReporteValorPorCategoria() {
        Map<CategoriaProducto, Double> valorPorCategoria = reporteService.generarReporteValorInventarioPorCategoria();

        String[] columnNames = { "Categoría", "Valor Total", "Porcentaje" };
        tableModel.setColumnIdentifiers(columnNames);
        tableModel.setRowCount(0);

        double valorTotal = reporteService.generarReporteValorTotalInventario();

        for (Map.Entry<CategoriaProducto, Double> entry : valorPorCategoria.entrySet()) {
            double valor = entry.getValue();
            double porcentaje = valorTotal > 0 ? (valor / valorTotal * 100) : 0;

            Object[] row = {
                    entry.getKey().getDescripcion(),
                    String.format("S/. %.2f", valor),
                    String.format("%.1f%%", porcentaje)
            };
            tableModel.addRow(row);
        }

        txtResumen.setText(String.format(
                "REPORTE DE VALOR DE INVENTARIO POR CATEGORÍA\n" +
                        "Valor Total del Inventario: S/. %.2f\n" +
                        "Número de Categorías: %d\n" +
                        "Fecha de Generación: %s",
                valorTotal, valorPorCategoria.size(), new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date())));
    }

    private void generarResumenEjecutivo() {
        Map<String, Object> resumen = reporteService.generarResumenEjecutivo();

        String[] columnNames = { "Métrica", "Valor" };
        tableModel.setColumnIdentifiers(columnNames);
        tableModel.setRowCount(0);

        for (Map.Entry<String, Object> entry : resumen.entrySet()) {
            Object[] row = { entry.getKey(), entry.getValue() };
            tableModel.addRow(row);
        }

        txtResumen.setText("RESUMEN EJECUTIVO DEL INVENTARIO\n" +
                "Este reporte muestra las métricas clave del sistema para la toma de decisiones.");
    }

    private boolean validarFechas() {
        if (dateInicio.getDate() == null || dateFin.getDate() == null) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione ambas fechas para este tipo de reporte",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (dateInicio.getDate().after(dateFin.getDate())) {
            JOptionPane.showMessageDialog(this,
                    "La fecha de inicio no puede ser posterior a la fecha fin",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    private void exportarReporte() {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                    "No hay datos para exportar. Genere un reporte primero.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            String nombreArchivo = "reporte_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".csv";
            boolean exito = reporteService.exportarReporteCSV(
                    java.util.Arrays.asList(tableModel.getDataVector().toArray()), nombreArchivo);

            if (exito) {
                String rutaCompleta = System.getProperty("user.dir") + java.io.File.separator + "reportes" + java.io.File.separator + nombreArchivo;
                JOptionPane.showMessageDialog(this,
                        "Reporte exportado exitosamente.\n\nUbicación: " + rutaCompleta,
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Error al exportar el reporte", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al exportar reporte: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarReporte() {
        tableModel.setRowCount(0);
        txtResumen.setText("Seleccione un tipo de reporte y haga clic en 'Generar Reporte'");
        cmbTipoReporte.setSelectedIndex(0);
        dateInicio.setDate(null);
        dateFin.setDate(null);
    }

    public void refrescar() {
        limpiarReporte();
    }
}
