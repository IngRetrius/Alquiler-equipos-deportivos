package com.deportur.vista.util;

import java.awt.Color;
import java.awt.Font;
import javax.swing.ImageIcon;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

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
    public static final String ICON_PATH = "/com/deportur/resources/icons/";
    public static final ImageIcon APP_ICON = new ImageIcon(UIConstants.class.getResource(ICON_PATH + "app_icon.png"));
    public static final ImageIcon LOGIN_ICON = new ImageIcon(UIConstants.class.getResource(ICON_PATH + "login.png"));
    public static final ImageIcon USER_ICON = new ImageIcon(UIConstants.class.getResource(ICON_PATH + "user.png"));
    public static final ImageIcon ADD_ICON = new ImageIcon(UIConstants.class.getResource(ICON_PATH + "add.png"));
    public static final ImageIcon EDIT_ICON = new ImageIcon(UIConstants.class.getResource(ICON_PATH + "edit.png"));
    public static final ImageIcon DELETE_ICON = new ImageIcon(UIConstants.class.getResource(ICON_PATH + "delete.png"));
    public static final ImageIcon REFRESH_ICON = new ImageIcon(UIConstants.class.getResource(ICON_PATH + "refresh.png"));
    public static final ImageIcon SEARCH_ICON = new ImageIcon(UIConstants.class.getResource(ICON_PATH + "search.png"));
    public static final ImageIcon INVENTORY_ICON = new ImageIcon(UIConstants.class.getResource(ICON_PATH + "inventory.png"));
    public static final ImageIcon RESERVATION_ICON = new ImageIcon(UIConstants.class.getResource(ICON_PATH + "reservation.png"));
    public static final ImageIcon CLIENT_ICON = new ImageIcon(UIConstants.class.getResource(ICON_PATH + "client.png"));
    public static final ImageIcon DESTINATION_ICON = new ImageIcon(UIConstants.class.getResource(ICON_PATH + "destination.png"));
    public static final ImageIcon TYPE_ICON = new ImageIcon(UIConstants.class.getResource(ICON_PATH + "type.png"));
    public static final ImageIcon SETTINGS_ICON = new ImageIcon(UIConstants.class.getResource(ICON_PATH + "settings.png"));
    public static final ImageIcon INFO_ICON = new ImageIcon(UIConstants.class.getResource(ICON_PATH + "info.png"));
    public static final ImageIcon LOGOUT_ICON = new ImageIcon(UIConstants.class.getResource(ICON_PATH + "logout.png"));
    public static final ImageIcon CARD_VIEW_ICON = new ImageIcon(UIConstants.class.getResource(ICON_PATH + "card_view.png"));
    public static final ImageIcon LIST_VIEW_ICON = new ImageIcon(UIConstants.class.getResource(ICON_PATH + "list_view.png"));
    
    // Imágenes
    public static final String IMAGE_PATH = "/com/deportur/resources/images/";
    public static final ImageIcon DEFAULT_EQUIPMENT_IMAGE = new ImageIcon(UIConstants.class.getResource(IMAGE_PATH + "default_equipment.png"));
    public static final ImageIcon DEFAULT_PROFILE_IMAGE = new ImageIcon(UIConstants.class.getResource(IMAGE_PATH + "default_profile.png"));
    public static final ImageIcon LOGIN_BACKGROUND = new ImageIcon(UIConstants.class.getResource(IMAGE_PATH + "login_background.jpg"));
    
    // Texto para tooltips
    public static final String ADD_TOOLTIP = "Agregar nuevo registro";
    public static final String EDIT_TOOLTIP = "Modificar registro seleccionado";
    public static final String DELETE_TOOLTIP = "Eliminar registro seleccionado";
    public static final String REFRESH_TOOLTIP = "Refrescar datos";
    public static final String SEARCH_TOOLTIP = "Buscar registros";
}