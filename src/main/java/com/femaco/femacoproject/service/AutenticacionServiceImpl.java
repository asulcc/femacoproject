package com.femaco.femacoproject.service;


import com.femaco.femacoproject.dao.UsuarioDAO;
import com.femaco.femacoproject.exception.UsuarioNoAutorizadoException;
import com.femaco.femacoproject.exception.UsuarioNoEncontradoException;
import com.femaco.femacoproject.model.Usuario;
import com.femaco.femacoproject.model.enums.RolUsuario;
import com.femaco.femacoproject.util.ListaEnlazada;
import com.femaco.femacoproject.util.Logger;
import com.femaco.femacoproject.util.SecurityUtil;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class AutenticacionServiceImpl implements AutenticacionService {
    private final UsuarioDAO usuarioDAO;
    private final Logger logger;
    private final Map<String, String> sesionesActivas; // token -> usuarioId
    private final Map<String, Date> ultimosAccesos;
    private final ListaEnlazada<String> historialAccesos;
    
    public AutenticacionServiceImpl(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
        this.logger = Logger.getInstance();
        this.sesionesActivas = new ConcurrentHashMap<>();
        this.ultimosAccesos = new ConcurrentHashMap<>();
        this.historialAccesos = new ListaEnlazada<>();
    }
    
    // ===== IMPLEMENTACIÓN AUTENTICACIÓN =====
    
    @Override
    public Usuario autenticar(String username, String password) throws UsuarioNoAutorizadoException {
        try {
            Optional<Usuario> usuarioOpt = usuarioDAO.obtenerPorUsername(username);
            
            if (usuarioOpt.isEmpty()) {
                logger.warning("Intento de autenticación fallido - Usuario no encontrado: " + username);
                throw new UsuarioNoAutorizadoException("Credenciales inválidas");
            }
            
            Usuario usuario = usuarioOpt.get();
            
            if (!usuario.isActivo()) {
                logger.warning("Intento de autenticación fallido - Usuario inactivo: " + username);
                throw new UsuarioNoAutorizadoException("Usuario inactivo");
            }
            
            if (!SecurityUtil.verifyPassword(password, usuario.getPassword())) {
                logger.warning("Intento de autenticación fallido - Password incorrecto: " + username);
                throw new UsuarioNoAutorizadoException("Credenciales inválidas");
            }
            
            // Actualizar último acceso
            usuarioDAO.actualizarUltimoAcceso(usuario.getId());
            usuario.registrarAcceso();
            
            // Generar token de sesión
            String token = SecurityUtil.generateSessionToken();
            sesionesActivas.put(token, usuario.getId());
            ultimosAccesos.put(usuario.getId(), new Date());
            
            // Registrar en historial
            historialAccesos.agregar(String.format(
                "ACCESO: %s - %s - %s", 
                new Date(), usuario.getUsername(), usuario.getRol()
            ));
            
            logger.info("Usuario autenticado exitosamente: " + username + " - Rol: " + usuario.getRol());
            return usuario;
            
        } catch (UsuarioNoAutorizadoException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error durante autenticación: " + username, e);
            throw new UsuarioNoAutorizadoException("Error durante autenticación", e);
        }
    }
    
    @Override
    public void cerrarSesion(String usuarioId) {
        try {
            // Eliminar todas las sesiones del usuario
            sesionesActivas.entrySet().removeIf(entry -> entry.getValue().equals(usuarioId));
            ultimosAccesos.remove(usuarioId);
            
            logger.info("Sesión cerrada para usuario: " + usuarioId);
        } catch (Exception e) {
            logger.error("Error al cerrar sesión: " + usuarioId, e);
        }
    }
    
    @Override
    public boolean estaAutenticado(String usuarioId) {
        return ultimosAccesos.containsKey(usuarioId) && 
               sesionesActivas.containsValue(usuarioId);
    }
    
    @Override
    public boolean validarTokenSesion(String token) {
        return sesionesActivas.containsKey(token);
    }
    
    // ===== IMPLEMENTACIÓN GESTIÓN DE USUARIOS =====
    
    @Override
    public boolean registrarUsuario(Usuario usuario) throws UsuarioNoAutorizadoException {
        try {
            // Validar que el usuario actual tenga permisos
            if (!puedeGestionarUsuarios(usuario.getId())) {
                throw new UsuarioNoAutorizadoException("No tiene permisos para registrar usuarios");
            }
            
            // Hash de la contraseña
            String passwordHash = SecurityUtil.hashPassword(usuario.getPassword());
            usuario.setPassword(passwordHash);
            
            boolean exito = usuarioDAO.agregar(usuario);
            if (exito) {
                logger.info("Usuario registrado: " + usuario.getUsername() + " - Rol: " + usuario.getRol());
            }
            return exito;
        } catch (UsuarioNoAutorizadoException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error al registrar usuario: " + usuario.getUsername(), e);
            return false;
        }
    }
    
    @Override
    public boolean actualizarUsuario(Usuario usuario) throws UsuarioNoEncontradoException, UsuarioNoAutorizadoException {
        try {
            if (!usuarioDAO.existeUsername(usuario.getUsername())) {
                throw new UsuarioNoEncontradoException("Usuario no encontrado: " + usuario.getUsername());
            }
            
            // Validar permisos
            if (!puedeGestionarUsuarios(usuario.getId())) {
                throw new UsuarioNoAutorizadoException("No tiene permisos para actualizar usuarios");
            }
            
            boolean exito = usuarioDAO.actualizar(usuario);
            if (exito) {
                logger.info("Usuario actualizado: " + usuario.getUsername());
            }
            return exito;
        } catch (UsuarioNoEncontradoException | UsuarioNoAutorizadoException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error al actualizar usuario: " + usuario.getUsername(), e);
            return false;
        }
    }
    
    @Override
    public boolean cambiarPassword(String usuarioId, String passwordActual, String nuevoPassword) 
                                 throws UsuarioNoAutorizadoException {
        try {
            Usuario usuario = usuarioDAO.obtenerPorId(usuarioId)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado: " + usuarioId));
            
            // Verificar password actual
            if (!SecurityUtil.verifyPassword(passwordActual, usuario.getPassword())) {
                throw new UsuarioNoAutorizadoException("Password actual incorrecto");
            }
            
            // Validar fortaleza del nuevo password
            if (!SecurityUtil.isStrongPassword(nuevoPassword)) {
                throw new UsuarioNoAutorizadoException("El nuevo password no cumple con los requisitos de seguridad");
            }
            
            String nuevoPasswordHash = SecurityUtil.hashPassword(nuevoPassword);
            boolean exito = usuarioDAO.cambiarPassword(usuarioId, nuevoPasswordHash);
            
            if (exito) {
                logger.info("Password cambiado para usuario: " + usuarioId);
            }
            return exito;
        } catch (UsuarioNoAutorizadoException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error al cambiar password: " + usuarioId, e);
            return false;
        }
    }
    
    @Override
    public boolean resetearPassword(String usuarioId, String usuarioAdministrador) 
                                  throws UsuarioNoAutorizadoException {
        try {
            // Validar que el administrador tenga permisos
            if (!puedeGestionarUsuarios(usuarioAdministrador)) {
                throw new UsuarioNoAutorizadoException("No tiene permisos para resetear passwords");
            }
            
            String passwordTemporal = SecurityUtil.generateTemporaryPassword();
            String passwordHash = SecurityUtil.hashPassword(passwordTemporal);
            
            boolean exito = usuarioDAO.cambiarPassword(usuarioId, passwordHash);
            if (exito) {
                logger.info("Password reseteado para usuario: " + usuarioId + " - Password temporal: " + 
                           SecurityUtil.maskSensitiveData(passwordTemporal));
                // En el futuro, aquí se enviaría el password temporal por email
            }
            return exito;
        } catch (UsuarioNoAutorizadoException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error al resetear password: " + usuarioId, e);
            return false;
        }
    }
    
    @Override
    public boolean desactivarUsuario(String usuarioId, String usuarioAdministrador) 
                                   throws UsuarioNoAutorizadoException {
        try {
            // Validar permisos
            if (!puedeGestionarUsuarios(usuarioAdministrador)) {
                throw new UsuarioNoAutorizadoException("No tiene permisos para desactivar usuarios");
            }
            
            // No permitir desactivarse a sí mismo
            if (usuarioId.equals(usuarioAdministrador)) {
                throw new UsuarioNoAutorizadoException("No puede desactivar su propio usuario");
            }
            
            boolean exito = usuarioDAO.desactivarUsuario(usuarioId);
            if (exito) {
                cerrarSesion(usuarioId); // Cerrar sesión si está activa
                logger.info("Usuario desactivado: " + usuarioId);
            }
            return exito;
        } catch (UsuarioNoAutorizadoException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error al desactivar usuario: " + usuarioId, e);
            return false;
        }
    }
    
    @Override
    public boolean activarUsuario(String usuarioId, String usuarioAdministrador) 
                                throws UsuarioNoAutorizadoException {
        try {
            // Validar permisos
            if (!puedeGestionarUsuarios(usuarioAdministrador)) {
                throw new UsuarioNoAutorizadoException("No tiene permisos para activar usuarios");
            }
            
            // Para activar, actualizamos el usuario estableciendo activo = true
            Usuario usuario = usuarioDAO.obtenerPorId(usuarioId)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado: " + usuarioId));
            
            usuario.setActivo(true);
            boolean exito = usuarioDAO.actualizar(usuario);
            
            if (exito) {
                logger.info("Usuario activado: " + usuarioId);
            }
            return exito;
        } catch (UsuarioNoAutorizadoException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error al activar usuario: " + usuarioId, e);
            return false;
        }
    }
    
    // ===== IMPLEMENTACIÓN CONSULTAS DE USUARIOS =====
    
    @Override
    public Usuario buscarUsuario(String id) throws UsuarioNoEncontradoException {
        try {
            return usuarioDAO.obtenerPorId(id)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado: " + id));
        } catch (UsuarioNoEncontradoException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error al buscar usuario: " + id, e);
            throw new UsuarioNoEncontradoException("Error al buscar usuario: " + id, e);
        }
    }
    
    @Override
    public Usuario buscarUsuarioPorUsername(String username) throws UsuarioNoEncontradoException {
        try {
            return usuarioDAO.obtenerPorUsername(username)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado: " + username));
        } catch (UsuarioNoEncontradoException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error al buscar usuario por username: " + username, e);
            throw new UsuarioNoEncontradoException("Error al buscar usuario: " + username, e);
        }
    }
    
    @Override
    public List<Usuario> listarUsuarios() {
        return usuarioDAO.obtenerTodos();
    }
    
    @Override
    public List<Usuario> listarUsuariosPorRol(RolUsuario rol) {
        return usuarioDAO.obtenerPorRol(rol);
    }
    
    @Override
    public List<Usuario> listarUsuariosActivos() {
        return usuarioDAO.obtenerActivos();
    }
    
    // ===== IMPLEMENTACIÓN AUTORIZACIÓN =====
    
    @Override
    public boolean tienePermiso(String usuarioId, String permiso) {
        try {
            Usuario usuario = buscarUsuario(usuarioId);
            
            switch (permiso) {
                case "GESTIONAR_PRODUCTOS":
                    return usuario.puedeGestionarProductos();
                case "GESTIONAR_USUARIOS":
                    return usuario.puedeGestionarUsuarios();
                case "GENERAR_REPORTES":
                    return usuario.puedeGenerarReportes();
                case "CONFIGURAR_SISTEMA":
                    return usuario.puedeConfigurarSistema();
                default:
                    return false;
            }
        } catch (UsuarioNoEncontradoException e) {
            return false;
        }
    }
    
    @Override
    public boolean puedeGestionarProductos(String usuarioId) {
        return tienePermiso(usuarioId, "GESTIONAR_PRODUCTOS");
    }
    
    @Override
    public boolean puedeGestionarUsuarios(String usuarioId) {
        return tienePermiso(usuarioId, "GESTIONAR_USUARIOS");
    }
    
    @Override
    public boolean puedeGenerarReportes(String usuarioId) {
        return tienePermiso(usuarioId, "GENERAR_REPORTES");
    }
    
    @Override
    public boolean puedeConfigurarSistema(String usuarioId) {
        return tienePermiso(usuarioId, "CONFIGURAR_SISTEMA");
    }
    
    // ===== IMPLEMENTACIÓN AUDITORÍA =====
    
    @Override
    public void registrarAcceso(String usuarioId) {
        try {
            usuarioDAO.actualizarUltimoAcceso(usuarioId);
            ultimosAccesos.put(usuarioId, new Date());
        } catch (Exception e) {
            logger.error("Error al registrar acceso: " + usuarioId, e);
        }
    }
    
    @Override
    public List<Usuario> obtenerHistorialAccesosRecientes() {
        // Retornar usuarios que han accedido recientemente
        return listarUsuariosActivos().stream()
                .filter(u -> u.getUltimoAcceso() != null)
                .sorted((u1, u2) -> u2.getUltimoAcceso().compareTo(u1.getUltimoAcceso()))
                .limit(10)
                .collect(Collectors.toList());
    }
    
    // ===== MÉTODOS DE MANTENIMIENTO =====
    
    public void limpiarSesionesExpiradas() {
        Date ahora = new Date();
        long tiempoExpiracion = 30 * 60 * 1000; // 30 minutos
        
        sesionesActivas.entrySet().removeIf(entry -> {
            String usuarioId = entry.getValue();
            Date ultimoAcceso = ultimosAccesos.get(usuarioId);
            if (ultimoAcceso == null) return true;
            
            long tiempoInactivo = ahora.getTime() - ultimoAcceso.getTime();
            if (tiempoInactivo > tiempoExpiracion) {
                logger.info("Sesión expirada para usuario: " + usuarioId);
                return true;
            }
            return false;
        });
    }
}
