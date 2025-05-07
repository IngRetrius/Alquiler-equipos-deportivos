package com.deportur.vista.paneles;

import com.deportur.controlador.InventarioController;
import com.deportur.modelo.EquipoDeportivo;
import com.deportur.modelo.TipoEquipo;
import com.deportur.modelo.DestinoTuristico;
import com.deportur.vista.MainFrame;
import com.deportur.vista.componentes.EquipoCard;
import com.deportur.vista.componentes.RoundedButton;
import com.deportur.vista.componentes.SearchBar;
import com.deportur.vista.componentes.StatusIndicator;
import com.deportur.vista.util.ImageCache;
import com.deportur.vista.util.UIConstants;
import com.deportur.vista.util.UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Panel de inventario con diseño mejorado y múltiples vistas
 */
public class PanelInventario extends JPanel implements EquipoCard.EquipoCardListener {
    
    private InventarioController controller;
    private boolean tienePermisoEdicion;
    
    // Componentes de la interfaz
    private SearchBar searchBar;
    private JToggleButton btnListView;
    private JToggleButton btnCardView;
    private RoundedButton btnAgregar;
    private RoundedButton btnModificar;
    private RoundedButton btnEliminar;
    private RoundedButton btnRefrescar;
    private JTable tblEquipos;
    private DefaultTableModel tableModel;
    private JScrollPane tableScrollPane;
    private JPanel cardsContainer;
    private JScrollPane cardsScrollPane;
    private JComboBox<TipoEquipo> cmbTipoFiltro;
    private JComboBox<DestinoTuristico> cmbDestinoFiltro;
    private JCheckBox chkSoloDisponibles;
    
    // Estado actual
    private List<EquipoDeportivo> equiposActuales = new ArrayList<>();
    private List<EquipoCard> equipoCards = new ArrayList<>();
    private EquipoDeportivo equipoSeleccionado;
    private EquipoCard cardSeleccionada;
    private boolean mostrandoTarjetas = true;
    
    public PanelInventario() {
        controller = new InventarioController();
        tienePermisoEdicion = MainFrame.getUsuarioController().puedeEditarInventario();
        initComponents();
        cargarDatos();
    }
    
