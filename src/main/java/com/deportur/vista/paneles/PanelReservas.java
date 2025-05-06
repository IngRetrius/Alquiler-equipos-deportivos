package com.deportur.vista.paneles;

import com.deportur.controlador.ReservasController;
import com.deportur.controlador.InventarioController;
import com.deportur.modelo.Reserva;
import com.deportur.modelo.Cliente;
import com.deportur.modelo.DestinoTuristico;
import com.deportur.modelo.EquipoDeportivo;
import com.deportur.modelo.DetalleReserva;
import com.deportur.vista.MainFrame;
import com.deportur.vista.componentes.RoundedButton;
import com.deportur.vista.componentes.SearchBar;
import com.deportur.vista.componentes.StatusIndicator;
import com.deportur.vista.util.UIConstants;
import com.deportur.vista.util.UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Panel de gestión de reservas con diseño mejorado
 */
public class PanelReservas extends JPanel {
    
    private ReservasController controller;
    private InventarioController inventarioController;
    private boolean tienePermiso;
    
    // Componentes de la interfaz
    private SearchBar searchBar;
    private RoundedButton btnCrear;
    private RoundedButton btnModificar;
    private RoundedButton btnCancelar;
    private RoundedButton btnConsultar;
    private RoundedButton btnRefrescar;
    private JTable tblReservas;
    private DefaultTableModel tableModel;
    private JScrollPane scrollPane;
    private JComboBox<String> cmbEstadoFiltro;
    private JFormattedTextField dateDesde;
    private JFormattedTextField dateHasta;
    private JCheckBox chkFiltroFecha;
    
    // Estado actual
    private List<Reserva> reservasActuales = new ArrayList<>();
    private Reserva reservaSeleccionada;
    
    public PanelReservas() {
        controller = new ReservasController();
        inventarioController = new InventarioController();
        tienePermiso = MainFrame.getUsuarioController().puedeGestionarReservas();
        
        initComponents();
        cargarDatos();
    }
    
    private void initComponents() {
        // Configurar el layout
        setLayout(new BorderLayout(0, 10));
        setBackground(UIConstants.BACKGROUND_COLOR);
        
        // Panel superior con búsqueda, filtros y botones
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setOpaque(false);
        
        // Panel de búsqueda
        JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
        searchPanel.setOpaque(false);
        
        String[] filterOptions = {"Todas", "Por Cliente", "Por Destino"};
        searchBar = new SearchBar("Buscar reservas...", filterOptions);
        searchBar.addSearchListener(e -> buscarReservas());
        searchBar.addSearchFieldKeyListener(e -> buscarReservas());
        
        searchPanel.add(searchBar, BorderLayout.CENTER);
        
        // Panel de filtros
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterPanel.setOpaque(false);
        
        // Filtro por estado
        filterPanel.add(new JLabel("Estado:"));
        cmbEstadoFiltro = new JComboBox<>(new String[] {"Todos", "Pendiente", "Confirmada", "En progreso", "Finalizada", "Cancelada"});
        UIUtils.styleComboBox(cmbEstadoFiltro);
        cmbEstadoFiltro.addActionListener(e -> aplicarFiltros());
        filterPanel.add(cmbEstadoFiltro);
        
        // Filtro por fecha
        chkFiltroFecha = new JCheckBox("Filtrar por fechas");
        chkFiltroFecha.setOpaque(false);
        chkFiltroFecha.addActionListener(e -> {
            dateDesde.setEnabled(chkFiltroFecha.isSelected());
            dateHasta.setEnabled(chkFiltroFecha.isSelected());
            aplicarFiltros();
        });
        filterPanel.add(chkFiltroFecha);
        
        filterPanel.add(new JLabel("Desde:"));
        // Crear un formato de fecha
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        dateDesde = new JFormattedTextField(dateFormat);
        dateDesde.setPreferredSize(new Dimension(120, UIConstants.FIELD_HEIGHT));
        dateDesde.setValue(new Date());
        dateDesde.setEnabled(false);
        dateDesde.addPropertyChangeListener("value", e -> aplicarFiltros());
        filterPanel.add(dateDesde);
        
        filterPanel.add(new JLabel("Hasta:"));
        dateHasta = new JFormattedTextField(dateFormat);
        dateHasta.setPreferredSize(new Dimension(120, UIConstants.FIELD_HEIGHT));
        // Establecer fecha hasta como 30 días después de la fecha actual
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 30);
        dateHasta.setValue(calendar.getTime());
        dateHasta.setEnabled(false);
        dateHasta.addPropertyChangeListener("value", e -> aplicarFiltros());
        filterPanel.add(dateHasta);
        
        // Panel de botones
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonsPanel.setOpaque(false);
        
        btnCrear = new RoundedButton("Nueva Reserva", UIConstants.ADD_ICON, UIConstants.SECONDARY_COLOR);
        btnCrear.addActionListener(e -> mostrarFormularioCrear());
        btnCrear.setEnabled(tienePermiso);
        
        btnModificar = new RoundedButton("Modificar", UIConstants.EDIT_ICON, UIConstants.PRIMARY_COLOR);
        btnModificar.addActionListener(e -> mostrarFormularioModificar());
        btnModificar.setEnabled(false); // Inicialmente deshabilitado
        
        btnCancelar = new RoundedButton("Cancelar Reserva", UIConstants.DELETE_ICON, UIConstants.WARNING_COLOR);
        btnCancelar.addActionListener(e -> cancelarReservaSeleccionada());
        btnCancelar.setEnabled(false); // Inicialmente deshabilitado
        
        btnConsultar = new RoundedButton("Consultar", UIConstants.SEARCH_ICON, new Color(70, 130, 180));
        btnConsultar.setForeground(Color.WHITE);
        btnConsultar.addActionListener(e -> consultarReservaSeleccionada());
        btnConsultar.setEnabled(false); // Inicialmente deshabilitado
        
        btnRefrescar = new RoundedButton("Refrescar", UIConstants.REFRESH_ICON, Color.LIGHT_GRAY);
        btnRefrescar.setForeground(Color.DARK_GRAY);
        btnRefrescar.addActionListener(e -> cargarDatos());
        
        buttonsPanel.add(btnCrear);
        buttonsPanel.add(btnModificar);
        buttonsPanel.add(btnCancelar);
        buttonsPanel.add(btnConsultar);
        buttonsPanel.add(btnRefrescar);
        
