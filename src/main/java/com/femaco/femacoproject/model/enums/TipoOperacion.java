package com.femaco.femacoproject.model.enums;

public enum TipoOperacion {
    CREAR("Crear"),
    ACTUALIZAR("Actualizar"),
    ELIMINAR("Eliminar"),
    CONSULTAR("Consultar"),
    BACKUP("Backup"),
    RESTAURAR("Restaurar");
    
    private final String descripcion;
    
    TipoOperacion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
}
