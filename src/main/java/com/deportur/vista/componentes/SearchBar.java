package com.deportur.vista.componentes;

import com.deportur.vista.util.UIConstants;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Componente de barra de búsqueda con funcionalidad de autocompletado
 */
public class SearchBar extends JPanel {
    
    private JTextField searchField;
    private JButton searchButton;
    private JComboBox<String> filterComboBox;
    private String placeholderText = "";
    
    /**
     * Constructor simple
     * 
     * @param placeholder Texto de marcador de posición
     */
    public SearchBar(String placeholder) {
        this(placeholder, null, false);
    }
    
    /**
     * Constructor con filtro
     * 
     * @param placeholder Texto de marcador de posición
     * @param filterOptions Opciones de filtro (si es null, no se muestra el combobox)
     */
    public SearchBar(String placeholder, String[] filterOptions) {
        this(placeholder, filterOptions, false);
    }
    
    /**
     * Constructor completo
     * 
     * @param placeholder Texto de marcador de posición
     * @param filterOptions Opciones de filtro (si es null, no se muestra el combobox)
     * @param withAutoComplete Si se habilita el autocompletado
     */
    public SearchBar(String placeholder, String[] filterOptions, boolean withAutoComplete) {
        this.placeholderText = placeholder;
        
        setLayout(new BorderLayout(5, 0));
        setBorder(new EmptyBorder(5, 5, 5, 5));
        setOpaque(false);
        
        // Campo de búsqueda con ícono
        searchField = new PlaceholderTextField(this.placeholderText);
        searchField.setFont(UIConstants.NORMAL_FONT);
        searchField.setPreferredSize(new Dimension(250, UIConstants.FIELD_HEIGHT));
        
        // Campo de filtro (opcional)
        if (filterOptions != null && filterOptions.length > 0) {
            filterComboBox = new JComboBox<>(filterOptions);
            filterComboBox.setFont(UIConstants.NORMAL_FONT);
            filterComboBox.setPreferredSize(new Dimension(150, UIConstants.FIELD_HEIGHT));
            
            JPanel filterPanel = new JPanel(new BorderLayout());
            filterPanel.setOpaque(false);
            filterPanel.add(new JLabel("Filtrar:"), BorderLayout.WEST);
            filterPanel.add(filterComboBox, BorderLayout.CENTER);
            
            add(filterPanel, BorderLayout.EAST);
        }
        
        // Botón de búsqueda
        searchButton = new JButton(UIConstants.SEARCH_ICON);
        searchButton.setToolTipText("Buscar");
        searchButton.setFocusPainted(false);
        searchButton.setPreferredSize(new Dimension(UIConstants.FIELD_HEIGHT, UIConstants.FIELD_HEIGHT));
        
        // Panel para campo de búsqueda y botón
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setOpaque(false);
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);
        
        add(searchPanel, BorderLayout.CENTER);
    }
    
    /**
     * Clase interna para campo de texto con placeholder
     */
    private class PlaceholderTextField extends JTextField {
        
        private final String placeholder;
        private boolean showingPlaceholder;
        
        public PlaceholderTextField(String placeholder) {
            this.placeholder = placeholder;
            this.showingPlaceholder = true;
            setText(this.placeholder);
            setForeground(Color.GRAY);
            
            // Aplicar borde redondeado
            setBorder(new CompoundBorder(
                    new LineBorder(new Color(210, 210, 210), 1, true),
                    new EmptyBorder(5, 10, 5, 10)));
            
            // Listeners para gestionar el placeholder
            addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    if (showingPlaceholder) {
                        setText("");
                        setForeground(Color.BLACK);
                        showingPlaceholder = false;
                    }
                }
                
                @Override
                public void focusLost(FocusEvent e) {
                    if (getText().isEmpty()) {
                        setText(PlaceholderTextField.this.placeholder);
                        setForeground(Color.GRAY);
                        showingPlaceholder = true;
                    }
                }
            });
        }
        
        @Override
        public String getText() {
            return showingPlaceholder ? "" : super.getText();
        }
    }
    
    /**
     * Obtiene el texto de búsqueda
     */
    public String getSearchText() {
        return searchField.getText();
    }
    
    /**
     * Obtiene el filtro seleccionado
     */
    public String getSelectedFilter() {
        return filterComboBox != null ? (String) filterComboBox.getSelectedItem() : null;
    }
    
    /**
     * Establece el texto de búsqueda
     */
    public void setSearchText(String text) {
        searchField.setText(text);
        searchField.setForeground(Color.BLACK);
    }
    
    /**
     * Agrega un listener para el botón de búsqueda
     */
    public void addSearchListener(java.awt.event.ActionListener listener) {
        searchButton.addActionListener(listener);
    }
    
    /**
     * Agrega un listener para la tecla Enter en el campo de búsqueda
     */
    public void addSearchFieldKeyListener(java.awt.event.ActionListener listener) {
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    listener.actionPerformed(null);
                }
            }
        });
    }
    
    /**
     * Agrega un listener para cambios en el filtro
     */
    public void addFilterChangeListener(java.awt.event.ActionListener listener) {
        if (filterComboBox != null) {
            filterComboBox.addActionListener(listener);
        }
    }
}