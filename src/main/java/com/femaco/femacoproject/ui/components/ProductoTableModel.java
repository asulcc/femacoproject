package com.femaco.femacoproject.ui.components;

import com.femaco.femacoproject.model.Producto;
import com.femaco.femacoproject.model.enums.EstadoProducto;
import javax.swing.table.AbstractTableModel;
import java.util.List;
import java.util.ArrayList;

public class ProductoTableModel extends AbstractTableModel {
    private final String[] columnNames = {
        "ID", "Nombre", "Categoría", "Stock Actual", "Stock Mínimo", 
        "Precio", "Ubicación", "Estado", "Proveedor"
    };
    
    private final Class[] columnClasses = {
        String.class, String.class, String.class, Integer.class, 
        Integer.class, Double.class, String.class, String.class, String.class
    };
    
    private List<Producto> productos;
    
    public ProductoTableModel() {
        this.productos = new ArrayList<>();
    }
    
    public ProductoTableModel(List<Producto> productos) {
        this.productos = new ArrayList<>(productos);
    }
    
    @Override
    public int getRowCount() {
        return productos.size();
    }
    
    @Override
    public int getColumnCount() {
        return columnNames.length;
    }
    
    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }
    
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return columnClasses[columnIndex];
    }
    
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex < 0 || rowIndex >= productos.size()) {
            return null;
        }
        
        Producto producto = productos.get(rowIndex);
        
        switch (columnIndex) {
            case 0: return producto.getId();
            case 1: return producto.getNombre();
            case 2: return producto.getCategoria().getDescripcion();
            case 3: return producto.getStockActual();
            case 4: return producto.getStockMinimo();
            case 5: return producto.getPrecio();
            case 6: return producto.getUbicacion();
            case 7: return producto.getEstado().getDescripcion();
            case 8: return producto.getProveedorId();
            default: return null;
        }
    }
    
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false; // Tabla no editable
    }
    
    public Producto getProductoAt(int rowIndex) {
        if (rowIndex < 0 || rowIndex >= productos.size()) {
            return null;
        }
        return productos.get(rowIndex);
    }
    
    public void setProductos(List<Producto> productos) {
        this.productos = new ArrayList<>(productos);
        fireTableDataChanged();
    }
    
    public void addProducto(Producto producto) {
        this.productos.add(producto);
        fireTableRowsInserted(productos.size() - 1, productos.size() - 1);
    }
    
    public void updateProducto(int rowIndex, Producto producto) {
        if (rowIndex >= 0 && rowIndex < productos.size()) {
            productos.set(rowIndex, producto);
            fireTableRowsUpdated(rowIndex, rowIndex);
        }
    }
    
    public void removeProducto(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < productos.size()) {
            productos.remove(rowIndex);
            fireTableRowsDeleted(rowIndex, rowIndex);
        }
    }
    
    public void clear() {
        int oldSize = productos.size();
        productos.clear();
        if (oldSize > 0) {
            fireTableRowsDeleted(0, oldSize - 1);
        }
    }
    
    public List<Producto> getProductos() {
        return new ArrayList<>(productos);
    }
    
    // Filtra los productos por estado
    public void filtrarPorEstado(EstadoProducto estado) {
        List<Producto> filtrados = new ArrayList<>();
        for (Producto producto : this.productos) {
            if (producto.getEstado() == estado) {
                filtrados.add(producto);
            }
        }
        setProductos(filtrados);
    }
    
    // Busca productos que contengan el texto en nombre o ID
    public void buscar(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return;
        }
        
        texto = texto.toLowerCase().trim();
        List<Producto> resultados = new ArrayList<>();
        
        for (Producto producto : this.productos) {
            if (producto.getNombre().toLowerCase().contains(texto) ||
                producto.getId().toLowerCase().contains(texto)) {
                resultados.add(producto);
            }
        }
        
        setProductos(resultados);
    }
}
