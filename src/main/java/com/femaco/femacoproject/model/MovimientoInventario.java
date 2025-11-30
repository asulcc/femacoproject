package com.femaco.femacoproject.model;

import com.femaco.femacoproject.model.enums.TipoMovimiento;
import java.util.Date;
import java.util.Objects;

public class MovimientoInventario {
    private String id;
    private String productoId;
    private TipoMovimiento tipo;
    private int cantidad;
    private String motivo;
    private Date fecha;
    private String usuarioId;
    private String referencia;
    private String productoNombre; // Campo calculado para UI
    
    // Constructor principal
    public MovimientoInventario(String productoId, TipoMovimiento tipo, 
                               int cantidad, String motivo, String usuarioId, 
                               String referencia) {
        this.productoId = productoId;
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.motivo = motivo;
        this.usuarioId = usuarioId;
        this.referencia = referencia;
        this.fecha = new Date();
        this.id = generarId();
    }
    
    // Constructor simplificado
    public MovimientoInventario(String productoId, TipoMovimiento tipo, 
                               int cantidad, String usuarioId) {
        this(productoId, tipo, cantidad, "Movimiento estándar", usuarioId, null);
    }
    
    private String generarId() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String random = String.valueOf((int)(Math.random() * 1000));
        return "MOV_" + timestamp + "_" + random;
    }
    
    public boolean esEntrada() {
        return tipo == TipoMovimiento.ENTRADA;
    }
    
    public boolean esSalida() {
        return tipo == TipoMovimiento.SALIDA;
    }
    
    public boolean esAjuste() {
        return tipo == TipoMovimiento.AJUSTE;
    }
    
    public String getDescripcionTipo() {
        return tipo.getDescripcion();
    }
    
    public double calcularValorMovimiento(double precioUnitario) {
        return cantidad * precioUnitario;
    }
    
    public boolean esReciente() {
        if (fecha == null) return false;
        long diff = new Date().getTime() - fecha.getTime();
        return diff <= (24 * 60 * 60 * 1000); // Últimas 24 horas
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MovimientoInventario that = (MovimientoInventario) o;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return String.format("Movimiento{id='%s', producto='%s', tipo=%s, cantidad=%d}", 
                           id, productoId, tipo, cantidad);
    }
    
    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getProductoId() { return productoId; }
    public void setProductoId(String productoId) { this.productoId = productoId; }
    
    public TipoMovimiento getTipo() { return tipo; }
    public void setTipo(TipoMovimiento tipo) { this.tipo = tipo; }
    
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
    
    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }
    
    public String getUsuarioId() { return usuarioId; }
    public void setUsuarioId(String usuarioId) { this.usuarioId = usuarioId; }
    
    public String getReferencia() { return referencia; }
    public void setReferencia(String referencia) { this.referencia = referencia; }
    
    public String getProductoNombre() { return productoNombre; }
    public void setProductoNombre(String productoNombre) { this.productoNombre = productoNombre; }

}
