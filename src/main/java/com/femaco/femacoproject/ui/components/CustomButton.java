package com.femaco.femacoproject.ui.components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CustomButton extends JButton {
    private Color backgroundColor;
    private Color hoverColor;
    private Color pressedColor;
    private Color textColor;
    private int borderRadius;
    private boolean isHovered = false;
    private boolean isPressed = false;
    
    public CustomButton(String text) {
        super(text);
        initDefaults();
        setupButton();
    }
    
    public CustomButton(String text, Color backgroundColor, Color textColor) {
        super(text);
        this.backgroundColor = backgroundColor;
        this.textColor = textColor;
        this.hoverColor = backgroundColor.brighter();
        this.pressedColor = backgroundColor.darker();
        setupButton();
    }
    
    private void initDefaults() {
        this.backgroundColor = new Color(0, 102, 204); // Azul
        this.hoverColor = new Color(0, 122, 255);
        this.pressedColor = new Color(0, 82, 164);
        this.textColor = Color.WHITE;
        this.borderRadius = 8;
    }
    
    private void setupButton() {
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setForeground(textColor);
        setBorder(new EmptyBorder(10, 20, 10, 20));
        setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                repaint();
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                isPressed = false;
                repaint();
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
                isPressed = true;
                repaint();
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                isPressed = false;
                repaint();
            }
        });
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        Color currentColor = backgroundColor;
        if (isPressed) {
            currentColor = pressedColor;
        } else if (isHovered) {
            currentColor = hoverColor;
        }
        
        // Fondo redondeado
        g2.setColor(currentColor);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), borderRadius, borderRadius);
        
        // Texto
        g2.setColor(getForeground());
        FontMetrics fm = g2.getFontMetrics();
        int textX = (getWidth() - fm.stringWidth(getText())) / 2;
        int textY = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
        g2.drawString(getText(), textX, textY);
        
        g2.dispose();
    }
    
    @Override
    public Dimension getPreferredSize() {
        Dimension size = super.getPreferredSize();
        return new Dimension(Math.max(size.width, 120), Math.max(size.height, 40));
    }
    
    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }
    
    @Override
    public Dimension getMaximumSize() {
        return getPreferredSize();
    }
    
    // Getters y Setters
    public Color getBackgroundColor() {
        return backgroundColor;
    }
    
    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
        this.hoverColor = backgroundColor.brighter();
        this.pressedColor = backgroundColor.darker();
        repaint();
    }
    
    public Color getHoverColor() {
        return hoverColor;
    }
    
    public void setHoverColor(Color hoverColor) {
        this.hoverColor = hoverColor;
        repaint();
    }
    
    public Color getPressedColor() {
        return pressedColor;
    }
    
    public void setPressedColor(Color pressedColor) {
        this.pressedColor = pressedColor;
        repaint();
    }
    
    public Color getTextColor() {
        return textColor;
    }
    
    public void setTextColor(Color textColor) {
        setForeground(textColor);
        this.textColor = textColor;
        repaint();
    }
    
    public int getBorderRadius() {
        return borderRadius;
    }
    
    public void setBorderRadius(int borderRadius) {
        this.borderRadius = borderRadius;
        repaint();
    }
    
    // Métodos de utilidad para estilos predefinidos
    public static CustomButton createPrimaryButton(String text) {
        return new CustomButton(text, new Color(0, 102, 204), Color.WHITE);
    }
    
    public static CustomButton createSuccessButton(String text) {
        return new CustomButton(text, new Color(40, 167, 69), Color.WHITE);
    }
    
    public static CustomButton createWarningButton(String text) {
        return new CustomButton(text, new Color(255, 193, 7), Color.BLACK);
    }
    
    public static CustomButton createDangerButton(String text) {
        return new CustomButton(text, new Color(220, 53, 69), Color.WHITE);
    }
    
    public static CustomButton createSecondaryButton(String text) {
        return new CustomButton(text, new Color(108, 117, 125), Color.WHITE);
    }
    public static CustomButton createInfoButton(String text) {
        return new CustomButton(text, new Color(25, 137, 255), Color.WHITE);
    }
    
    
}
