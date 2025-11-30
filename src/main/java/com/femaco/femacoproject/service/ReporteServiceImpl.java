package com.femaco.femacoproject.service;

import com.femaco.femacoproject.dao.MovimientoDAO;
import com.femaco.femacoproject.dao.ProductoDAO;
import com.femaco.femacoproject.model.MovimientoInventario;
import com.femaco.femacoproject.model.Producto;
import com.femaco.femacoproject.model.enums.CategoriaProducto;
import com.femaco.femacoproject.model.enums.TipoMovimiento;
import com.femaco.femacoproject.util.Logger;

import java.util.*;
import java.util.stream.Collectors;

public class ReporteServiceImpl implements ReporteService {
    private final ProductoDAO productoDAO;
    private final MovimientoDAO movimientoDAO;
    private final Logger logger;
    
    public ReporteServiceImpl(ProductoDAO productoDAO, MovimientoDAO movimientoDAO) {
        this.productoDAO = productoDAO;
        this.movimientoDAO = movimientoDAO;
        this.logger = Logger.getInstance();
    }
    
    // ===== IMPLEMENTACIÓN REPORTES DE INVENTARIO =====
    
    @Override
    public List<Producto> generarReporteStockActual() {
        logger.info("Generando reporte de stock actual");
        return productoDAO.obtenerTodos().stream()
                .filter(p -> p.getEstado() != com.femaco.femacoproject.model.enums.EstadoProducto.INACTIVO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Producto> generarReporteStockBajo() {
        logger.info("Generando reporte de stock bajo");
        return productoDAO.obtenerStockBajo();
    }
    
    @Override
    public List<Producto> generarReporteStockCritico() {
        logger.info("Generando reporte de stock crítico");
        return productoDAO.obtenerPorEstado(com.femaco.femacoproject.model.enums.EstadoProducto.STOCK_CRITICO);
    }
    
    @Override
    public Map<CategoriaProducto, List<Producto>> generarReportePorCategoria() {
        logger.info("Generando reporte por categoría");
        Map<CategoriaProducto, List<Producto>> reporte = new HashMap<>();
        
        for (CategoriaProducto categoria : CategoriaProducto.values()) {
            List<Producto> productos = productoDAO.obtenerPorCategoria(categoria).stream()
                    .filter(p -> p.getEstado() != com.femaco.femacoproject.model.enums.EstadoProducto.INACTIVO)
                    .collect(Collectors.toList());
            reporte.put(categoria, productos);
        }
        
        return reporte;
    }
    
    @Override
    public Map<CategoriaProducto, Double> generarReporteValorInventarioPorCategoria() {
        logger.info("Generando reporte de valor de inventario por categoría");
        Map<CategoriaProducto, Double> reporte = new HashMap<>();
        
        for (CategoriaProducto categoria : CategoriaProducto.values()) {
            double valorTotal = productoDAO.obtenerPorCategoria(categoria).stream()
                    .filter(p -> p.getEstado() != com.femaco.femacoproject.model.enums.EstadoProducto.INACTIVO)
                    .mapToDouble(Producto::calcularValorTotalStock)
                    .sum();
            reporte.put(categoria, valorTotal);
        }
        
        return reporte;
    }
    
    // ===== IMPLEMENTACIÓN REPORTES DE MOVIMIENTOS =====
    
    @Override
    public List<MovimientoInventario> generarReporteMovimientos(Date fechaInicio, Date fechaFin) {
        logger.info("Generando reporte de movimientos desde " + fechaInicio + " hasta " + fechaFin);
        return movimientoDAO.obtenerPorFecha(fechaInicio, fechaFin);
    }
    
    @Override
    public List<MovimientoInventario> generarReporteEntradas(Date fechaInicio, Date fechaFin) {
        logger.info("Generando reporte de entradas");
        return movimientoDAO.obtenerPorTipo(TipoMovimiento.ENTRADA).stream()
                .filter(m -> !m.getFecha().before(fechaInicio) && !m.getFecha().after(fechaFin))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<MovimientoInventario> generarReporteSalidas(Date fechaInicio, Date fechaFin) {
        logger.info("Generando reporte de salidas");
        return movimientoDAO.obtenerPorTipo(TipoMovimiento.SALIDA).stream()
                .filter(m -> !m.getFecha().before(fechaInicio) && !m.getFecha().after(fechaFin))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<MovimientoInventario> generarReporteMovimientosProducto(String productoId, Date fechaInicio, Date fechaFin) {
        logger.info("Generando reporte de movimientos para producto: " + productoId);
        return movimientoDAO.obtenerPorProducto(productoId).stream()
                .filter(m -> !m.getFecha().before(fechaInicio) && !m.getFecha().after(fechaFin))
                .collect(Collectors.toList());
    }
    
    // ===== IMPLEMENTACIÓN REPORTES ESTADÍSTICOS =====
    
    @Override
    public Map<TipoMovimiento, Long> generarEstadisticasMovimientos(Date fechaInicio, Date fechaFin) {
        logger.info("Generando estadísticas de movimientos");
        List<MovimientoInventario> movimientos = movimientoDAO.obtenerPorFecha(fechaInicio, fechaFin);
        
        return movimientos.stream()
                .collect(Collectors.groupingBy(MovimientoInventario::getTipo, Collectors.counting()));
    }
    
    @Override
    public Map<Producto, Integer> generarReporteProductosMasVendidos(Date fechaInicio, Date fechaFin, int limite) {
        logger.info("Generando reporte de productos más vendidos");
        List<MovimientoInventario> salidas = movimientoDAO.obtenerPorTipo(TipoMovimiento.SALIDA).stream()
                .filter(m -> !m.getFecha().before(fechaInicio) && !m.getFecha().after(fechaFin))
                .collect(Collectors.toList());
        
        Map<String, Integer> ventasPorProducto = new HashMap<>();
        for (MovimientoInventario movimiento : salidas) {
            ventasPorProducto.merge(movimiento.getProductoId(), movimiento.getCantidad(), Integer::sum);
        }
        
        // Convertir a mapa de Producto -> Cantidad
        Map<Producto, Integer> resultado = new HashMap<>();
        for (Map.Entry<String, Integer> entry : ventasPorProducto.entrySet()) {
            productoDAO.obtenerPorId(entry.getKey()).ifPresent(producto -> {
                resultado.put(producto, entry.getValue());
            });
        }
        
        return resultado.entrySet().stream()
                .sorted(Map.Entry.<Producto, Integer>comparingByValue().reversed())
                .limit(limite)
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    Map.Entry::getValue,
                    (e1, e2) -> e1,
                    LinkedHashMap::new
                ));
    }
    
    @Override
    public Map<Producto, Integer> generarReporteProductosMenosMovidos(Date fechaInicio, Date fechaFin, int limite) {
        logger.info("Generando reporte de productos menos movidos");
        List<MovimientoInventario> movimientos = movimientoDAO.obtenerPorFecha(fechaInicio, fechaFin);
        
        Map<String, Integer> movimientosPorProducto = new HashMap<>();
        for (MovimientoInventario movimiento : movimientos) {
            movimientosPorProducto.merge(movimiento.getProductoId(), movimiento.getCantidad(), Integer::sum);
        }
        
        // Incluir productos sin movimientos
        List<Producto> todosProductos = productoDAO.obtenerTodos();
        for (Producto producto : todosProductos) {
            movimientosPorProducto.putIfAbsent(producto.getId(), 0);
        }
        
        // Convertir a mapa de Producto -> Cantidad
        Map<Producto, Integer> resultado = new HashMap<>();
        for (Map.Entry<String, Integer> entry : movimientosPorProducto.entrySet()) {
            productoDAO.obtenerPorId(entry.getKey()).ifPresent(producto -> {
                resultado.put(producto, entry.getValue());
            });
        }
        
        return resultado.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .limit(limite)
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    Map.Entry::getValue,
                    (e1, e2) -> e1,
                    LinkedHashMap::new
                ));
    }
    
