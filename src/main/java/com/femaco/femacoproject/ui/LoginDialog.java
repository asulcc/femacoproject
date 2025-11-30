package com.femaco.femacoproject.ui;

import com.femaco.femacoproject.exception.UsuarioNoAutorizadoException;
import com.femaco.femacoproject.model.Usuario;
import com.femaco.femacoproject.service.AutenticacionService;
import com.femaco.femacoproject.ui.components.CustomButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class LoginDialog extends JDialog {
    private AutenticacionService autenticacionService;
    private Usuario usuarioAutenticado;
    
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private CustomButton btnLogin;
    private CustomButton btnCancel;
    private JLabel lblMensaje;
    
    public LoginDialog(Frame parent, AutenticacionService autenticacionService) {
        super(parent, "Iniciar Sesión - FEMACO Inventario", true);
        this.autenticacionService = autenticacionService;
        this.usuarioAutenticado = null;
        
        initComponents();
        setupLayout();
        configurarEventos();
        
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
        setLocationRelativeTo(parent);
        setResizable(false);
    }
    
    private void initComponents() {
        // Configurar el panel de contenido principal
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        
        // Logo/Header
        JPanel headerPanel = crearHeaderPanel();
        
        // Formulario de login
        JPanel formPanel = crearFormPanel();
        
        // Panel de botones
        JPanel buttonPanel = crearButtonPanel();
        
        // Panel de mensajes
        JPanel messagePanel = crearMessagePanel();
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        contentPanel.add(mainPanel, BorderLayout.CENTER);
        contentPanel.add(messagePanel, BorderLayout.SOUTH);
        
        // Establecer el panel de contenido
        setContentPane(contentPanel);
    }
    
    private JPanel crearHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        JLabel lblTitle = new JLabel("Sistema de Gestión de Inventario", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(new Color(0, 102, 204)); // Azul FEMACO
        
        JLabel lblSubtitle = new JLabel("FEMACO S.R.L.", JLabel.CENTER);
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtitle.setForeground(Color.GRAY);
        
        panel.add(lblTitle, BorderLayout.CENTER);
        panel.add(lblSubtitle, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel crearFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Credenciales de Acceso"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        
        // Username
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Usuario:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 0;
        txtUsername = new JTextField(20);
        txtUsername.setToolTipText("Ingrese su nombre de usuario");
        panel.add(txtUsername, gbc);
        
        // Password
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Contraseña:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        txtPassword = new JPasswordField(20);
        txtPassword.setToolTipText("Ingrese su contraseña");
        panel.add(txtPassword, gbc);
        
        return panel;
    }
    
    private JPanel crearButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        
        btnLogin = CustomButton.createPrimaryButton("Iniciar Sesión");
        btnLogin.setPreferredSize(new Dimension(120, 35));
        
        btnCancel = CustomButton.createSecondaryButton("Cancelar");
        btnCancel.setPreferredSize(new Dimension(120, 35));
        
        panel.add(btnLogin);
        panel.add(btnCancel);
        
        return panel;
    }
    
    private JPanel crearMessagePanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        lblMensaje = new JLabel(" ");
        lblMensaje.setForeground(Color.RED);
        lblMensaje.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        
        panel.add(lblMensaje);
        
        return panel;
    }
    
    private void setupLayout() {
        setPreferredSize(new Dimension(400, 350));
        getRootPane().setDefaultButton(btnLogin);
    }
    
    private void configurarEventos() {
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                realizarLogin();
            }
        });
        
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelarLogin();
            }
        });
        
        // Enter en campos de texto también ejecuta login
        KeyAdapter enterKeyAdapter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    realizarLogin();
                }
            }
        };
        
        txtUsername.addKeyListener(enterKeyAdapter);
        txtPassword.addKeyListener(enterKeyAdapter);
        
        // Cerrar con ESC
        getRootPane().registerKeyboardAction(
            new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    cancelarLogin();
                }
            },
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );
    }
    
    private void realizarLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());
        
        // Validaciones básicas
        if (username.isEmpty()) {
            mostrarError("Por favor ingrese su nombre de usuario");
            txtUsername.requestFocus();
            return;
        }
        
        if (password.isEmpty()) {
            mostrarError("Por favor ingrese su contraseña");
            txtPassword.requestFocus();
            return;
        }
        
        // Intentar autenticación
        try {
            usuarioAutenticado = autenticacionService.autenticar(username, password);
            mostrarExito("¡Bienvenido " + usuarioAutenticado.getNombreCompleto() + "!");
            
            // Pequeña pausa para mostrar mensaje de éxito
            Timer timer = new Timer(1000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dispose();
                }
            });
            timer.setRepeats(false);
            timer.start();
            
        } catch (UsuarioNoAutorizadoException e) {
            mostrarError("Credenciales inválidas. Por favor verifique su usuario y contraseña.");
            limpiarPassword();
            txtUsername.requestFocus();
        } catch (Exception e) {
            mostrarError("Error al conectar con el sistema: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void cancelarLogin() {
        usuarioAutenticado = null;
        dispose();
        System.exit(0);
    }
    
    private void mostrarError(String mensaje) {
        lblMensaje.setText(mensaje);
        lblMensaje.setForeground(Color.RED);
    }
    
    private void mostrarExito(String mensaje) {
        lblMensaje.setText(mensaje);
        lblMensaje.setForeground(new Color(40, 167, 69)); // Verde
    }
    
    private void limpiarPassword() {
        txtPassword.setText("");
    }
    
    public Usuario getUsuarioAutenticado() {
        return usuarioAutenticado;
    }
    
    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            txtUsername.setText("");
            txtPassword.setText("");
            lblMensaje.setText(" ");
            txtUsername.requestFocus();
        }
        super.setVisible(visible);
    }
}
