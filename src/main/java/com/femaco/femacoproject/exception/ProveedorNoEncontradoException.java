package com.femaco.femacoproject.exception;

/**
 * Excepción lanzada cuando un proveedor no existe en el sistema.
 * Se utiliza en operaciones de búsqueda, actualización o eliminación de proveedores.
 */
public class ProveedorNoEncontradoException extends InventarioException {
    private final String proveedorId;
    private final String criterioBusqueda;
    
    /**
     * Constructor con ID del proveedor.
     */
    public ProveedorNoEncontradoException(String proveedorId) {
        super(crearMensajePorId(proveedorId),
              "PROVEEDOR_NO_ENCONTRADO", 
              "GESTION_PROVEEDORES", 
              "BUSCAR_PROVEEDOR");
        this.proveedorId = proveedorId;
        this.criterioBusqueda = "ID";
    }
    
    /**
     * Constructor con criterio de búsqueda personalizado.
     */
    public ProveedorNoEncontradoException(String criterioBusqueda, String valor) {
        super(crearMensajePorCriterio(criterioBusqueda, valor),
              "PROVEEDOR_NO_ENCONTRADO", 
              "GESTION_PROVEEDORES", 
              "BUSCAR_PROVEEDOR");
        this.proveedorId = valor;
        this.criterioBusqueda = criterioBusqueda;
    }
    
    /**
     * Constructor con causa.
     */
    public ProveedorNoEncontradoException(String mensaje, Throwable causa) {
        super(mensaje, "PROVEEDOR_NO_ENCONTRADO", "GESTION_PROVEEDORES", "BUSCAR_PROVEEDOR", causa);
        this.proveedorId = "DESCONOCIDO";
        this.criterioBusqueda = "DESCONOCIDO";
    }
    
    private static String crearMensajePorId(String proveedorId) {
        return String.format("Proveedor con ID '%s' no encontrado en el sistema", proveedorId);
    }
    
    private static String crearMensajePorCriterio(String criterio, String valor) {
        return String.format("Proveedor no encontrado usando criterio '%s' con valor '%s'", criterio, valor);
    }
    
    // Getters
    public String getProveedorId() {
        return proveedorId;
    }
    
    public String getCriterioBusqueda() {
        return criterioBusqueda;
    }
    
    /**
     * Indica si la búsqueda fue por ID.
     */
    public boolean esBusquedaPorId() {
        return "ID".equals(criterioBusqueda);
    }
    
    /**
     * Indica si la búsqueda fue por nombre.
     */
    public boolean esBusquedaPorNombre() {
        return "NOMBRE".equals(criterioBusqueda);
    }
}
