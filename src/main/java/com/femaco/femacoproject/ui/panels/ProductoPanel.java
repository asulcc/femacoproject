package com.femaco.femacoproject.ui.panels;

import com.femaco.femacoproject.model.Producto;
import com.femaco.femacoproject.model.Proveedor;
import com.femaco.femacoproject.model.enums.CategoriaProducto;
import com.femaco.femacoproject.model.enums.EstadoProducto;
import com.femaco.femacoproject.service.GestionInventarioService;
import com.femaco.femacoproject.ui.components.AutoCompleteComboBox;
import com.femaco.femacoproject.ui.components.CustomButton;
import com.femaco.femacoproject.ui.components.ProductoTableModel;

import javax.swing.*;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;

public class ProductoPanel extends JPanel {
    private GestionInventarioService inventarioService;
    
    private ProductoTableModel tableModel;
    private JTable productosTable;
    private TableRowSorter<ProductoTableModel> sorter;
    
    private JTextField txtBuscar;
    private JComboBox<CategoriaProducto> cmbCategoriaFiltro;
    private JComboBox<EstadoProducto> cmbEstadoFiltro;
    
    private JTextField txtId;
    private JTextField txtNombre;
    private JComboBox<CategoriaProducto> cmbCategoria;
    private JSpinner spnStockMinimo;
    private JSpinner spnPrecio;
    private JTextField txtUbicacion;
//    private AutoCompleteComboBox cmbProveedor;
    private JComboBox<String> cmbProveedor;
    
    private CustomButton btnNuevo;
    private CustomButton btnGuardar;
    private CustomButton btnEditar;
    private CustomButton btnEliminar;
    private CustomButton btnLimpiar;
    
    private boolean modoEdicion = false;
    private String productoEditando = null;
    
    public ProductoPanel(GestionInventarioService inventarioService) {
        this.inventarioService = inventarioService;
        initComponents();
        setupLayout();
        cargarDatos();
        configurarEventos();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Panel de búsqueda y filtros
        JPanel searchPanel = crearSearchPanel();
        
        // Tabla de productos
        JPanel tablePanel = crearTablePanel();
        
        // Panel de formulario
        JPanel formPanel = crearFormPanel();
        
        // Panel de botones
        JPanel buttonsPanel = crearButtonsPanel();
        
        // Agrupar formulario y botones
        JPanel editorPanel = new JPanel(new BorderLayout());
        editorPanel.setBorder(BorderFactory.createTitledBorder("Editor de Productos"));
        editorPanel.add(formPanel, BorderLayout.CENTER);
        editorPanel.add(buttonsPanel, BorderLayout.SOUTH);
        
        add(searchPanel, BorderLayout.NORTH);
        add(tablePanel, BorderLayout.CENTER);
        add(editorPanel, BorderLayout.EAST);
    }
    
    private JPanel crearSearchPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Búsqueda y Filtros"));
        
        panel.add(new JLabel("Buscar:"));
        txtBuscar = new JTextField(20);
        panel.add(txtBuscar);
        
        panel.add(new JLabel("Categoría:"));
        cmbCategoriaFiltro = new JComboBox<>(CategoriaProducto.values());
        cmbCategoriaFiltro.insertItemAt(null, 0);
        cmbCategoriaFiltro.setSelectedIndex(0);
        panel.add(cmbCategoriaFiltro);
        
        panel.add(new JLabel("Estado:"));
        cmbEstadoFiltro = new JComboBox<>(EstadoProducto.values());
        cmbEstadoFiltro.insertItemAt(null, 0);
        cmbEstadoFiltro.setSelectedIndex(0);
        panel.add(cmbEstadoFiltro);
        
        CustomButton btnBuscar = CustomButton.createPrimaryButton("Buscar");
        btnBuscar.addActionListener(e -> buscarProductos());
        panel.add(btnBuscar);
        
        CustomButton btnLimpiarFiltros = CustomButton.createSecondaryButton("Limpiar");
        btnLimpiarFiltros.addActionListener(e -> limpiarFiltros());
        panel.add(btnLimpiarFiltros);
        
