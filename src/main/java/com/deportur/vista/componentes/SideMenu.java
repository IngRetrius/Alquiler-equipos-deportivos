package com.deportur.vista.componentes;

import com.deportur.vista.util.UIConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Menú lateral con iconos y efectos visuales
 */
public class SideMenu extends JPanel {
    
    private List<MenuOption> menuOptions = new ArrayList<>();
    private int selectedIndex = -1;
    private Color backgroundColor = new Color(48, 57, 82);
    private Color hoverColor = new Color(64, 75, 105);
    private Color selectedColor = new Color(78, 93, 148);
    private Color textColor = Color.WHITE;
    private MenuSelectionListener menuSelectionListener;
    
    /**
     * Constructor del menú lateral
     */
    public SideMenu() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(220, 600));
        setBackground(backgroundColor);
        setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(40, 40, 40)));
    }
    
    /**
     * Agrega una opción al menú
     * 
     * @param title Título de la opción
     * @param icon Icono de la opción
     * @return El índice de la opción agregada
     */
    public int addOption(String title, ImageIcon icon) {
        MenuOption option = new MenuOption(title, icon, menuOptions.size());
        menuOptions.add(option);
        add(option);
        
        if (menuOptions.size() == 1) {
            selectOption(0); // Seleccionar la primera opción por defecto
        }
        
        return menuOptions.size() - 1;
    }
    
    /**
     * Selecciona una opción del menú
     * 
     * @param index Índice de la opción a seleccionar
     */
    public void selectOption(int index) {
        if (index >= 0 && index < menuOptions.size()) {
            if (selectedIndex >= 0 && selectedIndex < menuOptions.size()) {
                menuOptions.get(selectedIndex).setSelected(false);
            }
            
            menuOptions.get(index).setSelected(true);
            selectedIndex = index;
            
            if (menuSelectionListener != null) {
                menuSelectionListener.onMenuItemSelected(index);
            }
        }
    }
    
    /**
     * Establece el listener para la selección de menú
     * 
     * @param listener Listener para la selección de menú
     */
    public void setMenuSelectionListener(MenuSelectionListener listener) {
        this.menuSelectionListener = listener;
    }
    
    /**
     * Opción de menú (componente interno)
     */
    private class MenuOption extends JPanel {
        private String title;
        private ImageIcon icon;
        private int index;
        private boolean selected = false;
        private boolean hovering = false;
        private JLabel titleLabel;
        private JLabel iconLabel;
        
        public MenuOption(String title, ImageIcon icon, int index) {
            this.title = title;
            this.icon = icon;
            this.index = index;
            
            setLayout(new BorderLayout());
            setBackground(backgroundColor);
            setPreferredSize(new Dimension(220, 50));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
            setBorder(new EmptyBorder(10, 15, 10, 15));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            // Crear componentes
            iconLabel = new JLabel(icon);
            iconLabel.setForeground(textColor);
            
            titleLabel = new JLabel(title);
            titleLabel.setForeground(textColor);
            titleLabel.setFont(UIConstants.NORMAL_FONT);
            
            // Panel para contener icono y texto
            JPanel contentPanel = new JPanel(new BorderLayout(10, 0));
            contentPanel.setOpaque(false);
            contentPanel.add(iconLabel, BorderLayout.WEST);
            contentPanel.add(titleLabel, BorderLayout.CENTER);
            
            add(contentPanel, BorderLayout.CENTER);
            
            // Agregar indicador de selección (barra vertical)
            JPanel selectionIndicator = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    if (selected) {
                        g.setColor(UIConstants.ACCENT_COLOR);
                        g.fillRect(0, 0, 3, getHeight());
                    }
                }
            };
            selectionIndicator.setOpaque(false);
            selectionIndicator.setPreferredSize(new Dimension(3, 0));
            add(selectionIndicator, BorderLayout.WEST);
            
            // Gestionar eventos de ratón
            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    SideMenu.this.selectOption(index);
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
        
        public void setSelected(boolean selected) {
            this.selected = selected;
            repaint();
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Pintar fondo según estado
            if (selected) {
                g2.setColor(selectedColor);
            } else if (hovering) {
                g2.setColor(hoverColor);
            } else {
                g2.setColor(backgroundColor);
            }
            
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();
            
            super.paintComponent(g);
        }
    }
    
    /**
     * Interfaz para listener de selección de menú
     */
    public interface MenuSelectionListener {
        void onMenuItemSelected(int index);
    }
}