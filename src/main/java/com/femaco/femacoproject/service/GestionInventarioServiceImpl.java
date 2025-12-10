package com.femaco.femacoproject.service;

import com.femaco.femacoproject.dao.MovimientoDAO;
import com.femaco.femacoproject.dao.ProductoDAO;
import com.femaco.femacoproject.dao.ProveedorDAO;
import com.femaco.femacoproject.exception.ProductoNoEncontradoException;
import com.femaco.femacoproject.exception.ProveedorNoEncontradoException;
import com.femaco.femacoproject.exception.StockInsuficienteException;
import com.femaco.femacoproject.model.MovimientoInventario;
import com.femaco.femacoproject.model.Producto;
import com.femaco.femacoproject.model.Proveedor;
import com.femaco.femacoproject.model.enums.CategoriaProducto;
import com.femaco.femacoproject.model.enums.EstadoProducto;
import com.femaco.femacoproject.model.enums.TipoMovimiento;
import com.femaco.femacoproject.util.ArbolBinarioBusqueda;
import com.femaco.femacoproject.util.Logger;

import java.util.*;
import java.util.stream.Collectors;

public class GestionInventarioServiceImpl implements GestionInventarioService {
    private final ProductoDAO productoDAO;
    private final MovimientoDAO movimientoDAO;
    private final ProveedorDAO proveedorDAO;
    private final ArbolBinarioBusqueda<Producto> arbolProductos;
    private final List<InventarioObserver> observadores;
    private final Logger logger;
    
    public GestionInventarioServiceImpl(ProductoDAO productoDAO, MovimientoDAO movimientoDAO, ProveedorDAO proveedorDAO) {
        this.productoDAO = productoDAO;
        this.movimientoDAO = movimientoDAO;
        this.proveedorDAO = proveedorDAO;
        this.arbolProductos = new ArbolBinarioBusqueda<>();
        this.observadores = new ArrayList<>();
        this.logger = Logger.getInstance();
        cargarProductosEnArbol();
    }
    
    private void cargarProductosEnArbol() {
        try {
            List<Producto> productos = productoDAO.obtenerTodos();
            for (Producto producto : productos) {
                arbolProductos.insertar(producto);
            }
            logger.info("Cargados " + productos.size() + " productos en árbol binario");
        } catch (Exception e) {
            logger.error("Error al cargar productos en árbol", e);
        }
    }
    
    // ===== IMPLEMENTACIÓN GESTIÓN DE PRODUCTOS =====
    
    @Override
    public boolean registrarProducto(Producto producto) {
        try {
            if (productoDAO.existeProducto(producto.getId())) {
                logger.warning("Intento de registrar producto con ID existente: " + producto.getId());
                return false;
            }
            
            boolean exito = productoDAO.agregar(producto);
            if (exito) {
                arbolProductos.insertar(producto);
                notificarObservadores(producto, "PRODUCTO_CREADO");
                logger.info("Producto registrado: " + producto.getId() + " - " + producto.getNombre());
            }
            return exito;
        } catch (Exception e) {
            logger.error("Error al registrar producto: " + producto.getId(), e);
            return false;
        }
    }
    
    @Override
    public boolean actualizarProducto(Producto producto) throws ProductoNoEncontradoException {
        try {
            if (!productoDAO.existeProducto(producto.getId())) {
                throw new ProductoNoEncontradoException("Producto no encontrado: " + producto.getId());
            }
            
            boolean exito = productoDAO.actualizar(producto);
            if (exito) {
                arbolProductos.actualizar(producto);
                notificarObservadores(producto, "PRODUCTO_ACTUALIZADO");
                logger.info("Producto actualizado: " + producto.getId());
            }
            return exito;
        } catch (ProductoNoEncontradoException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error al actualizar producto: " + producto.getId(), e);
            return false;
        }
    }
    
