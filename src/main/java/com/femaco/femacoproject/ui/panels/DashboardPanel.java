package com.femaco.femacoproject.ui.panels;

import com.femaco.femacoproject.model.MovimientoInventario;
import com.femaco.femacoproject.model.Producto;
import com.femaco.femacoproject.service.AlertaService;
import com.femaco.femacoproject.service.GestionInventarioService;
import com.femaco.femacoproject.service.ReporteService;
import com.femaco.femacoproject.ui.components.CustomButton;
import com.femaco.femacoproject.ui.components.StatusPanel;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class DashboardPanel extends JPanel {
    private GestionInventarioService inventarioService;
    private ReporteService reporteService;
    private AlertaService alertaService;
    
    private StatusPanel totalProductosPanel;
    private StatusPanel valorInventarioPanel;
    private StatusPanel stockBajoPanel;
    private StatusPanel stockCriticoPanel;
    private StatusPanel movimientosMesPanel;
    private StatusPanel alertasPanel;
    
    private JTable productosTable;
    private JTable movimientosTable;
    
    public DashboardPanel(GestionInventarioService inventarioService, 
                         ReporteService reporteService, 
                         AlertaService alertaService) {
        this.inventarioService = inventarioService;
        this.reporteService = reporteService;
        this.alertaService = alertaService;
        initComponents();
        setupLayout();
        cargarDatos();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Panel de métricas principales
        JPanel metricsPanel = crearMetricsPanel();
        
        // Panel de tablas
        JPanel tablesPanel = crearTablesPanel();
        
        // Panel de acciones rápidas
        JPanel actionsPanel = crearActionsPanel();
        
        add(metricsPanel, BorderLayout.NORTH);
        add(tablesPanel, BorderLayout.CENTER);
        add(actionsPanel, BorderLayout.SOUTH);
    }
    
    private JPanel crearMetricsPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 3, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Métricas Principales"));
        
        totalProductosPanel = StatusPanel.createPrimaryPanel(
            "Total Productos", "0", "Activos en inventario");
        
        valorInventarioPanel = StatusPanel.createInfoPanel(
            "Valor Inventario", "S/. 0.00", "Valor total estimado");
        
        stockBajoPanel = StatusPanel.createWarningPanel(
            "Stock Bajo", "0", "Productos bajo mínimo");
        
        stockCriticoPanel = StatusPanel.createDangerPanel(
            "Stock Crítico", "0", "Productos sin stock");
        
        movimientosMesPanel = StatusPanel.createSuccessPanel(
            "Movimientos Mes", "0", "Entradas y salidas");
        
        alertasPanel = StatusPanel.createWarningPanel(
            "Alertas Activas", "0", "Requieren atención");
        
        panel.add(totalProductosPanel);
        panel.add(valorInventarioPanel);
        panel.add(stockBajoPanel);
        panel.add(stockCriticoPanel);
        panel.add(movimientosMesPanel);
        panel.add(alertasPanel);
        
        return panel;
    }
    
    private JPanel crearTablesPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 10, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        // Panel de productos con stock bajo
        JPanel productosPanel = new JPanel(new BorderLayout());
        productosPanel.setBorder(BorderFactory.createTitledBorder("Productos con Stock Bajo"));
        
        String[] columnNames = {"Producto", "Stock Actual", "Stock Mínimo", "Estado"};
        Object[][] data = {};
        productosTable = new JTable(data, columnNames);
        productosTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane productosScroll = new JScrollPane(productosTable);
        productosPanel.add(productosScroll, BorderLayout.CENTER);
        
        // Panel de movimientos recientes
        JPanel movimientosPanel = new JPanel(new BorderLayout());
        movimientosPanel.setBorder(BorderFactory.createTitledBorder("Movimientos Recientes"));
        
        String[] movColumnNames = {"Producto", "Tipo", "Cantidad", "Fecha"};
        Object[][] movData = {};
        movimientosTable = new JTable(movData, movColumnNames);
        movimientosTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane movimientosScroll = new JScrollPane(movimientosTable);
        movimientosPanel.add(movimientosScroll, BorderLayout.CENTER);
        
        panel.add(productosPanel);
        panel.add(movimientosPanel);
        
        return panel;
    }
    
    private JPanel crearActionsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Acciones Rápidas"));
        
        CustomButton btnRegistrarEntrada = CustomButton.createSuccessButton("Registrar Entrada");
        CustomButton btnRegistrarSalida = CustomButton.createPrimaryButton("Registrar Salida");
        CustomButton btnVerReportes = CustomButton.createInfoButton("Ver Reportes");
        CustomButton btnGestionarProductos = CustomButton.createSecondaryButton("Gestionar Productos");
        
        // Agregar action listeners
        btnRegistrarEntrada.addActionListener(e -> abrirRegistroEntrada());
        btnRegistrarSalida.addActionListener(e -> abrirRegistroSalida());
        btnVerReportes.addActionListener(e -> abrirReportes());
        btnGestionarProductos.addActionListener(e -> abrirGestionProductos());
        
        panel.add(btnRegistrarEntrada);
        panel.add(btnRegistrarSalida);
        panel.add(btnVerReportes);
        panel.add(btnGestionarProductos);
        
        return panel;
    }
    
    private void setupLayout() {
        // Configuraciones adicionales de layout
        setPreferredSize(new Dimension(1200, 700));
    }
    
    public void cargarDatos() {
        try {
            // Cargar métricas principales
            int totalProductos = inventarioService.obtenerTotalProductos();
            double valorInventario = reporteService.generarReporteValorTotalInventario();
            List<Producto> productosStockBajo = inventarioService.obtenerProductosStockBajo();
            List<Producto> productosStockCritico = inventarioService.obtenerProductosStockCritico();
            List<MovimientoInventario> movimientosRecientes = inventarioService.obtenerMovimientosRecientes();
            List<String> alertasActivas = alertaService.obtenerAlertasActivas();
            
            // Actualizar panels de métricas
            totalProductosPanel.setValue(totalProductos);
            valorInventarioPanel.setValue(String.format("S/. %.2f", valorInventario));
            stockBajoPanel.setValue(productosStockBajo.size());
            stockCriticoPanel.setValue(productosStockCritico.size());
            movimientosMesPanel.setValue(movimientosRecientes.size());
            alertasPanel.setValue(alertasActivas.size());
            
            // Actualizar tabla de productos con stock bajo
            actualizarTablaProductosStockBajo(productosStockBajo);
            
            // Actualizar tabla de movimientos recientes
            actualizarTablaMovimientosRecientes(movimientosRecientes);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error al cargar datos del dashboard: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void actualizarTablaProductosStockBajo(List<Producto> productos) {
        String[] columnNames = {"Producto", "Stock Actual", "Stock Mínimo", "Estado"};
        Object[][] data = new Object[productos.size()][4];
        
        for (int i = 0; i < productos.size(); i++) {
            Producto p = productos.get(i);
            data[i][0] = p.getNombre();
            data[i][1] = p.getStockActual();
            data[i][2] = p.getStockMinimo();
            data[i][3] = p.getEstado().getDescripcion();
        }
        
        productosTable.setModel(new javax.swing.table.DefaultTableModel(data, columnNames));
    }
    
    private void actualizarTablaMovimientosRecientes(List<MovimientoInventario> movimientos) {
        String[] columnNames = {"Producto", "Tipo", "Cantidad", "Fecha"};
        Object[][] data = new Object[Math.min(movimientos.size(), 10)][4]; // Máximo 10 registros
        
        for (int i = 0; i < Math.min(movimientos.size(), 10); i++) {
            MovimientoInventario m = movimientos.get(i);
            data[i][0] = m.getProductoId();
            data[i][1] = m.getTipo().getDescripcion();
            data[i][2] = m.getCantidad();
            data[i][3] = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(m.getFecha());
        }
        
        movimientosTable.setModel(new javax.swing.table.DefaultTableModel(data, columnNames));
    }
    
    // Métodos para abrir otras ventanas/paneles
    private void abrirRegistroEntrada() {
        // Implementar apertura de diálogo de registro de entrada
        JOptionPane.showMessageDialog(this, "Abrir registro de entrada");
    }
    
    private void abrirRegistroSalida() {
        // Implementar apertura de diálogo de registro de salida
        JOptionPane.showMessageDialog(this, "Abrir registro de salida");
    }
    
    private void abrirReportes() {
        // Implementar apertura de panel de reportes
        JOptionPane.showMessageDialog(this, "Abrir reportes");
    }
    
    private void abrirGestionProductos() {
        // Implementar apertura de panel de gestión de productos
        JOptionPane.showMessageDialog(this, "Abrir gestión de productos");
    }
    
    public void refrescar() {
        cargarDatos();
    }
}