package com.femaco.femacoproject.exception;

/**
 * Excepción lanzada cuando un producto no existe en el inventario.
 * Se utiliza en operaciones de búsqueda, actualización o eliminación de productos.
 */
public class ProductoNoEncontradoException extends InventarioException {
    private final String productoId;
    private final String criterioBusqueda;
    
    /**
     * Constructor con ID del producto.
     */
    public ProductoNoEncontradoException(String productoId) {
        super(crearMensajePorId(productoId),
              "PRODUCTO_NO_ENCONTRADO", 
              "GESTION_PRODUCTOS", 
              "BUSCAR_PRODUCTO");
        this.productoId = productoId;
        this.criterioBusqueda = "ID";
    }
    
    /**
     * Constructor con criterio de búsqueda personalizado.
     */
    public ProductoNoEncontradoException(String criterioBusqueda, String valor) {
        super(crearMensajePorCriterio(criterioBusqueda, valor),
              "PRODUCTO_NO_ENCONTRADO", 
              "GESTION_PRODUCTOS", 
              "BUSCAR_PRODUCTO");
        this.productoId = valor;
        this.criterioBusqueda = criterioBusqueda;
    }
    
    /**
     * Constructor con causa.
     */
    public ProductoNoEncontradoException(String mensaje, Throwable causa) {
        super(mensaje, "PRODUCTO_NO_ENCONTRADO", "GESTION_PRODUCTOS", "BUSCAR_PRODUCTO", causa);
        this.productoId = "DESCONOCIDO";
        this.criterioBusqueda = "DESCONOCIDO";
    }
    
    private static String crearMensajePorId(String productoId) {
        return String.format("Producto con ID '%s' no encontrado en el inventario", productoId);
    }
    
    private static String crearMensajePorCriterio(String criterio, String valor) {
        return String.format("Producto no encontrado usando criterio '%s' con valor '%s'", criterio, valor);
    }
    
    // Getters
    public String getProductoId() {
        return productoId;
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