    private void initComponents() {
        // Configurar el layout
        setLayout(new BorderLayout(0, 10));
        setBackground(UIConstants.BACKGROUND_COLOR);
        
        // Panel superior con búsqueda, filtros y botones
        JPanel topPanel = new JPanel(new BorderLayout(10, 0));
        topPanel.setOpaque(false);
        
        // Panel de búsqueda
        JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
        searchPanel.setOpaque(false);
        
        searchBar = new SearchBar("Buscar equipos...");
        searchBar.addSearchListener(e -> buscarEquipos());
        searchBar.addSearchFieldKeyListener(e -> buscarEquipos());
        
        // Panel de vista
        JPanel viewPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        viewPanel.setOpaque(false);
        
        ButtonGroup viewGroup = new ButtonGroup();
        
        btnCardView = new JToggleButton(UIConstants.CARD_VIEW_ICON);
        btnCardView.setToolTipText("Vista de tarjetas");
        btnCardView.setSelected(true);
        btnCardView.addActionListener(e -> cambiarVista(true));
        
        btnListView = new JToggleButton(UIConstants.LIST_VIEW_ICON);
        btnListView.setToolTipText("Vista de lista");
        btnListView.addActionListener(e -> cambiarVista(false));
        
        viewGroup.add(btnCardView);
        viewGroup.add(btnListView);
        
        viewPanel.add(btnCardView);
        viewPanel.add(btnListView);
        
        // Panel de filtros
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterPanel.setOpaque(false);
        
        filterPanel.add(new JLabel("Tipo:"));
        cmbTipoFiltro = new JComboBox<>();
        cmbTipoFiltro.addItem(null); // Opción "Todos"
        UIUtils.styleComboBox(cmbTipoFiltro);
        cmbTipoFiltro.addActionListener(e -> aplicarFiltros());
        
        filterPanel.add(cmbTipoFiltro);
        
        filterPanel.add(new JLabel("Destino:"));
        cmbDestinoFiltro = new JComboBox<>();
        cmbDestinoFiltro.addItem(null); // Opción "Todos"
        UIUtils.styleComboBox(cmbDestinoFiltro);
        cmbDestinoFiltro.addActionListener(e -> aplicarFiltros());
        
        filterPanel.add(cmbDestinoFiltro);
        
        chkSoloDisponibles = new JCheckBox("Solo disponibles");
        chkSoloDisponibles.setOpaque(false);
        chkSoloDisponibles.addActionListener(e -> aplicarFiltros());
        
        filterPanel.add(chkSoloDisponibles);
        
        // Panel de botones
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonsPanel.setOpaque(false);
        
        btnAgregar = new RoundedButton("Agregar", UIConstants.ADD_ICON, UIConstants.SECONDARY_COLOR);
        btnAgregar.addActionListener(e -> mostrarFormularioAgregar());
        btnAgregar.setEnabled(tienePermisoEdicion);
        
        btnModificar = new RoundedButton("Modificar", UIConstants.EDIT_ICON, UIConstants.PRIMARY_COLOR);
        btnModificar.addActionListener(e -> mostrarFormularioModificar());
        btnModificar.setEnabled(false); // Inicialmente deshabilitado
        
        btnEliminar = new RoundedButton("Eliminar", UIConstants.DELETE_ICON, UIConstants.WARNING_COLOR);
        btnEliminar.addActionListener(e -> eliminarEquipoSeleccionado());
        btnEliminar.setEnabled(false); // Inicialmente deshabilitado
        
        btnRefrescar = new RoundedButton("Refrescar", UIConstants.REFRESH_ICON, Color.LIGHT_GRAY);
        btnRefrescar.setForeground(Color.DARK_GRAY);
        btnRefrescar.addActionListener(e -> cargarDatos());
        
        buttonsPanel.add(btnAgregar);
        buttonsPanel.add(btnModificar);
        buttonsPanel.add(btnEliminar);
        buttonsPanel.add(btnRefrescar);
        
        // Configurar botones según permisos
        btnModificar.setEnabled(tienePermisoEdicion);
        btnEliminar.setEnabled(tienePermisoEdicion);
        
        // Agregar componentes al panel superior
        searchPanel.add(searchBar, BorderLayout.CENTER);
        searchPanel.add(viewPanel, BorderLayout.EAST);
        
        topPanel.add(searchPanel, BorderLayout.NORTH);
        topPanel.add(filterPanel, BorderLayout.CENTER);
        topPanel.add(buttonsPanel, BorderLayout.SOUTH);
        
        // Panel de contenido principal
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        
        // Tabla de equipos
        String[] columnas = {"ID", "Nombre", "Tipo", "Marca", "Estado", "Precio Alquiler", "Fecha Adquisición", "Destino", "Disponible"};
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 8) { // Columna "Disponible"
                    return Boolean.class;
                }
                return Object.class;
            }
        };
        
        tblEquipos = new JTable(tableModel);
        UIUtils.styleTable(tblEquipos);
        
        tblEquipos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int filaSeleccionada = tblEquipos.getSelectedRow();
                if (filaSeleccionada != -1) {
                    int idEquipo = (int) tblEquipos.getValueAt(filaSeleccionada, 0);
                    equipoSeleccionado = controller.buscarEquipoPorId(idEquipo);
                    
                    // Habilitar botones de acción según permisos
                    actualizarBotonesAccion();
                    
                    if (e.getClickCount() == 2) {
                        // Doble clic muestra detalles o formulario de edición
                        if (tienePermisoEdicion) {
                            mostrarFormularioModificar();
                        } else {
                            mostrarDetallesEquipo(equipoSeleccionado);
                        }
                    }
                }
            }
        });
        
        tableScrollPane = new JScrollPane(tblEquipos);
        UIUtils.styleScrollPane(tableScrollPane);
        
        // Panel de tarjetas con GridLayout dinámico
        cardsContainer = new JPanel(new WrapLayout(FlowLayout.LEFT, 15, 15));
        cardsContainer.setOpaque(false);
        
        cardsScrollPane = new JScrollPane(cardsContainer);
        cardsScrollPane.setOpaque(false);
        cardsScrollPane.getViewport().setOpaque(false);
        cardsScrollPane.setBorder(BorderFactory.createEmptyBorder());
        cardsScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        // Inicialmente mostrar vista de tarjetas
        contentPanel.add(cardsScrollPane, BorderLayout.CENTER);
        
        // Agregar componentes al panel principal
        add(topPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
        
        // Cargar datos para los combos
        cargarTiposEquipo();
        cargarDestinosTuristicos();
    }
    
    /**
     * Cambia entre vista de tarjetas y vista de tabla
     */
    private void cambiarVista(boolean mostrarTarjetas) {
        if (this.mostrandoTarjetas == mostrarTarjetas) {
            return; // No hacer nada si ya está en la vista seleccionada
        }
        
        this.mostrandoTarjetas = mostrarTarjetas;
        
        // Obtener el panel contenedor (que tiene el BorderLayout)
        Container contentPanel = cardsScrollPane.getParent();
        contentPanel.removeAll();
        
        if (mostrarTarjetas) {
            contentPanel.add(cardsScrollPane, BorderLayout.CENTER);
        } else {
            contentPanel.add(tableScrollPane, BorderLayout.CENTER);
        }
        
        // Desseleccionar todo al cambiar de vista
        equipoSeleccionado = null;
        cardSeleccionada = null;
        tblEquipos.clearSelection();
        
        // Deshabilitar botones de acción
        actualizarBotonesAccion();
        
        // Redibujar
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    /**
     * Actualiza el estado de los botones de acción según la selección actual
     */
    private void actualizarBotonesAccion() {
        boolean haySeleccion = equipoSeleccionado != null;
        boolean puedeEditar = tienePermisoEdicion && haySeleccion;
        
        btnModificar.setEnabled(puedeEditar);
        btnEliminar.setEnabled(puedeEditar);
    }
    
    /**
     * Busca equipos según los criterios ingresados
     */
    private void buscarEquipos() {
        String criterio = searchBar.getSearchText().trim();
        
        if (criterio.isEmpty()) {
            aplicarFiltros(); // Mostrar todos (con filtros aplicados)
            return;
        }
        
        // Filtrar la lista de equipos actual según el criterio
        List<EquipoDeportivo> equiposFiltrados = new ArrayList<>();
        
        for (EquipoDeportivo equipo : equiposActuales) {
            if (coincideConCriterio(equipo, criterio)) {
                equiposFiltrados.add(equipo);
            }
        }
        
        actualizarVista(equiposFiltrados);
    }
    
    /**
     * Verifica si un equipo coincide con el criterio de búsqueda
     */
    private boolean coincideConCriterio(EquipoDeportivo equipo, String criterio) {
        String criterioLower = criterio.toLowerCase();
        
        return equipo.getNombre().toLowerCase().contains(criterioLower) ||
               equipo.getMarca().toLowerCase().contains(criterioLower) ||
               equipo.getTipo().getNombre().toLowerCase().contains(criterioLower) ||
               equipo.getDestino().getNombre().toLowerCase().contains(criterioLower);
    }
    
    /**
     * Aplica los filtros seleccionados a la lista completa de equipos
     */
    private void aplicarFiltros() {
        // Obtener valores de filtros
        TipoEquipo tipoSeleccionado = (TipoEquipo) cmbTipoFiltro.getSelectedItem();
        DestinoTuristico destinoSeleccionado = (DestinoTuristico) cmbDestinoFiltro.getSelectedItem();
        boolean soloDisponibles = chkSoloDisponibles.isSelected();
        
        // Filtrar la lista completa
        List<EquipoDeportivo> equiposFiltrados = new ArrayList<>();
        
        for (EquipoDeportivo equipo : equiposActuales) {
            boolean pasaFiltroTipo = tipoSeleccionado == null || 
                                     equipo.getTipo().getIdTipo() == tipoSeleccionado.getIdTipo();
            
            boolean pasaFiltroDestino = destinoSeleccionado == null || 
                                        equipo.getDestino().getIdDestino() == destinoSeleccionado.getIdDestino();
            
            boolean pasaFiltroDisponibilidad = !soloDisponibles || equipo.isDisponible();
            
            if (pasaFiltroTipo && pasaFiltroDestino && pasaFiltroDisponibilidad) {
                equiposFiltrados.add(equipo);
            }
        }
        
        actualizarVista(equiposFiltrados);
    }
    
    /**
     * Actualiza la vista con los equipos filtrados
     */
    private void actualizarVista(List<EquipoDeportivo> equipos) {
        // Actualizar tabla
        actualizarTabla(equipos);
        
        // Actualizar vista de tarjetas
        actualizarTarjetas(equipos);
        
        // Reiniciar selección
        equipoSeleccionado = null;
        cardSeleccionada = null;
        actualizarBotonesAccion();
    }
    
    /**
     * Carga los datos iniciales
     */
    private void cargarDatos() {
        // Obtener todos los equipos
        equiposActuales = controller.listarTodosLosEquipos();
        
        // Actualizar la vista según filtros activos
        aplicarFiltros();
    }
    
    /**
     * Carga los tipos de equipo para el filtro
     */
    private void cargarTiposEquipo() {
        cmbTipoFiltro.removeAllItems();
        cmbTipoFiltro.addItem(null); // Opción "Todos"
        
        List<TipoEquipo> tipos = controller.listarTodosLosTiposEquipo();
        for (TipoEquipo tipo : tipos) {
            cmbTipoFiltro.addItem(tipo);
        }
    }
    
    /**
     * Carga los destinos turísticos para el filtro
     */
    private void cargarDestinosTuristicos() {
        cmbDestinoFiltro.removeAllItems();
        cmbDestinoFiltro.addItem(null); // Opción "Todos"
        
        List<DestinoTuristico> destinos = controller.listarTodosLosDestinos();
        for (DestinoTuristico destino : destinos) {
            cmbDestinoFiltro.addItem(destino);
        }
    }
    
    /**
     * Actualiza la tabla con los equipos filtrados
     */
    private void actualizarTabla(List<EquipoDeportivo> equipos) {
        // Limpiar la tabla
        tableModel.setRowCount(0);
        
        if (equipos == null || equipos.isEmpty()) {
            return;
        }
        
        // Llenar la tabla con los datos
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        
        for (EquipoDeportivo equipo : equipos) {
            Object[] row = {
                equipo.getIdEquipo(),
                equipo.getNombre(),
                equipo.getTipo().getNombre(),
                equipo.getMarca(),
                equipo.getEstado(),
                UIUtils.formatCurrency(equipo.getPrecioAlquiler()),
                sdf.format(equipo.getFechaAdquisicion()),
                equipo.getDestino().getNombre(),
                equipo.isDisponible()
            };
            tableModel.addRow(row);
        }
    }
    
    /**
     * Actualiza la vista de tarjetas con los equipos filtrados
     */
    private void actualizarTarjetas(List<EquipoDeportivo> equipos) {
        // Limpiar el panel de tarjetas
        cardsContainer.removeAll();
        equipoCards.clear();
        
        if (equipos == null || equipos.isEmpty()) {
            // Mostrar mensaje si no hay equipos
            JLabel emptyLabel = new JLabel("No se encontraron equipos");
            emptyLabel.setFont(UIConstants.SUBTITLE_FONT);
            emptyLabel.setForeground(Color.GRAY);
            cardsContainer.add(emptyLabel);
        } else {
            // Crear tarjetas para cada equipo
            for (EquipoDeportivo equipo : equipos) {
                EquipoCard card = new EquipoCard(equipo);
                card.setCardListener(this);
                equipoCards.add(card);
                cardsContainer.add(card);
            }
        }
        
        // Refrescar el panel
        cardsContainer.revalidate();
        cardsContainer.repaint();
    }
    
    /**
     * Muestra el formulario para agregar un nuevo equipo
     */
    public void mostrarFormularioAgregar() {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Agregar Equipo", 
                                    Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(800, 600);
        dialog.setLocationRelativeTo(this);
        
        // Panel principal con dos columnas
        JPanel mainPanel = new JPanel(new BorderLayout(20, 0));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);
        
        // Panel izquierdo para la imagen
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setBackground(Color.WHITE);
        imagePanel.setBorder(new LineBorder(Color.LIGHT_GRAY, 1, true));
        imagePanel.setPreferredSize(new Dimension(300, 0));
        
        // Imagen inicial de placeholder
        JLabel imageLabel = new JLabel(UIConstants.DEFAULT_EQUIPMENT_IMAGE);
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        
        // Archivo seleccionado (para mantener la referencia)
        final File[] selectedImageFile = new File[1];
        
        // Botón para seleccionar imagen
        RoundedButton selectImageButton = new RoundedButton("Seleccionar Imagen", UIConstants.PRIMARY_COLOR);
        selectImageButton.setForeground(Color.WHITE);
        
        // Listener para el botón de seleccionar imagen
        selectImageButton.addActionListener(e -> {
            // Crear el selector de archivos
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Seleccionar Imagen para Equipo");
            
            // Filtrar solo archivos de imagen
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Archivos de Imagen", "jpg", "jpeg", "png", "svg");
            fileChooser.setFileFilter(filter);
            
            // Mostrar el diálogo
            int result = fileChooser.showOpenDialog(dialog);
            
            // Procesar la selección
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                selectedImageFile[0] = selectedFile;
                
                // Actualizar la vista previa
                try {
                    ImageIcon selectedIcon;
                    if (selectedFile.getName().toLowerCase().endsWith(".svg")) {
                        // Para SVG usar el cargador específico
                        selectedIcon = new ImageIcon(selectedFile.getPath());
                    } else {
                        // Para otros formatos usar la carga estándar
                        selectedIcon = new ImageIcon(selectedFile.getPath());
                    }
                    
                    // Redimensionar para la vista previa
                    Image img = selectedIcon.getImage().getScaledInstance(250, 180, Image.SCALE_SMOOTH);
                    imageLabel.setIcon(new ImageIcon(img));
                } catch (Exception ex) {
                    UIUtils.showErrorMessage(dialog, "Error al cargar la imagen: " + ex.getMessage(), "Error");
                }
            }
        });
        
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
        formPanel.add(new JLabel("Tipo:"), gbc);
        
        gbc.gridx = 1;
        JComboBox<TipoEquipo> cmbTipo = new JComboBox<>();
        cargarTiposEquipoEnCombo(cmbTipo);
        UIUtils.styleComboBox(cmbTipo);
        formPanel.add(cmbTipo, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Marca:"), gbc);
        
        gbc.gridx = 1;
        JTextField txtMarca = new JTextField(20);
        UIUtils.styleTextField(txtMarca);
        formPanel.add(txtMarca, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Estado:"), gbc);
        
        gbc.gridx = 1;
        JComboBox<String> cmbEstado = new JComboBox<>(new String[] {"Nuevo", "Bueno", "Regular", "Mantenimiento", "Fuera de servicio"});
        UIUtils.styleComboBox(cmbEstado);
        formPanel.add(cmbEstado, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Precio Alquiler:"), gbc);
        
        gbc.gridx = 1;
        JTextField txtPrecio = new JTextField(20);
        UIUtils.styleTextField(txtPrecio);
        formPanel.add(txtPrecio, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 5;
        formPanel.add(new JLabel("Fecha Adquisición:"), gbc);
        
        gbc.gridx = 1;
        JFormattedTextField txtFecha = new JFormattedTextField(new java.text.SimpleDateFormat("dd/MM/yyyy"));
        txtFecha.setValue(new Date());
        UIUtils.styleTextField(txtFecha);
        formPanel.add(txtFecha, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 6;
        formPanel.add(new JLabel("Destino:"), gbc);
        
        gbc.gridx = 1;
        JComboBox<DestinoTuristico> cmbDestino = new JComboBox<>();
        cargarDestinosTuristicosEnCombo(cmbDestino);
        UIUtils.styleComboBox(cmbDestino);
        formPanel.add(cmbDestino, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 7;
        formPanel.add(new JLabel("Disponible:"), gbc);
        
        gbc.gridx = 1;
        JCheckBox chkDisponible = new JCheckBox();
        chkDisponible.setSelected(true);
        chkDisponible.setOpaque(false);
        formPanel.add(chkDisponible, gbc);
        
        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);
        
        RoundedButton btnGuardar = new RoundedButton("Guardar", UIConstants.SECONDARY_COLOR);
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.addActionListener(e -> {
            try {
                String nombre = txtNombre.getText().trim();
                TipoEquipo tipo = (TipoEquipo) cmbTipo.getSelectedItem();
                String marca = txtMarca.getText().trim();
                String estado = (String) cmbEstado.getSelectedItem();
                double precio = Double.parseDouble(txtPrecio.getText().trim());
                Date fechaAdq = (Date) txtFecha.getValue();
                DestinoTuristico destino = (DestinoTuristico) cmbDestino.getSelectedItem();
                boolean disponible = chkDisponible.isSelected();
                
                if (nombre.isEmpty() || tipo == null || marca.isEmpty() || destino == null) {
                    UIUtils.showWarningMessage(dialog, "Todos los campos son obligatorios", "Campos incompletos");
                    return;
                }
                
                if (controller.registrarEquipo(nombre, tipo, marca, estado, precio, fechaAdq, destino, disponible)) {
                    // Buscar el equipo recién creado para obtener su ID
                    // Obtener todos los equipos y filtrar manualmente
                    List<EquipoDeportivo> todosEquipos = controller.listarTodosLosEquipos();
                    EquipoDeportivo nuevoEquipo = null;
                    
                    for (EquipoDeportivo equipo : todosEquipos) {
                        if (equipo.getNombre().equals(nombre) && equipo.getMarca().equals(marca)) {
                            nuevoEquipo = equipo;
                            break;
                        }
                    }
                    
                    // Si hay una imagen seleccionada, copiarla al directorio de recursos
                    if (selectedImageFile[0] != null && nuevoEquipo != null) {
                        String destFileName = nuevoEquipo.getIdEquipo() + ".jpg";
                        
                        // Ruta base del proyecto
                        String projectDir = System.getProperty("user.dir");
                        
                        // Ruta destino dentro del proyecto
                        String destFolderPath = projectDir + "/src/main/resources/com/deportur/resources/images/equipos/";
                        
                        // Crear directorio si no existe
                        File destFolder = new File(destFolderPath);
                        if (!destFolder.exists()) {
                            destFolder.mkdirs();
                        }
                        
                        File destFile = new File(destFolder, destFileName);
                        
                        try {
                            // Copiar el archivo
                            Files.copy(selectedImageFile[0].toPath(), destFile.toPath(), 
                                      StandardCopyOption.REPLACE_EXISTING);
                        } catch (IOException ex) {
                            System.err.println("Error al copiar la imagen: " + ex.getMessage());
                            // Continuar con el proceso a pesar del error en la imagen
                        }
                    }
                    
                    UIUtils.showInfoMessage(dialog, "Equipo registrado con éxito", "Éxito");
                    dialog.dispose();
                    cargarDatos();
                } else {
                    UIUtils.showErrorMessage(dialog, "Error al registrar el equipo", "Error");
                }
            } catch (NumberFormatException ex) {
                UIUtils.showErrorMessage(dialog, "El precio debe ser un valor numérico", "Error");
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
     * Muestra el formulario para modificar un equipo existente
     */
    private void mostrarFormularioModificar() {
        if (equipoSeleccionado == null) {
            UIUtils.showWarningMessage(this, "Debe seleccionar un equipo para modificar", "Advertencia");
            return;
        }
        
        // Similar a mostrarFormularioAgregar pero con datos precargados
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Modificar Equipo", 
                             Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(800, 600);
        dialog.setLocationRelativeTo(this);
        
        // Crear panel principal con dos columnas
        JPanel mainPanel = new JPanel(new BorderLayout(20, 0));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);
        
        // Panel izquierdo para la imagen
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setBackground(Color.WHITE);
        imagePanel.setBorder(new LineBorder(Color.LIGHT_GRAY, 1, true));
        imagePanel.setPreferredSize(new Dimension(300, 0));
        
        // Cargar imagen del equipo
        ImageIcon equipmentIcon = ImageCache.getEquipmentImage(equipoSeleccionado.getIdEquipo());
        JLabel imageLabel = new JLabel(equipmentIcon);
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        
        // Archivo seleccionado (para mantener la referencia)
        final File[] selectedImageFile = new File[1];
        
        // Botón para cambiar la imagen
        RoundedButton selectImageButton = new RoundedButton("Cambiar Imagen", UIConstants.PRIMARY_COLOR);
        selectImageButton.setForeground(Color.WHITE);
        selectImageButton.addActionListener(e -> {
            // Crear el selector de archivos
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Seleccionar Nueva Imagen para Equipo");
            
            // Filtrar solo archivos de imagen
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Archivos de Imagen", "jpg", "jpeg", "png", "svg");
            fileChooser.setFileFilter(filter);
            
            // Mostrar el diálogo
            int result = fileChooser.showOpenDialog(dialog);
            
            // Procesar la selección
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                selectedImageFile[0] = selectedFile;
                
                // Actualizar la vista previa
                try {
                    ImageIcon selectedIcon;
                    if (selectedFile.getName().toLowerCase().endsWith(".svg")) {
                        // Para SVG usar el cargador específico
                        selectedIcon = new ImageIcon(selectedFile.getPath());
                    } else {
                        // Para otros formatos usar la carga estándar
                        selectedIcon = new ImageIcon(selectedFile.getPath());
                    }
                    
                    // Redimensionar para la vista previa
                    Image img = selectedIcon.getImage().getScaledInstance(250, 180, Image.SCALE_SMOOTH);
                    imageLabel.setIcon(new ImageIcon(img));
                } catch (Exception ex) {
                    UIUtils.showErrorMessage(dialog, "Error al cargar la imagen: " + ex.getMessage(), "Error");
                }
            }
        });
        
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
        
        // Campos del formulario con datos del equipo
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Nombre:"), gbc);
        
        gbc.gridx = 1;
        JTextField txtNombre = new JTextField(20);
        txtNombre.setText(equipoSeleccionado.getNombre());
        UIUtils.styleTextField(txtNombre);
        formPanel.add(txtNombre, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Tipo:"), gbc);
        
        gbc.gridx = 1;
        JComboBox<TipoEquipo> cmbTipo = new JComboBox<>();
        cargarTiposEquipoEnCombo(cmbTipo);
        seleccionarTipoEquipo(cmbTipo, equipoSeleccionado.getTipo());
        UIUtils.styleComboBox(cmbTipo);
        formPanel.add(cmbTipo, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Marca:"), gbc);
        
        gbc.gridx = 1;
        JTextField txtMarca = new JTextField(20);
        txtMarca.setText(equipoSeleccionado.getMarca());
        UIUtils.styleTextField(txtMarca);
        formPanel.add(txtMarca, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Estado:"), gbc);
        
        gbc.gridx = 1;
        JComboBox<String> cmbEstado = new JComboBox<>(new String[] {"Nuevo", "Bueno", "Regular", "Mantenimiento", "Fuera de servicio"});
        cmbEstado.setSelectedItem(equipoSeleccionado.getEstado());
        UIUtils.styleComboBox(cmbEstado);
        formPanel.add(cmbEstado, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Precio Alquiler:"), gbc);
        
        gbc.gridx = 1;
        JTextField txtPrecio = new JTextField(20);
        txtPrecio.setText(String.valueOf(equipoSeleccionado.getPrecioAlquiler()));
        UIUtils.styleTextField(txtPrecio);
        formPanel.add(txtPrecio, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 5;
        formPanel.add(new JLabel("Fecha Adquisición:"), gbc);
        
        gbc.gridx = 1;
        JFormattedTextField txtFecha = new JFormattedTextField(new java.text.SimpleDateFormat("dd/MM/yyyy"));
        txtFecha.setValue(equipoSeleccionado.getFechaAdquisicion());
        UIUtils.styleTextField(txtFecha);
        formPanel.add(txtFecha, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 6;
        formPanel.add(new JLabel("Destino:"), gbc);
        
        gbc.gridx = 1;
        JComboBox<DestinoTuristico> cmbDestino = new JComboBox<>();
        cargarDestinosTuristicosEnCombo(cmbDestino);
        seleccionarDestinoTuristico(cmbDestino, equipoSeleccionado.getDestino());
        UIUtils.styleComboBox(cmbDestino);
        formPanel.add(cmbDestino, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 7;
        formPanel.add(new JLabel("Disponible:"), gbc);
        
        gbc.gridx = 1;
        JCheckBox chkDisponible = new JCheckBox();
        chkDisponible.setSelected(equipoSeleccionado.isDisponible());
        chkDisponible.setOpaque(false);
        formPanel.add(chkDisponible, gbc);
        
        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);
        
        RoundedButton btnGuardar = new RoundedButton("Guardar Cambios", UIConstants.SECONDARY_COLOR);
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.addActionListener(e -> {
            try {
                String nombre = txtNombre.getText().trim();
                TipoEquipo tipo = (TipoEquipo) cmbTipo.getSelectedItem();
                String marca = txtMarca.getText().trim();
                String estado = (String) cmbEstado.getSelectedItem();
                double precio = Double.parseDouble(txtPrecio.getText().trim());
                Date fechaAdq = (Date) txtFecha.getValue();
                DestinoTuristico destino = (DestinoTuristico) cmbDestino.getSelectedItem();
                boolean disponible = chkDisponible.isSelected();
                
                if (nombre.isEmpty() || tipo == null || marca.isEmpty() || destino == null) {
                    UIUtils.showWarningMessage(dialog, "Todos los campos son obligatorios", "Campos incompletos");
                    return;
                }
                
                if (controller.actualizarEquipo(equipoSeleccionado.getIdEquipo(), nombre, tipo, marca, estado, precio, fechaAdq, destino, disponible)) {
                    // Si hay una imagen seleccionada, copiarla al directorio de recursos
                    if (selectedImageFile[0] != null) {
                        String destFileName = equipoSeleccionado.getIdEquipo() + ".jpg";
                        
                        // Ruta base del proyecto
                        String projectDir = System.getProperty("user.dir");
                        
                        // Ruta destino dentro del proyecto
                        String destFolderPath = projectDir + "/src/main/resources/com/deportur/resources/images/equipos/";
                        
                        // Crear directorio si no existe
                        File destFolder = new File(destFolderPath);
                        if (!destFolder.exists()) {
                            destFolder.mkdirs();
                        }
                        
                        File destFile = new File(destFolder, destFileName);
                        
                        try {
                            // Copiar el archivo
                            Files.copy(selectedImageFile[0].toPath(), destFile.toPath(), 
                                      StandardCopyOption.REPLACE_EXISTING);
                            
                            // Limpiar la caché para que se cargue la nueva imagen
                            ImageCache.clearCache();
                        } catch (IOException ex) {
                            System.err.println("Error al copiar la imagen: " + ex.getMessage());
                            // Continuar con el proceso a pesar del error en la imagen
                        }
                    }
                    
                    UIUtils.showInfoMessage(dialog, "Equipo actualizado con éxito", "Éxito");
                    dialog.dispose();
                    cargarDatos();
                } else {
                    UIUtils.showErrorMessage(dialog, "Error al actualizar el equipo", "Error");
                }
            } catch (NumberFormatException ex) {
                UIUtils.showErrorMessage(dialog, "El precio debe ser un valor numérico", "Error");
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
     * Elimina el equipo actualmente seleccionado
     */
    private void eliminarEquipoSeleccionado() {
        if (equipoSeleccionado == null) {
            UIUtils.showWarningMessage(this, "Debe seleccionar un equipo para eliminar", "Advertencia");
            return;
        }
        
        int confirmacion = UIUtils.showConfirmDialog(
            this,
            "¿Está seguro de eliminar el equipo '" + equipoSeleccionado.getNombre() + "'?",
            "Confirmar eliminación"
        );
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            if (controller.eliminarEquipo(equipoSeleccionado.getIdEquipo())) {
                UIUtils.showInfoMessage(this, "Equipo eliminado con éxito", "Éxito");
                cargarDatos();
            } else {
                UIUtils.showErrorMessage(this, "Error al eliminar el equipo", "Error");
            }
        }
    }
    
    /**
     * Muestra los detalles de un equipo seleccionado
     */
    private void mostrarDetallesEquipo(EquipoDeportivo equipo) {
        if (equipo == null) return;
        
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Detalles del Equipo", 
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
        imagePanel.setPreferredSize(new Dimension(250, 0));
        
        ImageIcon equipmentIcon = ImageCache.getEquipmentImage(equipo.getIdEquipo());
        JLabel imageLabel = new JLabel(equipmentIcon);
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        
        imagePanel.add(imageLabel, BorderLayout.CENTER);
        
        // Panel de información (derecha)
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(Color.WHITE);
        
        // Encabezado con nombre y tipo
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        JLabel nameLabel = new JLabel(equipo.getNombre());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        nameLabel.setForeground(UIConstants.PRIMARY_COLOR);
        
        JLabel typeLabel = new JLabel(equipo.getTipo().getNombre());
        typeLabel.setFont(new Font("Segoe UI", Font.ITALIC, 16));
        typeLabel.setForeground(Color.DARK_GRAY);
        
        headerPanel.add(nameLabel, BorderLayout.NORTH);
        headerPanel.add(typeLabel, BorderLayout.CENTER);
        
        // Panel de características
        JPanel detailsPanel = new JPanel(new GridLayout(0, 1, 0, 10));
        detailsPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        detailsPanel.setOpaque(false);
        
        // Estado con indicador de color
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        statusPanel.setOpaque(false);
        statusPanel.add(new JLabel("Estado:"));
        statusPanel.add(StatusIndicator.createForEstado(equipo.getEstado()));
        
        if (equipo.isDisponible()) {
            statusPanel.add(Box.createHorizontalStrut(20));
            statusPanel.add(StatusIndicator.createPillIndicator("Disponible", new Color(0, 150, 0)));
        } else {
            statusPanel.add(Box.createHorizontalStrut(20));
            statusPanel.add(StatusIndicator.createPillIndicator("No Disponible", Color.GRAY));
        }
        
        // Precio
        JPanel pricePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pricePanel.setOpaque(false);
        
        JLabel priceLabel = new JLabel("Precio de Alquiler:");
        JLabel priceValueLabel = new JLabel(UIUtils.formatCurrency(equipo.getPrecioAlquiler()) + " / día");
        priceValueLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        priceValueLabel.setForeground(UIConstants.SECONDARY_COLOR);
        
        pricePanel.add(priceLabel);
        pricePanel.add(priceValueLabel);
        
        // Marca
        JPanel brandPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        brandPanel.setOpaque(false);
        
        JLabel brandLabel = new JLabel("Marca:");
        JLabel brandValueLabel = new JLabel(equipo.getMarca());
        brandValueLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        brandPanel.add(brandLabel);
        brandPanel.add(brandValueLabel);
        
        // Fecha de adquisición
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        datePanel.setOpaque(false);
        
        JLabel dateLabel = new JLabel("Fecha de Adquisición:");
        JLabel dateValueLabel = new JLabel(UIUtils.formatDate(equipo.getFechaAdquisicion()));
        
        datePanel.add(dateLabel);
        datePanel.add(dateValueLabel);
        
        // Destino
        JPanel destinationPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        destinationPanel.setOpaque(false);
        
        JLabel destinationLabel = new JLabel("Ubicación:");
        JLabel destinationValueLabel = new JLabel(equipo.getDestino().getNombre() + " (" + equipo.getDestino().getUbicacion() + ")");
        
        destinationPanel.add(destinationLabel);
        destinationPanel.add(destinationValueLabel);
        
        // Agregar todos los paneles de detalles
        detailsPanel.add(statusPanel);
        detailsPanel.add(pricePanel);
        detailsPanel.add(brandPanel);
        detailsPanel.add(datePanel);
        detailsPanel.add(destinationPanel);
        
        // Descripción (si existe)
        if (equipo.getTipo().getDescripcion() != null && !equipo.getTipo().getDescripcion().isEmpty()) {
            JPanel descriptionPanel = new JPanel(new BorderLayout());
            descriptionPanel.setOpaque(false);
            descriptionPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
            
            JLabel descTitleLabel = new JLabel("Descripción:");
            descTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            
            JTextArea descTextArea = new JTextArea(equipo.getTipo().getDescripcion());
            descTextArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            descTextArea.setLineWrap(true);
            descTextArea.setWrapStyleWord(true);
            descTextArea.setEditable(false);
            descTextArea.setBackground(new Color(245, 245, 245));
            descTextArea.setBorder(new EmptyBorder(10, 10, 10, 10));
            
            descriptionPanel.add(descTitleLabel, BorderLayout.NORTH);
            descriptionPanel.add(descTextArea, BorderLayout.CENTER);
            
            infoPanel.add(descriptionPanel, BorderLayout.SOUTH);
        }
        
        // Agregar componentes al panel de información
        infoPanel.add(headerPanel, BorderLayout.NORTH);
        infoPanel.add(detailsPanel, BorderLayout.CENTER);
        
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
    
    /**
     * Carga tipos de equipo en un combo
     */
    private void cargarTiposEquipoEnCombo(JComboBox<TipoEquipo> comboBox) {
        comboBox.removeAllItems();
        List<TipoEquipo> tipos = controller.listarTodosLosTiposEquipo();
        for (TipoEquipo tipo : tipos) {
            comboBox.addItem(tipo);
        }
    }
    
    /**
     * Carga destinos en un combo
     */
    private void cargarDestinosTuristicosEnCombo(JComboBox<DestinoTuristico> comboBox) {
        comboBox.removeAllItems();
        List<DestinoTuristico> destinos = controller.listarTodosLosDestinos();
        for (DestinoTuristico destino : destinos) {
            comboBox.addItem(destino);
        }
    }
    
    /**
     * Selecciona un tipo de equipo en el combo
     */
    private void seleccionarTipoEquipo(JComboBox<TipoEquipo> comboBox, TipoEquipo tipo) {
        for (int i = 0; i < comboBox.getItemCount(); i++) {
            TipoEquipo item = comboBox.getItemAt(i);
            if (item.getIdTipo() == tipo.getIdTipo()) {
                comboBox.setSelectedIndex(i);
                break;
            }
        }
    }
    
    /**
     * Selecciona un destino en el combo
     */
    private void seleccionarDestinoTuristico(JComboBox<DestinoTuristico> comboBox, DestinoTuristico destino) {
        for (int i = 0; i < comboBox.getItemCount(); i++) {
            DestinoTuristico item = comboBox.getItemAt(i);
            if (item.getIdDestino() == destino.getIdDestino()) {
                comboBox.setSelectedIndex(i);
                break;
            }
        }
    }
    
    /**
     * WrapLayout para display de tarjetas (flujo con wrap)
     */
    private class WrapLayout extends FlowLayout {
        public WrapLayout(int align, int hgap, int vgap) {
            super(align, hgap, vgap);
        }
        
        @Override
        public Dimension preferredLayoutSize(Container target) {
            return layoutSize(target, true);
        }
        
        @Override
        public Dimension minimumLayoutSize(Container target) {
            return layoutSize(target, false);
        }
        
        private Dimension layoutSize(Container target, boolean preferred) {
            synchronized (target.getTreeLock()) {
                int targetWidth = target.getWidth();
                
                if (targetWidth == 0) {
                    targetWidth = Integer.MAX_VALUE;
                }
                
                int hgap = getHgap();
                int vgap = getVgap();
                Insets insets = target.getInsets();
                int horizontalInsetsAndGap = insets.left + insets.right + (hgap * 2);
                int maxWidth = targetWidth - horizontalInsetsAndGap;
                
                Dimension dim = new Dimension(0, 0);
                int rowWidth = 0;
                int rowHeight = 0;
                
                int nmembers = target.getComponentCount();
                
                for (int i = 0; i < nmembers; i++) {
                    Component m = target.getComponent(i);
                    
                    if (m.isVisible()) {
                        Dimension d = preferred ? m.getPreferredSize() : m.getMinimumSize();
                        
                        if (rowWidth + d.width > maxWidth) {
                            addRow(dim, rowWidth, rowHeight);
                            rowWidth = 0;
                            rowHeight = 0;
                        }
                        
                        if (rowWidth != 0) {
                            rowWidth += hgap;
                        }
                        
                        rowWidth += d.width;
                        rowHeight = Math.max(rowHeight, d.height);
                    }
                }
                
                addRow(dim, rowWidth, rowHeight);
                
                dim.width += horizontalInsetsAndGap;
                dim.height += insets.top + insets.bottom + vgap * 2;
                
                return dim;
            }
        }
        
        private void addRow(Dimension dim, int rowWidth, int rowHeight) {
            dim.width = Math.max(dim.width, rowWidth);
            
            if (dim.height > 0) {
                dim.height += getVgap();
            }
            
            dim.height += rowHeight;
        }
    }
    
    // Implementación de la interfaz EquipoCard.EquipoCardListener
    
    @Override
    public void onCardClicked(EquipoCard card, EquipoDeportivo equipo) {
        // Deseleccionar la tarjeta anteriormente seleccionada
        if (cardSeleccionada != null) {
            cardSeleccionada.setSelected(false);
        }
        
        // Seleccionar la nueva tarjeta
        cardSeleccionada = card;
        cardSeleccionada.setSelected(true);
        
        // Actualizar el equipo seleccionado
        equipoSeleccionado = equipo;
        
        // Actualizar botones de acción
        actualizarBotonesAccion();
    }
    
    @Override
    public void onCardDoubleClicked(EquipoCard card, EquipoDeportivo equipo) {
        equipoSeleccionado = equipo;
        
        // Al hacer doble clic, mostrar detalles o formulario de edición
        if (tienePermisoEdicion) {
            mostrarFormularioModificar();
        } else {
            mostrarDetallesEquipo(equipo);
        }
    }
}