    @Override
    public Map<Producto, Double> generarReporteRotacionInventario() {
        logger.info("Generando reporte de rotación de inventario");
        // Implementación simplificada - en producción calcular con fórmula real
        Map<Producto, Double> rotacion = new HashMap<>();
        
        List<Producto> productos = productoDAO.obtenerTodos();
        for (Producto producto : productos) {
            // Rotación estimada basada en movimientos recientes
            int totalMovimientos = movimientoDAO.obtenerTotalMovimientosPorProducto(
                producto.getId(), TipoMovimiento.SALIDA
            );
            double rotacionEstimada = producto.getStockActual() > 0 ? 
                (double) totalMovimientos / producto.getStockActual() : 0;
            
            rotacion.put(producto, rotacionEstimada);
        }
        
        return rotacion;
    }
    
    // ===== IMPLEMENTACIÓN REPORTES FINANCIEROS =====
    
    @Override
    public double generarReporteValorTotalInventario() {
        logger.info("Generando reporte de valor total del inventario");
        return productoDAO.obtenerTodos().stream()
                .filter(p -> p.getEstado() != com.femaco.femacoproject.model.enums.EstadoProducto.INACTIVO)
                .mapToDouble(Producto::calcularValorTotalStock)
                .sum();
    }
    
    @Override
    public double generarReporteIngresosVentas(Date fechaInicio, Date fechaFin) {
        logger.info("Generando reporte de ingresos por ventas");
        List<MovimientoInventario> salidas = movimientoDAO.obtenerPorTipo(TipoMovimiento.SALIDA).stream()
                .filter(m -> !m.getFecha().before(fechaInicio) && !m.getFecha().after(fechaFin))
                .collect(Collectors.toList());
        
        double ingresos = 0;
        for (MovimientoInventario movimiento : salidas) {
            Optional<Producto> productoOpt = productoDAO.obtenerPorId(movimiento.getProductoId());
            if (productoOpt.isPresent()) {
                Producto producto = productoOpt.get();
                ingresos += movimiento.getCantidad() * producto.getPrecio();
            }
        }
        
        return ingresos;
    }
    
