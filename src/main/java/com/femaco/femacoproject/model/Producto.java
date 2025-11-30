package com.femaco.femacoproject.model;

import com.femaco.femacoproject.model.enums.CategoriaProducto;
import com.femaco.femacoproject.model.enums.EstadoProducto;
import java.util.Date;
import java.util.Objects;

public class Producto implements Comparable<Producto> {
    private String id;
    private String nombre;
    private CategoriaProducto categoria;
    private int stockActual;
    private int stockMinimo;
    private double precio;
    private String ubicacion;
    private EstadoProducto estado;
    private String proveedorId;
    private Date fechaCreacion;
    private Date fechaActualizacion;
    
    // Constructor completo
    public Producto(String id, String nombre, CategoriaProducto categoria, 
                   int stockActual, int stockMinimo, double precio, 
                   String ubicacion, String proveedorId) {
        this.id = id;
        this.nombre = nombre;
        this.categoria = categoria;
        this.stockActual = stockActual;
        this.stockMinimo = stockMinimo;
        this.precio = precio;
        this.ubicacion = ubicacion;
        this.proveedorId = proveedorId;
        this.estado = EstadoProducto.ACTIVO;
        this.fechaCreacion = new Date();
        this.fechaActualizacion = new Date();
        actualizarEstado();
    }
    
    // Constructor mínimo
    public Producto(String id, String nombre, CategoriaProducto categoria, 
                   int stockMinimo, double precio, String ubicacion) {
        this(id, nombre, categoria, 0, stockMinimo, precio, ubicacion, null);
    }
    
    public void actualizarEstado() {
        if (stockActual <= 0) {
            this.estado = EstadoProducto.STOCK_CRITICO;
        } else if (stockActual <= stockMinimo) {
            this.estado = EstadoProducto.STOCK_BAJO;
        } else {
            this.estado = EstadoProducto.ACTIVO;
        }
        this.fechaActualizacion = new Date();
    }
    
    public void aumentarStock(int cantidad) {
        if (cantidad > 0) {
            this.stockActual += cantidad;
            actualizarEstado();
        }
    }
    
    public void disminuirStock(int cantidad) {
        if (cantidad > 0 && cantidad <= stockActual) {
            this.stockActual -= cantidad;
            actualizarEstado();
        }
    }
    
    public boolean tieneStockSuficiente(int cantidadRequerida) {
        return stockActual >= cantidadRequerida;
    }
    
    public double calcularValorTotalStock() {
        return stockActual * precio;
    }
    
    public int getDiasCreacion() {
        if (fechaCreacion == null) return 0;
        long diff = new Date().getTime() - fechaCreacion.getTime();
        return (int) (diff / (1000 * 60 * 60 * 24));
    }
    
    @Override
    public int compareTo(Producto otro) {
        return this.id.compareTo(otro.id);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Producto producto = (Producto) o;
        return Objects.equals(id, producto.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return String.format("Producto{id='%s', nombre='%s', stock=%d, precio=%.2f}", 
                           id, nombre, stockActual, precio);
    }
    
    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { 
        this.nombre = nombre; 
        this.fechaActualizacion = new Date();
    }
    
    public CategoriaProducto getCategoria() { return categoria; }
    public void setCategoria(CategoriaProducto categoria) { 
        this.categoria = categoria; 
        this.fechaActualizacion = new Date();
    }
    
    public int getStockActual() { return stockActual; }
    public void setStockActual(int stockActual) { 
        this.stockActual = stockActual; 
        actualizarEstado();
    }
    
    public int getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(int stockMinimo) { 
        this.stockMinimo = stockMinimo; 
        actualizarEstado();
    }
    
    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { 
        this.precio = precio; 
        this.fechaActualizacion = new Date();
    }
    
    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { 
        this.ubicacion = ubicacion; 
        this.fechaActualizacion = new Date();
    }
    
    public EstadoProducto getEstado() { return estado; }
    public void setEstado(EstadoProducto estado) { 
        this.estado = estado; 
        this.fechaActualizacion = new Date();
    }
    
    public String getProveedorId() { return proveedorId; }
    public void setProveedorId(String proveedorId) { 
        this.proveedorId = proveedorId; 
        this.fechaActualizacion = new Date();
    }
    
    public Date getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(Date fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    
    public Date getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(Date fechaActualizacion) { 
        this.fechaActualizacion = fechaActualizacion; 
    }
}
