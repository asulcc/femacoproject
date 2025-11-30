package com.femaco.femacoproject.dao;

import com.femaco.femacoproject.model.Producto;
import com.femaco.femacoproject.model.enums.CategoriaProducto;
import com.femaco.femacoproject.model.enums.EstadoProducto;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProductoDAOImpl implements ProductoDAO {
    private static final Logger logger = Logger.getLogger(ProductoDAOImpl.class.getName());
    private final DatabaseConnection dbConnection;

    public ProductoDAOImpl() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    @Override
    public boolean agregar(Producto producto) {
        String sql = "INSERT INTO PRODUCTOS (id, nombre, categoria, stock_actual, stock_minimo, " +
                    "precio, ubicacion, estado, proveedor_id, fecha_creacion, fecha_actualizacion) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, producto.getId());
            pstmt.setString(2, producto.getNombre());
            pstmt.setString(3, producto.getCategoria().name());
            pstmt.setInt(4, producto.getStockActual());
            pstmt.setInt(5, producto.getStockMinimo());
            pstmt.setDouble(6, producto.getPrecio());
            pstmt.setString(7, producto.getUbicacion());
            pstmt.setString(8, producto.getEstado().name());
            pstmt.setString(9, producto.getProveedorId());
            pstmt.setTimestamp(10, new Timestamp(producto.getFechaCreacion().getTime()));
            pstmt.setTimestamp(11, new Timestamp(producto.getFechaActualizacion().getTime()));
            
            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al agregar producto: " + producto.getId(), e);
            return false;
        }
    }

    @Override
    public boolean actualizar(Producto producto) {
        String sql = "UPDATE PRODUCTOS SET nombre = ?, categoria = ?, stock_actual = ?, " +
                    "stock_minimo = ?, precio = ?, ubicacion = ?, estado = ?, proveedor_id = ?, " +
                    "fecha_actualizacion = ? WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, producto.getNombre());
            pstmt.setString(2, producto.getCategoria().name());
            pstmt.setInt(3, producto.getStockActual());
            pstmt.setInt(4, producto.getStockMinimo());
            pstmt.setDouble(5, producto.getPrecio());
            pstmt.setString(6, producto.getUbicacion());
            pstmt.setString(7, producto.getEstado().name());
            pstmt.setString(8, producto.getProveedorId());
            pstmt.setTimestamp(9, new Timestamp(new Date().getTime()));
            pstmt.setString(10, producto.getId());
            
            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al actualizar producto: " + producto.getId(), e);
            return false;
        }
    }

    @Override
    public boolean eliminar(String id) {
        String sql = "DELETE FROM PRODUCTOS WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, id);
            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al eliminar producto: " + id, e);
            return false;
        }
    }

    @Override
    public Optional<Producto> obtenerPorId(String id) {
        String sql = "SELECT * FROM PRODUCTOS WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapearProducto(rs));
            }
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al obtener producto: " + id, e);
        }
        
        return Optional.empty();
    }

    @Override
    public List<Producto> obtenerTodos() {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT * FROM PRODUCTOS ORDER BY nombre";
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                productos.add(mapearProducto(rs));
            }
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al obtener todos los productos", e);
        }
        
        return productos;
    }

    @Override
    public List<Producto> obtenerPorCategoria(CategoriaProducto categoria) {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT * FROM PRODUCTOS WHERE categoria = ? ORDER BY nombre";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, categoria.name());
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                productos.add(mapearProducto(rs));
            }
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al obtener productos por categoría: " + categoria, e);
        }
        
        return productos;
    }

    @Override
    public List<Producto> obtenerStockBajo() {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT * FROM PRODUCTOS WHERE stock_actual <= stock_minimo AND estado != 'DESCONTINUADO' ORDER BY stock_actual ASC";
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                productos.add(mapearProducto(rs));
            }
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al obtener productos con stock bajo", e);
        }
        
        return productos;
    }

    @Override
    public List<Producto> buscarPorNombre(String nombre) {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT * FROM PRODUCTOS WHERE nombre LIKE ? ORDER BY nombre";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + nombre + "%");
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                productos.add(mapearProducto(rs));
            }
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al buscar productos por nombre: " + nombre, e);
        }
        
        return productos;
    }

    @Override
    public boolean actualizarStock(String productoId, int nuevaCantidad) {
        String sql = "UPDATE PRODUCTOS SET stock_actual = ?, fecha_actualizacion = ? WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, nuevaCantidad);
            pstmt.setTimestamp(2, new Timestamp(new Date().getTime()));
            pstmt.setString(3, productoId);
            
            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al actualizar stock del producto: " + productoId, e);
            return false;
        }
    }

    @Override
    public boolean existeProducto(String id) {
        String sql = "SELECT 1 FROM PRODUCTOS WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al verificar existencia de producto: " + id, e);
            return false;
        }
    }

    @Override
    public List<Producto> obtenerPorProveedor(String proveedorId) {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT * FROM PRODUCTOS WHERE proveedor_id = ? ORDER BY nombre";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, proveedorId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                productos.add(mapearProducto(rs));
            }
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al obtener productos por proveedor: " + proveedorId, e);
        }
        
        return productos;
    }

    @Override
    public List<Producto> obtenerPorEstado(EstadoProducto estado) {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT * FROM PRODUCTOS WHERE estado = ? ORDER BY nombre";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, estado.name());
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                productos.add(mapearProducto(rs));
            }
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al obtener productos por estado: " + estado, e);
        }
        
        return productos;
    }

    @Override
    public int contarTotalProductos() {
        String sql = "SELECT COUNT(*) as total FROM PRODUCTOS";
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt("total");
            }
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al contar productos", e);
        }
        
        return 0;
    }

    private Producto mapearProducto(ResultSet rs) throws SQLException {
        Producto producto = new Producto(
            rs.getString("id"),
            rs.getString("nombre"),
            CategoriaProducto.valueOf(rs.getString("categoria")),
            rs.getInt("stock_actual"),
            rs.getInt("stock_minimo"),
            rs.getDouble("precio"),
            rs.getString("ubicacion"),
            rs.getString("proveedor_id")
        );
        
        producto.setEstado(EstadoProducto.valueOf(rs.getString("estado")));
        producto.setFechaCreacion(new Date(rs.getTimestamp("fecha_creacion").getTime()));
        
        Timestamp fechaActualizacion = rs.getTimestamp("fecha_actualizacion");
        if (fechaActualizacion != null) {
            producto.setFechaActualizacion(new Date(fechaActualizacion.getTime()));
        }
        
        return producto;
    }
}
