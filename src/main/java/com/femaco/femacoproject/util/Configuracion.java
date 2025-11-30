package com.femaco.femacoproject.util;

import java.io.*;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Configuracion {
    private static final Logger logger = Logger.getLogger(Configuracion.class.getName());
    private static final String CONFIG_FILE = "config.properties";
    private static Configuracion instance;
    private Properties properties;
    
    private Configuracion() {
        properties = new Properties();
        cargarConfiguracion();
    }
    
    public static synchronized Configuracion getInstance() {
        if (instance == null) {
            instance = new Configuracion();
        }
        return instance;
    }
    
    private void cargarConfiguracion() {
        try (InputStream input = new FileInputStream(CONFIG_FILE)) {
            properties.load(input);
            logger.info("Configuración cargada desde: " + CONFIG_FILE);
        } catch (FileNotFoundException e) {
            crearConfiguracionPorDefecto();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error al cargar configuración", e);
            crearConfiguracionPorDefecto();
        }
    }
    
    private void crearConfiguracionPorDefecto() {
        try (OutputStream output = new FileOutputStream(CONFIG_FILE)) {
            // Configuración por defecto
            properties.setProperty("database.url", "jdbc:sqlite:inventario_femaco.db");
            properties.setProperty("backup.path", "backups/");
            properties.setProperty("backup.auto", "true");
            properties.setProperty("backup.interval", "7"); // días
            properties.setProperty("stock.alerta.dias", "3");
            properties.setProperty("ui.theme", "system");
            properties.setProperty("reporte.max.items", "1000");
            properties.setProperty("session.timeout", "30"); // minutos
            properties.setProperty("password.expiry", "90"); // días
            
            properties.store(output, "Configuración FEMACO Inventario");
            logger.info("Configuración por defecto creada en: " + CONFIG_FILE);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error al crear configuración por defecto", e);
        }
    }
    
    public String getDatabaseUrl() {
        return properties.getProperty("database.url", "jdbc:sqlite:inventario_femaco.db");
    }
    
    public String getBackupPath() {
        return properties.getProperty("backup.path", "backups/");
    }
    
    public boolean isAutoBackupEnabled() {
        return Boolean.parseBoolean(properties.getProperty("backup.auto", "true"));
    }
    
    public int getBackupInterval() {
        return Integer.parseInt(properties.getProperty("backup.interval", "7"));
    }
    
    public int getStockAlertaDias() {
        return Integer.parseInt(properties.getProperty("stock.alerta.dias", "3"));
    }
    
    public String getUITheme() {
        return properties.getProperty("ui.theme", "system");
    }
    
    public int getReporteMaxItems() {
        return Integer.parseInt(properties.getProperty("reporte.max.items", "1000"));
    }
    
    public int getSessionTimeout() {
        return Integer.parseInt(properties.getProperty("session.timeout", "30"));
    }
    
    public int getPasswordExpiry() {
        return Integer.parseInt(properties.getProperty("password.expiry", "90"));
    }
    
    public void setProperty(String key, String value) {
        properties.setProperty(key, value);
        guardarConfiguracion();
    }
    
    private void guardarConfiguracion() {
        try (OutputStream output = new FileOutputStream(CONFIG_FILE)) {
            properties.store(output, "Configuración FEMACO Inventario - Actualizada");
            logger.info("Configuración guardada en: " + CONFIG_FILE);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error al guardar configuración", e);
        }
    }
}
