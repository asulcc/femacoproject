package com.femaco.femacoproject.ui.components;

import javax.swing.*;
import java.awt.*;

public class StatusPanel extends JPanel {
    private JLabel iconLabel;
    private JLabel titleLabel;
    private JLabel valueLabel;
    private JLabel subtitleLabel;
    
    public StatusPanel(String title, String value, String subtitle, Color color) {
        initComponents();
        setTitle(title);
        setValue(value);
        setSubtitle(subtitle);
        setColor(color);
        setupLayout();
    }
    
    public StatusPanel(String title, String value, Color color) {
        this(title, value, "", color);
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 5));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        // Icono (puede ser personalizado)
        iconLabel = new JLabel();
        iconLabel.setPreferredSize(new Dimension(40, 40));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Panel de texto
        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 2));
        textPanel.setBackground(Color.WHITE);
        
        titleLabel = new JLabel();
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        titleLabel.setForeground(Color.GRAY);
        
        valueLabel = new JLabel();
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        
        subtitleLabel = new JLabel();
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        subtitleLabel.setForeground(Color.GRAY);
        
        textPanel.add(titleLabel);
        textPanel.add(valueLabel);
        
        add(iconLabel, BorderLayout.WEST);
        add(textPanel, BorderLayout.CENTER);
        add(subtitleLabel, BorderLayout.SOUTH);
    }
    
    private void setupLayout() {
        revalidate();
        repaint();
    }
    
    // Getters y Setters
    public void setTitle(String title) {
        titleLabel.setText(title);
    }
    
    public void setValue(String value) {
        valueLabel.setText(value);
    }
    
    public void setSubtitle(String subtitle) {
        subtitleLabel.setText(subtitle);
        subtitleLabel.setVisible(subtitle != null && !subtitle.isEmpty());
    }
    
    public void setColor(Color color) {
        valueLabel.setForeground(color);
        // Puedes agregar un icono con el color si lo deseas
    }
    
    public void setIcon(Icon icon) {
        iconLabel.setIcon(icon);
    }
    
    public String getTitle() {
        return titleLabel.getText();
    }
    
    public String getValue() {
        return valueLabel.getText();
    }
    
    public String getSubtitle() {
        return subtitleLabel.getText();
    }
    
    // Métodos de utilidad para estilos predefinidos
    public static StatusPanel createInfoPanel(String title, String value, String subtitle) {
        return new StatusPanel(title, value, subtitle, new Color(23, 162, 184));
    }
    
    public static StatusPanel createSuccessPanel(String title, String value, String subtitle) {
        return new StatusPanel(title, value, subtitle, new Color(40, 167, 69));
    }
    
    public static StatusPanel createWarningPanel(String title, String value, String subtitle) {
        return new StatusPanel(title, value, subtitle, new Color(255, 193, 7));
    }
    
    public static StatusPanel createDangerPanel(String title, String value, String subtitle) {
        return new StatusPanel(title, value, subtitle, new Color(220, 53, 69));
    }
    
    public static StatusPanel createPrimaryPanel(String title, String value, String subtitle) {
        return new StatusPanel(title, value, subtitle, new Color(0, 102, 204));
    }
    
    /**
     * Actualiza el valor con formato numérico
     */
    public void setValue(int value) {
        setValue(String.valueOf(value));
    }
    
    public void setValue(double value) {
        setValue(String.format("%.2f", value));
    }
    
    /**
     * Agrega un tooltip a todo el panel
     */
    public void setToolTip(String tooltip) {
        setToolTipText(tooltip);
        titleLabel.setToolTipText(tooltip);
        valueLabel.setToolTipText(tooltip);
        subtitleLabel.setToolTipText(tooltip);
    }
}
