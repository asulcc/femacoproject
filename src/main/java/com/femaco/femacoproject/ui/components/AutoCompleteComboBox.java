package com.femaco.femacoproject.ui.components;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxEditor;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class AutoCompleteComboBox extends JComboBox<String> {
    private List<String> itemsCompletos;
    private boolean filtroActivo;
    
    public AutoCompleteComboBox() {
        super();
        initComponent();
    }
    
    public AutoCompleteComboBox(List<String> items) {
        super(items.toArray(new String[0]));
        this.itemsCompletos = new ArrayList<>(items);
        initComponent();
    }
    
    private void initComponent() {
        setEditable(true);
        setEditor(new AutoCompleteEditor());
        filtroActivo = false;
        
        // Configurar el editor
        JTextField textField = (JTextField) getEditor().getEditorComponent();
        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    return;
                }
                
                String texto = textField.getText();
                filtrarItems(texto);
            }
        });
    }
    
    private void filtrarItems(String filtro) {
        if (filtro == null || filtro.isEmpty()) {
            restaurarItemsCompletos();
            return;
        }
        
        List<String> itemsFiltrados = new ArrayList<>();
        for (String item : itemsCompletos) {
            if (item.toLowerCase().contains(filtro.toLowerCase())) {
                itemsFiltrados.add(item);
            }
        }
        
        actualizarItemsComboBox(itemsFiltrados);
        filtroActivo = true;
    }
    
    private void restaurarItemsCompletos() {
        if (filtroActivo) {
            actualizarItemsComboBox(itemsCompletos);
            filtroActivo = false;
        }
    }
    
    private void actualizarItemsComboBox(List<String> items) {
        setModel(new DefaultComboBoxModel<>(items.toArray(new String[0])));
        setSelectedItem(null);
        setPopupVisible(true);
    }
    
    public void setItems(List<String> items) {
        this.itemsCompletos = new ArrayList<>(items);
        restaurarItemsCompletos();
    }
    
    public List<String> getItems() {
        return new ArrayList<>(itemsCompletos);
    }
    
    public void addItem(String item) {
        if (!itemsCompletos.contains(item)) {
            itemsCompletos.add(item);
            restaurarItemsCompletos();
        }
    }
    
    public void removeItem(String item) {
        itemsCompletos.remove(item);
        restaurarItemsCompletos();
    }
    
    public void clear() {
        itemsCompletos.clear();
        restaurarItemsCompletos();
    }
    
    // Editor personalizado para el ComboBox
    private class AutoCompleteEditor extends BasicComboBoxEditor {
        private JTextField textField;
        
        public AutoCompleteEditor() {
            super();
            // Obtener la referencia al textField
            textField = (JTextField) getEditorComponent();
        }
        
        @Override
        public JTextField getEditorComponent() {
            if (textField == null) {
                textField = (JTextField) super.getEditorComponent();
                if (textField != null) {
                    // Configurar border SOLO cuando textField no es null
                    textField.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
                }
            }
            return textField;
        }

        @Override
        public void setItem(Object item) {
            // Asegurar que textField esté inicializado
            getEditorComponent();
            if (textField != null) {
                textField.setText(item != null ? item.toString() : "");
            }
        }

        @Override
        public Object getItem() {
            return textField != null ? textField.getText() : "";
        }
    }
}