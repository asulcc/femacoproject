package com.femaco.femacoproject.service;

import com.femaco.femacoproject.exception.ProductoNoEncontradoException;
import com.femaco.femacoproject.exception.ProveedorNoEncontradoException;
import com.femaco.femacoproject.exception.StockInsuficienteException;
import com.femaco.femacoproject.model.MovimientoInventario;
import com.femaco.femacoproject.model.Producto;
import com.femaco.femacoproject.model.Proveedor;
import com.femaco.femacoproject.model.enums.CategoriaProducto;
import com.femaco.femacoproject.model.enums.EstadoProducto;

import java.util.List;
import java.util.Date;

public interface GestionInventarioService {
    // ===== GESTIÓN DE PRODUCTOS =====
    
    /**
     * Registrar un nuevo producto en el inventario
     */
    boolean registrarProducto(Producto producto);
    
    /**
     * Actualizar información de un producto existente
     */
    boolean actualizarProducto(Producto producto) throws ProductoNoEncontradoException;
    
    /**
     * Eliminar un producto del inventario (eliminación lógica)
     */
    boolean eliminarProducto(String id) throws ProductoNoEncontradoException;
    
    /**
     * Buscar producto por ID
     */
    Producto buscarProducto(String id) throws ProductoNoEncontradoException;
    
    /**
     * Obtener todos los productos
     */
    List<Producto> listarProductos();
    
    /**
     * Buscar productos por nombre (búsqueda parcial)
     */
    List<Producto> buscarProductosPorNombre(String nombre);
    
    /**
     * Buscar productos por categoría
     */
    List<Producto> buscarProductosPorCategoria(CategoriaProducto categoria);
    
    /**
     * Buscar productos por estado
     */
    List<Producto> buscarProductosPorEstado(EstadoProducto estado);
    
    /**
     * Obtener productos con stock bajo (stock <= stock mínimo)
     */
    List<Producto> obtenerProductosStockBajo();
    
    /**
     * Obtener productos con stock crítico (stock = 0)
     */
    List<Producto> obtenerProductosStockCritico();
    
    // ===== MOVIMIENTOS DE INVENTARIO =====
    
    /**
     * Registrar entrada de stock (compra, devolución, etc.)
     */
    boolean registrarEntrada(String productoId, int cantidad, String motivo, 
                           String usuarioId, String referencia) throws ProductoNoEncontradoException;
    
    /**
     * Registrar salida de stock (venta, consumo, etc.)
     */
    boolean registrarSalida(String productoId, int cantidad, String motivo, 
                          String usuarioId, String referencia) 
                          throws StockInsuficienteException, ProductoNoEncontradoException;
    
    /**
     * Registrar ajuste de inventario (corrección de stock)
     */
    boolean registrarAjuste(String productoId, int nuevoStock, String motivo, 
                          String usuarioId) throws ProductoNoEncontradoException;
    
    // ===== CONSULTAS DE INVENTARIO =====
    
    /**
     * Obtener stock actual de un producto
     */
    int obtenerStockActual(String productoId) throws ProductoNoEncontradoException;
    
    /**
     * Verificar si hay stock suficiente para una salida
     */
    boolean verificarStockSuficiente(String productoId, int cantidadRequerida) 
                                   throws ProductoNoEncontradoException;
    
    /**
     * Obtener historial de movimientos de un producto
     */
    List<MovimientoInventario> obtenerHistorialProducto(String productoId);
    
    /**
     * Obtener todos los movimientos en un rango de fechas
     */
    List<MovimientoInventario> obtenerMovimientosPorFecha(Date fechaInicio, Date fechaFin);
    
    /**
     * Obtener movimientos recientes (últimos 30 días)
     */
    List<MovimientoInventario> obtenerMovimientosRecientes();
    
    // ===== GESTIÓN DE PROVEEDORES =====
    
    /**
     * Registrar nuevo proveedor
     */
    boolean registrarProveedor(Proveedor proveedor);
    
    /**
     * Actualizar información de proveedor
     */
    boolean actualizarProveedor(Proveedor proveedor) throws ProveedorNoEncontradoException;
    
    /**
     * Desactivar proveedor
     */
    boolean desactivarProveedor(String id) throws ProveedorNoEncontradoException;
    
    /**
     * Buscar proveedor por ID
     */
    Proveedor buscarProveedor(String id) throws ProveedorNoEncontradoException;
    
    /**
     * Listar todos los proveedores activos
     */
    List<Proveedor> listarProveedoresActivos();
    
    /**
     * Listar todos los proveedores
     */
    List<Proveedor> listarProveedores();
    
    // ===== MÉTRICAS Y ESTADÍSTICAS =====
    
    /**
     * Obtener valor total del inventario
     */
    double calcularValorTotalInventario();
    
    /**
     * Obtener cantidad total de productos
     */
    int obtenerTotalProductos();
    
    /**
     * Obtener cantidad de productos por categoría
     */
    int contarProductosPorCategoria(CategoriaProducto categoria);
    
    /**
     * Obtener productos más movidos (entradas + salidas)
     */
    List<Producto> obtenerProductosMasMovidos(int limite);
}
