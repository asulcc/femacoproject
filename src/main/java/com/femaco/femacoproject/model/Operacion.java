package com.femaco.femacoproject.model;

import com.femaco.femacoproject.model.enums.TipoOperacion;
import java.util.Date;
import java.util.Objects;

public class Operacion {
    private String id;
    private TipoOperacion tipo;
    private String descripcion;
    private Date fecha;
    private String usuarioId;
    private String entidadAfectada;
    private String idEntidadAfectada;
    private String datosAnteriores;
    private String datosNuevos;
    
    // Constructor principal
    public Operacion(TipoOperacion tipo, String descripcion, String usuarioId, 
                    String entidadAfectada, String idEntidadAfectada) {
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.usuarioId = usuarioId;
        this.entidadAfectada = entidadAfectada;
        this.idEntidadAfectada = idEntidadAfectada;
        this.fecha = new Date();
        this.id = generarId();
    }
    
    // Constructor simplificado
    public Operacion(TipoOperacion tipo, String descripcion, String usuarioId) {
        this(tipo, descripcion, usuarioId, null, null);
    }
    
    private String generarId() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        return "OP_" + timestamp + "_" + tipo.name();
    }
    
    public boolean esCreacion() {
        return tipo == TipoOperacion.CREAR;
    }
    
    public boolean esActualizacion() {
        return tipo == TipoOperacion.ACTUALIZAR;
    }
    
    public boolean esEliminacion() {
        return tipo == TipoOperacion.ELIMINAR;
    }
    
    public boolean esConsulta() {
        return tipo == TipoOperacion.CONSULTAR;
    }
    
    public String getResumen() {
        return String.format("[%s] %s - %s", 
                           tipo.getDescripcion(), 
                           descripcion, 
                           fecha.toString());
    }
    
    public boolean esReciente() {
        if (fecha == null) return false;
        long diff = new Date().getTime() - fecha.getTime();
        return diff <= (60 * 60 * 1000); // Última hora
    }
    
    public boolean afectaProducto() {
        return "PRODUCTO".equalsIgnoreCase(entidadAfectada);
    }
    
    public boolean afectaMovimiento() {
        return "MOVIMIENTO".equalsIgnoreCase(entidadAfectada);
    }
    
    public boolean afectaUsuario() {
        return "USUARIO".equalsIgnoreCase(entidadAfectada);
    }
    
    public boolean afectaProveedor() {
        return "PROVEEDOR".equalsIgnoreCase(entidadAfectada);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Operacion operacion = (Operacion) o;
        return Objects.equals(id, operacion.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return String.format("Operacion{id='%s', tipo=%s, descripcion='%s', usuario='%s'}", 
                           id, tipo, descripcion, usuarioId);
    }
    
    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public TipoOperacion getTipo() { return tipo; }
    public void setTipo(TipoOperacion tipo) { this.tipo = tipo; }
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }
    
    public String getUsuarioId() { return usuarioId; }
    public void setUsuarioId(String usuarioId) { this.usuarioId = usuarioId; }
    
    public String getEntidadAfectada() { return entidadAfectada; }
    public void setEntidadAfectada(String entidadAfectada) { this.entidadAfectada = entidadAfectada; }
    
    public String getIdEntidadAfectada() { return idEntidadAfectada; }
    public void setIdEntidadAfectada(String idEntidadAfectada) { this.idEntidadAfectada = idEntidadAfectada; }
    
    public String getDatosAnteriores() { return datosAnteriores; }
    public void setDatosAnteriores(String datosAnteriores) { this.datosAnteriores = datosAnteriores; }
    
    public String getDatosNuevos() { return datosNuevos; }
    public void setDatosNuevos(String datosNuevos) { this.datosNuevos = datosNuevos; }
}
