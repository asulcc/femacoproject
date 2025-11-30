package com.femaco.femacoproject.model.enums;

public enum EstadoProducto {
    ACTIVO("Activo"),
    INACTIVO("Inactivo"),
    DESCONTINUADO("Descontinuado"),
    STOCK_BAJO("Stock Bajo"),
    STOCK_CRITICO("Stock Crítico");
    
    private final String descripcion;
    
    EstadoProducto(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
}
