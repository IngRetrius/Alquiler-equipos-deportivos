package com.deportur.vista.util;

import javax.swing.ImageIcon;
import java.awt.Image;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Clase para gestionar la cache de imágenes y optimizar el rendimiento
 */
public class ImageCache {
    
    private static final Map<String, ImageIcon> imageCache = new HashMap<>();
    private static final String DEFAULT_IMAGES_PATH = "resources/images/";
    private static final String EQUIPMENT_IMAGES_PATH = DEFAULT_IMAGES_PATH + "equipos/";
    private static final String DESTINATION_IMAGES_PATH = DEFAULT_IMAGES_PATH + "destinos/";
    private static final String CLIENT_IMAGES_PATH = DEFAULT_IMAGES_PATH + "clientes/";
    
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
        
        // Intentar cargar la imagen desde el sistema de archivos
        File imageFile = new File(EQUIPMENT_IMAGES_PATH + equipmentId + ".jpg");
        if (imageFile.exists()) {
            ImageIcon icon = new ImageIcon(imageFile.getAbsolutePath());
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
        
        // Intentar cargar la imagen desde el sistema de archivos
        File imageFile = new File(DESTINATION_IMAGES_PATH + destinationId + ".jpg");
        if (imageFile.exists()) {
            ImageIcon icon = new ImageIcon(imageFile.getAbsolutePath());
            imageCache.put(key, icon);
            return icon;
        }
        
        // Si no existe la imagen, retornar una imagen por defecto para destinos
        if (!imageCache.containsKey("default_destination")) {
            ImageIcon defaultIcon = new ImageIcon(DEFAULT_IMAGES_PATH + "default_destination.png");
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
        
        // Intentar cargar la imagen desde el sistema de archivos
        File imageFile = new File(CLIENT_IMAGES_PATH + clientId + ".jpg");
        if (imageFile.exists()) {
            ImageIcon icon = new ImageIcon(imageFile.getAbsolutePath());
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