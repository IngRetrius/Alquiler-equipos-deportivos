package com.deportur.vista.componentes;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

/**
 * Panel personalizado que muestra una imagen con varias opciones de visualización
 */
public class ImagePanel extends JPanel {
    
    private Image image;
    private Image scaledImage;
    private int cornerRadius = 0;
    private boolean maintainAspectRatio = true;
    private boolean roundedCorners = false;
    private boolean centered = true;
    private boolean border = false;
    private Color borderColor = Color.LIGHT_GRAY;
    private int borderWidth = 1;
    private ImageScaleMode scaleMode = ImageScaleMode.FIT;
    
    /**
     * Modos de escalado de imagen
     */
    public enum ImageScaleMode {
        FIT,        // Ajustar la imagen manteniendo proporción
        FILL,       // Llenar el espacio estirando si es necesario
        STRETCH,    // Estirar la imagen para llenar el espacio
        CENTER      // Centrar la imagen sin escalar
    }
    
    /**
     * Constructor sin imagen
     */
    public ImagePanel() {
        setOpaque(false);
        configurePanel();
    }
    
    /**
     * Constructor con imagen
     * 
     * @param image Imagen a mostrar
     */
    public ImagePanel(Image image) {
        this.image = image;
        setOpaque(false);
        configurePanel();
    }
    
    /**
     * Constructor con ImageIcon
     * 
     * @param icon ImageIcon a mostrar
     */
    public ImagePanel(ImageIcon icon) {
        this.image = icon.getImage();
        setOpaque(false);
        configurePanel();
    }
    
