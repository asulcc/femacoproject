package com.femaco.femacoproject.model;

import java.util.Date;
import java.util.Objects;

public class Proveedor {
    private String id;
    private String nombre;
    private String contacto;
    private String telefono;
    private String email;
    private String direccion;
    private boolean activo;
    private Date fechaRegistro;
    
    // Constructor completo
    public Proveedor(String id, String nombre, String contacto, String telefono) {
        this.id = id;
        this.nombre = nombre;
        this.contacto = contacto;
        this.telefono = telefono;
        this.activo = true;
        this.fechaRegistro = new Date();
    }
    
    // Constructor mínimo
    public Proveedor(String id, String nombre) {
        this(id, nombre, null, null);
    }
    
    public boolean esActivo() {
        return activo;
    }
    
    public void activar() {
        this.activo = true;
    }
    
    public void desactivar() {
        this.activo = false;
    }
    
    public boolean tieneInformacionContactoCompleta() {
        return contacto != null && !contacto.trim().isEmpty() &&
               telefono != null && !telefono.trim().isEmpty();
    }
    
    public String getInformacionContacto() {
        StringBuilder info = new StringBuilder();
        if (contacto != null && !contacto.trim().isEmpty()) {
            info.append("Contacto: ").append(contacto);
        }
        if (telefono != null && !telefono.trim().isEmpty()) {
            if (info.length() > 0) info.append(" - ");
            info.append("Tel: ").append(telefono);
        }
        if (email != null && !email.trim().isEmpty()) {
            if (info.length() > 0) info.append(" - ");
            info.append("Email: ").append(email);
        }
        return info.toString();
    }
    
    public int getDiasRegistro() {
        if (fechaRegistro == null) return 0;
        long diff = new Date().getTime() - fechaRegistro.getTime();
        return (int) (diff / (1000 * 60 * 60 * 24));
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Proveedor proveedor = (Proveedor) o;
        return Objects.equals(id, proveedor.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return String.format("Proveedor{id='%s', nombre='%s', activo=%s}", 
                           id, nombre, activo);
    }
    
    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getContacto() { return contacto; }
    public void setContacto(String contacto) { this.contacto = contacto; }
    
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
    
    public Date getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(Date fechaRegistro) { this.fechaRegistro = fechaRegistro; }
}
