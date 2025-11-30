package com.femaco.femacoproject.exception;

/**
 * Excepción base para todas las excepciones del sistema de inventario.
 * Proporciona una base común para el manejo de errores en la aplicación.
 */
public class InventarioException extends Exception {
    private final String codigoError;
    private final String modulo;
    private final String accion;
    
    /**
     * Constructor básico con mensaje de error.
     */
    public InventarioException(String mensaje) {
        super(mensaje);
        this.codigoError = "ERROR_GENERICO";
        this.modulo = "SISTEMA";
        this.accion = "OPERACION_DESCONOCIDA";
    }
    
    /**
     * Constructor con mensaje y causa.
     */
    public InventarioException(String mensaje, Throwable causa) {
        super(mensaje, causa);
        this.codigoError = "ERROR_GENERICO";
        this.modulo = "SISTEMA";
        this.accion = "OPERACION_DESCONOCIDA";
    }
    
    /**
     * Constructor completo con todos los detalles.
     */
    public InventarioException(String mensaje, String codigoError, String modulo, String accion) {
        super(mensaje);
        this.codigoError = codigoError;
        this.modulo = modulo;
        this.accion = accion;
    }
    
    /**
     * Constructor completo con causa.
     */
    public InventarioException(String mensaje, String codigoError, String modulo, String accion, Throwable causa) {
        super(mensaje, causa);
        this.codigoError = codigoError;
        this.modulo = modulo;
        this.accion = accion;
    }
    
    // Getters
    public String getCodigoError() {
        return codigoError;
    }
    
    public String getModulo() {
        return modulo;
    }
    
    public String getAccion() {
        return accion;
    }
    
    /**
     * Obtiene un mensaje detallado del error.
     */
    public String getMensajeDetallado() {
        return String.format("[%s] %s - Módulo: %s - Acción: %s", 
                           codigoError, getMessage(), modulo, accion);
    }
    
    @Override
    public String toString() {
        return getMensajeDetallado();
    }
}
