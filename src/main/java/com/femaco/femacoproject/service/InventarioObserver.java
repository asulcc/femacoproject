package com.femaco.femacoproject.service;

import com.femaco.femacoproject.model.Producto;

/**
 * Interfaz Observer para notificar cambios en el inventario
 * Patrón Observer para notificaciones en tiempo real
 */
public interface InventarioObserver {
    
    /**
     * Método llamado cuando ocurre un cambio en el inventario
     * @param producto El producto afectado por el cambio
     * @param tipoEvento Tipo de evento: PRODUCTO_CREADO, PRODUCTO_ACTUALIZADO, 
     *                   PRODUCTO_ELIMINADO, ENTRADA_REGISTRADA, SALIDA_REGISTRADA, etc.
     */
    void onInventarioCambiado(Producto producto, String tipoEvento);
    
    /**
     * Método llamado cuando se actualiza el stock de un producto
     * @param producto Producto con stock actualizado
     * @param stockAnterior Valor del stock antes del cambio
     * @param stockNuevo Valor del stock después del cambio
     */
    default void onStockActualizado(Producto producto, int stockAnterior, int stockNuevo) {
        // Implementación por defecto vacía
    }
    
    /**
     * Método llamado cuando se genera una alerta de stock
     * @param producto Producto que generó la alerta
     * @param tipoAlerta Tipo de alerta: STOCK_BAJO, STOCK_CRITICO, etc.
     */
    default void onAlertaGenerada(Producto producto, String tipoAlerta) {
        // Implementación por defecto vacía
    }
}
