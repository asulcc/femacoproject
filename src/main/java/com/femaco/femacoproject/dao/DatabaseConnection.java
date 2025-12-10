package com.femaco.femacoproject.dao;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseConnection {
    private static final Logger logger = Logger.getLogger(DatabaseConnection.class.getName());
    private static DatabaseConnection instance;
    private Connection connection;
    private static final String DATABASE_URL = "jdbc:sqlite:inventario_femaco.db";
    
    private static final String INSERT_DEFAULT_DATA = """
            -- Insertar usuario administrador por defecto (password: admin123)
            INSERT OR IGNORE INTO USUARIOS (id, username, password, nombre_completo, email, rol) 
            VALUES ('ADM001', 'admin', '$2a$10$8K1p/a0dRTlM8bq6K1z8uO5VZ9JQY8VJZ8JQY8VJZ8JQY8VJZ8JQY8V', 
                   'Administrador Principal', 'admin@femaco.com', 'ADMINISTRADOR');
            
            -- Insertar usuario almacenero por defecto (password: almacen123)
            INSERT OR IGNORE INTO USUARIOS (id, username, password, nombre_completo, email, rol) 
            VALUES ('ALM001', 'almacenero', '$2a$10$8K1p/a0dRTlM8bq6K1z8uO5VZ9JQY8VJZ8JQY8VJZ8JQY8VJZ8JQY8', 
                   'Almacenero Principal', 'almacen@femaco.com', 'ALMACENERO');
            
            -- Insertar proveedores de ejemplo
            INSERT OR IGNORE INTO PROVEEDORES (id, nombre, contacto, telefono, email) 
            VALUES 
            ('PROV001', 'Ferretería Industrial SAC', 'Juan Pérez', '987654321', 'ventas@ferreteriaindustrial.com'),
            ('PROV002', 'Herramientas Premium EIRL', 'María García', '987654322', 'contacto@herramientaspremium.com'),
            ('PROV003', 'Materiales Construcción SA', 'Carlos López', '987654323', 'info@materialesconstruccion.com');
            """;

    private DatabaseConnection() {
        // Constructor privado para patrón Singleton
    }

    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                connection = DriverManager.getConnection(DATABASE_URL);
                // Activar claves foráneas en SQLite
                try (Statement stmt = connection.createStatement()) {
                    stmt.execute("PRAGMA foreign_keys = ON");
                }
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Error al conectar con la base de datos", e);
                throw e;
            }
        }
        return connection;
    }

    public void inicializarBaseDatos() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Insertar datos por defecto
            stmt.executeUpdate(INSERT_DEFAULT_DATA);
            
            logger.info("Base de datos inicializada correctamente");
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al inicializar la base de datos", e);
        }
    }

    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Error al cerrar la conexión", e);
            }
        }
    }

    public void backupDatabase(String backupPath) {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            stmt.executeUpdate("BACKUP TO '" + backupPath + "'");
            logger.info("Backup creado en: " + backupPath);
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al crear backup", e);
        }
    }
}
