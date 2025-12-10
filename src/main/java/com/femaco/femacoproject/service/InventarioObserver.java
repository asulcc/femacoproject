package com.femaco.femacoproject.service;

import com.femaco.femacoproject.model.Producto;

/**
 * Interfaz Observer para notificar cambios en el inventario
 * Patrón Observer para notificaciones en tiempo real
 */
public interface InventarioObserver {
    
    // Método llamado cuando ocurre un cambio en el inventario
    void onInventarioCambiado(Producto producto, String tipoEvento);
    
    // Método llamado cuando se actualiza el stock de un producto
    default void onStockActualizado(Producto producto, int stockAnterior, int stockNuevo) {
        // Implementación por defecto vacía
    }
    
    // Método llamado cuando se genera una alerta de stock
    default void onAlertaGenerada(Producto producto, String tipoAlerta) {
        // Implementación por defecto vacía
    }
}
