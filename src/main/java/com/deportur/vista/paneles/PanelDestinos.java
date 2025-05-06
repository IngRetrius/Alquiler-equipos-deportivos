package com.deportur.vista.paneles;

import com.deportur.controlador.InventarioController;
import com.deportur.modelo.DestinoTuristico;
import com.deportur.vista.MainFrame;
import com.deportur.vista.componentes.RoundedButton;
import com.deportur.vista.componentes.SearchBar;
import com.deportur.vista.util.ImageCache;
import com.deportur.vista.util.UIConstants;
import com.deportur.vista.util.UIUtils;
import java.awt.Dialog;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel de gestión de destinos turísticos con diseño mejorado
 */
public class PanelDestinos extends JPanel {
    
    private InventarioController controller;
    private boolean tienePermiso;
    
    // Componentes de la interfaz
    private SearchBar searchBar;
    private RoundedButton btnAgregar;
    private RoundedButton btnModificar;
    private RoundedButton btnEliminar;
    private RoundedButton btnRefrescar;
    private JTable tblDestinos;
    private DefaultTableModel tableModel;
    private JScrollPane scrollPane;
    
    // Estado actual
    private List<DestinoTuristico> destinosActuales = new ArrayList<>();
    private DestinoTuristico destinoSeleccionado;
    
    public PanelDestinos() {
        controller = new InventarioController();
        tienePermiso = MainFrame.getUsuarioController().puedeEditarDestinos();
        
        initComponents();
        cargarDatos();
    }
    
