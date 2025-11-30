package com.femaco.femacoproject.model.enums;

public enum RolUsuario {
    ADMINISTRADOR("Administrador"),
    ALMACENERO("Almacenero"),
    SUPERVISOR("Supervisor");
    
    private final String descripcion;
    
    RolUsuario(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
}