    @Override
    public boolean eliminarProducto(String id) throws ProductoNoEncontradoException {
        try {
            Producto producto = productoDAO.obtenerPorId(id)
                .orElseThrow(() -> new ProductoNoEncontradoException("Producto no encontrado: " + id));
            
            // Eliminación lógica - cambiar estado a INACTIVO
            producto.setEstado(EstadoProducto.INACTIVO);
            boolean exito = productoDAO.actualizar(producto);
            
            if (exito) {
                arbolProductos.eliminar(producto);
                notificarObservadores(producto, "PRODUCTO_ELIMINADO");
                logger.info("Producto eliminado (lógico): " + id);
            }
            return exito;
        } catch (ProductoNoEncontradoException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error al eliminar producto: " + id, e);
            return false;
        }
    }
    
    @Override
    public Producto buscarProducto(String id) throws ProductoNoEncontradoException {
        try {
            // Búsqueda en árbol binario
            Producto producto = arbolProductos.buscar(new Producto(id, "", CategoriaProducto.HERRAMIENTAS_MANUALES, 0, 0, null));
            
            if (producto == null) {
                // Volver al DAO si no está en el árbol
                producto = productoDAO.obtenerPorId(id)
                    .orElseThrow(() -> new ProductoNoEncontradoException("Producto no encontrado: " + id));
                arbolProductos.insertar(producto); // Agregar al árbol para futuras búsquedas
            }
            
            return producto;
        } catch (ProductoNoEncontradoException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error al buscar producto: " + id, e);
            throw new ProductoNoEncontradoException("Error al buscar producto: " + id, e);
        }
    }
    
    @Override
    public List<Producto> listarProductos() {
        return productoDAO.obtenerTodos();
    }
    
    @Override
    public List<Producto> buscarProductosPorNombre(String nombre) {
        return productoDAO.buscarPorNombre(nombre);
    }
    
    @Override
    public List<Producto> buscarProductosPorCategoria(CategoriaProducto categoria) {
        return productoDAO.obtenerPorCategoria(categoria);
    }
    
    @Override
    public List<Producto> buscarProductosPorEstado(EstadoProducto estado) {
        return productoDAO.obtenerPorEstado(estado);
    }
    
    @Override
    public List<Producto> obtenerProductosStockBajo() {
        return productoDAO.obtenerStockBajo();
    }
    
    @Override
    public List<Producto> obtenerProductosStockCritico() {
        return productoDAO.obtenerPorEstado(EstadoProducto.STOCK_CRITICO);
    }
    
    // ===== IMPLEMENTACIÓN MOVIMIENTOS DE INVENTARIO =====
    
    @Override
    public boolean registrarEntrada(String productoId, int cantidad, String motivo, 
                                  String usuarioId, String referencia) throws ProductoNoEncontradoException {
        try {
            Producto producto = buscarProducto(productoId);
            
            MovimientoInventario movimiento = new MovimientoInventario(
                productoId, TipoMovimiento.ENTRADA, cantidad, motivo, usuarioId, referencia
            );
            
            boolean movimientoRegistrado = movimientoDAO.registrar(movimiento);
            if (movimientoRegistrado) {
                // Actualizar stock
                producto.aumentarStock(cantidad);
                productoDAO.actualizar(producto);
                arbolProductos.actualizar(producto);
                
                notificarObservadores(producto, "ENTRADA_REGISTRADA");
                logger.logMovimiento(usuarioId, productoId, "ENTRADA", cantidad);
            }
            
            return movimientoRegistrado;
        } catch (ProductoNoEncontradoException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error al registrar entrada: " + productoId, e);
            return false;
        }
    }
    
