package com.femaco.femacoproject.service;

import com.femaco.femacoproject.exception.UsuarioNoAutorizadoException;
import com.femaco.femacoproject.exception.UsuarioNoEncontradoException;
import com.femaco.femacoproject.model.Usuario;
import com.femaco.femacoproject.model.enums.RolUsuario;
import java.util.List;

public interface AutenticacionService {
    // ===== AUTENTICACIÓN =====
    
    // Autenticar usuario con username y password
    Usuario autenticar(String username, String password) throws UsuarioNoAutorizadoException;
    
    // Cerrar sesión de usuario
    void cerrarSesion(String usuarioId);
    
    // Verificar si un usuario está autenticado
    boolean estaAutenticado(String usuarioId);
    
    // Validar token de sesión
    boolean validarTokenSesion(String token);
    
    // ===== GESTIÓN DE USUARIOS =====
    
    // Registrar nuevo usuario
    boolean registrarUsuario(Usuario usuario) throws UsuarioNoAutorizadoException;
    
    // Actualizar información de usuario
    boolean actualizarUsuario(Usuario usuario) throws UsuarioNoEncontradoException, UsuarioNoAutorizadoException;
    
    // Cambiar contraseña de usuario
    boolean cambiarPassword(String usuarioId, String passwordActual, String nuevoPassword) 
                          throws UsuarioNoAutorizadoException;
    
    // Resetear contraseña (solo administradores)
    boolean resetearPassword(String usuarioId, String usuarioAdministrador) 
                           throws UsuarioNoAutorizadoException;
    
    // Desactivar usuario
    boolean desactivarUsuario(String usuarioId, String usuarioAdministrador) 
                            throws UsuarioNoAutorizadoException;
    
    // Activar usuario
    boolean activarUsuario(String usuarioId, String usuarioAdministrador) 
                         throws UsuarioNoAutorizadoException;
    
    // ===== CONSULTAS DE USUARIOS =====
    
    // Buscar usuario por ID
    Usuario buscarUsuario(String id) throws UsuarioNoEncontradoException;
    
    // Buscar usuario por username
    Usuario buscarUsuarioPorUsername(String username) throws UsuarioNoEncontradoException;
    
    // Listar todos los usuarios
    List<Usuario> listarUsuarios();
    
    // Listar usuarios por rol
    List<Usuario> listarUsuariosPorRol(RolUsuario rol);
    
    // Listar usuarios activos
    List<Usuario> listarUsuariosActivos();
    
    // ===== AUTORIZACIÓN =====
    
    // Verificar si usuario tiene permiso para una acción
    boolean tienePermiso(String usuarioId, String permiso);
    
    // Verificar si usuario puede gestionar productos
    boolean puedeGestionarProductos(String usuarioId);
    
    // Verificar si usuario puede gestionar usuarios
    boolean puedeGestionarUsuarios(String usuarioId);
    
    // Verificar si usuario puede generar reportes
    boolean puedeGenerarReportes(String usuarioId);
    
    // Verificar si usuario puede configurar sistema
    boolean puedeConfigurarSistema(String usuarioId);
    
    // ===== AUDITORÍA =====
    
    // Registrar acceso de usuario
    void registrarAcceso(String usuarioId);
    
    // Obtener historial de accesos recientes
    List<Usuario> obtenerHistorialAccesosRecientes();
}