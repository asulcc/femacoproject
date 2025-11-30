package com.femaco.femacoproject.model;

import com.femaco.femacoproject.model.enums.EstadoPedido;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Pedido {
    private String id;
    private String proveedorId;
    private Date fechaCreacion;
    private Date fechaEntregaEstimada;
    private Date fechaEntregaReal;
    private EstadoPedido estado;
    private String observaciones;
    private String usuarioId;
    private List<DetallePedido> detalles;
    
    // Constructor principal
    public Pedido(String id, String proveedorId, String usuarioId) {
        this.id = id;
        this.proveedorId = proveedorId;
        this.usuarioId = usuarioId;
        this.fechaCreacion = new Date();
        this.estado = EstadoPedido.PENDIENTE;
        this.detalles = new ArrayList<>();
    }
    
    // Constructor simplificado
    public Pedido(String proveedorId, String usuarioId) {
        this(generarId(), proveedorId, usuarioId);
    }
    
    private static String generarId() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        return "PED_" + timestamp;
    }
    
    public void agregarDetalle(DetallePedido detalle) {
        if (detalles == null) {
            detalles = new ArrayList<>();
        }
        detalles.add(detalle);
    }
    
    public void eliminarDetalle(DetallePedido detalle) {
        if (detalles != null) {
            detalles.remove(detalle);
        }
    }
    
    public double calcularTotal() {
        if (detalles == null || detalles.isEmpty()) {
            return 0.0;
        }
        return detalles.stream()
                .mapToDouble(DetallePedido::calcularSubtotal)
                .sum();
    }
    
    public int getTotalItems() {
        if (detalles == null) return 0;
        return detalles.stream()
                .mapToInt(DetallePedido::getCantidad)
                .sum();
    }
    
    public boolean estaPendiente() {
        return estado == EstadoPedido.PENDIENTE;
    }
    
    public boolean estaEnProceso() {
        return estado == EstadoPedido.EN_PROCESO;
    }
    
    public boolean estaCompletado() {
        return estado == EstadoPedido.COMPLETADO;
    }
    
    public boolean estaCancelado() {
        return estado == EstadoPedido.CANCELADO;
    }
    
    public void marcarEnProceso() {
        this.estado = EstadoPedido.EN_PROCESO;
    }
    
    public void marcarCompletado() {
        this.estado = EstadoPedido.COMPLETADO;
        this.fechaEntregaReal = new Date();
    }
    
    public void marcarCancelado() {
        this.estado = EstadoPedido.CANCELADO;
    }
    
    public boolean tieneDetalles() {
        return detalles != null && !detalles.isEmpty();
    }
    
    public long getDiasCreacion() {
        if (fechaCreacion == null) return 0;
        long diff = new Date().getTime() - fechaCreacion.getTime();
        return diff / (1000 * 60 * 60 * 24);
    }
    
    public boolean estaAtrasado() {
        if (fechaEntregaEstimada == null || estaCompletado() || estaCancelado()) {
            return false;
        }
        return new Date().after(fechaEntregaEstimada);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pedido pedido = (Pedido) o;
        return Objects.equals(id, pedido.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return String.format("Pedido{id='%s', proveedor='%s', estado=%s, total=%.2f}", 
                           id, proveedorId, estado, calcularTotal());
    }
    
    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getProveedorId() { return proveedorId; }
    public void setProveedorId(String proveedorId) { this.proveedorId = proveedorId; }
    
    public Date getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(Date fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    
    public Date getFechaEntregaEstimada() { return fechaEntregaEstimada; }
    public void setFechaEntregaEstimada(Date fechaEntregaEstimada) { 
        this.fechaEntregaEstimada = fechaEntregaEstimada; 
    }
    
    public Date getFechaEntregaReal() { return fechaEntregaReal; }
    public void setFechaEntregaReal(Date fechaEntregaReal) { 
        this.fechaEntregaReal = fechaEntregaReal; 
    }
    
    public EstadoPedido getEstado() { return estado; }
    public void setEstado(EstadoPedido estado) { this.estado = estado; }
    
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    
    public String getUsuarioId() { return usuarioId; }
    public void setUsuarioId(String usuarioId) { this.usuarioId = usuarioId; }
    
    public List<DetallePedido> getDetalles() { return detalles; }
    public void setDetalles(List<DetallePedido> detalles) { this.detalles = detalles; }
}

// Clase interna para detalles del pedido
class DetallePedido {
    private String productoId;
    private int cantidad;
    private double precioUnitario;
    private String observaciones;
    
    public DetallePedido(String productoId, int cantidad, double precioUnitario) {
        this.productoId = productoId;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }
    
    public double calcularSubtotal() {
        return cantidad * precioUnitario;
    }
    
    // Getters y Setters
    public String getProductoId() { return productoId; }
    public void setProductoId(String productoId) { this.productoId = productoId; }
    
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    
    public double getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(double precioUnitario) { this.precioUnitario = precioUnitario; }
    
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
}
