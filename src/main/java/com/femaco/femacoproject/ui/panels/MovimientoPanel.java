package com.femaco.femacoproject.ui.panels;

import com.femaco.femacoproject.exception.ProductoNoEncontradoException;
import com.femaco.femacoproject.exception.StockInsuficienteException;
import com.femaco.femacoproject.model.MovimientoInventario;
import com.femaco.femacoproject.model.Producto;
import com.femaco.femacoproject.model.enums.TipoMovimiento;
import com.femaco.femacoproject.service.GestionInventarioService;
import com.femaco.femacoproject.ui.components.CustomButton;
import com.femaco.femacoproject.ui.components.MovimientoTableModel;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MovimientoPanel extends JPanel {
    private GestionInventarioService inventarioService;
    private String usuarioActual;
    
    private MovimientoTableModel tableModel;
    private JTable movimientosTable;
    
    private JComboBox<TipoMovimiento> cmbTipo;
    private JTextField txtProductoId;
    private JSpinner spnCantidad;
    private JTextArea txtMotivo;
    private JTextField txtReferencia;
    
    private JLabel lblStockActual;
    private JLabel lblStockMinimo;
    private JLabel lblEstadoProducto;
    
    private CustomButton btnBuscarProducto;
    private CustomButton btnRegistrar;
    private CustomButton btnLimpiar;
    
    private Producto productoSeleccionado;
    
    public MovimientoPanel(GestionInventarioService inventarioService, String usuarioActual) {
        this.inventarioService = inventarioService;
        this.usuarioActual = usuarioActual;
        initComponents();
        setupLayout();
        cargarDatos();
        configurarEventos();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Panel de formulario
        JPanel formPanel = crearFormPanel();
        
        // Panel de información del producto
        JPanel infoPanel = crearInfoPanel();
        
        // Panel de botones
        JPanel buttonsPanel = crearButtonsPanel();
        
        // Panel de registro (combinar formulario, info y botones)
        JPanel registroPanel = new JPanel(new BorderLayout(10, 10));
        registroPanel.setBorder(BorderFactory.createTitledBorder("Registro de Movimientos"));
        registroPanel.add(formPanel, BorderLayout.NORTH);
        registroPanel.add(infoPanel, BorderLayout.CENTER);
        registroPanel.add(buttonsPanel, BorderLayout.SOUTH);
        
        // Panel de tabla de movimientos
        JPanel tablePanel = crearTablePanel();
        
        add(registroPanel, BorderLayout.NORTH);
        add(tablePanel, BorderLayout.CENTER);
    }
    
    private JPanel crearFormPanel() {
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Tipo de movimiento
        panel.add(new JLabel("Tipo:*"));
        cmbTipo = new JComboBox<>(TipoMovimiento.values());
        cmbTipo.addActionListener(e -> actualizarInterfazPorTipo());
        panel.add(cmbTipo);
        
        // Producto
        panel.add(new JLabel("Producto:*"));
        JPanel productoPanel = new JPanel(new BorderLayout(5, 0));
        txtProductoId = new JTextField();
        btnBuscarProducto = new CustomButton("Buscar");
        btnBuscarProducto.setPreferredSize(new Dimension(80, 25));
        productoPanel.add(txtProductoId, BorderLayout.CENTER);
        productoPanel.add(btnBuscarProducto, BorderLayout.EAST);
        panel.add(productoPanel);
        
        // Cantidad
        panel.add(new JLabel("Cantidad:*"));
        spnCantidad = new JSpinner(new SpinnerNumberModel(1, 1, 10000, 1));
        panel.add(spnCantidad);
        
        // Referencia
        panel.add(new JLabel("Referencia:"));
        txtReferencia = new JTextField();
        panel.add(txtReferencia);
        
        // Motivo
        panel.add(new JLabel("Motivo:*"));
        txtMotivo = new JTextArea(3, 20);
        txtMotivo.setLineWrap(true);
        JScrollPane scrollMotivo = new JScrollPane(txtMotivo);
        panel.add(scrollMotivo);
        
        return panel;
    }
    
    private JPanel crearInfoPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Información del Producto"));
        
        panel.add(new JLabel("Stock Actual:"));
        lblStockActual = new JLabel("N/A");
        panel.add(lblStockActual);
        
        panel.add(new JLabel("Stock Mínimo:"));
        lblStockMinimo = new JLabel("N/A");
        panel.add(lblStockMinimo);
        
        panel.add(new JLabel("Estado:"));
        lblEstadoProducto = new JLabel("N/A");
        panel.add(lblEstadoProducto);
        
        return panel;
    }
    
    private JPanel crearButtonsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        btnRegistrar = CustomButton.createPrimaryButton("Registrar Movimiento");
        btnLimpiar = CustomButton.createSecondaryButton("Limpiar");
        
        panel.add(btnRegistrar);
        panel.add(btnLimpiar);
        
        return panel;
    }
    
    private JPanel crearTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Historial de Movimientos"));
        
        tableModel = new MovimientoTableModel();
        movimientosTable = new JTable(tableModel);
        
        JScrollPane scrollPane = new JScrollPane(movimientosTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void setupLayout() {
        setPreferredSize(new Dimension(900, 700));
    }
    
    private void configurarEventos() {
        btnBuscarProducto.addActionListener(e -> buscarProducto());
        btnRegistrar.addActionListener(e -> registrarMovimiento());
        btnLimpiar.addActionListener(e -> limpiarFormulario());
        
        // Enter en campo de producto busca automáticamente
        txtProductoId.addActionListener(e -> buscarProducto());
    }
    
    private void cargarDatos() {
        try {
            List<MovimientoInventario> movimientos = inventarioService.obtenerMovimientosRecientes();
            tableModel.setMovimientos(movimientos);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al cargar movimientos: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void buscarProducto() {
        String productoId = txtProductoId.getText().trim();
        if (productoId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese un ID de producto", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            productoSeleccionado = inventarioService.buscarProducto(productoId);
            mostrarInformacionProducto(productoSeleccionado);
            
        } catch (ProductoNoEncontradoException e) {
            JOptionPane.showMessageDialog(this,
                "Producto no encontrado: " + productoId,
                "Error", JOptionPane.ERROR_MESSAGE);
            limpiarInformacionProducto();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al buscar producto: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            limpiarInformacionProducto();
        }
    }
    
    private void mostrarInformacionProducto(Producto producto) {
        lblStockActual.setText(String.valueOf(producto.getStockActual()));
        lblStockMinimo.setText(String.valueOf(producto.getStockMinimo()));
        lblEstadoProducto.setText(producto.getEstado().getDescripcion());
        
        // Colorear según el estado
        switch (producto.getEstado()) {
            case STOCK_CRITICO:
                lblEstadoProducto.setForeground(Color.RED);
                break;
            case STOCK_BAJO:
                lblEstadoProducto.setForeground(Color.ORANGE);
                break;
            default:
                lblEstadoProducto.setForeground(Color.GREEN);
        }
        
        actualizarInterfazPorTipo();
    }
    
    private void limpiarInformacionProducto() {
        productoSeleccionado = null;
        lblStockActual.setText("N/A");
        lblStockMinimo.setText("N/A");
        lblEstadoProducto.setText("N/A");
        lblEstadoProducto.setForeground(Color.BLACK);
    }
    
    private void actualizarInterfazPorTipo() {
        if (productoSeleccionado == null) return;
        
        TipoMovimiento tipo = (TipoMovimiento) cmbTipo.getSelectedItem();
        if (tipo == TipoMovimiento.SALIDA) {
            int stockDisponible = productoSeleccionado.getStockActual();
            ((SpinnerNumberModel) spnCantidad.getModel()).setMaximum(stockDisponible);
            
            if (stockDisponible == 0) {
                btnRegistrar.setEnabled(false);
                btnRegistrar.setToolTipText("No hay stock disponible para salida");
            } else {
                btnRegistrar.setEnabled(true);
                btnRegistrar.setToolTipText(null);
            }
        } else {
            ((SpinnerNumberModel) spnCantidad.getModel()).setMaximum(10000);
            btnRegistrar.setEnabled(true);
            btnRegistrar.setToolTipText(null);
        }
    }
    
    private void registrarMovimiento() {
        try {
            // Validar formulario
            if (!validarFormulario()) {
                return;
            }
            
            TipoMovimiento tipo = (TipoMovimiento) cmbTipo.getSelectedItem();
            String productoId = txtProductoId.getText().trim();
            int cantidad = (Integer) spnCantidad.getValue();
            String motivo = txtMotivo.getText().trim();
            String referencia = txtReferencia.getText().trim();
            
            boolean exito = false;
            
            if (tipo == TipoMovimiento.ENTRADA) {
                exito = inventarioService.registrarEntrada(productoId, cantidad, motivo, usuarioActual, referencia);
            } else if (tipo == TipoMovimiento.SALIDA) {
                exito = inventarioService.registrarSalida(productoId, cantidad, motivo, usuarioActual, referencia);
            }
            
            if (exito) {
                JOptionPane.showMessageDialog(this, "Movimiento registrado exitosamente");
                cargarDatos();
                limpiarFormulario();
                
                // Actualizar información del producto
                if (productoSeleccionado != null) {
                    productoSeleccionado = inventarioService.buscarProducto(productoId);
                    mostrarInformacionProducto(productoSeleccionado);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Error al registrar movimiento", "Error", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (StockInsuficienteException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Stock Insuficiente", JOptionPane.ERROR_MESSAGE);
        } catch (ProductoNoEncontradoException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Producto No Encontrado", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al registrar movimiento: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void limpiarFormulario() {
        txtProductoId.setText("");
        spnCantidad.setValue(1);
        txtMotivo.setText("");
        txtReferencia.setText("");
        limpiarInformacionProducto();
        cmbTipo.setSelectedIndex(0);
        txtProductoId.requestFocus();
    }
    
    private boolean validarFormulario() {
        if (txtProductoId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto", "Error", JOptionPane.ERROR_MESSAGE);
            txtProductoId.requestFocus();
            return false;
        }
        
        if (productoSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Producto no válido", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if (txtMotivo.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El motivo es obligatorio", "Error", JOptionPane.ERROR_MESSAGE);
            txtMotivo.requestFocus();
            return false;
        }
        
        int cantidad = (Integer) spnCantidad.getValue();
        if (cantidad <= 0) {
            JOptionPane.showMessageDialog(this, "La cantidad debe ser mayor a 0", "Error", JOptionPane.ERROR_MESSAGE);
            spnCantidad.requestFocus();
            return false;
        }
        
        return true;
    }
    
    public void setUsuarioActual(String usuarioActual) {
        this.usuarioActual = usuarioActual;
    }
    
    public void refrescar() {
        cargarDatos();
    }
}
