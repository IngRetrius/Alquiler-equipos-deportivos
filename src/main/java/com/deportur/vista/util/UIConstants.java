package com.deportur.vista.util;

import java.awt.Color;
import java.awt.Font;
import javax.swing.ImageIcon;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.Image;
import java.net.URL;

/**
 * Constantes y recursos visuales para unificar el aspecto de la aplicación
 */
public class UIConstants {
    
    // Colores principales
    public static final Color PRIMARY_COLOR = new Color(51, 122, 183);       // Azul principal
    public static final Color SECONDARY_COLOR = new Color(92, 184, 92);      // Verde secundario
    public static final Color ACCENT_COLOR = new Color(240, 173, 78);        // Naranja acento
    public static final Color WARNING_COLOR = new Color(217, 83, 79);        // Rojo advertencia
    public static final Color LIGHT_COLOR = new Color(245, 245, 245);        // Gris claro
    public static final Color DARK_COLOR = new Color(51, 51, 51);            // Gris oscuro
    public static final Color HOVER_COLOR = new Color(230, 240, 250);        // Color al pasar el mouse
    
    // Fondos
    public static final Color BACKGROUND_COLOR = new Color(248, 249, 250);   // Fondo general
    public static final Color PANEL_COLOR = Color.WHITE;                     // Fondo de paneles
    public static final Color HEADER_COLOR = new Color(236, 240, 245);       // Fondo de encabezados
    
    // Fuentes
    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font SUBTITLE_FONT = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font NORMAL_FONT = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font SMALL_FONT = new Font("Segoe UI", Font.PLAIN, 11);
    
    // Bordes
    public static final Border ROUNDED_BORDER = new LineBorder(new Color(220, 220, 220), 1, true);
    public static final Border PANEL_BORDER = new CompoundBorder(
            new LineBorder(new Color(220, 220, 220), 1, true),
            new EmptyBorder(10, 10, 10, 10));
    public static final Border FIELD_BORDER = new LineBorder(new Color(210, 210, 210), 1, true);
    
    // Tamaños
    public static final int BUTTON_HEIGHT = 30;
    public static final int FIELD_HEIGHT = 30;
    public static final int PADDING = 10;
    public static final int BORDER_RADIUS = 8;
    
    // Iconos (rutas a iconos desde recursos)
    private static final String ICON_PATH = "/com/deportur/resources/icons/";
    private static final String IMAGE_PATH = "/com/deportur/resources/images/";
    
    // Método mejorado para cargar iconos con fallback visual
    private static ImageIcon safeLoadIcon(String path) {
        return safeLoadIcon(path, 24, 24);
    }
    
    // Método mejorado para cargar iconos con fallback visual y tamaño personalizado
    private static ImageIcon safeLoadIcon(String path, int width, int height) {
        try {
            // Verificar si es un SVG
            if (path.toLowerCase().endsWith(".svg")) {
                return SVGLoader.loadSVG(path, width, height);
            } else {
                // Para otros formatos de imagen (PNG, JPG)
                URL resourceUrl = UIConstants.class.getResource(path);
                if (resourceUrl != null) {
                    ImageIcon icon = new ImageIcon(resourceUrl);
                    
                    // Redimensionar si es necesario
                    if (icon.getIconWidth() != width || icon.getIconHeight() != height) {
                        Image scaledImage = icon.getImage().getScaledInstance(
                            width, height, Image.SCALE_SMOOTH);
                        return new ImageIcon(scaledImage);
                    }
                    return icon;
                }
            }
        } catch (Exception e) {
            System.err.println("Error cargando icono " + path + ": " + e.getMessage());
        }
        
        // Fallback a icono placeholder
        return SVGUtil.createPlaceholderIcon(path, width, height);
    }
    
    // Iconos de la aplicación
    public static final ImageIcon APP_ICON = safeLoadIcon(ICON_PATH + "app_icon.svg");
    public static final ImageIcon LOGIN_ICON = safeLoadIcon(ICON_PATH + "login.svg");
    public static final ImageIcon USER_ICON = safeLoadIcon(ICON_PATH + "user.svg");
    public static final ImageIcon ADD_ICON = safeLoadIcon(ICON_PATH + "add.svg");
    public static final ImageIcon EDIT_ICON = safeLoadIcon(ICON_PATH + "edit.svg");
    public static final ImageIcon DELETE_ICON = safeLoadIcon(ICON_PATH + "delete.svg");
    public static final ImageIcon REFRESH_ICON = safeLoadIcon(ICON_PATH + "refresh.svg");
    public static final ImageIcon SEARCH_ICON = safeLoadIcon(ICON_PATH + "search.svg");
    public static final ImageIcon INVENTORY_ICON = safeLoadIcon(ICON_PATH + "inventory.svg");
    public static final ImageIcon RESERVATION_ICON = safeLoadIcon(ICON_PATH + "reservation.svg");
    public static final ImageIcon CLIENT_ICON = safeLoadIcon(ICON_PATH + "client.svg");
    public static final ImageIcon DESTINATION_ICON = safeLoadIcon(ICON_PATH + "destination.svg");
    public static final ImageIcon TYPE_ICON = safeLoadIcon(ICON_PATH + "type.svg");
    public static final ImageIcon SETTINGS_ICON = safeLoadIcon(ICON_PATH + "settings.svg");
    public static final ImageIcon INFO_ICON = safeLoadIcon(ICON_PATH + "info.svg");
    public static final ImageIcon LOGOUT_ICON = safeLoadIcon(ICON_PATH + "logout.svg");
    public static final ImageIcon CARD_VIEW_ICON = safeLoadIcon(ICON_PATH + "card_view.svg");
    public static final ImageIcon LIST_VIEW_ICON = safeLoadIcon(ICON_PATH + "list_view.svg");
    
    // Imágenes
    public static final ImageIcon DEFAULT_EQUIPMENT_IMAGE = safeLoadIcon(IMAGE_PATH + "default_equipment.svg", 64, 64);
    public static final ImageIcon DEFAULT_PROFILE_IMAGE = safeLoadIcon(IMAGE_PATH + "default_profile.svg", 64, 64);
    public static final ImageIcon LOGIN_BACKGROUND = safeLoadIcon(IMAGE_PATH + "login_background.png", 450, 600);
    
    // Texto para tooltips
    public static final String ADD_TOOLTIP = "Agregar nuevo registro";
    public static final String EDIT_TOOLTIP = "Modificar registro seleccionado";
    public static final String DELETE_TOOLTIP = "Eliminar registro seleccionado";
    public static final String REFRESH_TOOLTIP = "Refrescar datos";
    public static final String SEARCH_TOOLTIP = "Buscar registros";
}