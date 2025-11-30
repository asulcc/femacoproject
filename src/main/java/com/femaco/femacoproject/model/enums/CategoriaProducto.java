package com.femaco.femacoproject.model.enums;

public enum CategoriaProducto {
    HERRAMIENTAS_MANUALES("Herramientas Manuales"),
    HERRAMIENTAS_ELECTRICAS("Herramientas Eléctricas"),
    MATERIALES_CONSTRUCCION("Materiales de Construcción"),
    FERRETERIA_GENERAL("Ferretería General"),
    SEGURIDAD_INDUSTRIAL("Seguridad Industrial"),
    PINTURAS("Pinturas y Accesorios");
    
    private final String descripcion;
    
    CategoriaProducto(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
}