    @Override
    public boolean registrarSalida(String productoId, int cantidad, String motivo, 
                                 String usuarioId, String referencia) 
                                 throws StockInsuficienteException, ProductoNoEncontradoException {
        try {
            Producto producto = buscarProducto(productoId);
            
            if (!producto.tieneStockSuficiente(cantidad)) {
                throw new StockInsuficienteException(
                    "Stock insuficiente para " + producto.getNombre() + 
                    ". Stock actual: " + producto.getStockActual() + 
                    ", solicitado: " + cantidad
                );
            }
            
            MovimientoInventario movimiento = new MovimientoInventario(
                productoId, TipoMovimiento.SALIDA, cantidad, motivo, usuarioId, referencia
            );
            
            boolean movimientoRegistrado = movimientoDAO.registrar(movimiento);
            if (movimientoRegistrado) {
                // Actualizar stock
                producto.disminuirStock(cantidad);
                productoDAO.actualizar(producto);
                arbolProductos.actualizar(producto);
                
                notificarObservadores(producto, "SALIDA_REGISTRADA");
                logger.logMovimiento(usuarioId, productoId, "SALIDA", cantidad);
            }
            
            return movimientoRegistrado;
        } catch (StockInsuficienteException | ProductoNoEncontradoException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error al registrar salida: " + productoId, e);
            return false;
        }
    }
    
    @Override
    public boolean registrarAjuste(String productoId, int nuevoStock, String motivo, 
                                 String usuarioId) throws ProductoNoEncontradoException {
        try {
            Producto producto = buscarProducto(productoId);
            int diferencia = nuevoStock - producto.getStockActual();
            TipoMovimiento tipo = diferencia > 0 ? TipoMovimiento.ENTRADA : TipoMovimiento.SALIDA;
            
            MovimientoInventario movimiento = new MovimientoInventario(
                productoId, TipoMovimiento.AJUSTE, Math.abs(diferencia), motivo, usuarioId, "AJUSTE"
            );
            
            boolean movimientoRegistrado = movimientoDAO.registrar(movimiento);
            if (movimientoRegistrado) {
                producto.setStockActual(nuevoStock);
                producto.actualizarEstado();
                productoDAO.actualizar(producto);
                arbolProductos.actualizar(producto);
                
                notificarObservadores(producto, "AJUSTE_REGISTRADO");
                logger.logMovimiento(usuarioId, productoId, "AJUSTE", Math.abs(diferencia));
            }
            
            return movimientoRegistrado;
        } catch (ProductoNoEncontradoException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error al registrar ajuste: " + productoId, e);
            return false;
        }
    }
    
    // ===== IMPLEMENTACIÓN CONSULTAS DE INVENTARIO =====
    
    @Override
    public int obtenerStockActual(String productoId) throws ProductoNoEncontradoException {
        Producto producto = buscarProducto(productoId);
        return producto.getStockActual();
    }
    
    @Override
    public boolean verificarStockSuficiente(String productoId, int cantidadRequerida) 
                                          throws ProductoNoEncontradoException {
        Producto producto = buscarProducto(productoId);
        return producto.tieneStockSuficiente(cantidadRequerida);
    }
    
    @Override
    public List<MovimientoInventario> obtenerHistorialProducto(String productoId) {
        return movimientoDAO.obtenerPorProducto(productoId);
    }
    
    @Override
    public List<MovimientoInventario> obtenerMovimientosPorFecha(Date fechaInicio, Date fechaFin) {
        return movimientoDAO.obtenerPorFecha(fechaInicio, fechaFin);
    }
    
