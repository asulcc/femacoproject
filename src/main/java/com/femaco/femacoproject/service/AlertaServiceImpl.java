package com.femaco.femacoproject.service;


import com.femaco.femacoproject.dao.ProductoDAO;
import com.femaco.femacoproject.dao.ProveedorDAO;
import com.femaco.femacoproject.model.Producto;
import com.femaco.femacoproject.model.enums.EstadoProducto;
import com.femaco.femacoproject.util.ListaEnlazada;
import com.femaco.femacoproject.util.Logger;
import java.util.*;
import java.util.stream.Collectors;

public class AlertaServiceImpl implements AlertaService {
    private final ProductoDAO productoDAO;
    private final ProveedorDAO proveedorDAO;
    private final Logger logger;
    private final Map<String, Integer> umbralesAlertas;
    private final ListaEnlazada<String> alertasActivas;
    
    public AlertaServiceImpl(ProductoDAO productoDAO, ProveedorDAO proveedorDAO) {
        this.productoDAO = productoDAO;
        this.proveedorDAO = proveedorDAO;
        this.logger = Logger.getInstance();
        this.umbralesAlertas = new HashMap<>();
        this.alertasActivas = new ListaEnlazada<>();
        inicializarUmbrales();
    }
    
    private void inicializarUmbrales() {
        umbralesAlertas.put("STOCK_BAJO", 3); // Días para alerta stock próximo a mínimo
        umbralesAlertas.put("INACTIVIDAD", 90); // Días sin movimientos
    }
    
    // ===== IMPLEMENTACIÓN ALERTAS DE STOCK =====
    
    @Override
    public List<Producto> verificarAlertasStockBajo() {
        logger.info("Verificando alertas de stock bajo");
        return productoDAO.obtenerStockBajo();
    }
    
    @Override
    public List<Producto> verificarAlertasStockCritico() {
        logger.info("Verificando alertas de stock crítico");
        return productoDAO.obtenerPorEstado(EstadoProducto.STOCK_CRITICO);
    }
    
    @Override
    public List<Producto> verificarAlertasStockProximoMinimo() {
        logger.info("Verificando alertas de stock próximo a mínimo");
        List<Producto> productos = productoDAO.obtenerTodos().stream()
                .filter(p -> p.getEstado() != EstadoProducto.INACTIVO)
                .filter(p -> p.getStockActual() > p.getStockMinimo()) // Aún no está bajo mínimo
                .filter(p -> p.getStockActual() <= p.getStockMinimo() * 1.5) // Está cerca del mínimo
                .collect(Collectors.toList());
        
        // Registrar alertas
        for (Producto producto : productos) {
            String alerta = String.format(
                "ALERTA_STOCK_PROXIMO: Producto %s está próximo a stock mínimo. Stock actual: %d, Mínimo: %d",
                producto.getNombre(), producto.getStockActual(), producto.getStockMinimo()
            );
            registrarAlerta(alerta);
        }
        
        return productos;
    }
    
    @Override
    public List<Producto> verificarProductosInactivos() {
        logger.info("Verificando productos inactivos");
        // En producción, se consultaría la base de datos por productos sin movimientos recientes
        // Implementación simplificada:
        return new ArrayList<>();
    }
    
    // ===== IMPLEMENTACIÓN ALERTAS DE PROVEEDORES =====
    
    @Override
    public List<String> verificarProveedoresSinProductos() {
        logger.info("Verificando proveedores sin productos");
        List<String> proveedoresSinProductos = new ArrayList<>();
        
        List<com.femaco.femacoproject.model.Proveedor> proveedores = proveedorDAO.obtenerActivos();
        for (com.femaco.femacoproject.model.Proveedor proveedor : proveedores) {
            List<Producto> productosProveedor = productoDAO.obtenerPorProveedor(proveedor.getId());
            if (productosProveedor.isEmpty()) {
                proveedoresSinProductos.add(proveedor.getNombre());
                registrarAlerta("PROVEEDOR_SIN_PRODUCTOS: " + proveedor.getNombre());
            }
        }
        
        return proveedoresSinProductos;
    }
    
