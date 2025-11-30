package com.femaco.femacoproject.dao;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseConnection {
    private static final Logger logger = Logger.getLogger(DatabaseConnection.class.getName());
    private static DatabaseConnection instance;
    private Connection connection;
    private static final String DATABASE_URL = "jdbc:sqlite:inventario_femaco.db";
    private static final String CREATE_TABLES_SQL = """
            -- Tabla de productos
            CREATE TABLE IF NOT EXISTS PRODUCTOS (
                id VARCHAR(20) PRIMARY KEY,
                nombre VARCHAR(100) NOT NULL,
                categoria VARCHAR(50) NOT NULL,
                stock_actual INTEGER NOT NULL CHECK (stock_actual >= 0),
                stock_minimo INTEGER NOT NULL CHECK (stock_minimo >= 0),
                precio DECIMAL(10,2) NOT NULL CHECK (precio >= 0),
                ubicacion VARCHAR(20) NOT NULL,
                estado VARCHAR(20) DEFAULT 'ACTIVO',
                proveedor_id VARCHAR(20),
                fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP,
                fecha_actualizacion DATETIME DEFAULT CURRENT_TIMESTAMP
            );
            
            -- Tabla de movimientos
            CREATE TABLE IF NOT EXISTS MOVIMIENTOS (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                producto_id VARCHAR(20) NOT NULL,
                tipo VARCHAR(10) NOT NULL CHECK (tipo IN ('ENTRADA', 'SALIDA', 'AJUSTE')),
                cantidad INTEGER NOT NULL CHECK (cantidad > 0),
                motivo TEXT,
                fecha DATETIME DEFAULT CURRENT_TIMESTAMP,
                usuario_id VARCHAR(20) NOT NULL,
                referencia VARCHAR(50),
                FOREIGN KEY (producto_id) REFERENCES PRODUCTOS(id) ON DELETE CASCADE
            );
            
            -- Tabla de proveedores
            CREATE TABLE IF NOT EXISTS PROVEEDORES (
                id VARCHAR(20) PRIMARY KEY,
                nombre VARCHAR(100) NOT NULL,
                contacto VARCHAR(100),
                telefono VARCHAR(15),
                email VARCHAR(100),
                direccion TEXT,
                activo BOOLEAN DEFAULT TRUE,
                fecha_registro DATETIME DEFAULT CURRENT_TIMESTAMP
            );
            
            -- Tabla de usuarios
            CREATE TABLE IF NOT EXISTS USUARIOS (
                id VARCHAR(20) PRIMARY KEY,
                username VARCHAR(50) UNIQUE NOT NULL,
                password VARCHAR(100) NOT NULL,
                nombre_completo VARCHAR(100) NOT NULL,
                email VARCHAR(100),
                rol VARCHAR(15) NOT NULL CHECK (rol IN ('ADMINISTRADOR', 'ALMACENERO', 'SUPERVISOR')),
                activo BOOLEAN DEFAULT TRUE,
                fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP,
                ultimo_acceso DATETIME
            );
            
            -- Índices para optimización
            CREATE INDEX IF NOT EXISTS idx_productos_categoria ON PRODUCTOS(categoria);
            CREATE INDEX IF NOT EXISTS idx_productos_estado ON PRODUCTOS(estado);
            CREATE INDEX IF NOT EXISTS idx_productos_stock ON PRODUCTOS(stock_actual);
            CREATE INDEX IF NOT EXISTS idx_movimientos_producto_id ON MOVIMIENTOS(producto_id);
            CREATE INDEX IF NOT EXISTS idx_movimientos_fecha ON MOVIMIENTOS(fecha);
            CREATE INDEX IF NOT EXISTS idx_movimientos_tipo ON MOVIMIENTOS(tipo);
            CREATE INDEX IF NOT EXISTS idx_usuarios_username ON USUARIOS(username);
            """;
    
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
            
            // Crear tablas
            stmt.executeUpdate(CREATE_TABLES_SQL);
            
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
