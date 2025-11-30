package com.femaco.femacoproject.util;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
    private static final String LOG_FILE = "logs/inventario.log";
    private static final SimpleDateFormat DATE_FORMAT = 
        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static Logger instance;
    private PrintWriter writer;
    private boolean debugMode;
    
    private Logger() {
        this.debugMode = false;
        inicializarLogger();
    }
    
    public static synchronized Logger getInstance() {
        if (instance == null) {
            instance = new Logger();
        }
        return instance;
    }
    
    private void inicializarLogger() {
        try {
            // Crear directorio de logs si no existe
            File logDir = new File("logs");
            if (!logDir.exists()) {
                logDir.mkdirs();
            }
            
            writer = new PrintWriter(new FileWriter(LOG_FILE, true), true);
            info("Logger inicializado - Sistema FEMACO Inventario");
        } catch (IOException e) {
            System.err.println("Error al inicializar logger: " + e.getMessage());
        }
    }
    
    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }
    
    public void info(String mensaje) {
        log("INFO", mensaje);
    }
    
    public void warning(String mensaje) {
        log("WARN", mensaje);
    }
    
    public void error(String mensaje) {
        log("ERROR", mensaje);
    }
    
    public void error(String mensaje, Exception e) {
        log("ERROR", mensaje + " - " + e.getMessage());
        if (debugMode) {
            e.printStackTrace(writer);
        }
    }
    
    public void debug(String mensaje) {
        if (debugMode) {
            log("DEBUG", mensaje);
        }
    }
    
    public void logOperacion(String usuario, String operacion, String entidad) {
        String mensaje = String.format("Usuario: %s - Operación: %s - Entidad: %s", 
                                     usuario, operacion, entidad);
        info(mensaje);
    }
    
    public void logMovimiento(String usuario, String producto, String tipo, int cantidad) {
        String mensaje = String.format("MOVIMIENTO - Usuario: %s - Producto: %s - Tipo: %s - Cantidad: %d", 
                                     usuario, producto, tipo, cantidad);
        info(mensaje);
    }
    
    private void log(String nivel, String mensaje) {
        String timestamp = DATE_FORMAT.format(new Date());
        String logEntry = String.format("[%s] %s - %s", timestamp, nivel, mensaje);
        
        if (writer != null) {
            writer.println(logEntry);
        }
        
        // También imprimir en consola en modo debug
        if (debugMode || nivel.equals("ERROR")) {
            System.out.println(logEntry);
        }
    }
    
    public void close() {
        if (writer != null) {
            info("Logger cerrado");
            writer.close();
        }
    }
    
    public String getLogFilePath() {
        return LOG_FILE;
    }
}
