package com.deportur.vista.componentes;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Componente visual para mostrar el estado de un elemento con código de colores
 */
public class StatusIndicator extends JPanel {
    
    private Color statusColor;
    private String statusText;
    private JLabel label;
    private boolean showDot = true;
    private int dotSize = 8;
    private int dotMargin = 5;
    
    /**
     * Constructor con texto y color
     * 
     * @param statusText Texto de estado
     * @param statusColor Color de estado
     */
    public StatusIndicator(String statusText, Color statusColor) {
        this.statusText = statusText;
        this.statusColor = statusColor;
        
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        
        // Crear componentes
        label = new JLabel(this.statusText);
        label.setForeground(this.statusColor);
        
        // Agregar componentes
        add(Box.createHorizontalStrut(dotSize + dotMargin));
        add(label);
        
        // Establecer tamaño preferido
        int preferredWidth = label.getPreferredSize().width + dotSize + dotMargin * 2;
        int preferredHeight = Math.max(label.getPreferredSize().height, dotSize);
        setPreferredSize(new Dimension(preferredWidth, preferredHeight));
    }
    
    /**
     * Constructor para crear un indicador de estado sin texto
     * 
     * @param statusColor Color del indicador
     */
    public StatusIndicator(Color statusColor) {
        this("", statusColor);
    }
    
    /**
     * Establece el texto de estado
     * 
     * @param text Nuevo texto
     */
    public void setStatusText(String text) {
        this.statusText = text;
        label.setText(this.statusText);
        
        // Actualizar tamaño preferido
        int preferredWidth = label.getPreferredSize().width + dotSize + dotMargin * 2;
        int preferredHeight = Math.max(label.getPreferredSize().height, dotSize);
        setPreferredSize(new Dimension(preferredWidth, preferredHeight));
        
        revalidate();
        repaint();
    }
    
    /**
     * Establece el color de estado
     * 
     * @param color Nuevo color
     */
    public void setStatusColor(Color color) {
        this.statusColor = color;
        label.setForeground(this.statusColor);
        repaint();
    }
    
    /**
     * Establece si se muestra el punto indicador
     * 
     * @param show true para mostrar, false para ocultar
     */
    public void setShowDot(boolean show) {
        this.showDot = show;
        repaint();
    }
    
    /**
     * Establece el tamaño del punto indicador
     * 
     * @param size Tamaño en píxeles
     */
    public void setDotSize(int size) {
        this.dotSize = size;
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (showDot) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Dibujar el punto indicador
            g2d.setColor(statusColor);
            int y = (getHeight() - dotSize) / 2;
            g2d.fillOval(0, y, dotSize, dotSize);
            
            g2d.dispose();
        }
    }
    
    /**
     * Método estático para crear un indicador según el estado común
     * 
     * @param estado Texto de estado
     * @return StatusIndicator configurado
     */
    public static StatusIndicator createForEstado(String estado) {
        Color color;
        
        switch (estado.toLowerCase()) {
            case "nuevo":
                color = new Color(0, 150, 0);
                break;
            case "bueno":
                color = new Color(34, 139, 34);
                break;
            case "regular":
                color = new Color(255, 165, 0);
                break;
            case "mantenimiento":
                color = new Color(255, 140, 0);
                break;
            case "fuera de servicio":
                color = new Color(220, 20, 60);
                break;
            case "pendiente":
                color = new Color(70, 130, 180);
                break;
            case "confirmada":
                color = new Color(0, 128, 0);
                break;
            case "en progreso":
                color = new Color(255, 165, 0);
                break;
            case "finalizada":
                color = new Color(100, 100, 100);
                break;
            case "cancelada":
                color = new Color(220, 20, 60);
                break;
            case "disponible":
                color = new Color(0, 128, 0);
                break;
            case "no disponible":
                color = new Color(128, 128, 128);
                break;
            default:
                color = Color.GRAY;
                break;
        }
        
        return new StatusIndicator(estado, color);
    }
    
    /**
     * Crea un indicador con fondo de color para mostrar un estado con mayor visibilidad
     */
    public static JPanel createPillIndicator(String statusText, Color statusColor) {
        // Panel principal con esquinas redondeadas y fondo de color
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Dibujar fondo con color más claro
                g2d.setColor(lighter(statusColor, 0.8f));
                g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
        
        // Etiqueta con el texto
        JLabel label = new JLabel(statusText);
        label.setForeground(darker(statusColor, 0.3f));
        label.setFont(new Font(label.getFont().getName(), Font.BOLD, label.getFont().getSize()));
        
        panel.add(label);
        
        return panel;
    }
    
    /**
     * Crea un color más claro
     */
    private static Color lighter(Color color, float factor) {
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        
        float[] hsb = Color.RGBtoHSB(r, g, b, null);
        hsb[1] = Math.max(0, hsb[1] - 0.3f); // Reducir saturación
        hsb[2] = Math.min(1.0f, hsb[2] + factor); // Aumentar brillo
        
        return Color.getHSBColor(hsb[0], hsb[1], hsb[2]);
    }
    
    /**
     * Crea un color más oscuro
     */
    private static Color darker(Color color, float factor) {
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        
        float[] hsb = Color.RGBtoHSB(r, g, b, null);
        hsb[2] = Math.max(0.0f, hsb[2] - factor); // Reducir brillo
        
        return Color.getHSBColor(hsb[0], hsb[1], hsb[2]);
    }
}