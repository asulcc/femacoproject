package com.femaco.femacoproject.service;

import com.femaco.femacoproject.model.MovimientoInventario;
import com.femaco.femacoproject.model.Producto;
import com.femaco.femacoproject.model.enums.CategoriaProducto;
import com.femaco.femacoproject.model.enums.TipoMovimiento;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ReporteService {
    // ===== REPORTES DE INVENTARIO =====
    
    // Generar reporte de stock actual
    List<Producto> generarReporteStockActual();
    
    // Generar reporte de productos con stock bajo
    List<Producto> generarReporteStockBajo();
    
    // Generar reporte de productos con stock crítico
    List<Producto> generarReporteStockCritico();
    
    // Generar reporte de productos por categoría
    Map<CategoriaProducto, List<Producto>> generarReportePorCategoria();
    
    // Generar reporte de valor de inventario por categoría
    Map<CategoriaProducto, Double> generarReporteValorInventarioPorCategoria();
    
    // ===== REPORTES DE MOVIMIENTOS =====
    
    // Generar reporte de movimientos por período
    List<MovimientoInventario> generarReporteMovimientos(Date fechaInicio, Date fechaFin);
    
    // Generar reporte de entradas por período
    List<MovimientoInventario> generarReporteEntradas(Date fechaInicio, Date fechaFin);
    
    // Generar reporte de salidas por período
    List<MovimientoInventario> generarReporteSalidas(Date fechaInicio, Date fechaFin);
    
    // Generar reporte de movimientos por producto
    List<MovimientoInventario> generarReporteMovimientosProducto(String productoId, Date fechaInicio, Date fechaFin);
    
    // ===== REPORTES ESTADÍSTICOS =====
    
    // Generar estadísticas de movimientos por tipo
    Map<TipoMovimiento, Long> generarEstadisticasMovimientos(Date fechaInicio, Date fechaFin);
    
    // Generar reporte de productos más vendidos
    Map<Producto, Integer> generarReporteProductosMasVendidos(Date fechaInicio, Date fechaFin, int limite);
    
    // Generar reporte de productos menos movidos
    Map<Producto, Integer> generarReporteProductosMenosMovidos(Date fechaInicio, Date fechaFin, int limite);
    
    // Generar reporte de rotación de inventario
    Map<Producto, Double> generarReporteRotacionInventario();
    
    // ===== REPORTES FINANCIEROS =====
    
    // Generar reporte de valor total del inventario
    double generarReporteValorTotalInventario();
    
    // Generar reporte de ingresos por ventas
    double generarReporteIngresosVentas(Date fechaInicio, Date fechaFin);
    
    // Generar reporte de costos por compras
    double generarReporteCostosCompras(Date fechaInicio, Date fechaFin);
    
    // ===== EXPORTACIÓN DE REPORTES =====
    
    // Exportar reporte a formato CSV
    boolean exportarReporteCSV(List<?> datos, String nombreArchivo);
    
    // Exportar reporte a formato PDF
    boolean exportarReportePDF(List<?> datos, String nombreArchivo, String titulo);
    
    // Generar resumen ejecutivo del inventario
    Map<String, Object> generarResumenEjecutivo();
}
