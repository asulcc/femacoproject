package com.femaco.femacoproject;

import com.femaco.femacoproject.dao.DatabaseConnection;
import com.femaco.femacoproject.ui.MainFrame;
import com.femaco.femacoproject.util.Configuracion;
import com.femaco.femacoproject.util.Logger;

import javax.swing.*;
import java.awt.*;

/**
 * Clase principal del Sistema de Gestión de Inventario para FEMACO S.R.L.
 * Punto de entrada de la aplicación.
 * 
 * @author GRUPO 4
 * @version 1.0
 * @since 2025
 */
public class Main {
    private static final Logger logger = Logger.getInstance();
    private static final Configuracion configuracion = Configuracion.getInstance();
    
    /**
     * Método principal - Punto de entrada de la aplicación
     * @param args Argumentos de línea de comandos
     */
    public static void main(String[] args) {
        // Configurar el Look and Feel del sistema
        configurarLookAndFeel();
        
        // Mostrar información de inicio
        mostrarInformacionInicio();
        
        // Inicializar base de datos
        inicializarBaseDatos();
        
        // Ejecutar la aplicación en el Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            try {
                crearYMostrarGUI();
            } catch (Exception e) {
                manejarErrorFatal("Error al crear la interfaz gráfica", e);
            }
        });
    }
    
    /**
     * Configura el Look and Feel de la aplicación para que coincida con el sistema operativo
     */
    private static void configurarLookAndFeel() {
        try {
            // Usar el Look and Feel del sistema
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            // Configuraciones específicas para mejorar la apariencia
            configurarUI();
            
            logger.info("Look and Feel configurado correctamente");
        } catch (Exception e) {
            logger.warning("No se pudo configurar el Look and Feel del sistema: " + e.getMessage());
            // Continuar con el Look and Feel por defecto
        }
    }
    
    /**
     * Configuraciones específicas de la interfaz de usuario
     */
    private static void configurarUI() {
        try {
            // Configurar fuentes y colores por defecto
            UIManager.put("Button.font", new Font("Segoe UI", Font.PLAIN, 12));
            UIManager.put("Label.font", new Font("Segoe UI", Font.PLAIN, 12));
            UIManager.put("TextField.font", new Font("Segoe UI", Font.PLAIN, 12));
            UIManager.put("ComboBox.font", new Font("Segoe UI", Font.PLAIN, 12));
            UIManager.put("Table.font", new Font("Segoe UI", Font.PLAIN, 11));
            UIManager.put("TableHeader.font", new Font("Segoe UI", Font.BOLD, 11));
            UIManager.put("TabbedPane.font", new Font("Segoe UI", Font.PLAIN, 12));
            
            // Configurar colores corporativos FEMACO
            UIManager.put("nimbusBase", new Color(0, 102, 204)); // Azul FEMACO
            UIManager.put("nimbusBlueGrey", new Color(0, 102, 204));
            UIManager.put("control", new Color(240, 240, 240));
            
        } catch (Exception e) {
            logger.warning("No se pudieron aplicar todas las configuraciones UI: " + e.getMessage());
        }
    }
    
    /**
     * Muestra información de inicio de la aplicación
     */
    private static void mostrarInformacionInicio() {
        String mensajeInicio = 
            "╔══════════════════════════════════════════════════════════════╗\n" +
            "║               SISTEMA DE GESTIÓN DE INVENTARIO              ║\n" +
            "║                     FEMACO S.R.L.                           ║\n" +
            "║                                                              ║\n" +
            "║  Versión: 1.0                                               ║\n" +
            "║  Desarrollado para: Ferretería FEMACO                       ║\n" +
            "║  Tecnologías: Java SE 17+, SQLite, Swing                    ║\n" +
            "║  Fecha: 2024                                                ║\n" +
            "║                                                              ║\n" +
            "║  Características:                                            ║\n" +
            "║  • Gestión de productos de alta rotación                    ║\n" +
            "║  • Control de stock en tiempo real                          ║\n" +
            "║  • Alertas automáticas de stock mínimo                      ║\n" +
            "║  • Reportes y estadísticas                                  ║\n" +
            "║  • Múltiples roles de usuario                               ║\n" +
            "║  • Backup automático de datos                               ║\n" +
            "╚══════════════════════════════════════════════════════════════╝";
        
        System.out.println(mensajeInicio);
        logger.info("Iniciando Sistema de Gestión de Inventario FEMACO v1.0");
    }
    
    /**
     * Inicializa la base de datos y realiza verificaciones
     */
    private static void inicializarBaseDatos() {
        try {
            logger.info("Inicializando base de datos...");
            
            // Obtener instancia de la conexión (esto inicializa la BD automáticamente)
            DatabaseConnection dbConnection = DatabaseConnection.getInstance();
            
            // Verificar que la base de datos esté accesible
            dbConnection.getConnection().close();
            
            logger.info("Base de datos inicializada correctamente");
            
        } catch (Exception e) {
            manejarErrorFatal("Error crítico al inicializar la base de datos", e);
        }
    }
    
    /**
     * Crea y muestra la interfaz gráfica principal
     */
    private static void crearYMostrarGUI() {
        try {
            logger.info("Creando interfaz gráfica principal...");
            
            // Crear la ventana principal
            MainFrame mainFrame = new MainFrame();
            
            // Configurar cierre seguro
            configurarCierreSeguro(mainFrame);
            
            logger.info("Interfaz gráfica creada correctamente");
            
        } catch (Exception e) {
            manejarErrorFatal("Error al crear la interfaz gráfica principal", e);
        }
    }
    
    /**
     * Configura el cierre seguro de la aplicación
     */
    private static void configurarCierreSeguro(MainFrame mainFrame) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Cerrando aplicación...");
            realizarLimpieza();
        }));
    }
    
    /**
     * Realiza tareas de limpieza antes de cerrar la aplicación
     */
    private static void realizarLimpieza() {
        try {
            logger.info("Realizando tareas de limpieza...");
            
            // Cerrar logger
            logger.close();
            
            // Realizar backup si está configurado
            if (configuracion.isAutoBackupEnabled()) {
                realizarBackupAutomatico();
            }
            
            logger.info("Aplicación cerrada correctamente");
            
        } catch (Exception e) {
            System.err.println("Error durante la limpieza: " + e.getMessage());
        }
    }
    
    /**
     * Realiza backup automático de la base de datos
     */
    private static void realizarBackupAutomatico() {
        try {
            DatabaseConnection dbConnection = DatabaseConnection.getInstance();
            String backupPath = configuracion.getBackupPath() + 
                              "backup_" + 
                              System.currentTimeMillis() + 
                              ".db";
            
            dbConnection.backupDatabase(backupPath);
            logger.info("Backup automático creado: " + backupPath);
            
        } catch (Exception e) {
            logger.error("Error al crear backup automático", e);
        }
    }
    
    /**
     * Maneja errores fatales mostrando un mensaje al usuario y registrando el error
     */
    private static void manejarErrorFatal(String mensaje, Exception e) {
        // Registrar el error
        logger.error(mensaje, e);
        
        // Mostrar mensaje de error al usuario
        mostrarErrorDialog(mensaje, e.getMessage());
        
        // Salir de la aplicación
        System.exit(1);
    }
    
    /**
     * Muestra un diálogo de error al usuario
     */
    private static void mostrarErrorDialog(String titulo, String mensaje) {
        try {
            // Asegurarse de que se ejecute en el EDT
            if (SwingUtilities.isEventDispatchThread()) {
                mostrarDialogoError(titulo, mensaje);
            } else {
                SwingUtilities.invokeAndWait(() -> mostrarDialogoError(titulo, mensaje));
            }
        } catch (Exception ex) {
            // Fallback: mostrar en consola
            System.err.println(titulo + ": " + mensaje);
        }
    }
    
    /**
     * Muestra el diálogo de error (debe ejecutarse en el EDT)
     */
    private static void mostrarDialogoError(String titulo, String mensaje) {
        JOptionPane.showMessageDialog(
            null,
            "<html><body style='width: 300px;'>" +
            "<b>" + titulo + "</b><br><br>" +
            mensaje + "<br><br>" +
            "La aplicación se cerrará." +
            "</body></html>",
            "Error Fatal - FEMACO Inventario",
            JOptionPane.ERROR_MESSAGE
        );
    }
    
    /**
     * Método para verificar el estado del sistema
     * @return true si el sistema está listo para funcionar
     */
    public static boolean verificarSistema() {
        try {
            // Verificar versión de Java
            String javaVersion = System.getProperty("java.version");
            if (javaVersion.compareTo("17") < 0) {
                logger.warning("Versión de Java no soportada: " + javaVersion + ". Se requiere Java 17 o superior.");
                return false;
            }
            
            // Verificar permisos de escritura
            if (!verificarPermisosEscritura()) {
                logger.error("No hay permisos de escritura en el directorio actual");
                return false;
            }
            
            logger.info("Sistema verificado correctamente. Java version: " + javaVersion);
            return true;
            
        } catch (Exception e) {
            logger.error("Error al verificar el sistema", e);
            return false;
        }
    }
    
    /**
     * Verifica permisos de escritura en el directorio actual
     */
    private static boolean verificarPermisosEscritura() {
        try {
            java.nio.file.Path tempFile = java.nio.file.Files.createTempFile("femaco_test", ".tmp");
            java.nio.file.Files.delete(tempFile);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Método para obtener información del sistema
     */
    public static String obtenerInfoSistema() {
        return String.format(
            "Sistema de Gestión de Inventario FEMACO v1.0\n" +
            "Java: %s (%s)\n" +
            "OS: %s %s\n" +
            "Arch: %s\n" +
            "Usuario: %s\n" +
            "Directorio: %s",
            System.getProperty("java.version"),
            System.getProperty("java.vendor"),
            System.getProperty("os.name"),
            System.getProperty("os.version"),
            System.getProperty("os.arch"),
            System.getProperty("user.name"),
            System.getProperty("user.dir")
        );
    }
}