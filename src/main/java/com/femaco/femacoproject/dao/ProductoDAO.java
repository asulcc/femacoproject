package com.femaco.femacoproject.dao;

import com.femaco.femacoproject.model.Producto;
import com.femaco.femacoproject.model.enums.CategoriaProducto;
import com.femaco.femacoproject.model.enums.EstadoProducto;
import java.util.List;
import java.util.Optional;

public interface ProductoDAO {
    // Operaciones CRUD básicas
    boolean agregar(Producto producto);
    boolean actualizar(Producto producto);
    boolean eliminar(String id);
    Optional<Producto> obtenerPorId(String id);
    List<Producto> obtenerTodos();
    
    // Operaciones específicas de negocio
    List<Producto> obtenerPorCategoria(CategoriaProducto categoria);
    List<Producto> obtenerPorEstado(EstadoProducto estado);
    List<Producto> obtenerStockBajo();
    List<Producto> buscarPorNombre(String nombre);
    boolean actualizarStock(String productoId, int nuevaCantidad);
    boolean existeProducto(String id);
    List<Producto> obtenerPorProveedor(String proveedorId);
    int contarTotalProductos();
}