    @Override
    public double generarReporteCostosCompras(Date fechaInicio, Date fechaFin) {
        logger.info("Generando reporte de costos por compras");
        List<MovimientoInventario> entradas = movimientoDAO.obtenerPorTipo(TipoMovimiento.ENTRADA).stream()
                .filter(m -> !m.getFecha().before(fechaInicio) && !m.getFecha().after(fechaFin))
                .collect(Collectors.toList());
        
        double costos = 0;
        for (MovimientoInventario movimiento : entradas) {
            Optional<Producto> productoOpt = productoDAO.obtenerPorId(movimiento.getProductoId());
            if (productoOpt.isPresent()) {
                Producto producto = productoOpt.get();
                costos += movimiento.getCantidad() * producto.getPrecio();
            }
        }
        
        return costos;
    }
    
    // ===== IMPLEMENTACIÓN EXPORTACIÓN DE REPORTES =====
    
    @Override
    public boolean exportarReporteCSV(List<?> datos, String nombreArchivo) {
        logger.info("Exportando reporte a CSV: " + nombreArchivo);
        // Implementación de exportación CSV
        // En producción, usar biblioteca como OpenCSV
        try {
            // Código de exportación simplificado
            return true;
        } catch (Exception e) {
            logger.error("Error al exportar reporte CSV: " + nombreArchivo, e);
            return false;
        }
    }
    
    @Override
    public boolean exportarReportePDF(List<?> datos, String nombreArchivo, String titulo) {
        logger.info("Exportando reporte a PDF: " + nombreArchivo);
        // Implementación de exportación PDF
        // En producción, usar biblioteca como iText o Apache PDFBox
        try {
            // Código de exportación simplificado
            return true;
        } catch (Exception e) {
            logger.error("Error al exportar reporte PDF: " + nombreArchivo, e);
            return false;
        }
    }
    
    @Override
    public Map<String, Object> generarResumenEjecutivo() {
        logger.info("Generando resumen ejecutivo");
        Map<String, Object> resumen = new HashMap<>();
        
        // Métricas clave
        resumen.put("totalProductos", productoDAO.contarTotalProductos());
        resumen.put("valorTotalInventario", generarReporteValorTotalInventario());
        resumen.put("productosStockBajo", productoDAO.obtenerStockBajo().size());
        resumen.put("productosStockCritico", productoDAO.obtenerPorEstado(com.femaco.femacoproject.model.enums.EstadoProducto.STOCK_CRITICO).size());
        
        // Movimientos del mes
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);
        Date fechaInicio = cal.getTime();
        Date fechaFin = new Date();
        
        List<MovimientoInventario> movimientosMes = movimientoDAO.obtenerPorFecha(fechaInicio, fechaFin);
        resumen.put("totalMovimientosMes", movimientosMes.size());
        resumen.put("entradasMes", movimientosMes.stream()
            .filter(m -> m.getTipo() == TipoMovimiento.ENTRADA).count());
        resumen.put("salidasMes", movimientosMes.stream()
            .filter(m -> m.getTipo() == TipoMovimiento.SALIDA).count());
        
        return resumen;
    }
}
