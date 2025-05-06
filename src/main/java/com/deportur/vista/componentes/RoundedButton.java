package com.deportur.vista.componentes;

import com.deportur.vista.util.UIConstants;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.border.EmptyBorder;

/**
 * Botón personalizado con bordes redondeados y efectos visuales
 */
public class RoundedButton extends JButton {
    
    private Color backgroundColor;
    private Color pressedColor;
    private Color hoverColor;
    private boolean isHover = false;
    private boolean isPressed = false;
    
    /**
     * Constructor para botón con texto
     * 
     * @param text Texto del botón
     * @param backgroundColor Color de fondo
     */
    public RoundedButton(String text, Color backgroundColor) {
        super(text);
        initialize(backgroundColor, null);
    }
    
    /**
     * Constructor para botón con texto e icono
     * 
     * @param text Texto del botón
     * @param icon Icono del botón
     * @param backgroundColor Color de fondo
     */
    public RoundedButton(String text, ImageIcon icon, Color backgroundColor) {
        super(text, icon);
        initialize(backgroundColor, icon);
    }
    
    /**
     * Constructor para botón solo con icono
     * 
     * @param icon Icono del botón
     * @param backgroundColor Color de fondo
     */
    public RoundedButton(ImageIcon icon, Color backgroundColor) {
        super(icon);
        initialize(backgroundColor, icon);
    }
    
    private void initialize(Color backgroundColor, ImageIcon icon) {
        this.backgroundColor = backgroundColor;
        this.hoverColor = adjustColor(backgroundColor, 0.1f); // Ligeramente más claro
        this.pressedColor = adjustColor(backgroundColor, -0.1f); // Ligeramente más oscuro
        
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(false);
        setForeground(Color.WHITE);
        setFont(UIConstants.NORMAL_FONT);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Añadir padding
        if (icon != null && getText() != null && !getText().isEmpty()) {
            setBorder(new EmptyBorder(8, 15, 8, 15));
        } else if (icon != null) {
            setBorder(new EmptyBorder(8, 8, 8, 8));
        } else {
            setBorder(new EmptyBorder(8, 20, 8, 20));
        }
        
        // Agregar listeners para efectos visuales
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHover = true;
                repaint();
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                isHover = false;
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
        
        // Determinar el color basado en el estado del botón
        if (isPressed) {
            g2.setColor(pressedColor);
        } else if (isHover) {
            g2.setColor(hoverColor);
        } else {
            g2.setColor(backgroundColor);
        }
        
        // Dibujar el fondo redondeado
        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), UIConstants.BORDER_RADIUS, UIConstants.BORDER_RADIUS));
        
        // Dibujar efecto de sombra sutil cuando está hover
        if (isHover && !isPressed) {
            g2.setColor(new Color(0, 0, 0, 40));
            g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, UIConstants.BORDER_RADIUS, UIConstants.BORDER_RADIUS));
        }
        
        g2.dispose();
        
        super.paintComponent(g);
    }
    
    /**
     * Ajusta el brillo de un color
     * 
     * @param color Color original
     * @param factor Factor de ajuste (positivo para aclarar, negativo para oscurecer)
     * @return Color ajustado
     */
    private Color adjustColor(Color color, float factor) {
        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        hsb[2] = Math.max(0, Math.min(1, hsb[2] + factor));
        return Color.getHSBColor(hsb[0], hsb[1], hsb[2]);
    }
}