package com.deportur.vista.paneles;

import com.deportur.controlador.ReservasController;
import com.deportur.controlador.UsuarioController;
import com.deportur.modelo.Cliente;
import com.deportur.vista.MainFrame;
import com.deportur.vista.componentes.ClienteCard;
import com.deportur.vista.componentes.RoundedButton;
import com.deportur.vista.componentes.SearchBar;
import com.deportur.vista.util.ImageCache;
import com.deportur.vista.util.UIConstants;
import com.deportur.vista.util.UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel de gestión de clientes con diseño mejorado
 */
public class PanelClientes extends JPanel implements ClienteCard.ClienteCardListener {
    
    private ReservasController controller;
    private boolean tienePermisoEliminar;
    private boolean tienePermisoEditar;
    
    // Componentes de la interfaz
    private SearchBar searchBar;
    private JToggleButton btnListView;
    private JToggleButton btnCardView;
    private RoundedButton btnAgregar;
    private RoundedButton btnModificar;
    private RoundedButton btnEliminar;
    private RoundedButton btnRefrescar;
    private JTable tblClientes;
    private DefaultTableModel tableModel;
    private JScrollPane tableScrollPane;
    private JPanel cardsContainer;
    private JScrollPane cardsScrollPane;
    
    // Estado actual
    private List<Cliente> clientesActuales = new ArrayList<>();
    private List<ClienteCard> clienteCards = new ArrayList<>();
    private Cliente clienteSeleccionado;
    private ClienteCard cardSeleccionada;
    private boolean mostrandoTarjetas = true;
    
