package com.deportur.vista.paneles;

import com.deportur.controlador.InventarioController;
import com.deportur.modelo.TipoEquipo;
import com.deportur.vista.MainFrame;
import com.deportur.vista.componentes.RoundedButton;
import com.deportur.vista.componentes.SearchBar;
import com.deportur.vista.util.UIConstants;
import com.deportur.vista.util.UIUtils;
import java.awt.Dialog;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel de gestión de tipos de equipo con diseño mejorado
 */
public class PanelTiposEquipo extends JPanel {
    
    private InventarioController controller;
    private boolean tienePermiso;
    
    // Componentes de la interfaz
    private SearchBar searchBar;
    private RoundedButton btnAgregar;
    private RoundedButton btnModificar;
    private RoundedButton btnEliminar;
    private RoundedButton btnRefrescar;
    private JTable tblTiposEquipo;
    private DefaultTableModel tableModel;
    private JScrollPane scrollPane;
    
    // Estado actual
    private List<TipoEquipo> tiposActuales = new ArrayList<>();
    private TipoEquipo tipoSeleccionado;
    
    public PanelTiposEquipo() {
        controller = new InventarioController();
        tienePermiso = MainFrame.getUsuarioController().puedeEditarTiposEquipo();
        
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
        
        searchBar = new SearchBar("Buscar tipos de equipo...");
        searchBar.addSearchListener(e -> buscarTiposEquipo());
        searchBar.addSearchFieldKeyListener(e -> buscarTiposEquipo());
        
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
        btnEliminar.addActionListener(e -> eliminarTipoSeleccionado());
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
        
        // Tabla de tipos de equipo
        String[] columnas = {"ID", "Nombre", "Descripción"};
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tblTiposEquipo = new JTable(tableModel);
        UIUtils.styleTable(tblTiposEquipo);
        
        tblTiposEquipo.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int filaSeleccionada = tblTiposEquipo.getSelectedRow();
                if (filaSeleccionada != -1) {
                    int idTipo = (int) tblTiposEquipo.getValueAt(filaSeleccionada, 0);
                    tipoSeleccionado = controller.buscarTipoEquipoPorId(idTipo);
                    
                    // Habilitar botones de acción según permisos
                    actualizarBotonesAccion();
                    
                    if (e.getClickCount() == 2) {
                        // Doble clic muestra formulario de edición o detalles
                        if (tienePermiso) {
                            mostrarFormularioModificar();
                        } else {
                            mostrarDetallesTipoEquipo(tipoSeleccionado);
                        }
                    }
                }
            }
        });
        
        scrollPane = new JScrollPane(tblTiposEquipo);
        UIUtils.styleScrollPane(scrollPane);
        
        // Agregar componentes al panel principal
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    /**
     * Actualiza el estado de los botones de acción según la selección actual
     */
    private void actualizarBotonesAccion() {
        boolean haySeleccion = tipoSeleccionado != null;
        
        if (tienePermiso) {
            btnModificar.setEnabled(haySeleccion);
            btnEliminar.setEnabled(haySeleccion);
        } else {
            btnModificar.setEnabled(false);
            btnEliminar.setEnabled(false);
        }
    }
    
    /**
     * Busca tipos de equipo según los criterios ingresados
     */
    private void buscarTiposEquipo() {
        String criterio = searchBar.getSearchText().trim();
        
        if (criterio.isEmpty()) {
            actualizarTabla(tiposActuales); // Mostrar todos
            return;
        }
        
        // Filtrar los tipos de equipo según el criterio
        List<TipoEquipo> tiposFiltrados = new ArrayList<>();
        
        for (TipoEquipo tipo : tiposActuales) {
            if (tipo.getNombre().toLowerCase().contains(criterio.toLowerCase()) ||
                (tipo.getDescripcion() != null && tipo.getDescripcion().toLowerCase().contains(criterio.toLowerCase()))) {
                tiposFiltrados.add(tipo);
            }
        }
        
        actualizarTabla(tiposFiltrados);
        
        // Reiniciar selección
        tipoSeleccionado = null;
        actualizarBotonesAccion();
    }
    
    /**
     * Carga los datos iniciales
     */
    private void cargarDatos() {
        tiposActuales = controller.listarTodosLosTiposEquipo();
        actualizarTabla(tiposActuales);
        
        // Reiniciar selección
        tipoSeleccionado = null;
        actualizarBotonesAccion();
    }
    
    /**
     * Actualiza la tabla con los tipos de equipo filtrados
     */
    private void actualizarTabla(List<TipoEquipo> tipos) {
        // Limpiar la tabla
        tableModel.setRowCount(0);
        
        if (tipos == null || tipos.isEmpty()) {
            return;
        }
        
        // Llenar la tabla con los datos
        for (TipoEquipo tipo : tipos) {
            Object[] row = {
                tipo.getIdTipo(),
                tipo.getNombre(),
                tipo.getDescripcion()
            };
            tableModel.addRow(row);
        }
    }
    
    /**
     * Muestra el formulario para agregar un nuevo tipo de equipo
     */
    private void mostrarFormularioAgregar() {
    JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Agregar Tipo de Equipo", 
                               Dialog.ModalityType.APPLICATION_MODAL);
    dialog.setLayout(new BorderLayout());
    dialog.setSize(500, 400);
    dialog.setLocationRelativeTo(this);
        
        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);
        
        // Panel de formulario
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
                String descripcion = txtDescripcion.getText().trim();
                
                if (nombre.isEmpty()) {
                    UIUtils.showWarningMessage(dialog, "El nombre es obligatorio", "Campo incompleto");
                    return;
                }
                
                if (controller.registrarTipoEquipo(nombre, descripcion)) {
                    UIUtils.showInfoMessage(dialog, "Tipo de equipo registrado con éxito", "Éxito");
                    dialog.dispose();
                    cargarDatos();
                } else {
                    UIUtils.showErrorMessage(dialog, "Error al registrar el tipo de equipo", "Error");
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
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }
    
    /**
     * Muestra el formulario para modificar un tipo de equipo existente
     */
    private void mostrarFormularioModificar() {
        if (tipoSeleccionado == null) {
            UIUtils.showWarningMessage(this, "Debe seleccionar un tipo de equipo para modificar", "Advertencia");
            return;
        }
        
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Modificar Tipo de Equipo", 
                           Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        
        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);
        
        // Panel de formulario
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1.0;
        
        // Campos del formulario con datos del tipo de equipo
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Nombre:"), gbc);
        
        gbc.gridx = 1;
        JTextField txtNombre = new JTextField(20);
        txtNombre.setText(tipoSeleccionado.getNombre());
        UIUtils.styleTextField(txtNombre);
        formPanel.add(txtNombre, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Descripción:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridheight = 3;
        JTextArea txtDescripcion = new JTextArea(5, 20);
        txtDescripcion.setText(tipoSeleccionado.getDescripcion());
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
                String descripcion = txtDescripcion.getText().trim();
                
                if (nombre.isEmpty()) {
                    UIUtils.showWarningMessage(dialog, "El nombre es obligatorio", "Campo incompleto");
                    return;
                }
                
                if (controller.actualizarTipoEquipo(tipoSeleccionado.getIdTipo(), nombre, descripcion)) {
                    UIUtils.showInfoMessage(dialog, "Tipo de equipo actualizado con éxito", "Éxito");
                    dialog.dispose();
                    cargarDatos();
                } else {
                    UIUtils.showErrorMessage(dialog, "Error al actualizar el tipo de equipo", "Error");
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
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }
    
    /**
     * Elimina el tipo de equipo actualmente seleccionado
     */
    private void eliminarTipoSeleccionado() {
        if (tipoSeleccionado == null) {
            UIUtils.showWarningMessage(this, "Debe seleccionar un tipo de equipo para eliminar", "Advertencia");
            return;
        }
        
        int confirmacion = UIUtils.showConfirmDialog(
            this,
            "¿Está seguro de eliminar el tipo de equipo '" + tipoSeleccionado.getNombre() + "'?\nEsta acción podría afectar a los equipos asociados a este tipo.",
            "Confirmar eliminación"
        );
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                if (controller.eliminarTipoEquipo(tipoSeleccionado.getIdTipo())) {
                    UIUtils.showInfoMessage(this, "Tipo de equipo eliminado con éxito", "Éxito");
                    cargarDatos();
                } else {
                    UIUtils.showErrorMessage(this, "Error al eliminar el tipo de equipo", "Error");
                }
            } catch (Exception ex) {
                UIUtils.showErrorMessage(this, "Error: " + ex.getMessage() + "\nEs posible que existan equipos asociados a este tipo.", "Error");
            }
        }
    }
    
    /**
     * Muestra los detalles de un tipo de equipo seleccionado
     */
    private void mostrarDetallesTipoEquipo(TipoEquipo tipo) {
        if (tipo == null) return;
        
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Detalles del Tipo de Equipo", 
                           Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        
        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);
        
        // Panel de información
        JPanel infoPanel = new JPanel(new BorderLayout(0, 20));
        infoPanel.setBackground(Color.WHITE);
        
        // Encabezado con nombre
        JLabel nameLabel = new JLabel(tipo.getNombre());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        nameLabel.setForeground(UIConstants.PRIMARY_COLOR);
        
        // Panel de descripción
        JPanel descriptionPanel = new JPanel(new BorderLayout(0, 10));
        descriptionPanel.setOpaque(false);
        
        JLabel descTitleLabel = new JLabel("Descripción:");
        descTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        JTextArea descTextArea = new JTextArea(tipo.getDescripcion());
        descTextArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descTextArea.setLineWrap(true);
        descTextArea.setWrapStyleWord(true);
        descTextArea.setEditable(false);
        descTextArea.setBackground(new Color(245, 245, 245));
        descTextArea.setBorder(new EmptyBorder(10, 10, 10, 10));
        JScrollPane scrollDesc = new JScrollPane(descTextArea);
        scrollDesc.setBorder(UIConstants.FIELD_BORDER);
        
        descriptionPanel.add(descTitleLabel, BorderLayout.NORTH);
        descriptionPanel.add(scrollDesc, BorderLayout.CENTER);
        
        // Panel de equipos asociados (opcional - no implementado completamente)
        JPanel associatedPanel = new JPanel(new BorderLayout(0, 10));
        associatedPanel.setOpaque(false);
        associatedPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        
        JLabel associatedTitleLabel = new JLabel("Equipos Asociados:");
        associatedTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        JButton btnVerEquipos = new RoundedButton("Ver Equipos de este Tipo", UIConstants.PRIMARY_COLOR);
        btnVerEquipos.setForeground(Color.WHITE);
        btnVerEquipos.addActionListener(e -> {
            // Implementación pendiente
            UIUtils.showInfoMessage(dialog, "Funcionalidad en desarrollo", "Información");
        });
        
        associatedPanel.add(associatedTitleLabel, BorderLayout.NORTH);
        associatedPanel.add(btnVerEquipos, BorderLayout.CENTER);
        
        // Agregar componentes al panel de información
        infoPanel.add(nameLabel, BorderLayout.NORTH);
        infoPanel.add(descriptionPanel, BorderLayout.CENTER);
        infoPanel.add(associatedPanel, BorderLayout.SOUTH);
        
        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        
        RoundedButton closeButton = new RoundedButton("Cerrar", Color.LIGHT_GRAY);
        closeButton.setForeground(Color.DARK_GRAY);
        closeButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(closeButton);
        
        // Agregar componentes al panel principal
        mainPanel.add(infoPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.add(mainPanel);
        dialog.setVisible(true);
    }
}