    @Override
    public List<Producto> verificarProductosSinProveedor() {
        logger.info("Verificando productos sin proveedor");
        List<Producto> productosSinProveedor = productoDAO.obtenerTodos().stream()
                .filter(p -> p.getEstado() != EstadoProducto.INACTIVO)
                .filter(p -> p.getProveedorId() == null || p.getProveedorId().trim().isEmpty())
                .collect(Collectors.toList());
        
        for (Producto producto : productosSinProveedor) {
            registrarAlerta("PRODUCTO_SIN_PROVEEDOR: " + producto.getNombre());
        }
        
        return productosSinProveedor;
    }
    
    // ===== IMPLEMENTACIÓN GESTIÓN DE ALERTAS =====
    
    @Override
    public List<String> obtenerAlertasActivas() {
        List<String> alertas = new ArrayList<>();
        for (String alerta : alertasActivas) {
            alertas.add(alerta);
        }
        return alertas;
    }
    
    @Override
    public boolean resolverAlerta(String idAlerta) {
        logger.info("Resolviendo alerta: " + idAlerta);
        return alertasActivas.eliminar(idAlerta);
    }
    
    @Override
    public Map<String, Integer> obtenerEstadisticasAlertas() {
        Map<String, Integer> estadisticas = new HashMap<>();
        
        estadisticas.put("STOCK_BAJO", verificarAlertasStockBajo().size());
        estadisticas.put("STOCK_CRITICO", verificarAlertasStockCritico().size());
        estadisticas.put("STOCK_PROXIMO_MINIMO", verificarAlertasStockProximoMinimo().size());
        estadisticas.put("PROVEEDORES_SIN_PRODUCTOS", verificarProveedoresSinProductos().size());
        estadisticas.put("PRODUCTOS_SIN_PROVEEDOR", verificarProductosSinProveedor().size());
        estadisticas.put("TOTAL_ALERTAS_ACTIVAS", alertasActivas.tamaño());
        
        return estadisticas;
    }
    
    @Override
    public boolean configurarUmbralAlerta(String tipoAlerta, int umbral) {
        if (umbral < 0) return false;
        
        umbralesAlertas.put(tipoAlerta, umbral);
        logger.info("Umbral de alerta configurado: " + tipoAlerta + " = " + umbral);
        return true;
    }
    
    // ===== IMPLEMENTACIÓN NOTIFICACIONES =====
    
    @Override
    public void enviarNotificacionesAlertas() {
        logger.info("Enviando notificaciones de alertas");
        
        // Verificar todas las alertas
        verificarAlertasStockBajo();
        verificarAlertasStockCritico();
        verificarAlertasStockProximoMinimo();
        verificarProveedoresSinProductos();
        verificarProductosSinProveedor();
        
        // En producción, aquí se enviarían notificaciones por email, etc.
        if (hayAlertasPendientes()) {
            logger.warning("Hay " + alertasActivas.tamaño() + " alertas pendientes que requieren atención");
        }
    }
    
    @Override
    public boolean hayAlertasPendientes() {
        return !alertasActivas.estaVacia();
    }
    
    @Override
    public int contarAlertasPorTipo(String tipoAlerta) {
        int count = 0;
        for (String alerta : alertasActivas) {
            if (alerta.contains(tipoAlerta)) {
                count++;
            }
        }
        return count;
    }
    
    // ===== MÉTODOS PRIVADOS =====
    
    private void registrarAlerta(String mensajeAlerta) {
        String alertaId = "ALERTA_" + System.currentTimeMillis() + "_" + 
                         mensajeAlerta.hashCode();
        String alertaCompleta = alertaId + "|" + mensajeAlerta;
        
        if (!alertasActivas.contiene(alertaCompleta)) {
            alertasActivas.agregar(alertaCompleta);
            logger.warning("Alerta registrada: " + mensajeAlerta);
        }
    }
}
