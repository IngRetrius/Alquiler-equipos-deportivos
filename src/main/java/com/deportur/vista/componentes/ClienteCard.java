package com.deportur.vista.componentes;

import com.deportur.modelo.Cliente;
import com.deportur.vista.util.ImageCache;
import com.deportur.vista.util.UIConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

/**
 * Componente de tarjeta para mostrar clientes de forma visual
 */
public class ClienteCard extends JPanel {
    
    private Cliente cliente;
    private boolean selected = false;
    private boolean hovering = false;
    private final Color BACKGROUND_COLOR = Color.WHITE;
    private final Color SELECTED_COLOR = new Color(235, 245, 255);
    private final Color HOVER_COLOR = new Color(245, 250, 255);
    private final Color BORDER_COLOR = new Color(220, 220, 220);
    private final Color SELECTED_BORDER_COLOR = UIConstants.PRIMARY_COLOR;
    private final int CARD_WIDTH = 250;
    private final int CARD_HEIGHT = 120;
    private JLabel nameLabel;
    private JLabel documentLabel;
    private JLabel contactLabel;
    private ClienteCardListener cardListener;
    
    /**
     * Constructor de la tarjeta de cliente
     * 
     * @param cliente Cliente a mostrar
     */
    public ClienteCard(Cliente cliente) {
        this.cliente = cliente;
        
        setLayout(new BorderLayout(10, 0));
        setPreferredSize(new Dimension(CARD_WIDTH, CARD_HEIGHT));
        setMaximumSize(new Dimension(CARD_WIDTH, CARD_HEIGHT));
        setBackground(BACKGROUND_COLOR);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(10, 10, 10, 10)));
        
        // Imagen del cliente
        ImageIcon clientIcon = ImageCache.getClientImage(cliente.getIdCliente());
        ImageIcon resizedIcon = ImageCache.resizeImage(clientIcon, 64, 64);
        
        JPanel avatarPanel = new JPanel(new BorderLayout());
        avatarPanel.setOpaque(false);
        avatarPanel.setPreferredSize(new Dimension(64, 64));
        
        JLabel avatarLabel = new JLabel(resizedIcon);
        avatarPanel.add(avatarLabel, BorderLayout.CENTER);
        
        // Panel de información
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);
        
        // Nombre completo
        nameLabel = new JLabel(cliente.getNombre() + " " + cliente.getApellido());
        nameLabel.setFont(UIConstants.SUBTITLE_FONT);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Documento
        documentLabel = new JLabel(cliente.getTipoDocumento() + ": " + cliente.getDocumento());
        documentLabel.setFont(UIConstants.NORMAL_FONT);
        documentLabel.setForeground(Color.DARK_GRAY);
        documentLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Contacto
        contactLabel = new JLabel(cliente.getEmail());
        contactLabel.setFont(UIConstants.SMALL_FONT);
        contactLabel.setForeground(Color.GRAY);
        contactLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Teléfono
        JLabel phoneLabel = new JLabel(cliente.getTelefono());
        phoneLabel.setFont(UIConstants.SMALL_FONT);
        phoneLabel.setForeground(Color.GRAY);
        phoneLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Agregar componentes al panel de información
        infoPanel.add(nameLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(documentLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(contactLabel);
        infoPanel.add(Box.createVerticalStrut(2));
        infoPanel.add(phoneLabel);
        
        // Agregar componentes a la tarjeta
        add(avatarPanel, BorderLayout.WEST);
        add(infoPanel, BorderLayout.CENTER);
        
        // Manejar eventos de ratón
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (cardListener != null) {
                    if (e.getClickCount() == 2) {
                        cardListener.onCardDoubleClicked(ClienteCard.this, cliente);
                    } else {
                        cardListener.onCardClicked(ClienteCard.this, cliente);
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
     * Obtiene el cliente representado por esta tarjeta
     */
    public Cliente getCliente() {
        return cliente;
    }
    
    /**
     * Establece el listener para eventos de la tarjeta
     */
    public void setCardListener(ClienteCardListener listener) {
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
     * Interfaz para listener de eventos de tarjeta
     */
    public interface ClienteCardListener {
        void onCardClicked(ClienteCard card, Cliente cliente);
        void onCardDoubleClicked(ClienteCard card, Cliente cliente);
    }
}