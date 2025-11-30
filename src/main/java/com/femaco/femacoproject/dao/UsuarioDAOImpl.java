package com.femaco.femacoproject.dao;

import com.femaco.femacoproject.model.Usuario;
import com.femaco.femacoproject.model.enums.RolUsuario;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UsuarioDAOImpl implements UsuarioDAO {
    private static final Logger logger = Logger.getLogger(UsuarioDAOImpl.class.getName());
    private final DatabaseConnection dbConnection;

    public UsuarioDAOImpl() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    @Override
    public boolean agregar(Usuario usuario) {
        String sql = "INSERT INTO USUARIOS (id, username, password, nombre_completo, email, rol, activo) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, usuario.getId());
            pstmt.setString(2, usuario.getUsername());
            pstmt.setString(3, usuario.getPassword());
            pstmt.setString(4, usuario.getNombreCompleto());
            pstmt.setString(5, usuario.getEmail());
            pstmt.setString(6, usuario.getRol().name());
            pstmt.setBoolean(7, usuario.isActivo());
            
            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al agregar usuario: " + usuario.getUsername(), e);
            return false;
        }
    }

    @Override
    public boolean actualizar(Usuario usuario) {
        String sql = "UPDATE USUARIOS SET username = ?, nombre_completo = ?, email = ?, " +
                    "rol = ?, activo = ? WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, usuario.getUsername());
            pstmt.setString(2, usuario.getNombreCompleto());
            pstmt.setString(3, usuario.getEmail());
            pstmt.setString(4, usuario.getRol().name());
            pstmt.setBoolean(5, usuario.isActivo());
            pstmt.setString(6, usuario.getId());
            
            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al actualizar usuario: " + usuario.getId(), e);
            return false;
        }
    }

    @Override
    public boolean eliminar(String id) {
        String sql = "DELETE FROM USUARIOS WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, id);
            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al eliminar usuario: " + id, e);
            return false;
        }
    }

    @Override
    public Optional<Usuario> obtenerPorId(String id) {
        String sql = "SELECT * FROM USUARIOS WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapearUsuario(rs));
            }
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al obtener usuario: " + id, e);
        }
        
        return Optional.empty();
    }

    @Override
    public Optional<Usuario> obtenerPorUsername(String username) {
        String sql = "SELECT * FROM USUARIOS WHERE username = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapearUsuario(rs));
            }
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al obtener usuario por username: " + username, e);
        }
        
        return Optional.empty();
    }

    @Override
    public List<Usuario> obtenerTodos() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM USUARIOS ORDER BY nombre_completo";
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                usuarios.add(mapearUsuario(rs));
            }
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al obtener todos los usuarios", e);
        }
        
        return usuarios;
    }

    @Override
    public List<Usuario> obtenerPorRol(RolUsuario rol) {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM USUARIOS WHERE rol = ? ORDER BY nombre_completo";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, rol.name());
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                usuarios.add(mapearUsuario(rs));
            }
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al obtener usuarios por rol: " + rol, e);
        }
        
        return usuarios;
    }

    @Override
    public List<Usuario> obtenerActivos() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM USUARIOS WHERE activo = TRUE ORDER BY nombre_completo";
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                usuarios.add(mapearUsuario(rs));
            }
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al obtener usuarios activos", e);
        }
        
        return usuarios;
    }

    @Override
    public boolean actualizarUltimoAcceso(String usuarioId) {
        String sql = "UPDATE USUARIOS SET ultimo_acceso = ? WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setTimestamp(1, new Timestamp(new Date().getTime()));
            pstmt.setString(2, usuarioId);
            
            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al actualizar último acceso: " + usuarioId, e);
            return false;
        }
    }

    @Override
    public boolean cambiarPassword(String usuarioId, String nuevoPasswordHash) {
        String sql = "UPDATE USUARIOS SET password = ? WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, nuevoPasswordHash);
            pstmt.setString(2, usuarioId);
            
            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al cambiar password: " + usuarioId, e);
            return false;
        }
    }

    @Override
    public boolean desactivarUsuario(String usuarioId) {
        String sql = "UPDATE USUARIOS SET activo = FALSE WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, usuarioId);
            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al desactivar usuario: " + usuarioId, e);
            return false;
        }
    }

    @Override
    public boolean existeUsername(String username) {
        String sql = "SELECT 1 FROM USUARIOS WHERE username = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al verificar existencia de username: " + username, e);
            return false;
        }
    }

    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario(
            rs.getString("id"),
            rs.getString("username"),
            rs.getString("password"),
            rs.getString("nombre_completo"),
            rs.getString("email"),
            RolUsuario.valueOf(rs.getString("rol"))
        );
        
        usuario.setActivo(rs.getBoolean("activo"));
        
        Timestamp fechaCreacion = rs.getTimestamp("fecha_creacion");
        if (fechaCreacion != null) {
            usuario.setFechaCreacion(new Date(fechaCreacion.getTime()));
        }
        
        Timestamp ultimoAcceso = rs.getTimestamp("ultimo_acceso");
        if (ultimoAcceso != null) {
            usuario.setUltimoAcceso(new Date(ultimoAcceso.getTime()));
        }
        
        return usuario;
    }
}
