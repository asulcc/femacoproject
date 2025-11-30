package com.femaco.femacoproject.dao;

import com.femaco.femacoproject.model.Usuario;
import com.femaco.femacoproject.model.enums.RolUsuario;
import java.util.List;
import java.util.Optional;

public interface UsuarioDAO {
    boolean agregar(Usuario usuario);
    boolean actualizar(Usuario usuario);
    boolean eliminar(String id);
    Optional<Usuario> obtenerPorId(String id);
    Optional<Usuario> obtenerPorUsername(String username);
    List<Usuario> obtenerTodos();
    List<Usuario> obtenerPorRol(RolUsuario rol);
    List<Usuario> obtenerActivos();
    boolean actualizarUltimoAcceso(String usuarioId);
    boolean cambiarPassword(String usuarioId, String nuevoPasswordHash);
    boolean desactivarUsuario(String usuarioId);
    boolean existeUsername(String username);
}