    @Override
    public List<MovimientoInventario> obtenerMovimientosRecientes() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -30);
        Date fechaInicio = cal.getTime();
        Date fechaFin = new Date();
        
        return movimientoDAO.obtenerPorFecha(fechaInicio, fechaFin);
    }
    
    // ===== IMPLEMENTACIÓN GESTIÓN DE PROVEEDORES =====
    
    @Override
    public boolean registrarProveedor(Proveedor proveedor) {
        try {
            boolean exito = proveedorDAO.agregar(proveedor);
            if (exito) {
                logger.info("Proveedor registrado: " + proveedor.getId() + " - " + proveedor.getNombre());
            }
            return exito;
        } catch (Exception e) {
            logger.error("Error al registrar proveedor: " + proveedor.getId(), e);
            return false;
        }
    }
    
    @Override
    public boolean actualizarProveedor(Proveedor proveedor) throws ProveedorNoEncontradoException {
        try {
            if (!proveedorDAO.existeProveedor(proveedor.getId())) {
                throw new ProveedorNoEncontradoException("Proveedor no encontrado: " + proveedor.getId());
            }
            
            boolean exito = proveedorDAO.actualizar(proveedor);
            if (exito) {
                logger.info("Proveedor actualizado: " + proveedor.getId());
            }
            return exito;
        } catch (ProveedorNoEncontradoException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error al actualizar proveedor: " + proveedor.getId(), e);
            return false;
        }
    }
    
    @Override
    public boolean desactivarProveedor(String id) throws ProveedorNoEncontradoException {
        try {
            if (!proveedorDAO.existeProveedor(id)) {
                throw new ProveedorNoEncontradoException("Proveedor no encontrado: " + id);
            }
            
            boolean exito = proveedorDAO.desactivarProveedor(id);
            if (exito) {
                logger.info("Proveedor desactivado: " + id);
            }
            return exito;
        } catch (ProveedorNoEncontradoException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error al desactivar proveedor: " + id, e);
            return false;
        }
    }
    
    @Override
    public Proveedor buscarProveedor(String id) throws ProveedorNoEncontradoException {
        try {
            return proveedorDAO.obtenerPorId(id)
                .orElseThrow(() -> new ProveedorNoEncontradoException("Proveedor no encontrado: " + id));
        } catch (ProveedorNoEncontradoException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error al buscar proveedor: " + id, e);
            throw new ProveedorNoEncontradoException("Error al buscar proveedor: " + id, e);
        }
    }
    
    @Override
    public List<Proveedor> listarProveedoresActivos() {
        return proveedorDAO.obtenerActivos();
    }
    
    @Override
    public List<Proveedor> listarProveedores() {
        return proveedorDAO.obtenerTodos();
    }
    
    // ===== IMPLEMENTACIÓN MÉTRICAS Y ESTADÍSTICAS =====
    
    @Override
    public double calcularValorTotalInventario() {
        return listarProductos().stream()
                .mapToDouble(Producto::calcularValorTotalStock)
                .sum();
    }
    
    @Override
    public int obtenerTotalProductos() {
        return productoDAO.contarTotalProductos();
    }
    
    @Override
    public int contarProductosPorCategoria(CategoriaProducto categoria) {
        return productoDAO.obtenerPorCategoria(categoria).size();
    }
    
    @Override
    public List<Producto> obtenerProductosMasMovidos(int limite) {
        List<Producto> todosProductos = listarProductos();
        
        return todosProductos.stream()
                .sorted((p1, p2) -> {
                    int movimientosP1 = movimientoDAO.obtenerTotalMovimientosPorProducto(p1.getId(), TipoMovimiento.SALIDA);
                    int movimientosP2 = movimientoDAO.obtenerTotalMovimientosPorProducto(p2.getId(), TipoMovimiento.SALIDA);
                    return Integer.compare(movimientosP2, movimientosP1);
                })
                .limit(limite)
                .collect(Collectors.toList());
    }
    
    // ===== PATRÓN OBSERVER =====
    
    public void agregarObservador(InventarioObserver observador) {
        observadores.add(observador);
    }
    
    public void removerObservador(InventarioObserver observador) {
        observadores.remove(observador);
    }
    
    private void notificarObservadores(Producto producto, String tipoEvento) {
        for (InventarioObserver observador : observadores) {
            try {
                observador.onInventarioCambiado(producto, tipoEvento);
            } catch (Exception e) {
                logger.error("Error notificando observador", e);
            }
        }
    }
}
