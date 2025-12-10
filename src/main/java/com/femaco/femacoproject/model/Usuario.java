package com.femaco.femacoproject.model;

import com.femaco.femacoproject.model.enums.RolUsuario;
import java.util.Date;
import java.util.Objects;

public class Usuario {
    private String id;
    private String username;
    private String password;
    private String nombreCompleto;
    private String email;
    private RolUsuario rol;
    private boolean activo;
    private Date fechaCreacion;
    private Date ultimoAcceso;
    
    // Constructor completo
    public Usuario(String id, String username, String password, 
                   String nombreCompleto, String email, RolUsuario rol) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.nombreCompleto = nombreCompleto;
        this.email = email;
        this.rol = rol;
        this.activo = true;
        this.fechaCreacion = new Date();
    }
    
    // Constructor simplificado
    public Usuario(String id, String username, String password, 
                   String nombreCompleto, RolUsuario rol) {
        this(id, username, password, nombreCompleto, null, rol);
    }
    
    public boolean esAdministrador() {
        return rol == RolUsuario.ADMINISTRADOR;
    }
    
    public boolean esAlmacenero() {
        return rol == RolUsuario.ALMACENERO;
    }
    
    public boolean esSupervisor() {
        return rol == RolUsuario.SUPERVISOR;
    }
    
    public void activar() {
        this.activo = true;
    }
    
    public void desactivar() {
        this.activo = false;
    }
    
    public void registrarAcceso() {
        this.ultimoAcceso = new Date();
    }
    
    public boolean puedeGestionarUsuarios() {
        return esAdministrador() || esSupervisor();
    }
    
    public boolean puedeGestionarProductos() {
        return esAdministrador() || esSupervisor() || esAlmacenero();
    }
    
    public boolean puedeGenerarReportes() {
        return esAdministrador() || esSupervisor();
    }
    
    public boolean puedeConfigurarSistema() {
        return esAdministrador();
    }
    
    public long getDiasInactivo() {
        if (ultimoAcceso == null) return -1; // Nunca ha accedido
        long diff = new Date().getTime() - ultimoAcceso.getTime();
        return diff / (1000 * 60 * 60 * 24);
    }
    
    public boolean necesitaCambioPassword() {
        if (fechaCreacion == null) return false;
        long diff = new Date().getTime() - fechaCreacion.getTime();
        long dias = diff / (1000 * 60 * 60 * 24);
        return dias > 90; // Cambiar cada 90 días
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return Objects.equals(id, usuario.id) && 
               Objects.equals(username, usuario.username);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, username);
    }
    
    @Override
    public String toString() {
        return String.format("Usuario{id='%s', username='%s', rol=%s, activo=%s}", 
                           id, username, rol, activo);
    }
    
    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public RolUsuario getRol() { return rol; }
    public void setRol(RolUsuario rol) { this.rol = rol; }
    
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
    
    public Date getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(Date fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    
    public Date getUltimoAcceso() { return ultimoAcceso; }
    public void setUltimoAcceso(Date ultimoAcceso) { this.ultimoAcceso = ultimoAcceso; }
}
