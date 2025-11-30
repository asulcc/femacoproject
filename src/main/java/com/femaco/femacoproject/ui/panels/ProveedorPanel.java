package com.femaco.femacoproject.ui.panels;

import com.femaco.femacoproject.model.Proveedor;
import com.femaco.femacoproject.service.GestionInventarioService;
import com.femaco.femacoproject.ui.components.CustomButton;
import com.femaco.femacoproject.util.Validador;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ProveedorPanel extends JPanel {
    private GestionInventarioService inventarioService;
    
    private JTable proveedoresTable;
    private DefaultTableModel tableModel;
    
    private JTextField txtBuscar;
    private JTextField txtId;
    private JTextField txtNombre;
    private JTextField txtContacto;
    private JTextField txtTelefono;
    private JTextField txtEmail;
    private JTextArea txtDireccion;
    private JCheckBox chkActivo;
    
    private CustomButton btnNuevo;
    private CustomButton btnGuardar;
    private CustomButton btnEditar;
    private CustomButton btnEliminar;
    private CustomButton btnLimpiar;
    
    private boolean modoEdicion = false;
    private String proveedorEditando = null;
    
    public ProveedorPanel(GestionInventarioService inventarioService) {
        this.inventarioService = inventarioService;
        initComponents();
        setupLayout();
        cargarDatos();
        configurarEventos();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Panel de búsqueda
        JPanel searchPanel = crearSearchPanel();
        
        // Tabla de proveedores
        JPanel tablePanel = crearTablePanel();
        
        // Panel de formulario
        JPanel formPanel = crearFormPanel();
        
        // Panel de botones
        JPanel buttonsPanel = crearButtonsPanel();
        
        // Agrupar formulario y botones
        JPanel editorPanel = new JPanel(new BorderLayout());
        editorPanel.setBorder(BorderFactory.createTitledBorder("Editor de Proveedores"));
        editorPanel.add(formPanel, BorderLayout.CENTER);
        editorPanel.add(buttonsPanel, BorderLayout.SOUTH);
        
        add(searchPanel, BorderLayout.NORTH);
        add(tablePanel, BorderLayout.CENTER);
        add(editorPanel, BorderLayout.EAST);
    }
    
    private JPanel crearSearchPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Búsqueda"));
        
        panel.add(new JLabel("Buscar:"));
        txtBuscar = new JTextField(20);
        panel.add(txtBuscar);
        
        CustomButton btnBuscar = CustomButton.createPrimaryButton("Buscar");
        btnBuscar.addActionListener(e -> buscarProveedores());
        panel.add(btnBuscar);
        
        CustomButton btnLimpiarBusqueda = CustomButton.createSecondaryButton("Limpiar");
        btnLimpiarBusqueda.addActionListener(e -> limpiarBusqueda());
        panel.add(btnLimpiarBusqueda);
        
        return panel;
    }
    
    private JPanel crearTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Lista de Proveedores"));
        panel.setPreferredSize(new Dimension(600, 400));
        
        String[] columnNames = {"ID", "Nombre", "Contacto", "Teléfono", "Email", "Estado"};
        tableModel = new DefaultTableModel(columnNames, 0);
        proveedoresTable = new JTable(tableModel);
        
        proveedoresTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        proveedoresTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                seleccionarProveedor();
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(proveedoresTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel crearFormPanel() {
        JPanel panel = new JPanel(new GridLayout(7, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // ID
        panel.add(new JLabel("ID:*"));
        txtId = new JTextField();
        panel.add(txtId);
        
        // Nombre
        panel.add(new JLabel("Nombre:*"));
        txtNombre = new JTextField();
        panel.add(txtNombre);
        
        // Contacto
        panel.add(new JLabel("Contacto:"));
        txtContacto = new JTextField();
        panel.add(txtContacto);
        
        // Teléfono
        panel.add(new JLabel("Teléfono:"));
        txtTelefono = new JTextField();
        panel.add(txtTelefono);
        
        // Email
        panel.add(new JLabel("Email:"));
        txtEmail = new JTextField();
        panel.add(txtEmail);
        
        // Dirección
        panel.add(new JLabel("Dirección:"));
        txtDireccion = new JTextArea(3, 20);
        txtDireccion.setLineWrap(true);
        JScrollPane scrollDireccion = new JScrollPane(txtDireccion);
        panel.add(scrollDireccion);
        
        // Activo
        panel.add(new JLabel("Activo:"));
        chkActivo = new JCheckBox();
        chkActivo.setSelected(true);
        panel.add(chkActivo);
        
        return panel;
    }
    
    private JPanel crearButtonsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        btnNuevo = CustomButton.createSuccessButton("Nuevo");
        btnGuardar = CustomButton.createPrimaryButton("Guardar");
        btnEditar = CustomButton.createWarningButton("Editar");
        btnEliminar = CustomButton.createDangerButton("Desactivar");
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
        btnNuevo.addActionListener(e -> nuevoProveedor());
        btnGuardar.addActionListener(e -> guardarProveedor());
        btnEditar.addActionListener(e -> editarProveedor());
        btnEliminar.addActionListener(e -> eliminarProveedor());
        btnLimpiar.addActionListener(e -> limpiarFormulario());
    }
    
    private void cargarDatos() {
        try {
            List<Proveedor> proveedores = inventarioService.listarProveedores();
            actualizarTablaProveedores(proveedores);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al cargar proveedores: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void actualizarTablaProveedores(List<Proveedor> proveedores) {
        tableModel.setRowCount(0);
        
        for (Proveedor prov : proveedores) {
            Object[] row = {
                prov.getId(),
                prov.getNombre(),
                prov.getContacto(),
                prov.getTelefono(),
                prov.getEmail(),
                prov.isActivo() ? "Activo" : "Inactivo"
            };
            tableModel.addRow(row);
        }
    }
    
    private void buscarProveedores() {
        String texto = txtBuscar.getText().trim();
        if (texto.isEmpty()) {
            cargarDatos();
            return;
        }
        
        try {
            List<Proveedor> proveedores = inventarioService.listarProveedores();
            proveedores.removeIf(p -> !p.getNombre().toLowerCase().contains(texto.toLowerCase()) &&
                                     !p.getId().toLowerCase().contains(texto.toLowerCase()));
            actualizarTablaProveedores(proveedores);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al buscar proveedores: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void limpiarBusqueda() {
        txtBuscar.setText("");
        cargarDatos();
    }
    
    private void seleccionarProveedor() {
        int selectedRow = proveedoresTable.getSelectedRow();
        if (selectedRow >= 0) {
            String id = tableModel.getValueAt(selectedRow, 0).toString();
            try {
                Proveedor proveedor = inventarioService.buscarProveedor(id);
                llenarFormulario(proveedor);
                habilitarEdicion(false);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Error al cargar proveedor: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void llenarFormulario(Proveedor proveedor) {
        txtId.setText(proveedor.getId());
        txtNombre.setText(proveedor.getNombre());
        txtContacto.setText(proveedor.getContacto());
        txtTelefono.setText(proveedor.getTelefono());
        txtEmail.setText(proveedor.getEmail());
        txtDireccion.setText(proveedor.getDireccion());
        chkActivo.setSelected(proveedor.isActivo());
        
        proveedorEditando = proveedor.getId();
        actualizarBotonEliminar();
    }
    
    private void limpiarFormulario() {
        txtId.setText("");
        txtNombre.setText("");
        txtContacto.setText("");
        txtTelefono.setText("");
        txtEmail.setText("");
        txtDireccion.setText("");
        chkActivo.setSelected(true);
        
        proveedorEditando = null;
        modoEdicion = false;
        habilitarEdicion(true);
        proveedoresTable.clearSelection();
        actualizarBotonEliminar();
    }
    
    private void habilitarEdicion(boolean habilitar) {
        txtId.setEditable(habilitar);
        txtNombre.setEditable(habilitar);
        txtContacto.setEditable(habilitar);
        txtTelefono.setEditable(habilitar);
        txtEmail.setEditable(habilitar);
        txtDireccion.setEditable(habilitar);
        chkActivo.setEnabled(habilitar);
        
        btnGuardar.setEnabled(habilitar);
        btnEditar.setEnabled(!habilitar && proveedorEditando != null);
    }
    
    private void actualizarBotonEliminar() {
        if (proveedorEditando != null) {
            try {
                Proveedor proveedor = inventarioService.buscarProveedor(proveedorEditando);
                if (proveedor.isActivo()) {
                    btnEliminar.setText("Desactivar");
                    btnEliminar.setBackgroundColor(new Color(220, 53, 69)); // Rojo
                } else {
                    btnEliminar.setText("Activar");
                    btnEliminar.setBackgroundColor(new Color(40, 167, 69)); // Verde
                }
                btnEliminar.setEnabled(true);
            } catch (Exception e) {
                btnEliminar.setEnabled(false);
            }
        } else {
            btnEliminar.setEnabled(false);
        }
    }
    
    private void nuevoProveedor() {
        limpiarFormulario();
        modoEdicion = true;
        habilitarEdicion(true);
        txtId.requestFocus();
    }
    
    private void editarProveedor() {
        if (proveedorEditando != null) {
            modoEdicion = true;
            habilitarEdicion(true);
            txtNombre.requestFocus();
        }
    }
    
    private void guardarProveedor() {
        try {
            // Validar campos obligatorios
            if (!validarFormulario()) {
                return;
            }
            
            String id = txtId.getText().trim();
            String nombre = txtNombre.getText().trim();
            String contacto = txtContacto.getText().trim();
            String telefono = txtTelefono.getText().trim();
            String email = txtEmail.getText().trim();
            String direccion = txtDireccion.getText().trim();
            boolean activo = chkActivo.isSelected();
            
            Proveedor proveedor;
            
            if (modoEdicion && proveedorEditando != null) {
                // Modo edición
                proveedor = inventarioService.buscarProveedor(proveedorEditando);
                proveedor.setNombre(nombre);
                proveedor.setContacto(contacto);
                proveedor.setTelefono(telefono);
                proveedor.setEmail(email);
                proveedor.setDireccion(direccion);
                proveedor.setActivo(activo);
                
                inventarioService.actualizarProveedor(proveedor);
                JOptionPane.showMessageDialog(this, "Proveedor actualizado exitosamente");
                
            } else {
                // Modo nuevo
                proveedor = new Proveedor(id, nombre, contacto, telefono);
                proveedor.setEmail(email);
                proveedor.setDireccion(direccion);
                proveedor.setActivo(activo);
                
                inventarioService.registrarProveedor(proveedor);
                JOptionPane.showMessageDialog(this, "Proveedor registrado exitosamente");
            }
            
            cargarDatos();
            limpiarFormulario();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al guardar proveedor: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void eliminarProveedor() {
        if (proveedorEditando == null) {
            return;
        }
        
        try {
            Proveedor proveedor = inventarioService.buscarProveedor(proveedorEditando);
            String accion = proveedor.isActivo() ? "desactivar" : "activar";
            
            int confirm = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de " + accion + " el proveedor '" + proveedorEditando + "'?",
                "Confirmar " + (proveedor.isActivo() ? "Desactivación" : "Activación"),
                JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                if (proveedor.isActivo()) {
                    inventarioService.desactivarProveedor(proveedorEditando);
                } else {
                    // Para activar, actualizamos el proveedor estableciendo activo = true
                    proveedor.setActivo(true);
                    inventarioService.actualizarProveedor(proveedor);
                }
                
                JOptionPane.showMessageDialog(this, 
                    "Proveedor " + (proveedor.isActivo() ? "desactivado" : "activado") + " exitosamente");
                cargarDatos();
                limpiarFormulario();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al " + (proveedorEditando != null ? "desactivar" : "activar") + " proveedor: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private boolean validarFormulario() {
        if (txtId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El ID es obligatorio", "Error", JOptionPane.ERROR_MESSAGE);
            txtId.requestFocus();
            return false;
        }
        
        if (txtNombre.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre es obligatorio", "Error", JOptionPane.ERROR_MESSAGE);
            txtNombre.requestFocus();
            return false;
        }
        
        // Validar email si está presente
        String email = txtEmail.getText().trim();
        if (!email.isEmpty() && !Validador.validarEmail(email)) {
            JOptionPane.showMessageDialog(this, "El formato del email no es válido", "Error", JOptionPane.ERROR_MESSAGE);
            txtEmail.requestFocus();
            return false;
        }
        
        // Validar teléfono si está presente
        String telefono = txtTelefono.getText().trim();
        if (!telefono.isEmpty() && !Validador.validarTelefono(telefono)) {
            JOptionPane.showMessageDialog(this, "El formato del teléfono no es válido", "Error", JOptionPane.ERROR_MESSAGE);
            txtTelefono.requestFocus();
            return false;
        }
        
        return true;
    }
    
    public void refrescar() {
        cargarDatos();
    }
}