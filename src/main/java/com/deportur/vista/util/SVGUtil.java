package com.deportur.vista.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import javax.swing.ImageIcon;

/**
 * Utilidad para trabajar con archivos SVG
 */
public class SVGUtil {
    
    private static final String BASE_PATH = "src/main/resources";
    
    /**
     * Crea o reemplaza un archivo SVG en la ubicación especificada
     * 
     * @param relativePath Ruta relativa donde se guardará el archivo
     * @param svgContent Contenido SVG a guardar
     * @return true si la operación fue exitosa
     */
    public static boolean saveSVG(String relativePath, String svgContent) {
        // Asegurar que el directorio exista
        File file = new File(BASE_PATH + relativePath);
        File parent = file.getParentFile();
        
        if (!parent.exists()) {
            parent.mkdirs();
        }
        
        try (OutputStream out = new FileOutputStream(file)) {
            out.write(svgContent.getBytes(StandardCharsets.UTF_8));
            return true;
        } catch (IOException e) {
            System.err.println("Error al guardar SVG en " + relativePath + ": " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Genera un icono simple basado en el nombre del recurso
     * 
     * @param name Nombre del recurso
     * @param width Ancho del icono
     * @param height Alto del icono
     * @return ImageIcon generado
     */
    public static ImageIcon createPlaceholderIcon(String name, int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        
        // Configurar para dibujo de alta calidad
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        
        // Determinar color basado en el nombre
        Color color;
        if (name.contains("add") || name.contains("secondary")) {
            color = UIConstants.SECONDARY_COLOR;
        } else if (name.contains("delete") || name.contains("warning") || name.contains("logout")) {
            color = UIConstants.WARNING_COLOR;
        } else {
            color = UIConstants.PRIMARY_COLOR;
        }
        
        // Dibujar un fondo redondeado y semitransparente
        g2d.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 30));
        g2d.fillRoundRect(2, 2, width - 4, height - 4, 8, 8);
        
        // Dibujar el borde
        g2d.setColor(color);
        g2d.drawRoundRect(2, 2, width - 4, height - 4, 8, 8);
        
        // Extraer la primera letra para usarla como inicial
        String letter;
        if (name.contains("/")) {
            String[] parts = name.split("/");
            String filename = parts[parts.length - 1];
            letter = filename.substring(0, 1).toUpperCase();
        } else {
            letter = name.substring(0, 1).toUpperCase();
        }
        
        // Dibujar la letra en el centro
        g2d.setColor(color.darker());
        g2d.setFont(g2d.getFont().deriveFont(12f));
        
        java.awt.FontMetrics metrics = g2d.getFontMetrics();
        int x = (width - metrics.stringWidth(letter)) / 2;
        int y = ((height - metrics.getHeight()) / 2) + metrics.getAscent();
        
        g2d.drawString(letter, x, y);
        
        g2d.dispose();
        return new ImageIcon(image);
    }
}