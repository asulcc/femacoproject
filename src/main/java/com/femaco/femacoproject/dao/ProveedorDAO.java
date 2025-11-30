package com.femaco.femacoproject.dao;

import com.femaco.femacoproject.model.Proveedor;
import java.util.List;
import java.util.Optional;

public interface ProveedorDAO {
    boolean agregar(Proveedor proveedor);
    boolean actualizar(Proveedor proveedor);
    boolean eliminar(String id);
    Optional<Proveedor> obtenerPorId(String id);
    List<Proveedor> obtenerTodos();
    List<Proveedor> obtenerActivos();
    List<Proveedor> buscarPorNombre(String nombre);
    boolean desactivarProveedor(String id);
    boolean existeProveedor(String id);
}
