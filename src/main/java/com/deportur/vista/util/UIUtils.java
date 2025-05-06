package com.deportur.vista.util;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.text.SimpleDateFormat;

/**
 * Utilidades para la interfaz gráfica
 */
public class UIUtils {
    
    /**
     * Configura un look and feel moderno para toda la aplicación
     */
    public static void configureGlobalLookAndFeel() {
        try {
            // Intentar usar FlatLaf (requiere la dependencia en el pom.xml)
            // UIManager.setLookAndFeel(new FlatLightLaf());
            
            // Si FlatLaf no está disponible, usar el look and feel del sistema
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            // Configurar propiedades globales
            UIManager.put("Button.arc", UIConstants.BORDER_RADIUS);
            UIManager.put("Component.arc", UIConstants.BORDER_RADIUS);
            UIManager.put("ProgressBar.arc", UIConstants.BORDER_RADIUS);
            UIManager.put("TextComponent.arc", UIConstants.BORDER_RADIUS);
            
            UIManager.put("Button.margin", new Insets(6, 14, 6, 14));
            UIManager.put("Button.font", UIConstants.NORMAL_FONT);
            
            UIManager.put("Label.font", UIConstants.NORMAL_FONT);
            UIManager.put("TextField.font", UIConstants.NORMAL_FONT);
            UIManager.put("ComboBox.font", UIConstants.NORMAL_FONT);
            UIManager.put("Table.font", UIConstants.NORMAL_FONT);
            
            UIManager.put("Table.alternateRowColor", new Color(245, 246, 247));
            UIManager.put("Table.showVerticalLines", true);
            UIManager.put("Table.showHorizontalLines", true);
            UIManager.put("Table.gridColor", new Color(230, 230, 230));
            
            UIManager.put("TableHeader.background", UIConstants.HEADER_COLOR);
            UIManager.put("TableHeader.font", UIConstants.SUBTITLE_FONT);
            
            UIManager.put("TabbedPane.font", UIConstants.NORMAL_FONT);
            UIManager.put("TabbedPane.selectedForeground", UIConstants.PRIMARY_COLOR);
            
            UIManager.put("Panel.background", UIConstants.PANEL_COLOR);
            UIManager.put("ContentPane.background", UIConstants.BACKGROUND_COLOR);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Configura un JButton con estilo moderno
     */
    public static void styleButton(JButton button, Color background, Color foreground, String tooltip) {
        button.setBackground(background);
        button.setForeground(foreground);
        button.setFont(UIConstants.NORMAL_FONT);
        button.setFocusPainted(false);
        button.setBorderPainted(true);
        button.setContentAreaFilled(true);
        button.setOpaque(true);
        button.setToolTipText(tooltip);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new LineBorder(background.darker(), 1, true));
        
        // Efecto hover
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(background.brighter());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(background);
            }
        });
    }
    
    /**
     * Configura un JTextField con estilo moderno
     */
    public static void styleTextField(JTextField textField) {
        textField.setFont(UIConstants.NORMAL_FONT);
        textField.setBackground(Color.WHITE);
        textField.setBorder(UIConstants.FIELD_BORDER);
        textField.setPreferredSize(new Dimension(textField.getPreferredSize().width, UIConstants.FIELD_HEIGHT));
    }
    
    /**
     * Configura un JComboBox con estilo moderno
     */
    public static void styleComboBox(JComboBox<?> comboBox) {
        comboBox.setFont(UIConstants.NORMAL_FONT);
        comboBox.setBackground(Color.WHITE);
        comboBox.setBorder(UIConstants.FIELD_BORDER);
        comboBox.setPreferredSize(new Dimension(comboBox.getPreferredSize().width, UIConstants.FIELD_HEIGHT));
    }
    
    /**
     * Configura un JTable con estilo moderno
     */
    public static void styleTable(JTable table) {
        table.setFont(UIConstants.NORMAL_FONT);
        table.setRowHeight(30);
        table.setShowGrid(true);
        table.setGridColor(new Color(230, 230, 230));
        table.setSelectionBackground(UIConstants.PRIMARY_COLOR.brighter());
        table.setSelectionForeground(Color.WHITE);
        table.setIntercellSpacing(new Dimension(5, 5));
        table.setFillsViewportHeight(true);
        
        // Estilo de encabezado
        JTableHeader header = table.getTableHeader();
        header.setBackground(UIConstants.HEADER_COLOR);
        header.setForeground(UIConstants.DARK_COLOR);
        header.setFont(UIConstants.SUBTITLE_FONT);
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 35));
        
        // Centrar texto en columnas numéricas
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        
        for (int i = 0; i < table.getColumnCount(); i++) {
            Class<?> columnClass = table.getColumnClass(i);
            if (Number.class.isAssignableFrom(columnClass) || Boolean.class.isAssignableFrom(columnClass)) {
                table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }
        }
    }
    
    /**
     * Configura un JScrollPane con estilo moderno
     */
    public static void styleScrollPane(JScrollPane scrollPane) {
        scrollPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        scrollPane.getViewport().setBackground(Color.WHITE);
    }
    
    /**
     * Configura un JPanel con estilo moderno
     */
    public static void stylePanel(JPanel panel) {
        panel.setBackground(UIConstants.PANEL_COLOR);
        panel.setBorder(UIConstants.PANEL_BORDER);
    }
    
    /**
     * Crea un panel con bordes redondeados
     */
    public static JPanel createRoundedPanel(Color backgroundColor) {
        return new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(backgroundColor);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), UIConstants.BORDER_RADIUS, UIConstants.BORDER_RADIUS));
                g2.dispose();
            }
        };
    }
    
    /**
     * Crea un JPanel con una imagen de fondo
     */
    public static JPanel createPanelWithBackgroundImage(ImageIcon image) {
        return new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (image != null && image.getImage() != null) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    g2d.drawImage(image.getImage(), 0, 0, getWidth(), getHeight(), this);
                    g2d.dispose();
                }
            }
        };
    }
    
    /**
     * Muestra mensajes de diálogo con estilo mejorado
     */
    public static void showInfoMessage(Component parentComponent, String message, String title) {
        JOptionPane.showMessageDialog(parentComponent, message, title, JOptionPane.INFORMATION_MESSAGE, UIConstants.INFO_ICON);
    }
    
    public static void showErrorMessage(Component parentComponent, String message, String title) {
        JOptionPane.showMessageDialog(parentComponent, message, title, JOptionPane.ERROR_MESSAGE);
    }
    
    public static void showWarningMessage(Component parentComponent, String message, String title) {
        JOptionPane.showMessageDialog(parentComponent, message, title, JOptionPane.WARNING_MESSAGE);
    }
    
    public static int showConfirmDialog(Component parentComponent, String message, String title) {
        return JOptionPane.showConfirmDialog(parentComponent, message, title, JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
    }
    
    /**
     * Formatea fechas para mostrarlas en la UI
     */
    public static String formatDate(java.util.Date date) {
        if (date == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(date);
    }
    
    /**
     * Formatea valores de moneda para mostrarlos en la UI
     */
    public static String formatCurrency(double value) {
        return String.format("$%,.2f", value);
    }
}