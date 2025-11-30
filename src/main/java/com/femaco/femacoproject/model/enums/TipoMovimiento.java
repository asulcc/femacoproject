package com.femaco.femacoproject.model.enums;

public enum TipoMovimiento {
    ENTRADA("Entrada de Stock"),
    SALIDA("Salida de Stock"),
    AJUSTE("Ajuste de Inventario");
    
    private final String descripcion;
    
    TipoMovimiento(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
}
