package com.femaco.femacoproject.dao;

import com.femaco.femacoproject.model.MovimientoInventario;
import com.femaco.femacoproject.model.enums.TipoMovimiento;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MovimientoDAOImpl implements MovimientoDAO {
    private static final Logger logger = Logger.getLogger(MovimientoDAOImpl.class.getName());
    private final DatabaseConnection dbConnection;

    public MovimientoDAOImpl() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    @Override
    public boolean registrar(MovimientoInventario movimiento) {
        String sql = "INSERT INTO MOVIMIENTOS (producto_id, tipo, cantidad, motivo, fecha, usuario_id, referencia) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, movimiento.getProductoId());
            pstmt.setString(2, movimiento.getTipo().name());
            pstmt.setInt(3, movimiento.getCantidad());
            pstmt.setString(4, movimiento.getMotivo());
            pstmt.setTimestamp(5, new Timestamp(movimiento.getFecha().getTime()));
            pstmt.setString(6, movimiento.getUsuarioId());
            pstmt.setString(7, movimiento.getReferencia());
            
            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al registrar movimiento", e);
            return false;
        }
    }

    @Override
    public Optional<MovimientoInventario> obtenerPorId(String id) {
        String sql = "SELECT * FROM MOVIMIENTOS WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapearMovimiento(rs));
            }
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al obtener movimiento: " + id, e);
        }
        
        return Optional.empty();
    }

    @Override
    public List<MovimientoInventario> obtenerPorProducto(String productoId) {
        List<MovimientoInventario> movimientos = new ArrayList<>();
        String sql = "SELECT * FROM MOVIMIENTOS WHERE producto_id = ? ORDER BY fecha DESC";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, productoId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                movimientos.add(mapearMovimiento(rs));
            }
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al obtener movimientos por producto: " + productoId, e);
        }
        
        return movimientos;
    }

    @Override
    public List<MovimientoInventario> obtenerPorFecha(Date fechaInicio, Date fechaFin) {
        List<MovimientoInventario> movimientos = new ArrayList<>();
        String sql = "SELECT * FROM MOVIMIENTOS WHERE fecha BETWEEN ? AND ? ORDER BY fecha DESC";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setTimestamp(1, new Timestamp(fechaInicio.getTime()));
            pstmt.setTimestamp(2, new Timestamp(fechaFin.getTime()));
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                movimientos.add(mapearMovimiento(rs));
            }
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al obtener movimientos por fecha", e);
        }
        
        return movimientos;
    }

    @Override
    public List<MovimientoInventario> obtenerPorTipo(TipoMovimiento tipo) {
        List<MovimientoInventario> movimientos = new ArrayList<>();
        String sql = "SELECT * FROM MOVIMIENTOS WHERE tipo = ? ORDER BY fecha DESC";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, tipo.name());
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                movimientos.add(mapearMovimiento(rs));
            }
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al obtener movimientos por tipo: " + tipo, e);
        }
        
        return movimientos;
    }

    @Override
    public List<MovimientoInventario> obtenerTodos() {
        List<MovimientoInventario> movimientos = new ArrayList<>();
        String sql = "SELECT * FROM MOVIMIENTOS ORDER BY fecha DESC";
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                movimientos.add(mapearMovimiento(rs));
            }
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al obtener todos los movimientos", e);
        }
        
        return movimientos;
    }

    @Override
    public List<MovimientoInventario> obtenerPorUsuario(String usuarioId) {
        List<MovimientoInventario> movimientos = new ArrayList<>();
        String sql = "SELECT * FROM MOVIMIENTOS WHERE usuario_id = ? ORDER BY fecha DESC";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, usuarioId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                movimientos.add(mapearMovimiento(rs));
            }
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al obtener movimientos por usuario: " + usuarioId, e);
        }
        
        return movimientos;
    }

    @Override
    public int obtenerTotalMovimientosPorProducto(String productoId, TipoMovimiento tipo) {
        String sql = "SELECT SUM(cantidad) as total FROM MOVIMIENTOS WHERE producto_id = ? AND tipo = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, productoId);
            pstmt.setString(2, tipo.name());
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("total");
            }
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al obtener total de movimientos", e);
        }
        
        return 0;
    }

    @Override
    public List<MovimientoInventario> obtenerUltimosMovimientos(int limite) {
        List<MovimientoInventario> movimientos = new ArrayList<>();
        String sql = "SELECT * FROM MOVIMIENTOS ORDER BY fecha DESC LIMIT ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, limite);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                movimientos.add(mapearMovimiento(rs));
            }
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al obtener últimos movimientos", e);
        }
        
        return movimientos;
    }

    private MovimientoInventario mapearMovimiento(ResultSet rs) throws SQLException {
        MovimientoInventario movimiento = new MovimientoInventario(
            rs.getString("producto_id"),
            TipoMovimiento.valueOf(rs.getString("tipo")),
            rs.getInt("cantidad"),
            rs.getString("motivo"),
            rs.getString("usuario_id"),
            rs.getString("referencia")
        );
        
        movimiento.setId(rs.getString("id"));
        movimiento.setFecha(new Date(rs.getTimestamp("fecha").getTime()));
        
        return movimiento;
    }
}
