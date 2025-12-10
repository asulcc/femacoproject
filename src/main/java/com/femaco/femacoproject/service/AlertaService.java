package com.femaco.femacoproject.service;

import com.femaco.femacoproject.model.Producto;
import java.util.List;
import java.util.Map;

public interface AlertaService {
    // ===== ALERTAS DE STOCK =====
    
    // Verificar y generar alertas de stock bajo
    List<Producto> verificarAlertasStockBajo();
    
    // Verificar y generar alertas de stock crítico
    List<Producto> verificarAlertasStockCritico();
    
    // Verificar productos que pronto alcanzarán stock mínimo
    List<Producto> verificarAlertasStockProximoMinimo();
    
    // Verificar productos sin movimientos en período prolongado
    List<Producto> verificarProductosInactivos();
    
    // ===== ALERTAS DE PROVEEDORES =====
    
    // Verificar proveedores sin productos asociados
    List<String> verificarProveedoresSinProductos();
    
    // Verificar productos sin proveedor asignado
    List<Producto> verificarProductosSinProveedor();
    
    // ===== GESTIÓN DE ALERTAS =====
    
    // Obtener todas las alertas activas
    List<String> obtenerAlertasActivas();
    
    // Marcar alerta como resuelta
    boolean resolverAlerta(String idAlerta);
    
    // Obtener estadísticas de alertas
    Map<String, Integer> obtenerEstadisticasAlertas();
    
    // Configurar umbrales para alertas
    boolean configurarUmbralAlerta(String tipoAlerta, int umbral);
    
    // ===== NOTIFICACIONES =====
    
    // Enviar notificaciones de alertas pendientes
    void enviarNotificacionesAlertas();
    
    // Verificar si hay alertas pendientes
    boolean hayAlertasPendientes();
    
    // Obtener cantidad de alertas por tipo
    int contarAlertasPorTipo(String tipoAlerta);
}
