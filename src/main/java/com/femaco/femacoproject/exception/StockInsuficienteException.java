package com.femaco.femacoproject.exception;

public class StockInsuficienteException extends InventarioException {
    private final String productoId;
    private final int stockActual;
    private final int stockSolicitado;
    
    // Constructor básico con información del producto y stock.
    public StockInsuficienteException(String productoId, int stockActual, int stockSolicitado) {
        super(crearMensaje(productoId, stockActual, stockSolicitado),
              "STOCK_INSUFICIENTE", 
              "GESTION_INVENTARIO", 
              "REGISTRAR_SALIDA");
        this.productoId = productoId;
        this.stockActual = stockActual;
        this.stockSolicitado = stockSolicitado;
    }
    
    // Constructor con mensaje personalizado.
    public StockInsuficienteException(String mensaje) {
        super(mensaje, "STOCK_INSUFICIENTE", "GESTION_INVENTARIO", "REGISTRAR_SALIDA");
        this.productoId = "DESCONOCIDO";
        this.stockActual = 0;
        this.stockSolicitado = 0;
    }
    
    // Constructor con causa.
    public StockInsuficienteException(String mensaje, Throwable causa) {
        super(mensaje, "STOCK_INSUFICIENTE", "GESTION_INVENTARIO", "REGISTRAR_SALIDA", causa);
        this.productoId = "DESCONOCIDO";
        this.stockActual = 0;
        this.stockSolicitado = 0;
    }
    
    private static String crearMensaje(String productoId, int stockActual, int stockSolicitado) {
        return String.format("Stock insuficiente para el producto '%s'. Stock actual: %d, Stock solicitado: %d, Diferencia: %d",
                           productoId, stockActual, stockSolicitado, stockSolicitado - stockActual);
    }
    
    // Getters
    public String getProductoId() {
        return productoId;
    }
    
    public int getStockActual() {
        return stockActual;
    }
    
    public int getStockSolicitado() {
        return stockSolicitado;
    }
    
    public int getDiferencia() {
        return stockSolicitado - stockActual;
    }
    
    // Indica si el stock actual es cero.
    public boolean esStockCero() {
        return stockActual == 0;
    }
    
    // Obtiene el porcentaje de stock disponible respecto al solicitado.
    public double getPorcentajeDisponible() {
        if (stockSolicitado == 0) return 100.0;
        return (double) stockActual / stockSolicitado * 100;
    }
}
