package com.deportur.vista.componentes;

import com.deportur.modelo.EquipoDeportivo;
import com.deportur.vista.util.ImageCache;
import com.deportur.vista.util.UIConstants;
import com.deportur.vista.util.UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.text.SimpleDateFormat;

/**
 * Componente de tarjeta para mostrar equipos de forma visual
 */
public class EquipoCard extends JPanel {
    
    private EquipoDeportivo equipo;
    private boolean selected = false;
    private boolean hovering = false;
    private final Color BACKGROUND_COLOR = Color.WHITE;
    private final Color SELECTED_COLOR = new Color(235, 245, 255);
    private final Color HOVER_COLOR = new Color(245, 250, 255);
    private final Color BORDER_COLOR = new Color(220, 220, 220);
    private final Color SELECTED_BORDER_COLOR = UIConstants.PRIMARY_COLOR;
    private final int CARD_WIDTH = 280;
    private final int CARD_HEIGHT = 320;
    private JLabel nameLabel;
    private JLabel typeLabel;
    private JLabel priceLabel;
    private JLabel statusLabel;
    private JLabel imageLabel;
    private EquipoCardListener cardListener;
    
    /**
     * Constructor de la tarjeta de equipo
     * 
     * @param equipo EquipoDeportivo a mostrar
     */
    public EquipoCard(EquipoDeportivo equipo) {
        this.equipo = equipo;
        
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(CARD_WIDTH, CARD_HEIGHT));
        setMaximumSize(new Dimension(CARD_WIDTH, CARD_HEIGHT));
        setBackground(BACKGROUND_COLOR);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(10, 10, 10, 10)));
        
        // Cargar imagen del equipo
        ImageIcon equipmentIcon = ImageCache.getEquipmentImage(equipo.getIdEquipo());
        ImageIcon resizedIcon = ImageCache.resizeImage(equipmentIcon, 260, 180);
        
        // Panel de imagen
        imageLabel = new JLabel(resizedIcon, JLabel.CENTER);
        imageLabel.setPreferredSize(new Dimension(260, 180));
        imageLabel.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        
        // Panel de información
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);
        infoPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        // Nombre del equipo
        nameLabel = new JLabel(equipo.getNombre());
        nameLabel.setFont(UIConstants.SUBTITLE_FONT);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Tipo de equipo
        typeLabel = new JLabel(equipo.getTipo().getNombre());
        typeLabel.setFont(UIConstants.NORMAL_FONT);
        typeLabel.setForeground(Color.DARK_GRAY);
        typeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Precio de alquiler
        priceLabel = new JLabel(UIUtils.formatCurrency(equipo.getPrecioAlquiler()) + " / día");
        priceLabel.setFont(UIConstants.SUBTITLE_FONT);
        priceLabel.setForeground(UIConstants.SECONDARY_COLOR);
        priceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Estado y disponibilidad
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        statusPanel.setOpaque(false);
        statusPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        String statusText = equipo.getEstado();
        Color statusColor = getStatusColor(equipo.getEstado());
        
        statusLabel = new JLabel(statusText);
        statusLabel.setFont(UIConstants.SMALL_FONT);
        statusLabel.setForeground(statusColor);
        
        JPanel dotPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(statusColor);
                g.fillOval(0, (getHeight() - 8) / 2, 8, 8);
            }
        };
        dotPanel.setOpaque(false);
        dotPanel.setPreferredSize(new Dimension(12, 12));
        
        statusPanel.add(dotPanel);
        statusPanel.add(Box.createHorizontalStrut(5));
        statusPanel.add(statusLabel);
        
        if (equipo.isDisponible()) {
            statusPanel.add(Box.createHorizontalStrut(10));
            JLabel disponibleLabel = new JLabel("Disponible");
            disponibleLabel.setFont(UIConstants.SMALL_FONT);
            disponibleLabel.setForeground(UIConstants.SECONDARY_COLOR);
            statusPanel.add(disponibleLabel);
        }
        
        // Última línea con destino y fecha
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String infoText = equipo.getDestino().getNombre() + " | Adq: " + sdf.format(equipo.getFechaAdquisicion());
        JLabel infoLabel = new JLabel(infoText);
        infoLabel.setFont(UIConstants.SMALL_FONT);
        infoLabel.setForeground(Color.GRAY);
        infoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Agregar componentes al panel de información
        infoPanel.add(nameLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(typeLabel);
        infoPanel.add(Box.createVerticalStrut(10));
        infoPanel.add(priceLabel);
        infoPanel.add(Box.createVerticalStrut(10));
        infoPanel.add(statusPanel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(infoLabel);
        
        // Agregar componentes a la tarjeta
        add(imageLabel, BorderLayout.NORTH);
        add(infoPanel, BorderLayout.CENTER);
        
        // Manejar eventos de ratón
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (cardListener != null) {
                    if (e.getClickCount() == 2) {
                        cardListener.onCardDoubleClicked(EquipoCard.this, equipo);
                    } else {
                        cardListener.onCardClicked(EquipoCard.this, equipo);
                    }
                }
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                hovering = true;
                repaint();
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                hovering = false;
                repaint();
            }
        });
    }
    
    /**
     * Establece si la tarjeta está seleccionada
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
        updateAppearance();
    }
    
    /**
     * Obtiene el equipo representado por esta tarjeta
     */
    public EquipoDeportivo getEquipo() {
        return equipo;
    }
    
    /**
     * Establece el listener para eventos de la tarjeta
     */
    public void setCardListener(EquipoCardListener listener) {
        this.cardListener = listener;
    }
    
    /**
     * Actualiza la apariencia de la tarjeta según su estado
     */
    private void updateAppearance() {
        if (selected) {
            setBackground(SELECTED_COLOR);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(SELECTED_BORDER_COLOR, 2, true),
                    new EmptyBorder(9, 9, 9, 9)));
        } else if (hovering) {
            setBackground(HOVER_COLOR);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                    new EmptyBorder(10, 10, 10, 10)));
        } else {
            setBackground(BACKGROUND_COLOR);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                    new EmptyBorder(10, 10, 10, 10)));
        }
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Pintar fondo con esquinas redondeadas
        if (selected) {
            g2.setColor(SELECTED_COLOR);
        } else if (hovering) {
            g2.setColor(HOVER_COLOR);
        } else {
            g2.setColor(BACKGROUND_COLOR);
        }
        
        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), UIConstants.BORDER_RADIUS, UIConstants.BORDER_RADIUS));
        g2.dispose();
        
        super.paintComponent(g);
    }
    
    /**
     * Obtiene el color según el estado del equipo
     */
    private Color getStatusColor(String estado) {
        switch (estado.toLowerCase()) {
            case "nuevo":
                return new Color(0, 150, 0);
            case "bueno":
                return new Color(34, 139, 34);
            case "regular":
                return new Color(255, 165, 0);
            case "mantenimiento":
                return new Color(255, 140, 0);
            case "fuera de servicio":
                return new Color(220, 20, 60);
            default:
                return Color.GRAY;
        }
    }
    
    /**
     * Interfaz para listener de eventos de tarjeta
     */
    public interface EquipoCardListener {
        void onCardClicked(EquipoCard card, EquipoDeportivo equipo);
        void onCardDoubleClicked(EquipoCard card, EquipoDeportivo equipo);
    }
}