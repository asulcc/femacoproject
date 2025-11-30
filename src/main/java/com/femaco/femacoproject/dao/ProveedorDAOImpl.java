package com.femaco.femacoproject.dao;

import com.femaco.femacoproject.model.Proveedor;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProveedorDAOImpl implements ProveedorDAO {
    private static final Logger logger = Logger.getLogger(ProveedorDAOImpl.class.getName());
    private final DatabaseConnection dbConnection;

    public ProveedorDAOImpl() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    @Override
    public boolean agregar(Proveedor proveedor) {
        String sql = "INSERT INTO PROVEEDORES (id, nombre, contacto, telefono, email, direccion, activo) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, proveedor.getId());
            pstmt.setString(2, proveedor.getNombre());
            pstmt.setString(3, proveedor.getContacto());
            pstmt.setString(4, proveedor.getTelefono());
            pstmt.setString(5, proveedor.getEmail());
            pstmt.setString(6, proveedor.getDireccion());
            pstmt.setBoolean(7, proveedor.isActivo());
            
            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al agregar proveedor: " + proveedor.getId(), e);
            return false;
        }
    }

    @Override
    public boolean actualizar(Proveedor proveedor) {
        String sql = "UPDATE PROVEEDORES SET nombre = ?, contacto = ?, telefono = ?, " +
                    "email = ?, direccion = ?, activo = ? WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, proveedor.getNombre());
            pstmt.setString(2, proveedor.getContacto());
            pstmt.setString(3, proveedor.getTelefono());
            pstmt.setString(4, proveedor.getEmail());
            pstmt.setString(5, proveedor.getDireccion());
            pstmt.setBoolean(6, proveedor.isActivo());
            pstmt.setString(7, proveedor.getId());
            
            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al actualizar proveedor: " + proveedor.getId(), e);
            return false;
        }
    }

    @Override
    public boolean eliminar(String id) {
        String sql = "DELETE FROM PROVEEDORES WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, id);
            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al eliminar proveedor: " + id, e);
            return false;
        }
    }

    @Override
    public Optional<Proveedor> obtenerPorId(String id) {
        String sql = "SELECT * FROM PROVEEDORES WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapearProveedor(rs));
            }
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al obtener proveedor: " + id, e);
        }
        
        return Optional.empty();
    }

    @Override
    public List<Proveedor> obtenerTodos() {
        List<Proveedor> proveedores = new ArrayList<>();
        String sql = "SELECT * FROM PROVEEDORES ORDER BY nombre";
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                proveedores.add(mapearProveedor(rs));
            }
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al obtener todos los proveedores", e);
        }
        
        return proveedores;
    }

    @Override
    public List<Proveedor> obtenerActivos() {
        List<Proveedor> proveedores = new ArrayList<>();
        String sql = "SELECT * FROM PROVEEDORES WHERE activo = TRUE ORDER BY nombre";
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                proveedores.add(mapearProveedor(rs));
            }
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al obtener proveedores activos", e);
        }
        
        return proveedores;
    }

    @Override
    public List<Proveedor> buscarPorNombre(String nombre) {
        List<Proveedor> proveedores = new ArrayList<>();
        String sql = "SELECT * FROM PROVEEDORES WHERE nombre LIKE ? ORDER BY nombre";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + nombre + "%");
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                proveedores.add(mapearProveedor(rs));
            }
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al buscar proveedores por nombre: " + nombre, e);
        }
        
        return proveedores;
    }

    @Override
    public boolean desactivarProveedor(String id) {
        String sql = "UPDATE PROVEEDORES SET activo = FALSE WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, id);
            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al desactivar proveedor: " + id, e);
            return false;
        }
    }

    @Override
    public boolean existeProveedor(String id) {
        String sql = "SELECT 1 FROM PROVEEDORES WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al verificar existencia de proveedor: " + id, e);
            return false;
        }
    }

    private Proveedor mapearProveedor(ResultSet rs) throws SQLException {
        Proveedor proveedor = new Proveedor(
            rs.getString("id"),
            rs.getString("nombre"),
            rs.getString("contacto"),
            rs.getString("telefono")
        );
        
        proveedor.setEmail(rs.getString("email"));
        proveedor.setDireccion(rs.getString("direccion"));
        proveedor.setActivo(rs.getBoolean("activo"));
        
        Timestamp fechaRegistro = rs.getTimestamp("fecha_registro");
        if (fechaRegistro != null) {
            proveedor.setFechaRegistro(new Date(fechaRegistro.getTime()));
        }
        
        return proveedor;
    }
}