    private void configurePanel() {
        // Escuchar cambios de tamaño para reescalar la imagen
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateScaledImage();
                repaint();
            }
        });
    }
    
    /**
     * Establece la imagen a mostrar
     * 
     * @param image Nueva imagen
     */
    public void setImage(Image image) {
        this.image = image;
        updateScaledImage();
        repaint();
    }
    
    /**
     * Establece la imagen a mostrar desde un ImageIcon
     * 
     * @param icon Nuevo ImageIcon
     */
    public void setImage(ImageIcon icon) {
        if (icon != null) {
            this.image = icon.getImage();
            updateScaledImage();
            repaint();
        }
    }
    
    /**
     * Establece si se mantiene la relación de aspecto
     * 
     * @param maintain true para mantener la proporción, false para permitir distorsión
     */
    public void setMaintainAspectRatio(boolean maintain) {
        this.maintainAspectRatio = maintain;
        updateScaledImage();
        repaint();
    }
    
    /**
     * Establece si la imagen debe tener esquinas redondeadas
     * 
     * @param rounded true para esquinas redondeadas, false para esquinas normales
     */
    public void setRoundedCorners(boolean rounded) {
        this.roundedCorners = rounded;
        repaint();
    }
    
    /**
     * Establece el radio de las esquinas redondeadas
     * 
     * @param radius Radio en píxeles
     */
    public void setCornerRadius(int radius) {
        this.cornerRadius = radius;
        repaint();
    }
    
    /**
     * Establece si la imagen debe estar centrada
     * 
     * @param centered true para centrar, false para alinear arriba-izquierda
     */
    public void setCentered(boolean centered) {
        this.centered = centered;
        repaint();
    }
    
    /**
     * Establece si el panel debe tener un borde
     * 
     * @param border true para mostrar borde, false para ocultarlo
     */
    public void setBorder(boolean border) {
        this.border = border;
        repaint();
    }
    
    /**
     * Establece el color del borde
     * 
     * @param color Color del borde
     */
    public void setBorderColor(Color color) {
        this.borderColor = color;
        repaint();
    }
    
    /**
     * Establece el ancho del borde
     * 
     * @param width Ancho en píxeles
     */
    public void setBorderWidth(int width) {
        this.borderWidth = width;
        repaint();
    }
    
    /**
     * Establece el modo de escalado de imagen
     * 
     * @param mode Modo de escalado
     */
    public void setScaleMode(ImageScaleMode mode) {
        this.scaleMode = mode;
        updateScaledImage();
        repaint();
    }
    
    /**
     * Actualiza la imagen escalada según el tamaño actual del panel
     */
    private void updateScaledImage() {
        if (image == null || getWidth() <= 0 || getHeight() <= 0) {
            return;
        }
        
        int originalWidth = image.getWidth(this);
        int originalHeight = image.getHeight(this);
        
        if (originalWidth <= 0 || originalHeight <= 0) {
            return;
        }
        
        int targetWidth = getWidth();
        int targetHeight = getHeight();
        
        // Determinar el tamaño basado en el modo de escalado
        switch (scaleMode) {
            case FIT:
                if (maintainAspectRatio) {
                    double widthRatio = (double) targetWidth / originalWidth;
                    double heightRatio = (double) targetHeight / originalHeight;
                    double ratio = Math.min(widthRatio, heightRatio);
                    
                    targetWidth = (int) (originalWidth * ratio);
                    targetHeight = (int) (originalHeight * ratio);
                }
                break;
                
            case FILL:
                if (maintainAspectRatio) {
                    double widthRatio = (double) targetWidth / originalWidth;
                    double heightRatio = (double) targetHeight / originalHeight;
                    double ratio = Math.max(widthRatio, heightRatio);
                    
                    targetWidth = (int) (originalWidth * ratio);
                    targetHeight = (int) (originalHeight * ratio);
                }
                break;
                
            case CENTER:
                targetWidth = originalWidth;
                targetHeight = originalHeight;
                break;
                
            case STRETCH:
                // Ya está configurado para estirar a todo el panel
                break;
        }
        
        // Crear la imagen escalada
        scaledImage = image.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        
        int width = getWidth();
        int height = getHeight();
        
        // Dibujar fondo si es necesario
        if (isOpaque()) {
            g2d.setColor(getBackground());
            g2d.fillRect(0, 0, width, height);
        }
        
        if (scaledImage != null) {
            int x = 0;
            int y = 0;
            int imgWidth = scaledImage.getWidth(this);
            int imgHeight = scaledImage.getHeight(this);
            
            // Centrar la imagen si es necesario
            if (centered) {
                x = (width - imgWidth) / 2;
                y = (height - imgHeight) / 2;
            }
            
            // Dibujar la imagen con esquinas redondeadas si es necesario
            if (roundedCorners) {
                // Crear una imagen temporal para aplicar el recorte
                BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                Graphics2D bfg = bufferedImage.createGraphics();
                
                try {
                    bfg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    // Crear la forma redondeada
                    int radius = cornerRadius > 0 ? cornerRadius : Math.min(width, height) / 10;
                    RoundRectangle2D roundedRect = new RoundRectangle2D.Float(0, 0, width, height, radius, radius);
                    
                    // Recortar usando la forma redondeada
                    bfg.setClip(roundedRect);
                    
                    // Dibujar la imagen en el buffer
                    bfg.drawImage(scaledImage, x, y, null);
                    
                    // Dibujar el buffer en el componente
                    g2d.drawImage(bufferedImage, 0, 0, null);
                } finally {
                    bfg.dispose();
                }
            } else {
                // Dibujar la imagen normalmente
                g2d.drawImage(scaledImage, x, y, null);
            }
            
            // Dibujar borde si es necesario
            if (border) {
                g2d.setColor(borderColor);
                g2d.setStroke(new BasicStroke(borderWidth));
                
                if (roundedCorners) {
                    int radius = cornerRadius > 0 ? cornerRadius : Math.min(width, height) / 10;
                    g2d.draw(new RoundRectangle2D.Float(0, 0, width - 1, height - 1, radius, radius));
                } else {
                    g2d.drawRect(0, 0, width - 1, height - 1);
                }
            }
        }
        
        g2d.dispose();
    }
}