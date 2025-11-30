package com.femaco.femacoproject.ui.panels;

import com.femaco.femacoproject.model.Usuario;
import com.femaco.femacoproject.model.enums.RolUsuario;
import com.femaco.femacoproject.service.AutenticacionService;
import com.femaco.femacoproject.ui.components.CustomButton;
import com.femaco.femacoproject.util.SecurityUtil;
import com.femaco.femacoproject.util.Validador;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class UsuarioPanel extends JPanel {
    private AutenticacionService autenticacionService;
    private String usuarioActualId;
    
    private JTable usuariosTable;
    private DefaultTableModel tableModel;
    
    private JTextField txtBuscar;
    private JTextField txtId;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JTextField txtNombreCompleto;
    private JTextField txtEmail;
    private JComboBox<RolUsuario> cmbRol;
    private JCheckBox chkActivo;
    
    private CustomButton btnNuevo;
    private CustomButton btnGuardar;
    private CustomButton btnEditar;
    private CustomButton btnEliminar;
    private CustomButton btnLimpiar;
    private CustomButton btnResetPassword;
    
    private boolean modoEdicion = false;
    private String usuarioEditando = null;
    
    public UsuarioPanel(AutenticacionService autenticacionService, String usuarioActualId) {
        this.autenticacionService = autenticacionService;
        this.usuarioActualId = usuarioActualId;
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
        
        // Tabla de usuarios
        JPanel tablePanel = crearTablePanel();
        
        // Panel de formulario
        JPanel formPanel = crearFormPanel();
        
        // Panel de botones
        JPanel buttonsPanel = crearButtonsPanel();
        
        // Agrupar formulario y botones
        JPanel editorPanel = new JPanel(new BorderLayout());
        editorPanel.setBorder(BorderFactory.createTitledBorder("Editor de Usuarios"));
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
        btnBuscar.addActionListener(e -> buscarUsuarios());
        panel.add(btnBuscar);
        
        CustomButton btnLimpiarBusqueda = CustomButton.createSecondaryButton("Limpiar");
        btnLimpiarBusqueda.addActionListener(e -> limpiarBusqueda());
        panel.add(btnLimpiarBusqueda);
        
        return panel;
    }
    
    private JPanel crearTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Lista de Usuarios"));
        panel.setPreferredSize(new Dimension(600, 400));
        
        String[] columnNames = {"ID", "Username", "Nombre Completo", "Rol", "Email", "Estado", "Último Acceso"};
        tableModel = new DefaultTableModel(columnNames, 0);
        usuariosTable = new JTable(tableModel);
        
        usuariosTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        usuariosTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                seleccionarUsuario();
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(usuariosTable);
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
        
        // Username
        panel.add(new JLabel("Username:*"));
        txtUsername = new JTextField();
        panel.add(txtUsername);
        
        // Password (solo para nuevos usuarios)
        panel.add(new JLabel("Password:"));
        txtPassword = new JPasswordField();
        panel.add(txtPassword);
        
        // Nombre Completo
        panel.add(new JLabel("Nombre Completo:*"));
        txtNombreCompleto = new JTextField();
        panel.add(txtNombreCompleto);
        
        // Email
        panel.add(new JLabel("Email:"));
        txtEmail = new JTextField();
        panel.add(txtEmail);
        
        // Rol
        panel.add(new JLabel("Rol:*"));
        cmbRol = new JComboBox<>(RolUsuario.values());
        panel.add(cmbRol);
        
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
        btnResetPassword = CustomButton.createInfoButton("Reset Password");
        btnLimpiar = CustomButton.createSecondaryButton("Limpiar");
        
        panel.add(btnNuevo);
        panel.add(btnGuardar);
        panel.add(btnEditar);
        panel.add(btnEliminar);
        panel.add(btnResetPassword);
        panel.add(btnLimpiar);
        
        return panel;
    }
    
    private void setupLayout() {
        setPreferredSize(new Dimension(1000, 600));
    }
    
    private void configurarEventos() {
        btnNuevo.addActionListener(e -> nuevoUsuario());
        btnGuardar.addActionListener(e -> guardarUsuario());
        btnEditar.addActionListener(e -> editarUsuario());
        btnEliminar.addActionListener(e -> eliminarUsuario());
        btnResetPassword.addActionListener(e -> resetPassword());
        btnLimpiar.addActionListener(e -> limpiarFormulario());
    }
    
    private void cargarDatos() {
        try {
            if (!autenticacionService.puedeGestionarUsuarios(usuarioActualId)) {
                JOptionPane.showMessageDialog(this,
                    "No tiene permisos para gestionar usuarios",
                    "Acceso Denegado", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            List<Usuario> usuarios = autenticacionService.listarUsuarios();
            actualizarTablaUsuarios(usuarios);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al cargar usuarios: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void actualizarTablaUsuarios(List<Usuario> usuarios) {
        tableModel.setRowCount(0);
        
        for (Usuario usuario : usuarios) {
            String ultimoAcceso = usuario.getUltimoAcceso() != null ? 
                new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(usuario.getUltimoAcceso()) : "Nunca";
            
            Object[] row = {
                usuario.getId(),
                usuario.getUsername(),
                usuario.getNombreCompleto(),
                usuario.getRol().getDescripcion(),
                usuario.getEmail(),
                usuario.isActivo() ? "Activo" : "Inactivo",
                ultimoAcceso
            };
            tableModel.addRow(row);
        }
    }
    
    private void buscarUsuarios() {
        String texto = txtBuscar.getText().trim();
        if (texto.isEmpty()) {
            cargarDatos();
            return;
        }
        
        try {
            List<Usuario> usuarios = autenticacionService.listarUsuarios();
            usuarios.removeIf(u -> !u.getNombreCompleto().toLowerCase().contains(texto.toLowerCase()) &&
                                  !u.getUsername().toLowerCase().contains(texto.toLowerCase()) &&
                                  !u.getId().toLowerCase().contains(texto.toLowerCase()));
            actualizarTablaUsuarios(usuarios);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al buscar usuarios: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void limpiarBusqueda() {
        txtBuscar.setText("");
        cargarDatos();
    }
    
    private void seleccionarUsuario() {
        int selectedRow = usuariosTable.getSelectedRow();
        if (selectedRow >= 0) {
            String id = tableModel.getValueAt(selectedRow, 0).toString();
            try {
                Usuario usuario = autenticacionService.buscarUsuario(id);
                llenarFormulario(usuario);
                habilitarEdicion(false);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Error al cargar usuario: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void llenarFormulario(Usuario usuario) {
        txtId.setText(usuario.getId());
        txtUsername.setText(usuario.getUsername());
        txtPassword.setText(""); // No mostrar password
        txtNombreCompleto.setText(usuario.getNombreCompleto());
        txtEmail.setText(usuario.getEmail());
        cmbRol.setSelectedItem(usuario.getRol());
        chkActivo.setSelected(usuario.isActivo());
        
        usuarioEditando = usuario.getId();
        actualizarBotones();
    }
    
    private void limpiarFormulario() {
        txtId.setText("");
        txtUsername.setText("");
        txtPassword.setText("");
        txtNombreCompleto.setText("");
        txtEmail.setText("");
        cmbRol.setSelectedIndex(0);
        chkActivo.setSelected(true);
        
        usuarioEditando = null;
        modoEdicion = false;
        habilitarEdicion(true);
        usuariosTable.clearSelection();
        actualizarBotones();
    }
    
    private void habilitarEdicion(boolean habilitar) {
        txtId.setEditable(habilitar);
        txtUsername.setEditable(habilitar);
        txtPassword.setEnabled(habilitar);
        txtNombreCompleto.setEditable(habilitar);
        txtEmail.setEditable(habilitar);
        cmbRol.setEnabled(habilitar);
        chkActivo.setEnabled(habilitar);
        
        btnGuardar.setEnabled(habilitar);
        btnEditar.setEnabled(!habilitar && usuarioEditando != null);
    }
    
    private void actualizarBotones() {
        if (usuarioEditando != null) {
            try {
                Usuario usuario = autenticacionService.buscarUsuario(usuarioEditando);
                if (usuario.isActivo()) {
                    btnEliminar.setText("Desactivar");
                    btnEliminar.setBackgroundColor(new Color(220, 53, 69)); // Rojo
                } else {
                    btnEliminar.setText("Activar");
                    btnEliminar.setBackgroundColor(new Color(40, 167, 69)); // Verde
                }
                btnEliminar.setEnabled(true);
                btnResetPassword.setEnabled(true);
            } catch (Exception e) {
                btnEliminar.setEnabled(false);
                btnResetPassword.setEnabled(false);
            }
        } else {
            btnEliminar.setEnabled(false);
            btnResetPassword.setEnabled(false);
        }
        
        // No permitir editar el usuario actual
        if (usuarioEditando != null && usuarioEditando.equals(usuarioActualId)) {
            btnEliminar.setEnabled(false);
            btnEliminar.setToolTipText("No puede desactivar su propio usuario");
        } else {
            btnEliminar.setToolTipText(null);
        }
    }
    
    private void nuevoUsuario() {
        limpiarFormulario();
        modoEdicion = true;
        habilitarEdicion(true);
        txtId.requestFocus();
    }
    
    private void editarUsuario() {
        if (usuarioEditando != null) {
            modoEdicion = true;
            habilitarEdicion(true);
            txtNombreCompleto.requestFocus();
        }
    }
    
    private void guardarUsuario() {
        try {
            // Validar permisos
            if (!autenticacionService.puedeGestionarUsuarios(usuarioActualId)) {
                JOptionPane.showMessageDialog(this,
                    "No tiene permisos para gestionar usuarios",
                    "Acceso Denegado", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Validar campos obligatorios
            if (!validarFormulario()) {
                return;
            }
            
            String id = txtId.getText().trim();
            String username = txtUsername.getText().trim();
            String password = new String(txtPassword.getPassword());
            String nombreCompleto = txtNombreCompleto.getText().trim();
            String email = txtEmail.getText().trim();
            RolUsuario rol = (RolUsuario) cmbRol.getSelectedItem();
            boolean activo = chkActivo.isSelected();
            
            Usuario usuario;
            
            if (modoEdicion && usuarioEditando != null) {
                // Modo edición
                usuario = autenticacionService.buscarUsuario(usuarioEditando);
                usuario.setUsername(username);
                usuario.setNombreCompleto(nombreCompleto);
                usuario.setEmail(email);
                usuario.setRol(rol);
                usuario.setActivo(activo);
                
                // Solo actualizar password si se proporcionó uno nuevo
                if (!password.isEmpty()) {
                    if (!SecurityUtil.isStrongPassword(password)) {
                        JOptionPane.showMessageDialog(this,
                            "El password no cumple con los requisitos de seguridad",
                            "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    usuario.setPassword(SecurityUtil.hashPassword(password));
                }
                
                autenticacionService.actualizarUsuario(usuario);
                JOptionPane.showMessageDialog(this, "Usuario actualizado exitosamente");
                
            } else {
                // Modo nuevo - password es obligatorio
                if (password.isEmpty()) {
                    JOptionPane.showMessageDialog(this, 
                        "El password es obligatorio para nuevos usuarios", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                    txtPassword.requestFocus();
                    return;
                }
                
                if (!SecurityUtil.isStrongPassword(password)) {
                    JOptionPane.showMessageDialog(this,
                        "El password no cumple con los requisitos de seguridad. " +
                        "Debe tener al menos 8 caracteres, incluir mayúsculas, minúsculas, números y símbolos.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                String passwordHash = SecurityUtil.hashPassword(password);
                usuario = new Usuario(id, username, passwordHash, nombreCompleto, email, rol);
                usuario.setActivo(activo);
                
                autenticacionService.registrarUsuario(usuario);
                JOptionPane.showMessageDialog(this, "Usuario registrado exitosamente");
            }
            
            cargarDatos();
            limpiarFormulario();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al guardar usuario: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void eliminarUsuario() {
        if (usuarioEditando == null || usuarioEditando.equals(usuarioActualId)) {
            return;
        }
        
        try {
            Usuario usuario = autenticacionService.buscarUsuario(usuarioEditando);
            String accion = usuario.isActivo() ? "desactivar" : "activar";
            
            int confirm = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de " + accion + " el usuario '" + usuarioEditando + "'?",
                "Confirmar " + (usuario.isActivo() ? "Desactivación" : "Activación"),
                JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                if (usuario.isActivo()) {
                    autenticacionService.desactivarUsuario(usuarioEditando, usuarioActualId);
                } else {
                    autenticacionService.activarUsuario(usuarioEditando, usuarioActualId);
                }
                
                JOptionPane.showMessageDialog(this, 
                    "Usuario " + (usuario.isActivo() ? "desactivado" : "activado") + " exitosamente");
                cargarDatos();
                limpiarFormulario();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al " + (usuarioEditando != null ? "desactivar" : "activar") + " usuario: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void resetPassword() {
        if (usuarioEditando == null) {
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Está seguro de resetear el password del usuario '" + usuarioEditando + "'?\n" +
            "Se generará un password temporal que deberá ser cambiado en el próximo inicio de sesión.",
            "Confirmar Reset de Password", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                autenticacionService.resetearPassword(usuarioEditando, usuarioActualId);
                JOptionPane.showMessageDialog(this, 
                    "Password reseteado exitosamente. El usuario recibirá un password temporal.");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Error al resetear password: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private boolean validarFormulario() {
        if (txtId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El ID es obligatorio", "Error", JOptionPane.ERROR_MESSAGE);
            txtId.requestFocus();
            return false;
        }
        
        if (txtUsername.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El username es obligatorio", "Error", JOptionPane.ERROR_MESSAGE);
            txtUsername.requestFocus();
            return false;
        }
        
        if (txtNombreCompleto.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre completo es obligatorio", "Error", JOptionPane.ERROR_MESSAGE);
            txtNombreCompleto.requestFocus();
            return false;
        }
        
        // Validar email si está presente
        String email = txtEmail.getText().trim();
        if (!email.isEmpty() && !Validador.validarEmail(email)) {
            JOptionPane.showMessageDialog(this, "El formato del email no es válido", "Error", JOptionPane.ERROR_MESSAGE);
            txtEmail.requestFocus();
            return false;
        }
        
        return true;
    }
    
    public void setUsuarioActualId(String usuarioActualId) {
        this.usuarioActualId = usuarioActualId;
    }
    
    public void refrescar() {
        cargarDatos();
    }
}