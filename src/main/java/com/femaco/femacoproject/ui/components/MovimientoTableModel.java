package com.femaco.femacoproject.ui.components;

import com.femaco.femacoproject.model.MovimientoInventario;
import com.femaco.femacoproject.model.enums.TipoMovimiento;
import javax.swing.table.AbstractTableModel;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ArrayList;

public class MovimientoTableModel extends AbstractTableModel {
    private final String[] columnNames = {
        "ID", "Producto", "Tipo", "Cantidad", "Motivo", "Fecha", "Usuario", "Referencia"
    };
    
    private final Class[] columnClasses = {
        String.class, String.class, String.class, Integer.class, 
        String.class, String.class, String.class, String.class
    };
    
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    
    private List<MovimientoInventario> movimientos;
    
    public MovimientoTableModel() {
        this.movimientos = new ArrayList<>();
    }
    
    public MovimientoTableModel(List<MovimientoInventario> movimientos) {
        this.movimientos = new ArrayList<>(movimientos);
    }
    
    @Override
    public int getRowCount() {
        return movimientos.size();
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
        if (rowIndex < 0 || rowIndex >= movimientos.size()) {
            return null;
        }
        
        MovimientoInventario movimiento = movimientos.get(rowIndex);
        
        switch (columnIndex) {
            case 0: return movimiento.getId();
            case 1: return movimiento.getProductoNombre() != null ? 
                          movimiento.getProductoNombre() : movimiento.getProductoId();
            case 2: return movimiento.getTipo().getDescripcion();
            case 3: return movimiento.getCantidad();
            case 4: return movimiento.getMotivo();
            case 5: return dateFormat.format(movimiento.getFecha());
            case 6: return movimiento.getUsuarioId();
            case 7: return movimiento.getReferencia();
            default: return null;
        }
    }
    
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }
    
    public MovimientoInventario getMovimientoAt(int rowIndex) {
        if (rowIndex < 0 || rowIndex >= movimientos.size()) {
            return null;
        }
        return movimientos.get(rowIndex);
    }
    
    public void setMovimientos(List<MovimientoInventario> movimientos) {
        this.movimientos = new ArrayList<>(movimientos);
        fireTableDataChanged();
    }
    
    public void addMovimiento(MovimientoInventario movimiento) {
        this.movimientos.add(0, movimiento); // Agregar al inicio para mostrar los más recientes primero
        fireTableRowsInserted(0, 0);
    }
    
    public void removeMovimiento(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < movimientos.size()) {
            movimientos.remove(rowIndex);
            fireTableRowsDeleted(rowIndex, rowIndex);
        }
    }
    
    public void clear() {
        int oldSize = movimientos.size();
        movimientos.clear();
        if (oldSize > 0) {
            fireTableRowsDeleted(0, oldSize - 1);
        }
    }
    
    public List<MovimientoInventario> getMovimientos() {
        return new ArrayList<>(movimientos);
    }
    
    // Filtra movimientos por tipo
    public void filtrarPorTipo(TipoMovimiento tipo) {
        List<MovimientoInventario> filtrados = new ArrayList<>();
        for (MovimientoInventario movimiento : this.movimientos) {
            if (movimiento.getTipo() == tipo) {
                filtrados.add(movimiento);
            }
        }
        setMovimientos(filtrados);
    }
    
    // Filtra movimientos por producto
    public void filtrarPorProducto(String productoId) {
        List<MovimientoInventario> filtrados = new ArrayList<>();
        for (MovimientoInventario movimiento : this.movimientos) {
            if (movimiento.getProductoId().equals(productoId)) {
                filtrados.add(movimiento);
            }
        }
        setMovimientos(filtrados);
    }
}
