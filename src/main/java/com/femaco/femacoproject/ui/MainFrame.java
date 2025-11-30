package com.femaco.femacoproject.ui;

import com.femaco.femacoproject.dao.MovimientoDAO;
import com.femaco.femacoproject.dao.MovimientoDAOImpl;
import com.femaco.femacoproject.dao.ProductoDAO;
import com.femaco.femacoproject.dao.ProductoDAOImpl;
import com.femaco.femacoproject.dao.ProveedorDAO;
import com.femaco.femacoproject.dao.ProveedorDAOImpl;
import com.femaco.femacoproject.dao.UsuarioDAO;
import com.femaco.femacoproject.dao.UsuarioDAOImpl;
import com.femaco.femacoproject.model.Usuario;
import com.femaco.femacoproject.service.AlertaService;
import com.femaco.femacoproject.service.AutenticacionService;
import com.femaco.femacoproject.service.GestionInventarioService;
import com.femaco.femacoproject.service.ReporteService;
import com.femaco.femacoproject.ui.components.CustomButton;
import com.femaco.femacoproject.ui.panels.DashboardPanel;
import com.femaco.femacoproject.ui.panels.MovimientoPanel;
import com.femaco.femacoproject.ui.panels.ProductoPanel;
import com.femaco.femacoproject.ui.panels.ProveedorPanel;
import com.femaco.femacoproject.ui.panels.ReportePanel;
import com.femaco.femacoproject.ui.panels.UsuarioPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainFrame extends JFrame {
    private Usuario usuarioActual;
    
    // Servicios
    private AutenticacionService autenticacionService;
    private GestionInventarioService gestionInventarioService;
    private ReporteService reporteService;
    private AlertaService alertaService;
    
    // DAOs
    private ProductoDAO productoDAO;
    private MovimientoDAO movimientoDAO;
    private ProveedorDAO proveedorDAO;
    private UsuarioDAO usuarioDAO;
    
    // Paneles
    private DashboardPanel dashboardPanel;
    private ProductoPanel productoPanel;
    private MovimientoPanel movimientoPanel;
    private ProveedorPanel proveedorPanel;
    private ReportePanel reportePanel;
    private UsuarioPanel usuarioPanel;
    
    // Componentes UI
    private JTabbedPane tabbedPane;
    private JLabel lblUsuarioInfo;
    private JLabel lblEstadoSistema;
    private CustomButton btnCerrarSesion;
    private Timer timerActualizacion;
    
    public MainFrame() {
        super("Sistema de Gestión de Inventario - FEMACO S.R.L.");
        inicializarServicios();
        initComponents();
        setupLayout();
        configurarEventos();
        mostrarLogin();
    }
    
    private void inicializarServicios() {
        try {
            // Inicializar DAOs
            productoDAO = new ProductoDAOImpl();
            movimientoDAO = new MovimientoDAOImpl();
            proveedorDAO = new ProveedorDAOImpl();
            usuarioDAO = new UsuarioDAOImpl();
            
            // Inicializar servicios
            autenticacionService = new com.femaco.femacoproject.service.AutenticacionServiceImpl(usuarioDAO);
            gestionInventarioService = new com.femaco.femacoproject.service.GestionInventarioServiceImpl(
                productoDAO, movimientoDAO, proveedorDAO);
            reporteService = new com.femaco.femacoproject.service.ReporteServiceImpl(productoDAO, movimientoDAO);
            alertaService = new com.femaco.femacoproject.service.AlertaServiceImpl(productoDAO, proveedorDAO);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al inicializar servicios: " + e.getMessage(),
                "Error de Inicialización", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }
    
    private void initComponents() {
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Barra de título personalizada
        JPanel titleBar = crearTitleBar();
        
        // Panel principal con pestañas
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        // Barra de estado
        JPanel statusBar = crearStatusBar();
        
        add(titleBar, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
        add(statusBar, BorderLayout.SOUTH);
    }
    
    private JPanel crearTitleBar() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(0, 102, 204)); // Azul FEMACO
        panel.setPreferredSize(new Dimension(800, 60));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        
        // Logo y título
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(new Color(0, 102, 204));
        
        JLabel lblTitle = new JLabel("FEMACO - Sistema de Inventario");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE);
        
        titlePanel.add(lblTitle);
        
        // Info usuario y controles
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userPanel.setBackground(new Color(0, 102, 204));
        
        lblUsuarioInfo = new JLabel("No autenticado");
        lblUsuarioInfo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblUsuarioInfo.setForeground(Color.WHITE);
        
        btnCerrarSesion = CustomButton.createDangerButton("Cerrar Sesión");
        btnCerrarSesion.setPreferredSize(new Dimension(120, 30));
        
        userPanel.add(lblUsuarioInfo);
        userPanel.add(Box.createHorizontalStrut(10));
        userPanel.add(btnCerrarSesion);
        
        panel.add(titlePanel, BorderLayout.WEST);
        panel.add(userPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel crearStatusBar() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, Color.GRAY),
            BorderFactory.createEmptyBorder(3, 5, 3, 5)
        ));
        panel.setBackground(new Color(240, 240, 240));
        
        lblEstadoSistema = new JLabel("Sistema listo");
        lblEstadoSistema.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        
        JLabel lblVersion = new JLabel("v1.0 - FEMACO S.R.L.");
        lblVersion.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblVersion.setForeground(Color.GRAY);
        
        panel.add(lblEstadoSistema, BorderLayout.WEST);
        panel.add(lblVersion, BorderLayout.EAST);
        
        return panel;
    }
    
    private void setupLayout() {
        setPreferredSize(new Dimension(1200, 800));
        setMinimumSize(new Dimension(1000, 600));
        pack();
        setLocationRelativeTo(null);
    }
    
    private void configurarEventos() {
        // Cierre de ventana
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                confirmarSalida();
            }
        });
        
        // Cerrar sesión
        btnCerrarSesion.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                confirmarCerrarSesion();
            }
        });
        
        // Timer para actualizaciones automáticas
        timerActualizacion = new Timer(30000, new ActionListener() { // 30 segundos
            @Override
            public void actionPerformed(ActionEvent e) {
                actualizarPaneles();
            }
        });
    }
    
    private void mostrarLogin() {
        LoginDialog loginDialog = new LoginDialog(this, autenticacionService);
        loginDialog.setVisible(true);
        
        usuarioActual = loginDialog.getUsuarioAutenticado();
        
        if (usuarioActual != null) {
            inicializarAplicacion();
        } else {
            System.exit(0);
        }
    }
    
    private void inicializarAplicacion() {
        // Actualizar información de usuario
        actualizarInfoUsuario();
        
        // Crear paneles según permisos
        crearPaneles();
        
        // Configurar visibilidad de pestañas según rol
        configurarVisibilidadPestanas();
        
        // Iniciar timer de actualizaciones
        timerActualizacion.start();
        
        // Mostrar mensaje de bienvenida
        mostrarBienvenida();
        
        setVisible(true);
    }
    
    private void actualizarInfoUsuario() {
        String infoUsuario = String.format("%s (%s) - %s", 
            usuarioActual.getNombreCompleto(),
            usuarioActual.getUsername(),
            usuarioActual.getRol().getDescripcion());
        lblUsuarioInfo.setText(infoUsuario);
    }
    
    private void crearPaneles() {
        // Dashboard (siempre visible)
        dashboardPanel = new DashboardPanel(gestionInventarioService, reporteService, alertaService);
        tabbedPane.addTab("Dashboard", createIcon("📊"), dashboardPanel, "Vista general del sistema");
        
        // Productos (disponible para todos los roles excepto si están restringidos)
        if (autenticacionService.puedeGestionarProductos(usuarioActual.getId())) {
            productoPanel = new ProductoPanel(gestionInventarioService);
            tabbedPane.addTab("Productos", createIcon("📦"), productoPanel, "Gestión de productos");
        }
        
        // Movimientos (disponible para todos los roles)
        movimientoPanel = new MovimientoPanel(gestionInventarioService, usuarioActual.getId());
        tabbedPane.addTab("Movimientos", createIcon("🔄"), movimientoPanel, "Registro de movimientos");
        
        // Proveedores
        if (autenticacionService.puedeGestionarProductos(usuarioActual.getId())) {
            proveedorPanel = new ProveedorPanel(gestionInventarioService);
            tabbedPane.addTab("Proveedores", createIcon("🏢"), proveedorPanel, "Gestión de proveedores");
        }
        
        // Reportes
        if (autenticacionService.puedeGenerarReportes(usuarioActual.getId())) {
            reportePanel = new ReportePanel(reporteService, gestionInventarioService);
            tabbedPane.addTab("Reportes", createIcon("📈"), reportePanel, "Generación de reportes");
        }
        
        // Usuarios (solo administradores)
        if (autenticacionService.puedeGestionarUsuarios(usuarioActual.getId())) {
            usuarioPanel = new UsuarioPanel(autenticacionService, usuarioActual.getId());
            tabbedPane.addTab("Usuarios", createIcon("👥"), usuarioPanel, "Gestión de usuarios");
        }
    }
    
    private void configurarVisibilidadPestanas() {
        // Por defecto, mostrar todas las pestañas creadas
        // Puedes agregar lógica adicional aquí para restringir más según necesidades específicas
    }
    
    private Icon createIcon(String emoji) {
        // Icono simple usando emoji - en producción usar imágenes reales
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                g.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
                g.drawString(emoji, x, y + 15);
            }
            
            @Override
            public int getIconWidth() { return 20; }
            
            @Override
            public int getIconHeight() { return 20; }
        };
    }
    
    private void actualizarPaneles() {
        try {
            if (dashboardPanel != null) {
                dashboardPanel.refrescar();
            }
            
            // Actualizar estado del sistema
            actualizarEstadoSistema();
            
        } catch (Exception e) {
            lblEstadoSistema.setText("Error en actualización automática: " + e.getMessage());
        }
    }
    
    private void actualizarEstadoSistema() {
        try {
            int totalProductos = gestionInventarioService.obtenerTotalProductos();
            int productosStockBajo = gestionInventarioService.obtenerProductosStockBajo().size();
            int alertasActivas = alertaService.obtenerAlertasActivas().size();
            
            String estado = String.format("Productos: %d | Stock bajo: %d | Alertas: %d | Conectado", 
                totalProductos, productosStockBajo, alertasActivas);
            lblEstadoSistema.setText(estado);
            
        } catch (Exception e) {
            lblEstadoSistema.setText("Error al obtener estado del sistema");
        }
    }
    
    private void mostrarBienvenida() {
        String mensaje = String.format(
            "¡Bienvenido %s!\n\n" +
            "Sistema de Gestión de Inventario FEMACO\n" +
            "Rol: %s\n" +
            "Hora de acceso: %s",
            usuarioActual.getNombreCompleto(),
            usuarioActual.getRol().getDescripcion(),
            new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(new java.util.Date())
        );
        
        JOptionPane.showMessageDialog(this, mensaje, "Bienvenido", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void confirmarSalida() {
        int opcion = JOptionPane.showConfirmDialog(this,
            "¿Está seguro de que desea salir del sistema?",
            "Confirmar Salida",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (opcion == JOptionPane.YES_OPTION) {
            salirSistema();
        }
    }
    
    private void confirmarCerrarSesion() {
        int opcion = JOptionPane.showConfirmDialog(this,
            "¿Está seguro de que desea cerrar sesión?",
            "Confirmar Cierre de Sesión",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (opcion == JOptionPane.YES_OPTION) {
            cerrarSesion();
        }
    }
    
    private void cerrarSesion() {
        try {
            // Detener timer
            if (timerActualizacion != null) {
                timerActualizacion.stop();
            }
            
            // Cerrar sesión en el servicio
            autenticacionService.cerrarSesion(usuarioActual.getId());
            
            // Limpiar paneles
            tabbedPane.removeAll();
            dashboardPanel = null;
            productoPanel = null;
            movimientoPanel = null;
            proveedorPanel = null;
            reportePanel = null;
            usuarioPanel = null;
            
            // Mostrar login nuevamente
            usuarioActual = null;
            lblUsuarioInfo.setText("No autenticado");
            lblEstadoSistema.setText("Sistema listo");
            
            mostrarLogin();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al cerrar sesión: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void salirSistema() {
        try {
            // Detener timer
            if (timerActualizacion != null) {
                timerActualizacion.stop();
            }
            
            // Cerrar sesión
            if (usuarioActual != null) {
                autenticacionService.cerrarSesion(usuarioActual.getId());
            }
            
            // Salir
            dispose();
            System.exit(0);
            
        } catch (Exception e) {
            dispose();
            System.exit(0);
        }
    }
    
    // Métodos públicos para acceso desde otros componentes
    public Usuario getUsuarioActual() {
        return usuarioActual;
    }
    
    public AutenticacionService getAutenticacionService() {
        return autenticacionService;
    }
    
    public GestionInventarioService getGestionInventarioService() {
        return gestionInventarioService;
    }
    
    public void mostrarPanel(String nombrePanel) {
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            if (tabbedPane.getTitleAt(i).equals(nombrePanel)) {
                tabbedPane.setSelectedIndex(i);
                break;
            }
        }
    }
    
    public void actualizarInterfaz() {
        actualizarPaneles();
    }
}