        return panel;
    }
    
    private JPanel crearTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Lista de Productos"));
        panel.setPreferredSize(new Dimension(600, 400));
        
        tableModel = new ProductoTableModel();
        productosTable = new JTable(tableModel);
        sorter = new TableRowSorter<>(tableModel);
        productosTable.setRowSorter(sorter);
        
        productosTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        productosTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                seleccionarProducto();
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(productosTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel crearFormPanel() {
        JPanel panel = new JPanel(new GridLayout(7, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // ID
        panel.add(new JLabel("ID:"));
        txtId = new JTextField();
        txtId.setEditable(false);
        panel.add(txtId);
        
        // Nombre
        panel.add(new JLabel("Nombre:*"));
        txtNombre = new JTextField();
        panel.add(txtNombre);
        
        // Categoría
        panel.add(new JLabel("Categoría:*"));
        cmbCategoria = new JComboBox<>(CategoriaProducto.values());
        panel.add(cmbCategoria);
        
        // Stock Mínimo
        panel.add(new JLabel("Stock Mínimo:*"));
        spnStockMinimo = new JSpinner(new SpinnerNumberModel(0, 0, 10000, 1));
        panel.add(spnStockMinimo);
        
        // Precio
        panel.add(new JLabel("Precio (S/.):*"));
        spnPrecio = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 1000000.0, 0.1));
        panel.add(spnPrecio);
        
        // Ubicación
        panel.add(new JLabel("Ubicación:*"));
        txtUbicacion = new JTextField();
        panel.add(txtUbicacion);
        
        // Proveedor
        panel.add(new JLabel("Proveedor:"));
//        cmbProveedor = new AutoCompleteComboBox();
        cmbProveedor = new JComboBox<String>();
        panel.add(cmbProveedor);
        
        return panel;
    }
    
    private JPanel crearButtonsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        btnNuevo = CustomButton.createSuccessButton("Nuevo");
        btnGuardar = CustomButton.createPrimaryButton("Guardar");
        btnEditar = CustomButton.createWarningButton("Editar");
        btnEliminar = CustomButton.createDangerButton("Eliminar");
        btnLimpiar = CustomButton.createSecondaryButton("Limpiar");
        
        panel.add(btnNuevo);
        panel.add(btnGuardar);
        panel.add(btnEditar);
        panel.add(btnEliminar);
        panel.add(btnLimpiar);
        
        return panel;
    }
    
    private void setupLayout() {
        setPreferredSize(new Dimension(1000, 600));
    }
    
    private void configurarEventos() {
        btnNuevo.addActionListener(e -> nuevoProducto());
        btnGuardar.addActionListener(e -> guardarProducto());
        btnEditar.addActionListener(e -> editarProducto());
        btnEliminar.addActionListener(e -> eliminarProducto());
        btnLimpiar.addActionListener(e -> limpiarFormulario());
    }
    
    private void cargarDatos() {
        try {
            List<Producto> productos = inventarioService.listarProductos();
            tableModel.setProductos(productos);
            
            // Cargar proveedores para el combobox
            List<Proveedor> proveedores = inventarioService.listarProveedores();
            cmbProveedor.removeAllItems();
            for (Proveedor prov : proveedores) {
                cmbProveedor.addItem(prov.getId() + " - " + prov.getNombre());
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al cargar productos: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void buscarProductos() {
        String texto = txtBuscar.getText().trim();
        CategoriaProducto categoria = (CategoriaProducto) cmbCategoriaFiltro.getSelectedItem();
        EstadoProducto estado = (EstadoProducto) cmbEstadoFiltro.getSelectedItem();
        
        List<Producto> productos = inventarioService.listarProductos();
        
        // Aplicar filtros
        if (!texto.isEmpty()) {
            productos.removeIf(p -> !p.getNombre().toLowerCase().contains(texto.toLowerCase()) &&
                                   !p.getId().toLowerCase().contains(texto.toLowerCase()));
        }
        
        if (categoria != null) {
            productos.removeIf(p -> p.getCategoria() != categoria);
        }
        
        if (estado != null) {
            productos.removeIf(p -> p.getEstado() != estado);
        }
        
        tableModel.setProductos(productos);
    }
    
    private void limpiarFiltros() {
        txtBuscar.setText("");
        cmbCategoriaFiltro.setSelectedIndex(0);
        cmbEstadoFiltro.setSelectedIndex(0);
        cargarDatos();
    }
    
    private void seleccionarProducto() {
        int selectedRow = productosTable.getSelectedRow();
        if (selectedRow >= 0) {
            int modelRow = productosTable.convertRowIndexToModel(selectedRow);
            Producto producto = tableModel.getProductoAt(modelRow);
            if (producto != null) {
                llenarFormulario(producto);
                habilitarEdicion(false);
            }
        }
    }
    
    private void llenarFormulario(Producto producto) {
        txtId.setText(producto.getId());
        txtNombre.setText(producto.getNombre());
        cmbCategoria.setSelectedItem(producto.getCategoria());
        spnStockMinimo.setValue(producto.getStockMinimo());
        spnPrecio.setValue(producto.getPrecio());
        txtUbicacion.setText(producto.getUbicacion());
        
        if (producto.getProveedorId() != null) {
            cmbProveedor.setSelectedItem(producto.getProveedorId());
        } else {
            cmbProveedor.setSelectedItem(null);
        }
        
        productoEditando = producto.getId();
    }
    
    private void limpiarFormulario() {
        txtId.setText("");
        txtNombre.setText("");
        cmbCategoria.setSelectedIndex(0);
        spnStockMinimo.setValue(0);
        spnPrecio.setValue(0.0);
        txtUbicacion.setText("");
        cmbProveedor.setSelectedItem(null);
        
        productoEditando = null;
        modoEdicion = false;
        habilitarEdicion(true);
        productosTable.clearSelection();
    }
    
    private void habilitarEdicion(boolean habilitar) {
        txtNombre.setEditable(habilitar);
        cmbCategoria.setEnabled(habilitar);
        spnStockMinimo.setEnabled(habilitar);
        spnPrecio.setEnabled(habilitar);
        txtUbicacion.setEditable(habilitar);
        cmbProveedor.setEnabled(habilitar);
        
        btnGuardar.setEnabled(habilitar);
        btnEditar.setEnabled(!habilitar && productoEditando != null);
        btnEliminar.setEnabled(!habilitar && productoEditando != null);
    }
    
    private void nuevoProducto() {
        limpiarFormulario();
        modoEdicion = true;
        habilitarEdicion(true);
        txtNombre.requestFocus();
    }
    
    private void editarProducto() {
        if (productoEditando != null) {
            modoEdicion = true;
            habilitarEdicion(true);
            txtNombre.requestFocus();
        }
    }
    
    private void guardarProducto() {
        try {
            // Validar campos obligatorios
            if (!validarFormulario()) {
                return;
            }
            
            String id = txtId.getText().trim();
            String nombre = txtNombre.getText().trim();
            CategoriaProducto categoria = (CategoriaProducto) cmbCategoria.getSelectedItem();
            int stockMinimo = (Integer) spnStockMinimo.getValue();
            double precio = (Double) spnPrecio.getValue();
            String ubicacion = txtUbicacion.getText().trim();
            String proveedorId = obtenerIdProveedorSeleccionado();
            
            Producto producto;
            
            if (modoEdicion && productoEditando != null) {
                // Modo edición
                producto = inventarioService.buscarProducto(productoEditando);
                producto.setNombre(nombre);
                producto.setCategoria(categoria);
                producto.setStockMinimo(stockMinimo);
                producto.setPrecio(precio);
                producto.setUbicacion(ubicacion);
                producto.setProveedorId(proveedorId);
                
                inventarioService.actualizarProducto(producto);
                JOptionPane.showMessageDialog(this, "Producto actualizado exitosamente");
                
            } else {
                // Modo nuevo
                // Generar ID automático si está vacío
                if (id.isEmpty()) {
                    id = generarIdProducto(nombre);
                }
                
                producto = new Producto(id, nombre, categoria, 0, stockMinimo, precio, ubicacion, proveedorId);
                inventarioService.registrarProducto(producto);
                JOptionPane.showMessageDialog(this, "Producto registrado exitosamente");
            }
            
            cargarDatos();
            limpiarFormulario();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al guardar producto: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void eliminarProducto() {
        if (productoEditando == null) {
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Está seguro de eliminar el producto '" + productoEditando + "'?",
            "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                inventarioService.eliminarProducto(productoEditando);
                JOptionPane.showMessageDialog(this, "Producto eliminado exitosamente");
                cargarDatos();
                limpiarFormulario();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Error al eliminar producto: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private boolean validarFormulario() {
        if (txtNombre.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre es obligatorio", "Error", JOptionPane.ERROR_MESSAGE);
            txtNombre.requestFocus();
            return false;
        }
        
        if (cmbCategoria.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "La categoría es obligatoria", "Error", JOptionPane.ERROR_MESSAGE);
            cmbCategoria.requestFocus();
            return false;
        }
        
        if (txtUbicacion.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "La ubicación es obligatoria", "Error", JOptionPane.ERROR_MESSAGE);
            txtUbicacion.requestFocus();
            return false;
        }
        
        return true;
    }
    
    private String obtenerIdProveedorSeleccionado() {
        Object selected = cmbProveedor.getSelectedItem();
        if (selected != null) {
            String texto = selected.toString();
            if (texto.contains(" - ")) {
                return texto.split(" - ")[0];
            }
            return texto;
        }
        return null;
    }
    
    private String generarIdProducto(String nombre) {
        String base = nombre.substring(0, Math.min(3, nombre.length())).toUpperCase();
        String timestamp = String.valueOf(System.currentTimeMillis());
        return base + "_" + timestamp.substring(timestamp.length() - 4);
    }
    
    public void refrescar() {
        cargarDatos();
    }
}
