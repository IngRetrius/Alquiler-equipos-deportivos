package com.deportur.vista.util;

import javax.swing.ImageIcon;
import java.awt.Image;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Clase para gestionar la cache de imágenes y optimizar el rendimiento
 */
public class ImageCache {
    
    private static final Map<String, ImageIcon> imageCache = new HashMap<>();
    private static final String EQUIPMENT_IMAGES_PATH = "/com/deportur/resources/images/equipos/";
    private static final String DESTINATION_IMAGES_PATH = "/com/deportur/resources/images/destinos/";
    private static final String CLIENT_IMAGES_PATH = "/com/deportur/resources/images/clientes/";
    
    /**
     * Obtiene una imagen de equipo, primero buscando en la cache
     * 
     * @param equipmentId ID del equipo
     * @return ImageIcon con la imagen del equipo o una imagen por defecto
     */
    public static ImageIcon getEquipmentImage(int equipmentId) {
        String key = "equipment_" + equipmentId;
        if (imageCache.containsKey(key)) {
            return imageCache.get(key);
        }
        
        // Primero intentar SVG
        String svgPath = EQUIPMENT_IMAGES_PATH + equipmentId + ".svg";
        URL svgUrl = ImageCache.class.getResource(svgPath);
        if (svgUrl != null) {
            try {
                ImageIcon icon = SVGLoader.loadSVG(svgPath, 200, 150);
                imageCache.put(key, icon);
                return icon;
            } catch (Exception e) {
                System.err.println("Error cargando SVG: " + e.getMessage());
            }
        }
        
        // Si no hay SVG, intentar con JPG
        String jpgPath = EQUIPMENT_IMAGES_PATH + equipmentId + ".jpg";
        URL jpgUrl = ImageCache.class.getResource(jpgPath);
        if (jpgUrl != null) {
            ImageIcon icon = new ImageIcon(jpgUrl);
            imageCache.put(key, icon);
            return icon;
        }
        
        // Si no existe la imagen, retornar la imagen por defecto
        if (!imageCache.containsKey("default_equipment")) {
            imageCache.put("default_equipment", UIConstants.DEFAULT_EQUIPMENT_IMAGE);
        }
        
        return imageCache.get("default_equipment");
    }
    
    /**
     * Obtiene una imagen de destino turístico
     * 
     * @param destinationId ID del destino turístico
     * @return ImageIcon con la imagen del destino o una imagen por defecto
     */
    public static ImageIcon getDestinationImage(int destinationId) {
        String key = "destination_" + destinationId;
        if (imageCache.containsKey(key)) {
            return imageCache.get(key);
        }
        
        // Primero intentar SVG
        String svgPath = DESTINATION_IMAGES_PATH + destinationId + ".svg";
        URL svgUrl = ImageCache.class.getResource(svgPath);
        if (svgUrl != null) {
            try {
                ImageIcon icon = SVGLoader.loadSVG(svgPath, 300, 200);
                imageCache.put(key, icon);
                return icon;
            } catch (Exception e) {
                System.err.println("Error cargando SVG: " + e.getMessage());
            }
        }
        
        // Si no hay SVG, intentar con JPG
        String jpgPath = DESTINATION_IMAGES_PATH + destinationId + ".jpg";
        URL jpgUrl = ImageCache.class.getResource(jpgPath);
        if (jpgUrl != null) {
            ImageIcon icon = new ImageIcon(jpgUrl);
            imageCache.put(key, icon);
            return icon;
        }
        
        // Si no existe la imagen, retornar la imagen por defecto
        if (!imageCache.containsKey("default_destination")) {
            ImageIcon defaultIcon = new ImageIcon(ImageCache.class.getResource("/com/deportur/resources/images/default_destination.png"));
            // Verificar si la imagen se cargó correctamente sin usar MediaTracker
            if (defaultIcon.getImage() == null) {
                defaultIcon = SVGUtil.createPlaceholderIcon("destination", 200, 150);
            }
            imageCache.put("default_destination", defaultIcon);
        }
        
        return imageCache.get("default_destination");
    }
    
    /**
     * Obtiene una imagen de cliente/perfil
     * 
     * @param clientId ID del cliente
     * @return ImageIcon con la imagen del cliente o una imagen por defecto
     */
    public static ImageIcon getClientImage(int clientId) {
        String key = "client_" + clientId;
        if (imageCache.containsKey(key)) {
            return imageCache.get(key);
        }
        
        // Primero intentar SVG
        String svgPath = CLIENT_IMAGES_PATH + clientId + ".svg";
        URL svgUrl = ImageCache.class.getResource(svgPath);
        if (svgUrl != null) {
            try {
                ImageIcon icon = SVGLoader.loadSVG(svgPath, 64, 64);
                imageCache.put(key, icon);
                return icon;
            } catch (Exception e) {
                System.err.println("Error cargando SVG: " + e.getMessage());
            }
        }
        
        // Si no hay SVG, intentar con JPG
        String jpgPath = CLIENT_IMAGES_PATH + clientId + ".jpg";
        URL jpgUrl = ImageCache.class.getResource(jpgPath);
        if (jpgUrl != null) {
            ImageIcon icon = new ImageIcon(jpgUrl);
            imageCache.put(key, icon);
            return icon;
        }
        
        // Si no existe la imagen, retornar la imagen por defecto
        return UIConstants.DEFAULT_PROFILE_IMAGE;
    }
    
    /**
     * Redimensiona una imagen manteniendo su proporción
     * 
     * @param originalIcon ImageIcon original
     * @param width Ancho deseado
     * @param height Alto deseado
     * @return ImageIcon redimensionado
     */
    public static ImageIcon resizeImage(ImageIcon originalIcon, int width, int height) {
        Image originalImage = originalIcon.getImage();
        
        // Calcular dimensiones manteniendo la proporción
        double widthRatio = (double) width / originalImage.getWidth(null);
        double heightRatio = (double) height / originalImage.getHeight(null);
        double ratio = Math.min(widthRatio, heightRatio);
        
        int newWidth = (int) (originalImage.getWidth(null) * ratio);
        int newHeight = (int) (originalImage.getHeight(null) * ratio);
        
        // Crear y retornar la nueva imagen
        Image resizedImage = originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        return new ImageIcon(resizedImage);
    }
    
    /**
     * Agrega una imagen a la caché
     * 
     * @param key Clave para identificar la imagen
     * @param image ImageIcon a almacenar
     */
    public static void addToCache(String key, ImageIcon image) {
        imageCache.put(key, image);
    }
    
    /**
     * Limpia la caché de imágenes
     */
    public static void clearCache() {
        imageCache.clear();
    }
}