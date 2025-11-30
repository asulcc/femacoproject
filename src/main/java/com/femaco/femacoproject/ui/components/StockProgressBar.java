package com.femaco.femacoproject.ui.components;

import javax.swing.*;
import java.awt.*;

public class StockProgressBar extends JProgressBar {
    private int stockActual;
    private int stockMinimo;
    private int stockMaximo;
    private String textoPersonalizado;
    
    public StockProgressBar() {
        super(0, 100);
        setStockActual(0);
        setStockMinimo(0);
        setStockMaximo(100);
        setStringPainted(true);
        setFont(new Font("Segoe UI", Font.PLAIN, 11));
    }
    
    public StockProgressBar(int stockActual, int stockMinimo, int stockMaximo) {
        this();
        setStockActual(stockActual);
        setStockMinimo(stockMinimo);
        setStockMaximo(stockMaximo);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        // Determinar color basado en el nivel de stock
        updateAppearance();
        super.paintComponent(g);
    }
    
    private void updateAppearance() {
        if (stockMaximo <= 0) {
            setValue(0);
            setString("N/A");
            setForeground(Color.GRAY);
            return;
        }
        
        double porcentaje = (double) stockActual / stockMaximo * 100;
        setValue((int) porcentaje);
        
        // Configurar texto y color basado en el estado del stock
        if (textoPersonalizado != null) {
            setString(textoPersonalizado);
        } else {
            setString(String.format("%d / %d (%.1f%%)", stockActual, stockMaximo, porcentaje));
        }
        
        // Colores basados en el nivel de stock respecto al mínimo
        if (stockActual == 0) {
            setForeground(new Color(220, 53, 69)); // Rojo - Stock crítico
        } else if (stockActual <= stockMinimo) {
            setForeground(new Color(255, 193, 7)); // Amarillo - Stock bajo
        } else if (stockActual <= stockMinimo * 2) {
            setForeground(new Color(23, 162, 184)); // Azul claro - Stock medio
        } else {
            setForeground(new Color(40, 167, 69)); // Verde - Stock bueno
        }
    }
    
    // Getters y Setters
    public int getStockActual() {
        return stockActual;
    }
    
    public void setStockActual(int stockActual) {
        this.stockActual = Math.max(0, stockActual);
        updateAppearance();
        repaint();
    }
    
    public int getStockMinimo() {
        return stockMinimo;
    }
    
    public void setStockMinimo(int stockMinimo) {
        this.stockMinimo = Math.max(0, stockMinimo);
        updateAppearance();
        repaint();
    }
    
    public int getStockMaximo() {
        return stockMaximo;
    }
    
    public void setStockMaximo(int stockMaximo) {
        this.stockMaximo = Math.max(1, stockMaximo);
        updateAppearance();
        repaint();
    }
    
    public String getTextoPersonalizado() {
        return textoPersonalizado;
    }
    
    public void setTextoPersonalizado(String textoPersonalizado) {
        this.textoPersonalizado = textoPersonalizado;
        updateAppearance();
        repaint();
    }
    
    public void setValoresStock(int actual, int minimo, int maximo) {
        this.stockActual = Math.max(0, actual);
        this.stockMinimo = Math.max(0, minimo);
        this.stockMaximo = Math.max(1, maximo);
        updateAppearance();
        repaint();
    }
    
    /**
     * Obtiene el estado del stock como texto
     */
    public String getEstadoStock() {
        if (stockActual == 0) {
            return "CRÍTICO";
        } else if (stockActual <= stockMinimo) {
            return "BAJO";
        } else if (stockActual <= stockMinimo * 1.5) {
            return "MEDIO";
        } else {
            return "NORMAL";
        }
    }
    
    /**
     * Verifica si el stock está en nivel crítico
     */
    public boolean isStockCritico() {
        return stockActual == 0;
    }
    
    /**
     * Verifica si el stock está bajo el mínimo
     */
    public boolean isStockBajo() {
        return stockActual > 0 && stockActual <= stockMinimo;
    }
    
    /**
     * Obtiene el porcentaje de stock respecto al máximo
     */
    public double getPorcentajeStock() {
        if (stockMaximo <= 0) return 0;
        return (double) stockActual / stockMaximo * 100;
    }
    
    /**
     * Obtiene el porcentaje de stock respecto al mínimo
     */
    public double getPorcentajeSobreMinimo() {
        if (stockMinimo <= 0) return Double.POSITIVE_INFINITY;
        return (double) stockActual / stockMinimo * 100;
    }
}