    public PanelClientes() {
        controller = new ReservasController();
        UsuarioController usuarioController = MainFrame.getUsuarioController();
        tienePermisoEliminar = usuarioController.puedeEliminarClientes();
        tienePermisoEditar = usuarioController.puedeEditarClientes();
        
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
        
        String[] filterOptions = {"Todos", "Por Nombre", "Por Documento"};
        searchBar = new SearchBar("Buscar clientes...", filterOptions);
        searchBar.addSearchListener(e -> buscarClientes());
        searchBar.addSearchFieldKeyListener(e -> buscarClientes());
        
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
        
        // Panel de botones
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonsPanel.setOpaque(false);
        
        btnAgregar = new RoundedButton("Agregar", UIConstants.ADD_ICON, UIConstants.SECONDARY_COLOR);
        btnAgregar.addActionListener(e -> mostrarFormularioAgregar());
        btnAgregar.setEnabled(tienePermisoEditar);
        
        btnModificar = new RoundedButton("Modificar", UIConstants.EDIT_ICON, UIConstants.PRIMARY_COLOR);
        btnModificar.addActionListener(e -> mostrarFormularioModificar());
        btnModificar.setEnabled(false); // Inicialmente deshabilitado
        
        btnEliminar = new RoundedButton("Eliminar", UIConstants.DELETE_ICON, UIConstants.WARNING_COLOR);
        btnEliminar.addActionListener(e -> eliminarClienteSeleccionado());
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
        searchPanel.add(searchBar, BorderLayout.CENTER);
        searchPanel.add(viewPanel, BorderLayout.EAST);
        
        topPanel.add(searchPanel, BorderLayout.NORTH);
        topPanel.add(buttonsPanel, BorderLayout.SOUTH);
        
        // Panel de contenido principal
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        
        // Tabla de clientes
        String[] columnas = {"ID", "Nombre", "Apellido", "Documento", "Tipo Doc", "Teléfono", "Email", "Dirección"};
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tblClientes = new JTable(tableModel);
        UIUtils.styleTable(tblClientes);
        
        tblClientes.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int filaSeleccionada = tblClientes.getSelectedRow();
                if (filaSeleccionada != -1) {
                    int idCliente = (int) tblClientes.getValueAt(filaSeleccionada, 0);
                    clienteSeleccionado = controller.buscarClientePorId(idCliente);
                    
                    // Habilitar botones de acción según permisos
                    actualizarBotonesAccion();
                    
                    if (e.getClickCount() == 2) {
                        // Doble clic muestra formulario de edición o detalles
                        if (tienePermisoEditar) {
                            mostrarFormularioModificar();
                        } else {
                            mostrarDetallesCliente(clienteSeleccionado);
                        }
                    }
                }
            }
        });
        
        tableScrollPane = new JScrollPane(tblClientes);
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
        clienteSeleccionado = null;
        cardSeleccionada = null;
        tblClientes.clearSelection();
        
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
        boolean haySeleccion = clienteSeleccionado != null;
        
        btnModificar.setEnabled(tienePermisoEditar && haySeleccion);
        btnEliminar.setEnabled(tienePermisoEliminar && haySeleccion);
    }
    
    /**
     * Busca clientes según los criterios ingresados
     */
    private void buscarClientes() {
        String criterio = searchBar.getSearchText().trim();
        String filtro = searchBar.getSelectedFilter();
        
        if (criterio.isEmpty()) {
            actualizarVista(clientesActuales); // Mostrar todos
            return;
        }
        
        // Buscar según el filtro
        List<Cliente> clientesFiltrados = new ArrayList<>();
        
        if (filtro.equals("Por Documento")) {
            Cliente cliente = controller.buscarClientePorDocumento(criterio);
            if (cliente != null) {
                clientesFiltrados.add(cliente);
            }
        } else if (filtro.equals("Por Nombre")) {
            clientesFiltrados = controller.buscarClientesPorNombreOApellido(criterio);
        } else {
            // Filtro "Todos" - buscar en todos los campos
            for (Cliente cliente : clientesActuales) {
                if (coincideConCriterio(cliente, criterio)) {
                    clientesFiltrados.add(cliente);
                }
            }
        }
        
        actualizarVista(clientesFiltrados);
    }
    
    /**
     * Verifica si un cliente coincide con el criterio de búsqueda
     */
    private boolean coincideConCriterio(Cliente cliente, String criterio) {
        String criterioLower = criterio.toLowerCase();
        
        return cliente.getNombre().toLowerCase().contains(criterioLower) ||
               cliente.getApellido().toLowerCase().contains(criterioLower) ||
               cliente.getDocumento().toLowerCase().contains(criterioLower) ||
               cliente.getEmail().toLowerCase().contains(criterioLower) ||
               cliente.getTelefono().toLowerCase().contains(criterioLower);
    }
    
    /**
     * Carga los datos iniciales
     */
    private void cargarDatos() {
        // Obtener todos los clientes
        clientesActuales = controller.listarTodosLosClientes();
        
        // Actualizar la vista
        actualizarVista(clientesActuales);
    }
    
    /**
     * Actualiza la vista con los clientes filtrados
     */
    private void actualizarVista(List<Cliente> clientes) {
        // Actualizar tabla
        actualizarTabla(clientes);
        
        // Actualizar vista de tarjetas
        actualizarTarjetas(clientes);
        
        // Reiniciar selección
        clienteSeleccionado = null;
        cardSeleccionada = null;
        actualizarBotonesAccion();
    }
    
    /**
     * Actualiza la tabla con los clientes filtrados
     */
    private void actualizarTabla(List<Cliente> clientes) {
        // Limpiar la tabla
        tableModel.setRowCount(0);
        
        if (clientes == null || clientes.isEmpty()) {
            return;
        }
        
        // Llenar la tabla con los datos
        for (Cliente cliente : clientes) {
            Object[] row = {
                cliente.getIdCliente(),
                cliente.getNombre(),
                cliente.getApellido(),
                cliente.getDocumento(),
                cliente.getTipoDocumento(),
                cliente.getTelefono(),
                cliente.getEmail(),
                cliente.getDireccion()
            };
            tableModel.addRow(row);
        }
    }
    
    /**
     * Actualiza la vista de tarjetas con los clientes filtrados
     */
    private void actualizarTarjetas(List<Cliente> clientes) {
        // Limpiar el panel de tarjetas
        cardsContainer.removeAll();
        clienteCards.clear();
        
        if (clientes == null || clientes.isEmpty()) {
            // Mostrar mensaje si no hay clientes
            JLabel emptyLabel = new JLabel("No se encontraron clientes");
            emptyLabel.setFont(UIConstants.SUBTITLE_FONT);
            emptyLabel.setForeground(Color.GRAY);
            cardsContainer.add(emptyLabel);
        } else {
            // Crear tarjetas para cada cliente
            for (Cliente cliente : clientes) {
                ClienteCard card = new ClienteCard(cliente);
                card.setCardListener(this);
                clienteCards.add(card);
                cardsContainer.add(card);
            }
        }
        
        // Refrescar el panel
        cardsContainer.revalidate();
        cardsContainer.repaint();
    }
    
    /**
     * Muestra el formulario para agregar un nuevo cliente
     */
    public void mostrarFormularioAgregar() {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Agregar Cliente", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(600, 500);
        dialog.setLocationRelativeTo(this);
        
        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout(20, 0));
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
        formPanel.add(new JLabel("Apellido:"), gbc);
        
        gbc.gridx = 1;
        JTextField txtApellido = new JTextField(20);
        UIUtils.styleTextField(txtApellido);
        formPanel.add(txtApellido, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Documento:"), gbc);
        
        gbc.gridx = 1;
        JTextField txtDocumento = new JTextField(20);
        UIUtils.styleTextField(txtDocumento);
        formPanel.add(txtDocumento, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Tipo Documento:"), gbc);
        
        gbc.gridx = 1;
        JComboBox<String> cmbTipoDocumento = new JComboBox<>(new String[] {"CC", "CE", "Pasaporte"});
        UIUtils.styleComboBox(cmbTipoDocumento);
        formPanel.add(cmbTipoDocumento, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Teléfono:"), gbc);
        
        gbc.gridx = 1;
        JTextField txtTelefono = new JTextField(20);
        UIUtils.styleTextField(txtTelefono);
        formPanel.add(txtTelefono, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 5;
        formPanel.add(new JLabel("Email:"), gbc);
        
        gbc.gridx = 1;
        JTextField txtEmail = new JTextField(20);
        UIUtils.styleTextField(txtEmail);
        formPanel.add(txtEmail, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 6;
        formPanel.add(new JLabel("Dirección:"), gbc);
        
        gbc.gridx = 1;
        JTextField txtDireccion = new JTextField(20);
        UIUtils.styleTextField(txtDireccion);
        formPanel.add(txtDireccion, gbc);
        
        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);
        
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
                    dialog.dispose();
                    cargarDatos();
                } else {
                    UIUtils.showErrorMessage(dialog, "Error al registrar el cliente", "Error");
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
     * Muestra el formulario para modificar un cliente existente
     */
    private void mostrarFormularioModificar() {
        if (clienteSeleccionado == null) {
            UIUtils.showWarningMessage(this, "Debe seleccionar un cliente para modificar", "Advertencia");
            return;
        }
        
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Modificar Cliente", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(600, 500);
        dialog.setLocationRelativeTo(this);
        
        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout(20, 0));
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
        
        // Campos del formulario con datos del cliente
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Nombre:"), gbc);
        
        gbc.gridx = 1;
        JTextField txtNombre = new JTextField(20);
        txtNombre.setText(clienteSeleccionado.getNombre());
        UIUtils.styleTextField(txtNombre);
        formPanel.add(txtNombre, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Apellido:"), gbc);
        
        gbc.gridx = 1;
        JTextField txtApellido = new JTextField(20);
        txtApellido.setText(clienteSeleccionado.getApellido());
        UIUtils.styleTextField(txtApellido);
        formPanel.add(txtApellido, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Documento:"), gbc);
        
        gbc.gridx = 1;
        JTextField txtDocumento = new JTextField(20);
        txtDocumento.setText(clienteSeleccionado.getDocumento());
        UIUtils.styleTextField(txtDocumento);
        formPanel.add(txtDocumento, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Tipo Documento:"), gbc);
        
        gbc.gridx = 1;
        JComboBox<String> cmbTipoDocumento = new JComboBox<>(new String[] {"CC", "CE", "Pasaporte"});
        cmbTipoDocumento.setSelectedItem(clienteSeleccionado.getTipoDocumento());
        UIUtils.styleComboBox(cmbTipoDocumento);
        formPanel.add(cmbTipoDocumento, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Teléfono:"), gbc);
        
        gbc.gridx = 1;
        JTextField txtTelefono = new JTextField(20);
        txtTelefono.setText(clienteSeleccionado.getTelefono());
        UIUtils.styleTextField(txtTelefono);
        formPanel.add(txtTelefono, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 5;
        formPanel.add(new JLabel("Email:"), gbc);
        
        gbc.gridx = 1;
        JTextField txtEmail = new JTextField(20);
        txtEmail.setText(clienteSeleccionado.getEmail());
        UIUtils.styleTextField(txtEmail);
        formPanel.add(txtEmail, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 6;
        formPanel.add(new JLabel("Dirección:"), gbc);
        
        gbc.gridx = 1;
        JTextField txtDireccion = new JTextField(20);
        txtDireccion.setText(clienteSeleccionado.getDireccion());
        UIUtils.styleTextField(txtDireccion);
        formPanel.add(txtDireccion, gbc);
        
        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);
        
        RoundedButton btnGuardar = new RoundedButton("Guardar Cambios", UIConstants.SECONDARY_COLOR);
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
                
                if (controller.actualizarCliente(clienteSeleccionado.getIdCliente(), nombre, apellido, documento, tipoDocumento, telefono, email, direccion)) {
                    UIUtils.showInfoMessage(dialog, "Cliente actualizado con éxito", "Éxito");
                    dialog.dispose();
                    cargarDatos();
                } else {
                    UIUtils.showErrorMessage(dialog, "Error al actualizar el cliente", "Error");
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
     * Elimina el cliente actualmente seleccionado
     */
    private void eliminarClienteSeleccionado() {
        if (clienteSeleccionado == null) {
            UIUtils.showWarningMessage(this, "Debe seleccionar un cliente para eliminar", "Advertencia");
            return;
        }
        
        int confirmacion = UIUtils.showConfirmDialog(
            this,
            "¿Está seguro de eliminar al cliente '" + clienteSeleccionado.getNombre() + " " + clienteSeleccionado.getApellido() + "'?",
            "Confirmar eliminación"
        );
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            if (controller.eliminarCliente(clienteSeleccionado.getIdCliente())) {
                UIUtils.showInfoMessage(this, "Cliente eliminado con éxito", "Éxito");
                cargarDatos();
            } else {
                UIUtils.showErrorMessage(this, "Error al eliminar el cliente", "Error");
            }
        }
    }
    
    /**
     * Muestra los detalles de un cliente seleccionado
     */
    private void mostrarDetallesCliente(Cliente cliente) {
        if (cliente == null) return;
        
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Detalles del Cliente", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(this);
        
        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);
        
        // Panel de imagen (izquierda)
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setBackground(Color.WHITE);
        imagePanel.setPreferredSize(new Dimension(150, 0));
        
        ImageIcon clientIcon = ImageCache.getClientImage(cliente.getIdCliente());
        JLabel imageLabel = new JLabel(clientIcon);
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        
        imagePanel.add(imageLabel, BorderLayout.CENTER);
        
        // Panel de información (derecha)
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(Color.WHITE);
        
        // Encabezado con nombre completo
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        JLabel nameLabel = new JLabel(cliente.getNombre() + " " + cliente.getApellido());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        nameLabel.setForeground(UIConstants.PRIMARY_COLOR);
        
        JLabel docLabel = new JLabel(cliente.getTipoDocumento() + ": " + cliente.getDocumento());
        docLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        docLabel.setForeground(Color.DARK_GRAY);
        
        headerPanel.add(nameLabel, BorderLayout.NORTH);
        headerPanel.add(docLabel, BorderLayout.CENTER);
        
        // Panel de detalles de contacto
        JPanel contactPanel = new JPanel(new GridLayout(0, 1, 0, 10));
        contactPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        contactPanel.setOpaque(false);
        
        // Teléfono
        JPanel phonePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        phonePanel.setOpaque(false);
        
        JLabel phoneLabel = new JLabel("Teléfono:");
        phoneLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        JLabel phoneValueLabel = new JLabel(cliente.getTelefono());
        phoneValueLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        phonePanel.add(phoneLabel);
        phonePanel.add(phoneValueLabel);
        
        // Email
        JPanel emailPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        emailPanel.setOpaque(false);
        
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        JLabel emailValueLabel = new JLabel(cliente.getEmail());
        emailValueLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        emailPanel.add(emailLabel);
        emailPanel.add(emailValueLabel);
        
        // Dirección
        JPanel addressPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addressPanel.setOpaque(false);
        
        JLabel addressLabel = new JLabel("Dirección:");
        addressLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        JLabel addressValueLabel = new JLabel(cliente.getDireccion());
        addressValueLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        addressPanel.add(addressLabel);
        addressPanel.add(addressValueLabel);
        
        // Agregar todos los paneles de contacto
        contactPanel.add(phonePanel);
        contactPanel.add(emailPanel);
        contactPanel.add(addressPanel);
        
        // Botones para acciones relacionadas
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actionsPanel.setOpaque(false);
        actionsPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        
        RoundedButton btnVerReservas = new RoundedButton("Ver Reservas", UIConstants.PRIMARY_COLOR);
        btnVerReservas.setForeground(Color.WHITE);
        btnVerReservas.addActionListener(e -> {
            // Implementar visualización de reservas del cliente
            UIUtils.showInfoMessage(dialog, "Funcionalidad en desarrollo", "Información");
        });
        
        RoundedButton btnNuevaReserva = new RoundedButton("Nueva Reserva", UIConstants.SECONDARY_COLOR);
        btnNuevaReserva.setForeground(Color.WHITE);
        btnNuevaReserva.addActionListener(e -> {
            // Implementar creación de nueva reserva para este cliente
            UIUtils.showInfoMessage(dialog, "Funcionalidad en desarrollo", "Información");
        });
        
        actionsPanel.add(btnVerReservas);
        actionsPanel.add(Box.createHorizontalStrut(10));
        actionsPanel.add(btnNuevaReserva);
        
        // Agregar componentes al panel de información
        infoPanel.add(headerPanel, BorderLayout.NORTH);
        infoPanel.add(contactPanel, BorderLayout.CENTER);
        infoPanel.add(actionsPanel, BorderLayout.SOUTH);
        
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
    
    // Implementación de la interfaz ClienteCard.ClienteCardListener
    
    @Override
    public void onCardClicked(ClienteCard card, Cliente cliente) {
        // Deseleccionar la tarjeta anteriormente seleccionada
        if (cardSeleccionada != null) {
            cardSeleccionada.setSelected(false);
        }
        
        // Seleccionar la nueva tarjeta
        cardSeleccionada = card;
        cardSeleccionada.setSelected(true);
        
        // Actualizar el cliente seleccionado
        clienteSeleccionado = cliente;
        
        // Actualizar botones de acción
        actualizarBotonesAccion();
    }
    
    @Override
    public void onCardDoubleClicked(ClienteCard card, Cliente cliente) {
        clienteSeleccionado = cliente;
        
        // Al hacer doble clic, mostrar formulario de edición o detalles
        if (tienePermisoEditar) {
            mostrarFormularioModificar();
        } else {
            mostrarDetallesCliente(cliente);
        }
    }
}