        // Agregar componentes al panel superior
        topPanel.add(searchPanel, BorderLayout.NORTH);
        topPanel.add(filterPanel, BorderLayout.CENTER);
        topPanel.add(buttonsPanel, BorderLayout.SOUTH);
        
        // Tabla de reservas
        String[] columnas = {"ID", "Cliente", "Fecha Creación", "Fecha Inicio", "Fecha Fin", "Destino", "Estado"};
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tblReservas = new JTable(tableModel);
        UIUtils.styleTable(tblReservas);
        
        tblReservas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int filaSeleccionada = tblReservas.getSelectedRow();
                if (filaSeleccionada != -1) {
                    int idReserva = (int) tblReservas.getValueAt(filaSeleccionada, 0);
                    reservaSeleccionada = controller.consultarReserva(idReserva);
                    
                    // Habilitar/deshabilitar botones según el estado de la reserva
                    actualizarBotonesAccion();
                    
                    if (e.getClickCount() == 2) {
                        consultarReservaSeleccionada();
                    }
                }
            }
        });
        
        scrollPane = new JScrollPane(tblReservas);
        UIUtils.styleScrollPane(scrollPane);
        
        // Agregar componentes al panel principal
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    /**
     * Actualiza el estado de los botones de acción según la selección actual
     */
    private void actualizarBotonesAccion() {
        boolean haySeleccion = reservaSeleccionada != null;
        
        btnConsultar.setEnabled(haySeleccion);
        
        if (haySeleccion && tienePermiso) {
            // Verificar el estado de la reserva para determinar qué acciones están disponibles
            String estado = reservaSeleccionada.getEstado();
            
            boolean puedeModificar = "Pendiente".equals(estado) || "Confirmada".equals(estado);
            boolean puedeCancelar = !("Finalizada".equals(estado) || "Cancelada".equals(estado));
            
            btnModificar.setEnabled(puedeModificar);
            btnCancelar.setEnabled(puedeCancelar);
        } else {
            btnModificar.setEnabled(false);
            btnCancelar.setEnabled(false);
        }
    }
    
    /**
     * Busca reservas según los criterios ingresados
     */
    private void buscarReservas() {
        String criterio = searchBar.getSearchText().trim();
        String filtro = searchBar.getSelectedFilter();
        
        if (criterio.isEmpty()) {
            // Aplicar solo otros filtros
            aplicarFiltros();
            return;
        }
        
        // Filtrar según el criterio y tipo de filtro
        List<Reserva> reservasFiltradas = new ArrayList<>();
        
        try {
            if ("Por Cliente".equals(filtro)) {
                // Intentar buscar por ID de cliente primero
                try {
                    int idCliente = Integer.parseInt(criterio);
                    reservasFiltradas = controller.buscarReservasPorCliente(idCliente);
                } catch (NumberFormatException nfe) {
                    // Si no es un número, buscar por nombre/apellido en las reservas actuales
                    for (Reserva reserva : reservasActuales) {
                        Cliente cliente = reserva.getCliente();
                        if (cliente.getNombre().toLowerCase().contains(criterio.toLowerCase()) ||
                            cliente.getApellido().toLowerCase().contains(criterio.toLowerCase()) ||
                            cliente.getDocumento().toLowerCase().contains(criterio.toLowerCase())) {
                            reservasFiltradas.add(reserva);
                        }
                    }
                }
            } else if ("Por Destino".equals(filtro)) {
                // Intentar buscar por ID de destino primero
                try {
                    int idDestino = Integer.parseInt(criterio);
                    reservasFiltradas = controller.buscarReservasPorDestino(idDestino);
                } catch (NumberFormatException nfe) {
                    // Si no es un número, buscar por nombre de destino en las reservas actuales
                    for (Reserva reserva : reservasActuales) {
                        DestinoTuristico destino = reserva.getDestino();
                        if (destino.getNombre().toLowerCase().contains(criterio.toLowerCase()) ||
                            destino.getUbicacion().toLowerCase().contains(criterio.toLowerCase())) {
                            reservasFiltradas.add(reserva);
                        }
                    }
                }
            } else {
                // Filtro "Todas" - buscar en todos los campos disponibles
                for (Reserva reserva : reservasActuales) {
                    if (coincideConCriterio(reserva, criterio)) {
                        reservasFiltradas.add(reserva);
                    }
                }
            }
        } catch (Exception e) {
            UIUtils.showErrorMessage(this, "Error en la búsqueda: " + e.getMessage(), "Error");
            return;
        }
        
        // Aplicar filtros adicionales (estado y fecha)
        reservasFiltradas = aplicarFiltrosAdicionales(reservasFiltradas);
        
        // Actualizar tabla
        actualizarTabla(reservasFiltradas);
    }
    
    /**
     * Verifica si una reserva coincide con el criterio de búsqueda
     */
    private boolean coincideConCriterio(Reserva reserva, String criterio) {
        String criterioLower = criterio.toLowerCase();
        
        return reserva.getCliente().getNombre().toLowerCase().contains(criterioLower) ||
               reserva.getCliente().getApellido().toLowerCase().contains(criterioLower) ||
               reserva.getCliente().getDocumento().toLowerCase().contains(criterioLower) ||
               reserva.getDestino().getNombre().toLowerCase().contains(criterioLower) ||
               reserva.getEstado().toLowerCase().contains(criterioLower);
    }
    
    /**
     * Aplica los filtros seleccionados (estado y fechas)
     */
    private void aplicarFiltros() {
        // Aplicar filtros a la lista completa de reservas
        List<Reserva> reservasFiltradas = aplicarFiltrosAdicionales(reservasActuales);
        
        // Actualizar tabla
        actualizarTabla(reservasFiltradas);
    }
    
    /**
     * Aplica filtros adicionales (estado y fechas) a una lista de reservas
     */
    private List<Reserva> aplicarFiltrosAdicionales(List<Reserva> reservas) {
        List<Reserva> resultado = new ArrayList<>();
        
        // Obtener valores de filtros
        String estadoSeleccionado = (String) cmbEstadoFiltro.getSelectedItem();
        boolean filtrarPorFechas = chkFiltroFecha.isSelected();
        Date fechaDesde = (Date) dateDesde.getValue();
        Date fechaHasta = (Date) dateHasta.getValue();
        
        // Aplicar filtros
        for (Reserva reserva : reservas) {
            boolean pasaFiltroEstado = "Todos".equals(estadoSeleccionado) || 
                                     reserva.getEstado().equals(estadoSeleccionado);
            
            boolean pasaFiltroFechas = true;
            if (filtrarPorFechas && fechaDesde != null && fechaHasta != null) {
                // Verificar si hay solapamiento entre el rango de fechas del filtro y la reserva
                pasaFiltroFechas = 
                    !(reserva.getFechaFin().before(fechaDesde) || reserva.getFechaInicio().after(fechaHasta));
            }
            
            if (pasaFiltroEstado && pasaFiltroFechas) {
                resultado.add(reserva);
            }
        }
        
        return resultado;
    }
    
    /**
     * Carga los datos iniciales
     */
    private void cargarDatos() {
        reservasActuales = controller.listarTodasLasReservas();
        actualizarTabla(reservasActuales);
        
        // Reiniciar selección
        reservaSeleccionada = null;
        actualizarBotonesAccion();
    }
    
    /**
     * Actualiza la tabla con las reservas filtradas
     */
    private void actualizarTabla(List<Reserva> reservas) {
        // Limpiar la tabla
        tableModel.setRowCount(0);
        
        if (reservas == null || reservas.isEmpty()) {
            return;
        }
        
        // Llenar la tabla con los datos
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        
        for (Reserva reserva : reservas) {
            Object[] row = {
                reserva.getIdReserva(),
                reserva.getCliente().getNombre() + " " + reserva.getCliente().getApellido(),
                sdf.format(reserva.getFechaCreacion()),
                sdf.format(reserva.getFechaInicio()),
                sdf.format(reserva.getFechaFin()),
                reserva.getDestino().getNombre(),
                reserva.getEstado()
            };
            tableModel.addRow(row);
        }
        
        // Aplicar renderer para colorear filas según estado
        tblReservas.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    String estado = (String) table.getValueAt(row, 6); // Columna de estado
                    
                    switch (estado) {
                        case "Pendiente":
                            c.setBackground(new Color(230, 230, 250)); // Lavender
                            break;
                        case "Confirmada":
                            c.setBackground(new Color(240, 255, 240)); // Honeydew
                            break;
                        case "En progreso":
                            c.setBackground(new Color(255, 255, 224)); // Light Yellow
                            break;
                        case "Finalizada":
                            c.setBackground(new Color(245, 245, 245)); // Light Grey
                            break;
                        case "Cancelada":
                            c.setBackground(new Color(255, 228, 225)); // Misty Rose
                            break;
                        default:
                            c.setBackground(Color.WHITE);
                            break;
                    }
                }
                
                return c;
            }
        });
    }
    
    /**
     * Muestra el formulario para crear una nueva reserva
     */
    public void mostrarFormularioCrear() {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Nueva Reserva", 
                                    Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(900, 600);
        dialog.setLocationRelativeTo(this);
        
        // Panel principal con pestañas (Wizard)
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Panel de datos de la reserva (Paso 1)
        JPanel panelDatos = new JPanel(new BorderLayout(20, 20));
        panelDatos.setBorder(new EmptyBorder(20, 20, 20, 20));
        panelDatos.setBackground(Color.WHITE);
        
        // Panel de formulario
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1.0;
        
        // Cliente
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Cliente:"), gbc);
        
        gbc.gridx = 1;
        JComboBox<Cliente> cmbCliente = new JComboBox<>();
        cargarClientes(cmbCliente);
        UIUtils.styleComboBox(cmbCliente);
        formPanel.add(cmbCliente, gbc);
        
        gbc.gridx = 2;
        RoundedButton btnNuevoCliente = new RoundedButton("Nuevo Cliente", UIConstants.ADD_ICON, UIConstants.PRIMARY_COLOR);
        btnNuevoCliente.setForeground(Color.WHITE);
        btnNuevoCliente.addActionListener(e -> mostrarFormularioCliente(cmbCliente));
        formPanel.add(btnNuevoCliente, gbc);
        
        // Destino
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Destino:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        JComboBox<DestinoTuristico> cmbDestino = new JComboBox<>();
        cargarDestinos(cmbDestino);
        UIUtils.styleComboBox(cmbDestino);
        formPanel.add(cmbDestino, gbc);
        
        // Fechas
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("Fecha Inicio:"), gbc);
        
        gbc.gridx = 1;
        JFormattedTextField txtFechaInicio = new JFormattedTextField(new SimpleDateFormat("dd/MM/yyyy"));
        txtFechaInicio.setValue(new Date());
        txtFechaInicio.setPreferredSize(new Dimension(0, UIConstants.FIELD_HEIGHT));
        formPanel.add(txtFechaInicio, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Fecha Fin:"), gbc);
        
        gbc.gridx = 1;
        JFormattedTextField txtFechaFin = new JFormattedTextField(new SimpleDateFormat("dd/MM/yyyy"));
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 7); // Una semana después
        txtFechaFin.setValue(calendar.getTime());
        txtFechaFin.setPreferredSize(new Dimension(0, UIConstants.FIELD_HEIGHT));
        formPanel.add(txtFechaFin, gbc);
        
        // Panel de equipos disponibles
        JPanel equiposPanel = new JPanel(new BorderLayout(10, 10));
        equiposPanel.setBorder(BorderFactory.createTitledBorder("Selección de Equipos"));
        equiposPanel.setBackground(Color.WHITE);
        
        // Botón para buscar equipos disponibles
        JPanel buscarEquiposPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buscarEquiposPanel.setOpaque(false);
        
        RoundedButton btnBuscarEquipos = new RoundedButton("Buscar Equipos Disponibles", UIConstants.SEARCH_ICON, UIConstants.PRIMARY_COLOR);
        btnBuscarEquipos.setForeground(Color.WHITE);
        buscarEquiposPanel.add(btnBuscarEquipos);
        
        // Listas de equipos
        JPanel listasPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        listasPanel.setOpaque(false);
        
        // Lista de equipos disponibles
        DefaultListModel<EquipoDeportivo> modeloDisponibles = new DefaultListModel<>();
        JList<EquipoDeportivo> listaDisponibles = new JList<>(modeloDisponibles);
        listaDisponibles.setCellRenderer(new EquipoListCellRenderer());
        listaDisponibles.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        
        JScrollPane scrollDisponibles = new JScrollPane(listaDisponibles);
        scrollDisponibles.setBorder(BorderFactory.createTitledBorder("Equipos Disponibles"));
        
        // Lista de equipos seleccionados
        DefaultListModel<EquipoDeportivo> modeloSeleccionados = new DefaultListModel<>();
        JList<EquipoDeportivo> listaSeleccionados = new JList<>(modeloSeleccionados);
        listaSeleccionados.setCellRenderer(new EquipoListCellRenderer());
        listaSeleccionados.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        
        JScrollPane scrollSeleccionados = new JScrollPane(listaSeleccionados);
        scrollSeleccionados.setBorder(BorderFactory.createTitledBorder("Equipos Seleccionados"));
        
        // Botones para mover equipos entre listas
        JPanel botonesMovimientoPanel = new JPanel(new GridLayout(2, 1, 0, 10));
        botonesMovimientoPanel.setOpaque(false);
        
        RoundedButton btnAgregar = new RoundedButton(">>", UIConstants.SECONDARY_COLOR);
        btnAgregar.setForeground(Color.WHITE);
        
        RoundedButton btnQuitar = new RoundedButton("<<", UIConstants.SECONDARY_COLOR);
        btnQuitar.setForeground(Color.WHITE);
        
        botonesMovimientoPanel.add(btnAgregar);
        botonesMovimientoPanel.add(btnQuitar);
        
        // Panel central con botones
        JPanel panelCentro = new JPanel(new BorderLayout());
        panelCentro.setOpaque(false);
        panelCentro.add(botonesMovimientoPanel, BorderLayout.CENTER);
        
        // Añadir listas y botones al panel
        listasPanel.add(scrollDisponibles);
        listasPanel.add(scrollSeleccionados);
        
        // Panel para mostrar total de la reserva
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalPanel.setOpaque(false);
        
        JLabel labelTotalTexto = new JLabel("Total:");
        labelTotalTexto.setFont(new Font("Segoe UI", Font.BOLD, 16));
        
        JLabel labelTotal = new JLabel("$0.00");
        labelTotal.setFont(new Font("Segoe UI", Font.BOLD, 18));
        labelTotal.setForeground(UIConstants.SECONDARY_COLOR);
        
        totalPanel.add(labelTotalTexto);
        totalPanel.add(labelTotal);
        
        // Añadir todo al panel de equipos
        equiposPanel.add(buscarEquiposPanel, BorderLayout.NORTH);
        equiposPanel.add(listasPanel, BorderLayout.CENTER);
        equiposPanel.add(panelCentro, BorderLayout.EAST);
        equiposPanel.add(totalPanel, BorderLayout.SOUTH);
        
        // Panel de confirmación
        JPanel panelConfirmacion = new JPanel(new BorderLayout(20, 20));
        panelConfirmacion.setBorder(new EmptyBorder(20, 20, 20, 20));
        panelConfirmacion.setBackground(Color.WHITE);
        
        // Resumen de la reserva
        JPanel resumenPanel = new JPanel(new BorderLayout());
        resumenPanel.setBackground(Color.WHITE);
        resumenPanel.setBorder(BorderFactory.createTitledBorder("Resumen de la Reserva"));
        
        // Tabla de resumen
        String[] columnasResumen = {"Equipo", "Marca", "Precio"};
        DefaultTableModel modeloResumen = new DefaultTableModel(columnasResumen, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable tablaResumen = new JTable(modeloResumen);
        UIUtils.styleTable(tablaResumen);
        
        JScrollPane scrollResumen = new JScrollPane(tablaResumen);
        UIUtils.styleScrollPane(scrollResumen);
        
        // Añadir tabla al panel de resumen
        resumenPanel.add(scrollResumen, BorderLayout.CENTER);
        
        // Panel con detalles de la reserva
        JPanel detallesPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        detallesPanel.setBackground(Color.WHITE);
        detallesPanel.setBorder(BorderFactory.createTitledBorder("Detalles de la Reserva"));
        
        JLabel lblClienteResumen = new JLabel("Cliente:");
        JLabel lblClienteValor = new JLabel("");
        lblClienteValor.setFont(UIConstants.SUBTITLE_FONT);
        
        JLabel lblDestinoResumen = new JLabel("Destino:");
        JLabel lblDestinoValor = new JLabel("");
        lblDestinoValor.setFont(UIConstants.SUBTITLE_FONT);
        
        JLabel lblFechaInicioResumen = new JLabel("Fecha Inicio:");
        JLabel lblFechaInicioValor = new JLabel("");
        lblFechaInicioValor.setFont(UIConstants.SUBTITLE_FONT);
        
        JLabel lblFechaFinResumen = new JLabel("Fecha Fin:");
        JLabel lblFechaFinValor = new JLabel("");
        lblFechaFinValor.setFont(UIConstants.SUBTITLE_FONT);
        
        JLabel lblDuracionResumen = new JLabel("Duración:");
        JLabel lblDuracionValor = new JLabel("");
        lblDuracionValor.setFont(UIConstants.SUBTITLE_FONT);
        
        JLabel lblTotalResumen = new JLabel("Total:");
        JLabel lblTotalValor = new JLabel("$0.00");
        lblTotalValor.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTotalValor.setForeground(UIConstants.SECONDARY_COLOR);
        
        detallesPanel.add(lblClienteResumen);
        detallesPanel.add(lblClienteValor);
        detallesPanel.add(lblDestinoResumen);
        detallesPanel.add(lblDestinoValor);
        detallesPanel.add(lblFechaInicioResumen);
        detallesPanel.add(lblFechaInicioValor);
        detallesPanel.add(lblFechaFinResumen);
        detallesPanel.add(lblFechaFinValor);
        detallesPanel.add(lblDuracionResumen);
        detallesPanel.add(lblDuracionValor);
        detallesPanel.add(lblTotalResumen);
        detallesPanel.add(lblTotalValor);
        
        // Añadir paneles al panel de confirmación
        panelConfirmacion.add(resumenPanel, BorderLayout.CENTER);
        panelConfirmacion.add(detallesPanel, BorderLayout.SOUTH);
        
        // Añadir componentes a panelDatos
        JPanel contenedorDatos = new JPanel(new BorderLayout(0, 20));
        contenedorDatos.setOpaque(false);
        contenedorDatos.add(formPanel, BorderLayout.NORTH);
        contenedorDatos.add(equiposPanel, BorderLayout.CENTER);
        
        panelDatos.add(contenedorDatos, BorderLayout.CENTER);
        
        // Añadir paneles al tabbedPane
        tabbedPane.addTab("1. Selección", null, panelDatos, "Seleccione cliente, destino, fechas y equipos");
        tabbedPane.addTab("2. Confirmación", null, panelConfirmacion, "Confirme los detalles de la reserva");
        tabbedPane.setEnabledAt(1, false); // Inicialmente deshabilitado
        
        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);
        
        RoundedButton btnSiguiente = new RoundedButton("Siguiente", UIConstants.PRIMARY_COLOR);
        btnSiguiente.setForeground(Color.WHITE);
        
        RoundedButton btnAtras = new RoundedButton("Atrás", Color.LIGHT_GRAY);
        btnAtras.setForeground(Color.DARK_GRAY);
        btnAtras.setVisible(false);
        
        RoundedButton btnConfirmar = new RoundedButton("Confirmar Reserva", UIConstants.SECONDARY_COLOR);
        btnConfirmar.setForeground(Color.WHITE);
        btnConfirmar.setVisible(false);
        
        RoundedButton btnCancelarForm = new RoundedButton("Cancelar", Color.LIGHT_GRAY);
        btnCancelarForm.setForeground(Color.DARK_GRAY);
        btnCancelarForm.addActionListener(e -> dialog.dispose());
        
        // Para btnAtras (volver a la pestaña anterior)
        btnAtras.addActionListener(e -> {
            tabbedPane.setSelectedIndex(0);
            btnSiguiente.setVisible(true);
            btnAtras.setVisible(false);
            btnConfirmar.setVisible(false);
        });
        
        // Eventos
        // Para btnAgregar (mover equipos de disponibles a seleccionados)
        btnAgregar.addActionListener(e -> {
            List<EquipoDeportivo> seleccionados = listaDisponibles.getSelectedValuesList();
            for (EquipoDeportivo equipo : seleccionados) {
                modeloDisponibles.removeElement(equipo);
                modeloSeleccionados.addElement(equipo);
            }
            actualizarTotalReserva(modeloSeleccionados, labelTotal);
        });
        
        // Para btnQuitar (mover equipos de seleccionados a disponibles)
        btnQuitar.addActionListener(e -> {
            List<EquipoDeportivo> seleccionados = listaSeleccionados.getSelectedValuesList();
            for (EquipoDeportivo equipo : seleccionados) {
                modeloSeleccionados.removeElement(equipo);
                modeloDisponibles.addElement(equipo);
            }
            actualizarTotalReserva(modeloSeleccionados, labelTotal);
        });
        
        // Eventos para buscar equipos disponibles
        btnBuscarEquipos.addActionListener(e -> {
            try {
                // Limpiar listas
                modeloDisponibles.clear();
                modeloSeleccionados.clear();
                
                // Validar entrada
                if (cmbDestino.getSelectedItem() == null) {
                    UIUtils.showWarningMessage(dialog, "Debe seleccionar un destino", "Advertencia");
                    return;
                }
                
                if (txtFechaInicio.getValue() == null || txtFechaFin.getValue() == null) {
                    UIUtils.showWarningMessage(dialog, "Debe especificar las fechas de inicio y fin", "Advertencia");
                    return;
                }
                
                Date fechaInicio = (Date) txtFechaInicio.getValue();
                Date fechaFin = (Date) txtFechaFin.getValue();
                
                if (fechaInicio.after(fechaFin)) {
                    UIUtils.showWarningMessage(dialog, "La fecha de inicio no puede ser posterior a la fecha de fin", "Advertencia");
                    return;
                }
                
                if (fechaInicio.before(new Date())) {
                    UIUtils.showWarningMessage(dialog, "La fecha de inicio no puede ser anterior a la fecha actual", "Advertencia");
                    return;
                }
                
                // Buscar equipos disponibles
                DestinoTuristico destino = (DestinoTuristico) cmbDestino.getSelectedItem();
                
                java.sql.Date sqlFechaInicio = new java.sql.Date(fechaInicio.getTime());
                java.sql.Date sqlFechaFin = new java.sql.Date(fechaFin.getTime());
                
                List<EquipoDeportivo> equiposDisponibles = inventarioController.buscarEquiposDisponibles(
                    destino.getIdDestino(), sqlFechaInicio, sqlFechaFin);
                
                if (equiposDisponibles == null || equiposDisponibles.isEmpty()) {
                    UIUtils.showInfoMessage(dialog, "No hay equipos disponibles en ese destino para las fechas seleccionadas", "Información");
                    return;
                }
                
                // Llenar lista de disponibles
                for (EquipoDeportivo equipo : equiposDisponibles) {
                    modeloDisponibles.addElement(equipo);
                }
                
            } catch (Exception ex) {
                UIUtils.showErrorMessage(dialog, "Error al buscar equipos disponibles: " + ex.getMessage(), "Error");
            }
        });
        
        // Para botón Siguiente
        btnSiguiente.addActionListener(e -> {
            try {
                // Validar selecciones
                if (cmbCliente.getSelectedItem() == null) {
                    UIUtils.showWarningMessage(dialog, "Debe seleccionar un cliente", "Advertencia");
                    return;
                }
                
                if (cmbDestino.getSelectedItem() == null) {
                    UIUtils.showWarningMessage(dialog, "Debe seleccionar un destino", "Advertencia");
                    return;
                }
                
                if (txtFechaInicio.getValue() == null || txtFechaFin.getValue() == null) {
                    UIUtils.showWarningMessage(dialog, "Debe especificar las fechas de inicio y fin", "Advertencia");
                    return;
                }
                
                if (modeloSeleccionados.size() == 0) {
                    UIUtils.showWarningMessage(dialog, "Debe seleccionar al menos un equipo", "Advertencia");
                    return;
                }
                
                // Ir a la pestaña de confirmación
                tabbedPane.setSelectedIndex(1);
                tabbedPane.setEnabledAt(1, true);
                btnSiguiente.setVisible(false);
                btnAtras.setVisible(true);
                btnConfirmar.setVisible(true);
                
                // Actualizar datos de resumen
                Cliente cliente = (Cliente) cmbCliente.getSelectedItem();
                DestinoTuristico destino = (DestinoTuristico) cmbDestino.getSelectedItem();
                Date fechaInicio = (Date) txtFechaInicio.getValue();
                Date fechaFin = (Date) txtFechaFin.getValue();
                
                lblClienteValor.setText(cliente.getNombre() + " " + cliente.getApellido());
                lblDestinoValor.setText(destino.getNombre() + " (" + destino.getUbicacion() + ")");
                lblFechaInicioValor.setText(UIUtils.formatDate(fechaInicio));
                lblFechaFinValor.setText(UIUtils.formatDate(fechaFin));
                
                // Calcular duración
                long diff = fechaFin.getTime() - fechaInicio.getTime();
                long dias = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) + 1; // +1 porque se cuenta el día final
                lblDuracionValor.setText(dias + " día" + (dias > 1 ? "s" : ""));
                
                // Llenar tabla de resumen
                modeloResumen.setRowCount(0);
                double total = 0;
                
                for (int i = 0; i < modeloSeleccionados.size(); i++) {
                    EquipoDeportivo equipo = modeloSeleccionados.getElementAt(i);
                    double precio = equipo.getPrecioAlquiler();
                    total += precio;
                    
                    Object[] row = {
                        equipo.getNombre(),
                        equipo.getMarca(),
                        UIUtils.formatCurrency(precio)
                    };
                    modeloResumen.addRow(row);
                }
                
                lblTotalValor.setText(UIUtils.formatCurrency(total));
                
            } catch (Exception ex) {
                UIUtils.showErrorMessage(dialog, "Error: " + ex.getMessage(), "Error");
            }
        });
        
        // Para botón confirmar reserva
        btnConfirmar.addActionListener(e -> {
            try {
                // Crear la reserva
                Cliente cliente = (Cliente) cmbCliente.getSelectedItem();
                DestinoTuristico destino = (DestinoTuristico) cmbDestino.getSelectedItem();
                Date fechaInicio = (Date) txtFechaInicio.getValue();
                Date fechaFin = (Date) txtFechaFin.getValue();
                
                // Crear lista de equipos
                List<EquipoDeportivo> equipos = new ArrayList<>();
                for (int i = 0; i < modeloSeleccionados.size(); i++) {
                    equipos.add(modeloSeleccionados.getElementAt(i));
                }
                
                if (controller.crearReserva(cliente, fechaInicio, fechaFin, destino, equipos)) {
                    UIUtils.showInfoMessage(dialog, "Reserva creada con éxito", "Éxito");
                    dialog.dispose();
                    cargarDatos();
                } else {
                    UIUtils.showErrorMessage(dialog, "Error al crear la reserva", "Error");
                }
            } catch (Exception ex) {
                UIUtils.showErrorMessage(dialog, "Error: " + ex.getMessage(), "Error");
            }
        });
        
        buttonPanel.add(btnAtras);
        buttonPanel.add(btnCancelarForm);
        buttonPanel.add(btnSiguiente);
        buttonPanel.add(btnConfirmar);
        
        // Agregar componentes al diálogo
        dialog.add(tabbedPane, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    /**
     * Muestra el formulario para modificar una reserva existente
     */
    private void mostrarFormularioModificar() {
        if (reservaSeleccionada == null) {
            UIUtils.showWarningMessage(this, "Debe seleccionar una reserva para modificar", "Advertencia");
            return;
        }
        
        // Verificar si la reserva puede ser modificada
        if ("Finalizada".equals(reservaSeleccionada.getEstado()) || "Cancelada".equals(reservaSeleccionada.getEstado())) {
            UIUtils.showWarningMessage(this, "No se puede modificar una reserva " + reservaSeleccionada.getEstado().toLowerCase(), "Advertencia");
            return;
        }
        
        UIUtils.showInfoMessage(this, "La modificación de reservas existentes está en desarrollo", "Información");
    }
    
    /**
     * Cancela la reserva actualmente seleccionada
     */
    private void cancelarReservaSeleccionada() {
        if (reservaSeleccionada == null) {
            UIUtils.showWarningMessage(this, "Debe seleccionar una reserva para cancelar", "Advertencia");
            return;
        }
        
        // Verificar si la reserva puede ser cancelada
        if ("Finalizada".equals(reservaSeleccionada.getEstado()) || "Cancelada".equals(reservaSeleccionada.getEstado())) {
            UIUtils.showWarningMessage(this, "No se puede cancelar una reserva " + reservaSeleccionada.getEstado().toLowerCase(), "Advertencia");
            return;
        }
        
        int confirmacion = UIUtils.showConfirmDialog(
            this,
            "¿Está seguro de cancelar la reserva #" + reservaSeleccionada.getIdReserva() + " del cliente " + 
            reservaSeleccionada.getCliente().getNombre() + " " + reservaSeleccionada.getCliente().getApellido() + "?",
            "Confirmar cancelación"
        );
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            if (controller.cancelarReserva(reservaSeleccionada.getIdReserva())) {
                UIUtils.showInfoMessage(this, "Reserva cancelada con éxito", "Éxito");
                cargarDatos();
            } else {
                UIUtils.showErrorMessage(this, "Error al cancelar la reserva", "Error");
            }
        }
    }
    
    /**
     * Consulta y muestra los detalles de la reserva seleccionada
     */
    private void consultarReservaSeleccionada() {
        if (reservaSeleccionada == null) {
            UIUtils.showWarningMessage(this, "Debe seleccionar una reserva para consultar", "Advertencia");
            return;
        }
        
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Detalles de la Reserva", 
                                    Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(800, 600);
        dialog.setLocationRelativeTo(this);
        
        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);
        
        // Panel de encabezado con información general
        JPanel headerPanel = new JPanel(new BorderLayout(10, 10));
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        // Título con número de reserva
        JLabel titleLabel = new JLabel("Reserva #" + reservaSeleccionada.getIdReserva());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(UIConstants.PRIMARY_COLOR);
        
        // Estado
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        statusPanel.setOpaque(false);
        
        JLabel statusLabel = new JLabel("Estado:");
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        JPanel statusIndicatorPanel = StatusIndicator.createPillIndicator(
            reservaSeleccionada.getEstado(), 
            getColorForStatus(reservaSeleccionada.getEstado())
        );
        
        statusPanel.add(statusLabel);
        statusPanel.add(statusIndicatorPanel);
        
        // Panel de información general
        JPanel infoPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        infoPanel.setOpaque(false);
        infoPanel.setBorder(BorderFactory.createTitledBorder("Información General"));
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        
        // Cliente
        JLabel clienteLabel = new JLabel("Cliente:");
        clienteLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        JLabel clienteValueLabel = new JLabel(reservaSeleccionada.getCliente().getNombre() + " " + 
                                           reservaSeleccionada.getCliente().getApellido());
        clienteValueLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        // Documento
        JLabel documentoLabel = new JLabel("Documento:");
        documentoLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        JLabel documentoValueLabel = new JLabel(reservaSeleccionada.getCliente().getTipoDocumento() + ": " +
                                            reservaSeleccionada.getCliente().getDocumento());
        documentoValueLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        // Destino
        JLabel destinoLabel = new JLabel("Destino:");
        destinoLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        JLabel destinoValueLabel = new JLabel(reservaSeleccionada.getDestino().getNombre() + " (" + 
                                           reservaSeleccionada.getDestino().getUbicacion() + ")");
        destinoValueLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        // Fechas
        JLabel fechasLabel = new JLabel("Período:");
        fechasLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        String periodoText = "Del " + sdf.format(reservaSeleccionada.getFechaInicio()) + 
                           " al " + sdf.format(reservaSeleccionada.getFechaFin());
        JLabel fechasValueLabel = new JLabel(periodoText);
        fechasValueLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        // Agregar componentes al panel de información
        infoPanel.add(clienteLabel);
        infoPanel.add(clienteValueLabel);
        infoPanel.add(documentoLabel);
        infoPanel.add(documentoValueLabel);
        infoPanel.add(destinoLabel);
        infoPanel.add(destinoValueLabel);
        infoPanel.add(fechasLabel);
        infoPanel.add(fechasValueLabel);
        
        // Agregar componentes al panel de encabezado
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(statusPanel, BorderLayout.CENTER);
        headerPanel.add(infoPanel, BorderLayout.SOUTH);
        
        // Panel de equipos reservados
        JPanel equiposPanel = new JPanel(new BorderLayout());
        equiposPanel.setBackground(Color.WHITE);
        equiposPanel.setBorder(BorderFactory.createTitledBorder("Equipos Reservados"));
        
        // Tabla de equipos
        String[] columnasEquipos = {"Equipo", "Tipo", "Marca", "Estado", "Precio"};
        DefaultTableModel modeloEquipos = new DefaultTableModel(columnasEquipos, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable tablaEquipos = new JTable(modeloEquipos);
        UIUtils.styleTable(tablaEquipos);
        
        JScrollPane scrollEquipos = new JScrollPane(tablaEquipos);
        UIUtils.styleScrollPane(scrollEquipos);
        
        // Llenar tabla de equipos
        if (reservaSeleccionada.getDetalles() != null && !reservaSeleccionada.getDetalles().isEmpty()) {
            double total = 0;
            
            for (DetalleReserva detalle : reservaSeleccionada.getDetalles()) {
                EquipoDeportivo equipo = detalle.getEquipo();
                double precio = detalle.getPrecioUnitario();
                total += precio;
                
                Object[] row = {
                    equipo.getNombre(),
                    equipo.getTipo().getNombre(),
                    equipo.getMarca(),
                    equipo.getEstado(),
                    UIUtils.formatCurrency(precio)
                };
                modeloEquipos.addRow(row);
            }
            
            // Panel de total
            JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            totalPanel.setOpaque(false);
            
            JLabel totalLabel = new JLabel("Total:");
            totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
            
            JLabel totalValueLabel = new JLabel(UIUtils.formatCurrency(total));
            totalValueLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
            totalValueLabel.setForeground(UIConstants.SECONDARY_COLOR);
            
            totalPanel.add(totalLabel);
            totalPanel.add(totalValueLabel);
            
            equiposPanel.add(scrollEquipos, BorderLayout.CENTER);
            equiposPanel.add(totalPanel, BorderLayout.SOUTH);
        } else {
            JLabel noEquiposLabel = new JLabel("No hay equipos registrados en esta reserva");
            noEquiposLabel.setHorizontalAlignment(JLabel.CENTER);
            noEquiposLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            equiposPanel.add(noEquiposLabel, BorderLayout.CENTER);
        }
        
        // Panel de acciones (botones)
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionsPanel.setOpaque(false);
        
        if (tienePermiso && !("Finalizada".equals(reservaSeleccionada.getEstado()) || "Cancelada".equals(reservaSeleccionada.getEstado()))) {
            RoundedButton btnCancelarReserva = new RoundedButton("Cancelar Reserva", UIConstants.WARNING_COLOR);
            btnCancelarReserva.setForeground(Color.WHITE);
            btnCancelarReserva.addActionListener(e -> {
                int confirmacion = UIUtils.showConfirmDialog(
                    dialog,
                    "¿Está seguro de cancelar esta reserva?",
                    "Confirmar cancelación"
                );
                
                if (confirmacion == JOptionPane.YES_OPTION) {
                    if (controller.cancelarReserva(reservaSeleccionada.getIdReserva())) {
                        UIUtils.showInfoMessage(dialog, "Reserva cancelada con éxito", "Éxito");
                        dialog.dispose();
                        cargarDatos();
                    } else {
                        UIUtils.showErrorMessage(dialog, "Error al cancelar la reserva", "Error");
                    }
                }
            });
            actionsPanel.add(btnCancelarReserva);
        }
        
        if (tienePermiso && "Pendiente".equals(reservaSeleccionada.getEstado())) {
            RoundedButton btnConfirmarReserva = new RoundedButton("Confirmar Reserva", UIConstants.SECONDARY_COLOR);
            btnConfirmarReserva.setForeground(Color.WHITE);
            btnConfirmarReserva.addActionListener(e -> {
                UIUtils.showInfoMessage(dialog, "Funcionalidad en desarrollo", "Información");
            });
            actionsPanel.add(btnConfirmarReserva);
        }
        
        RoundedButton btnCerrar = new RoundedButton("Cerrar", Color.LIGHT_GRAY);
        btnCerrar.setForeground(Color.DARK_GRAY);
        btnCerrar.addActionListener(e -> dialog.dispose());
        actionsPanel.add(btnCerrar);
        
        // Agregar componentes al panel principal
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(equiposPanel, BorderLayout.CENTER);
        mainPanel.add(actionsPanel, BorderLayout.SOUTH);
        
        dialog.add(mainPanel);
        dialog.setVisible(true);
    }
    
    /**
     * Muestra el formulario para crear un nuevo cliente
     */
    private void mostrarFormularioCliente(JComboBox<Cliente> cmbCliente) {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Nuevo Cliente", 
                                    Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Campos del formulario
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Nombre:"), gbc);
        
        gbc.gridx = 1;
        JTextField txtNombre = new JTextField(20);
        UIUtils.styleTextField(txtNombre);
        panel.add(txtNombre, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Apellido:"), gbc);
        
        gbc.gridx = 1;
        JTextField txtApellido = new JTextField(20);
        UIUtils.styleTextField(txtApellido);
        panel.add(txtApellido, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Documento:"), gbc);
        
        gbc.gridx = 1;
        JTextField txtDocumento = new JTextField(20);
        UIUtils.styleTextField(txtDocumento);
        panel.add(txtDocumento, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Tipo Documento:"), gbc);
        
        gbc.gridx = 1;
        JComboBox<String> cmbTipoDocumento = new JComboBox<>(new String[] {"CC", "CE", "Pasaporte"});
        UIUtils.styleComboBox(cmbTipoDocumento);
        panel.add(cmbTipoDocumento, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("Teléfono:"), gbc);
        
        gbc.gridx = 1;
        JTextField txtTelefono = new JTextField(20);
        UIUtils.styleTextField(txtTelefono);
        panel.add(txtTelefono, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(new JLabel("Email:"), gbc);
        
        gbc.gridx = 1;
        JTextField txtEmail = new JTextField(20);
        UIUtils.styleTextField(txtEmail);
        panel.add(txtEmail, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 6;
        panel.add(new JLabel("Dirección:"), gbc);
        
        gbc.gridx = 1;
        JTextField txtDireccion = new JTextField(20);
        UIUtils.styleTextField(txtDireccion);
        panel.add(txtDireccion, gbc);
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        RoundedButton btnGuardar = new RoundedButton("Guardar", UIConstants.SECONDARY_COLOR);
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.addActionListener(e -> {
            try {
                String nombre = txtNombre.getText().trim();
                String apellido = txtApellido.getText().trim();
                String documento = txtDocumento.getText().trim();
                String tipoDocumento = (String) cmbTipoDocumento.getSelectedItem();
                String telefono = txtTelefono.getText().trim();
                String email = txtEmail.getText().trim();
                String direccion = txtDireccion.getText().trim();
                
                if (controller.registrarCliente(nombre, apellido, documento, tipoDocumento, telefono, email, direccion)) {
                    UIUtils.showInfoMessage(dialog, "Cliente registrado con éxito", "Éxito");
                    
                    // Actualizar el combobox de clientes
                    cargarClientes(cmbCliente);
                    
                    // Seleccionar el nuevo cliente
                    Cliente nuevoCliente = controller.buscarClientePorDocumento(documento);
                    if (nuevoCliente != null) {
                        for (int i = 0; i < cmbCliente.getItemCount(); i++) {
                            if (cmbCliente.getItemAt(i).getIdCliente() == nuevoCliente.getIdCliente()) {
                                cmbCliente.setSelectedIndex(i);
                                break;
                            }
                        }
                    }
                    
                    dialog.dispose();
                } else {
                    UIUtils.showErrorMessage(dialog, "Error al registrar el cliente", "Error");
                }
            } catch (Exception ex) {
                UIUtils.showErrorMessage(dialog, "Error: " + ex.getMessage(), "Error");
            }
        });
        
        RoundedButton btnCancelarDialog = new RoundedButton("Cancelar", Color.LIGHT_GRAY);
        btnCancelarDialog.setForeground(Color.DARK_GRAY);
        btnCancelarDialog.addActionListener(e -> dialog.dispose());
        
        panelBotones.add(btnCancelarDialog);
        panelBotones.add(btnGuardar);
        
        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(panelBotones, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    /**
     * Carga los clientes en un combo
     */
    private void cargarClientes(JComboBox<Cliente> comboBox) {
        comboBox.removeAllItems();
        List<Cliente> clientes = controller.listarTodosLosClientes();
        for (Cliente cliente : clientes) {
            comboBox.addItem(cliente);
        }
    }
    
    /**
     * Carga los destinos en un combo
     */
    private void cargarDestinos(JComboBox<DestinoTuristico> comboBox) {
        comboBox.removeAllItems();
        List<DestinoTuristico> destinos = inventarioController.listarTodosLosDestinos();
        for (DestinoTuristico destino : destinos) {
            comboBox.addItem(destino);
        }
    }
    
    /**
     * Obtiene un color según el estado de la reserva
     */
    private Color getColorForStatus(String estado) {
        switch (estado) {
            case "Pendiente":
                return new Color(70, 130, 180);
            case "Confirmada":
                return new Color(0, 128, 0);
            case "En progreso":
                return new Color(255, 165, 0);
            case "Finalizada":
                return new Color(100, 100, 100);
            case "Cancelada":
                return new Color(220, 20, 60);
            default:
                return Color.GRAY;
        }
    }
    
    /**
     * Actualiza el total de la reserva en tiempo real
     */
    private void actualizarTotalReserva(DefaultListModel<EquipoDeportivo> modeloSeleccionados, JLabel labelTotal) {
        double total = 0;
        for (int i = 0; i < modeloSeleccionados.size(); i++) {
            total += modeloSeleccionados.getElementAt(i).getPrecioAlquiler();
        }
        labelTotal.setText(UIUtils.formatCurrency(total));
    }
    
    /**
     * Renderer personalizado para mostrar equipos en las listas
     */
    private class EquipoListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            
            if (value instanceof EquipoDeportivo) {
                EquipoDeportivo equipo = (EquipoDeportivo) value;
                setText(equipo.getNombre() + " - " + equipo.getMarca() + " ($" + equipo.getPrecioAlquiler() + ")");
            }
            
            return c;
        }
    }
}