    private void initComponents() {
        // Configurar el layout
        setLayout(new BorderLayout(0, 10));
        setBackground(UIConstants.BACKGROUND_COLOR);
        
        // Panel superior con búsqueda y botones
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setOpaque(false);
        
        // Panel de búsqueda
        JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
        searchPanel.setOpaque(false);
        
        searchBar = new SearchBar("Buscar destinos...");
        searchBar.addSearchListener(e -> buscarDestinos());
        searchBar.addSearchFieldKeyListener(e -> buscarDestinos());
        
        searchPanel.add(searchBar, BorderLayout.CENTER);
        
        // Panel de botones
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonsPanel.setOpaque(false);
        
        btnAgregar = new RoundedButton("Agregar", UIConstants.ADD_ICON, UIConstants.SECONDARY_COLOR);
        btnAgregar.addActionListener(e -> mostrarFormularioAgregar());
        btnAgregar.setEnabled(tienePermiso);
        
        btnModificar = new RoundedButton("Modificar", UIConstants.EDIT_ICON, UIConstants.PRIMARY_COLOR);
        btnModificar.addActionListener(e -> mostrarFormularioModificar());
        btnModificar.setEnabled(false); // Inicialmente deshabilitado
        
        btnEliminar = new RoundedButton("Eliminar", UIConstants.DELETE_ICON, UIConstants.WARNING_COLOR);
        btnEliminar.addActionListener(e -> eliminarDestinoSeleccionado());
        btnEliminar.setEnabled(false); // Inicialmente deshabilitado
        
        btnRefrescar = new RoundedButton("Refrescar", UIConstants.REFRESH_ICON, Color.LIGHT_GRAY);
        btnRefrescar.setForeground(Color.DARK_GRAY);
        btnRefrescar.addActionListener(e -> cargarDatos());
        
        buttonsPanel.add(btnAgregar);
        buttonsPanel.add(btnModificar);
        buttonsPanel.add(btnEliminar);
        buttonsPanel.add(btnRefrescar);
        
        // Configurar botones según permisos
        btnModificar.setEnabled(false);
        btnEliminar.setEnabled(false);
        
        // Agregar componentes al panel superior
        topPanel.add(searchPanel, BorderLayout.NORTH);
        topPanel.add(buttonsPanel, BorderLayout.SOUTH);
        
        // Tabla de destinos
        String[] columnas = {"ID", "Nombre", "Ubicación", "Descripción"};
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tblDestinos = new JTable(tableModel);
        UIUtils.styleTable(tblDestinos);
        
        tblDestinos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int filaSeleccionada = tblDestinos.getSelectedRow();
                if (filaSeleccionada != -1) {
                    int idDestino = (int) tblDestinos.getValueAt(filaSeleccionada, 0);
                    destinoSeleccionado = controller.buscarDestinoPorId(idDestino);
                    
                    // Habilitar botones de acción según permisos
                    actualizarBotonesAccion();
                    
                    if (e.getClickCount() == 2) {
                        // Doble clic muestra formulario de edición o detalles
                        if (tienePermiso) {
                            mostrarFormularioModificar();
                        } else {
                            mostrarDetallesDestino(destinoSeleccionado);
                        }
                    }
                }
            }
        });
        
        scrollPane = new JScrollPane(tblDestinos);
        UIUtils.styleScrollPane(scrollPane);
        
        // Agregar componentes al panel principal
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    /**
     * Actualiza el estado de los botones de acción según la selección actual
     */
    private void actualizarBotonesAccion() {
        boolean haySeleccion = destinoSeleccionado != null;
        
        if (tienePermiso) {
            btnModificar.setEnabled(haySeleccion);
            btnEliminar.setEnabled(haySeleccion);
        } else {
            btnModificar.setEnabled(false);
            btnEliminar.setEnabled(false);
        }
    }
    
    /**
     * Busca destinos según los criterios ingresados
     */
    private void buscarDestinos() {
        String criterio = searchBar.getSearchText().trim();
        
        if (criterio.isEmpty()) {
            actualizarTabla(destinosActuales); // Mostrar todos
            return;
        }
        
        // Buscar según el criterio
        List<DestinoTuristico> destinosFiltrados = controller.buscarDestinosPorNombreOUbicacion(criterio);
        actualizarTabla(destinosFiltrados);
        
        // Reiniciar selección
        destinoSeleccionado = null;
        actualizarBotonesAccion();
    }
    
    /**
     * Carga los datos iniciales
     */
    private void cargarDatos() {
        destinosActuales = controller.listarTodosLosDestinos();
        actualizarTabla(destinosActuales);
        
        // Reiniciar selección
        destinoSeleccionado = null;
        actualizarBotonesAccion();
    }
    
    /**
     * Actualiza la tabla con los destinos filtrados
     */
    private void actualizarTabla(List<DestinoTuristico> destinos) {
        // Limpiar la tabla
        tableModel.setRowCount(0);
        
        if (destinos == null || destinos.isEmpty()) {
            return;
        }
        
        // Llenar la tabla con los datos
        for (DestinoTuristico destino : destinos) {
            Object[] row = {
                destino.getIdDestino(),
                destino.getNombre(),
                destino.getUbicacion(),
                destino.getDescripcion()
            };
            tableModel.addRow(row);
        }
    }
    
    /**
     * Muestra el formulario para agregar un nuevo destino
     */
    private void mostrarFormularioAgregar() {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Agregar Destino Turístico", 
                                     Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(700, 500);
        dialog.setLocationRelativeTo(this);
        
        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout(20, 0));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);
        
        // Panel izquierdo para la imagen
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setBackground(Color.WHITE);
        imagePanel.setBorder(new LineBorder(Color.LIGHT_GRAY, 1, true));
        imagePanel.setPreferredSize(new Dimension(300, 0));
        
        JLabel imageLabel = new JLabel();
        imageLabel.setIcon(ImageCache.getDestinationImage(0)); // Imagen por defecto
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        
        JButton selectImageButton = new RoundedButton("Seleccionar Imagen", UIConstants.PRIMARY_COLOR);
        selectImageButton.setForeground(Color.WHITE);
        
        imagePanel.add(imageLabel, BorderLayout.CENTER);
        imagePanel.add(selectImageButton, BorderLayout.SOUTH);
        
        // Panel derecho para el formulario
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1.0;
        
        // Campos del formulario
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Nombre:"), gbc);
        
        gbc.gridx = 1;
        JTextField txtNombre = new JTextField(20);
        UIUtils.styleTextField(txtNombre);
        formPanel.add(txtNombre, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Ubicación:"), gbc);
        
        gbc.gridx = 1;
        JTextField txtUbicacion = new JTextField(20);
        UIUtils.styleTextField(txtUbicacion);
        formPanel.add(txtUbicacion, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Descripción:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridheight = 3;
        JTextArea txtDescripcion = new JTextArea(5, 20);
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        JScrollPane scrollDescripcion = new JScrollPane(txtDescripcion);
        scrollDescripcion.setBorder(UIConstants.FIELD_BORDER);
        formPanel.add(scrollDescripcion, gbc);
        
        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);
        
        RoundedButton btnGuardar = new RoundedButton("Guardar", UIConstants.SECONDARY_COLOR);
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.addActionListener(e -> {
            try {
                String nombre = txtNombre.getText().trim();
                String ubicacion = txtUbicacion.getText().trim();
                String descripcion = txtDescripcion.getText().trim();
                
                if (nombre.isEmpty() || ubicacion.isEmpty()) {
                    UIUtils.showWarningMessage(dialog, "El nombre y la ubicación son obligatorios", "Campos incompletos");
                    return;
                }
                
                if (controller.registrarDestino(nombre, ubicacion, descripcion)) {
                    UIUtils.showInfoMessage(dialog, "Destino turístico registrado con éxito", "Éxito");
                    dialog.dispose();
                    cargarDatos();
                } else {
                    UIUtils.showErrorMessage(dialog, "Error al registrar el destino turístico", "Error");
                }
            } catch (Exception ex) {
                UIUtils.showErrorMessage(dialog, "Error: " + ex.getMessage(), "Error");
            }
        });
        
        RoundedButton btnCancelar = new RoundedButton("Cancelar", Color.LIGHT_GRAY);
        btnCancelar.setForeground(Color.DARK_GRAY);
        btnCancelar.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(btnCancelar);
        buttonPanel.add(btnGuardar);
        
        // Agregar paneles al diálogo
        mainPanel.add(imagePanel, BorderLayout.WEST);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }
    
    /**
     * Muestra el formulario para modificar un destino existente
     */
    private void mostrarFormularioModificar() {
        if (destinoSeleccionado == null) {
            UIUtils.showWarningMessage(this, "Debe seleccionar un destino para modificar", "Advertencia");
            return;
        }
        
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Modificar Destino Turístico", 
                             Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(700, 500);
        dialog.setLocationRelativeTo(this);
        
        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout(20, 0));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);
        
        // Panel izquierdo para la imagen
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setBackground(Color.WHITE);
        imagePanel.setBorder(new LineBorder(Color.LIGHT_GRAY, 1, true));
        imagePanel.setPreferredSize(new Dimension(300, 0));
        
        JLabel imageLabel = new JLabel();
        imageLabel.setIcon(ImageCache.getDestinationImage(destinoSeleccionado.getIdDestino()));
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        
        JButton selectImageButton = new RoundedButton("Cambiar Imagen", UIConstants.PRIMARY_COLOR);
        selectImageButton.setForeground(Color.WHITE);
        
        imagePanel.add(imageLabel, BorderLayout.CENTER);
        imagePanel.add(selectImageButton, BorderLayout.SOUTH);
        
        // Panel derecho para el formulario
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1.0;
        
        // Campos del formulario con datos del destino
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Nombre:"), gbc);
        
        gbc.gridx = 1;
        JTextField txtNombre = new JTextField(20);
        txtNombre.setText(destinoSeleccionado.getNombre());
        UIUtils.styleTextField(txtNombre);
        formPanel.add(txtNombre, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Ubicación:"), gbc);
        
        gbc.gridx = 1;
        JTextField txtUbicacion = new JTextField(20);
        txtUbicacion.setText(destinoSeleccionado.getUbicacion());
        UIUtils.styleTextField(txtUbicacion);
        formPanel.add(txtUbicacion, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Descripción:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridheight = 3;
        JTextArea txtDescripcion = new JTextArea(5, 20);
        txtDescripcion.setText(destinoSeleccionado.getDescripcion());
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        JScrollPane scrollDescripcion = new JScrollPane(txtDescripcion);
        scrollDescripcion.setBorder(UIConstants.FIELD_BORDER);
        formPanel.add(scrollDescripcion, gbc);
        
        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);
        
        RoundedButton btnGuardar = new RoundedButton("Guardar Cambios", UIConstants.SECONDARY_COLOR);
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.addActionListener(e -> {
            try {
                String nombre = txtNombre.getText().trim();
                String ubicacion = txtUbicacion.getText().trim();
                String descripcion = txtDescripcion.getText().trim();
                
                if (nombre.isEmpty() || ubicacion.isEmpty()) {
                    UIUtils.showWarningMessage(dialog, "El nombre y la ubicación son obligatorios", "Campos incompletos");
                    return;
                }
                
                if (controller.actualizarDestino(destinoSeleccionado.getIdDestino(), nombre, ubicacion, descripcion)) {
                    UIUtils.showInfoMessage(dialog, "Destino turístico actualizado con éxito", "Éxito");
                    dialog.dispose();
                    cargarDatos();
                } else {
                    UIUtils.showErrorMessage(dialog, "Error al actualizar el destino turístico", "Error");
                }
            } catch (Exception ex) {
                UIUtils.showErrorMessage(dialog, "Error: " + ex.getMessage(), "Error");
            }
        });
        
        RoundedButton btnCancelar = new RoundedButton("Cancelar", Color.LIGHT_GRAY);
        btnCancelar.setForeground(Color.DARK_GRAY);
        btnCancelar.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(btnCancelar);
        buttonPanel.add(btnGuardar);
        
        // Agregar paneles al diálogo
        mainPanel.add(imagePanel, BorderLayout.WEST);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }
    
    /**
     * Elimina el destino actualmente seleccionado
     */
    private void eliminarDestinoSeleccionado() {
        if (destinoSeleccionado == null) {
            UIUtils.showWarningMessage(this, "Debe seleccionar un destino para eliminar", "Advertencia");
            return;
        }
        
        int confirmacion = UIUtils.showConfirmDialog(
            this,
            "¿Está seguro de eliminar el destino '" + destinoSeleccionado.getNombre() + "'?\nEsta acción eliminará todos los equipos asociados a este destino.",
            "Confirmar eliminación"
        );
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                if (controller.eliminarDestino(destinoSeleccionado.getIdDestino())) {
                    UIUtils.showInfoMessage(this, "Destino turístico eliminado con éxito", "Éxito");
                    cargarDatos();
                } else {
                    UIUtils.showErrorMessage(this, "Error al eliminar el destino turístico", "Error");
                }
            } catch (Exception ex) {
                UIUtils.showErrorMessage(this, "Error: " + ex.getMessage() + "\nEs posible que existan equipos o reservas asociadas a este destino.", "Error");
            }
        }
    }
    
    /**
     * Muestra los detalles de un destino seleccionado
     */
    private void mostrarDetallesDestino(DestinoTuristico destino) {
        if (destino == null) return;
        
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Detalles del Destino Turístico", 
                             Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(700, 500);
        dialog.setLocationRelativeTo(this);
        
        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);
        
        // Panel de imagen (izquierda)
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setBackground(Color.WHITE);
        imagePanel.setBorder(new LineBorder(Color.LIGHT_GRAY, 1, true));
        imagePanel.setPreferredSize(new Dimension(300, 0));
        
        ImageIcon destinationIcon = ImageCache.getDestinationImage(destino.getIdDestino());
        JLabel imageLabel = new JLabel(destinationIcon);
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        
        imagePanel.add(imageLabel, BorderLayout.CENTER);
        
        // Panel de información (derecha)
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(Color.WHITE);
        
        // Encabezado con nombre
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        JLabel nameLabel = new JLabel(destino.getNombre());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        nameLabel.setForeground(UIConstants.PRIMARY_COLOR);
        
        JLabel locationLabel = new JLabel(destino.getUbicacion());
        locationLabel.setFont(new Font("Segoe UI", Font.ITALIC, 18));
        locationLabel.setForeground(Color.DARK_GRAY);
        
        headerPanel.add(nameLabel, BorderLayout.NORTH);
        headerPanel.add(locationLabel, BorderLayout.CENTER);
        
        // Panel de descripción
        JPanel descriptionPanel = new JPanel(new BorderLayout());
        descriptionPanel.setOpaque(false);
        descriptionPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        
        JLabel descTitleLabel = new JLabel("Descripción:");
        descTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        JTextArea descTextArea = new JTextArea(destino.getDescripcion());
        descTextArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descTextArea.setLineWrap(true);
        descTextArea.setWrapStyleWord(true);
        descTextArea.setEditable(false);
        descTextArea.setBackground(new Color(245, 245, 245));
        descTextArea.setBorder(new EmptyBorder(10, 10, 10, 10));
        JScrollPane scrollDesc = new JScrollPane(descTextArea);
        scrollDesc.setBorder(new LineBorder(new Color(220, 220, 220), 1, true));
        
        descriptionPanel.add(descTitleLabel, BorderLayout.NORTH);
        descriptionPanel.add(scrollDesc, BorderLayout.CENTER);
        
        // Panel de estadísticas (opcional)
        JPanel statsPanel = new JPanel(new BorderLayout());
        statsPanel.setOpaque(false);
        statsPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        
        JLabel statsTitleLabel = new JLabel("Información Adicional:");
        statsTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        JPanel statsContentPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        statsContentPanel.setOpaque(false);
        statsContentPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        // Aquí se podrían agregar estadísticas como cantidad de equipos, reservas, etc.
        // Por simplicidad, solo mostramos un par de campos de ejemplo
        
        statsContentPanel.add(new JLabel("ID:"));
        statsContentPanel.add(new JLabel(String.valueOf(destino.getIdDestino())));
        
        // Agregar botón para ver equipos disponibles en este destino
        JButton btnVerEquipos = new RoundedButton("Ver Equipos Disponibles", UIConstants.PRIMARY_COLOR);
        btnVerEquipos.setForeground(Color.WHITE);
        btnVerEquipos.addActionListener(e -> {
            // Implementación pendiente
            UIUtils.showInfoMessage(dialog, "Funcionalidad en desarrollo", "Información");
        });
        
        statsPanel.add(statsTitleLabel, BorderLayout.NORTH);
        statsPanel.add(statsContentPanel, BorderLayout.CENTER);
        statsPanel.add(btnVerEquipos, BorderLayout.SOUTH);
        
        // Agregar componentes al panel de información
        infoPanel.add(headerPanel, BorderLayout.NORTH);
        infoPanel.add(descriptionPanel, BorderLayout.CENTER);
        infoPanel.add(statsPanel, BorderLayout.SOUTH);
        
        // Botón de cerrar
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        
        RoundedButton closeButton = new RoundedButton("Cerrar", Color.LIGHT_GRAY);
        closeButton.setForeground(Color.DARK_GRAY);
        closeButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(closeButton);
        
        // Agregar componentes al panel principal
        mainPanel.add(imagePanel, BorderLayout.WEST);
        mainPanel.add(infoPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.add(mainPanel);
        dialog.setVisible(true);
    }
}