package com.deportur.vista.paneles;

import com.deportur.controlador.UsuarioController;
import com.deportur.modelo.Usuario;
import com.deportur.vista.componentes.RoundedButton;
import com.deportur.vista.componentes.SearchBar;
import com.deportur.vista.componentes.StatusIndicator;
import com.deportur.vista.util.ImageCache;
import com.deportur.vista.util.UIConstants;
import com.deportur.vista.util.UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Panel de gestión de usuarios del sistema con diseño mejorado
 */
public class PanelUsuarios extends JPanel {
    
    private UsuarioController controller;
    private boolean tienePermiso;
    
    // Componentes de la interfaz
    private SearchBar searchBar;
    private RoundedButton btnAgregar;
    private RoundedButton btnModificar;
    private RoundedButton btnEliminar;
    private RoundedButton btnRefrescar;
    private JTable tblUsuarios;
    private DefaultTableModel tableModel;
    private JScrollPane scrollPane;
    private JCheckBox chkMostrarInactivos;
    
    // Estado actual
    private List<Usuario> usuariosActuales = new ArrayList<>();
    private Usuario usuarioSeleccionado;
    
    /**
     * Constructor
     * 
     * @param usuarioController Controlador de usuarios
     */
    public PanelUsuarios(UsuarioController usuarioController) {
        this.controller = usuarioController;
        tienePermiso = controller.esAdministrador();
        
        if (!tienePermiso) {
            showUnauthorizedPanel();
        } else {
            initComponents();
            cargarDatos();
        }
    }
    
    /**
     * Muestra un panel de acceso no autorizado
     */
    private void showUnauthorizedPanel() {
        setLayout(new BorderLayout());
        setBackground(UIConstants.BACKGROUND_COLOR);
        
        JPanel unauthorizedPanel = new JPanel(new GridBagLayout());
        unauthorizedPanel.setOpaque(false);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.CENTER;
        
        JLabel iconLabel = new JLabel(UIConstants.INFO_ICON);
        JLabel messageLabel = new JLabel("No tiene permisos para acceder a esta sección");
        messageLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        messageLabel.setForeground(UIConstants.WARNING_COLOR);
        
        unauthorizedPanel.add(iconLabel, gbc);
        gbc.insets = new Insets(20, 0, 0, 0);
        unauthorizedPanel.add(messageLabel, gbc);
        
        add(unauthorizedPanel, BorderLayout.CENTER);
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
        
        searchBar = new SearchBar("Buscar usuarios...");
        searchBar.addSearchListener(e -> buscarUsuarios());
        searchBar.addSearchFieldKeyListener(e -> buscarUsuarios());
        
        searchPanel.add(searchBar, BorderLayout.CENTER);
        
        // Panel de filtros
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterPanel.setOpaque(false);
        
        chkMostrarInactivos = new JCheckBox("Mostrar usuarios inactivos");
        chkMostrarInactivos.setOpaque(false);
        chkMostrarInactivos.addActionListener(e -> cargarDatos());
        
        filterPanel.add(chkMostrarInactivos);
        
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
        btnEliminar.addActionListener(e -> eliminarUsuarioSeleccionado());
        btnEliminar.setEnabled(false); // Inicialmente deshabilitado
        
        btnRefrescar = new RoundedButton("Refrescar", UIConstants.REFRESH_ICON, Color.LIGHT_GRAY);
        btnRefrescar.setForeground(Color.DARK_GRAY);
        btnRefrescar.addActionListener(e -> cargarDatos());
        
        buttonsPanel.add(btnAgregar);
        buttonsPanel.add(btnModificar);
        buttonsPanel.add(btnEliminar);
        buttonsPanel.add(btnRefrescar);
        
        // Agregar componentes al panel superior
        topPanel.add(searchPanel, BorderLayout.NORTH);
        topPanel.add(filterPanel, BorderLayout.CENTER);
        topPanel.add(buttonsPanel, BorderLayout.SOUTH);
        
        // Tabla de usuarios
        String[] columnas = {"ID", "Usuario", "Nombre", "Apellido", "Email", "Rol", "Estado", "Fecha Creación"};
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tblUsuarios = new JTable(tableModel);
        UIUtils.styleTable(tblUsuarios);
        
        tblUsuarios.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int filaSeleccionada = tblUsuarios.getSelectedRow();
                if (filaSeleccionada != -1) {
                    int idUsuario = (int) tblUsuarios.getValueAt(filaSeleccionada, 0);
                    try {
                        usuarioSeleccionado = controller.buscarUsuarioPorId(idUsuario);
                        
                        // Habilitar botones de acción según permisos y validación adicional
                        actualizarBotonesAccion();
                        
                        if (e.getClickCount() == 2) {
                            // Doble clic muestra formulario de edición o detalles
                            if (tienePermiso) {
                                mostrarFormularioModificar();
                            } else {
                                mostrarDetallesUsuario(usuarioSeleccionado);
                            }
                        }
                    } catch (Exception ex) {
                        UIUtils.showErrorMessage(PanelUsuarios.this, "Error: " + ex.getMessage(), "Error");
                    }
                }
            }
        });
        
        scrollPane = new JScrollPane(tblUsuarios);
        UIUtils.styleScrollPane(scrollPane);
        
        // Agregar componentes al panel principal
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    /**
     * Actualiza el estado de los botones de acción según la selección actual
     */
    private void actualizarBotonesAccion() {
        boolean haySeleccion = usuarioSeleccionado != null;
        boolean esUsuarioActual = haySeleccion && 
                                controller.getUsuarioActual().getIdUsuario() == usuarioSeleccionado.getIdUsuario();
        
        // No se puede eliminar a uno mismo, y se debe verificar que quede al menos un administrador
        boolean puedeEliminar = haySeleccion && !esUsuarioActual && tienePermiso;
        
        // Para modificar solo se requiere selección y permisos
        boolean puedeModificar = haySeleccion && tienePermiso;
        
        btnModificar.setEnabled(puedeModificar);
        btnEliminar.setEnabled(puedeEliminar);
    }
    
    /**
     * Busca usuarios según los criterios ingresados
     */
    private void buscarUsuarios() {
        String criterio = searchBar.getSearchText().trim();
        
        if (criterio.isEmpty()) {
            actualizarTabla(usuariosActuales); // Mostrar todos
            return;
        }
        
        // Filtrar los usuarios según el criterio
        List<Usuario> usuariosFiltrados = new ArrayList<>();
        
        for (Usuario usuario : usuariosActuales) {
            if (usuario.getNombreUsuario().toLowerCase().contains(criterio.toLowerCase()) ||
                usuario.getNombre().toLowerCase().contains(criterio.toLowerCase()) ||
                usuario.getApellido().toLowerCase().contains(criterio.toLowerCase()) ||
                usuario.getEmail().toLowerCase().contains(criterio.toLowerCase()) ||
                usuario.getRol().toLowerCase().contains(criterio.toLowerCase())) {
                usuariosFiltrados.add(usuario);
            }
        }
        
        actualizarTabla(usuariosFiltrados);
        
        // Reiniciar selección
        usuarioSeleccionado = null;
        actualizarBotonesAccion();
    }
    
    /**
     * Carga los datos iniciales
     */
    private void cargarDatos() {
        try {
            if (chkMostrarInactivos.isSelected()) {
                usuariosActuales = controller.listarTodosLosUsuarios();
            } else {
                usuariosActuales = controller.listarUsuariosActivos();
            }
            
            actualizarTabla(usuariosActuales);
            
            // Reiniciar selección
            usuarioSeleccionado = null;
            actualizarBotonesAccion();
        } catch (Exception e) {
            UIUtils.showErrorMessage(this, "Error al cargar usuarios: " + e.getMessage(), "Error");
        }
    }
    
    /**
     * Actualiza la tabla con los usuarios filtrados
     */
    private void actualizarTabla(List<Usuario> usuarios) {
        // Limpiar la tabla
        tableModel.setRowCount(0);
        
        if (usuarios == null || usuarios.isEmpty()) {
            return;
        }
        
        // Llenar la tabla con los datos
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        
        for (Usuario usuario : usuarios) {
            Object[] row = {
                usuario.getIdUsuario(),
                usuario.getNombreUsuario(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getEmail(),
                usuario.getRol(),
                usuario.isActivo() ? "Activo" : "Inactivo",
                usuario.getFechaCreacion() != null ? sdf.format(usuario.getFechaCreacion()) : ""
            };
            tableModel.addRow(row);
        }
        
        // Aplicar renderer para colorear filas según estado
        tblUsuarios.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, 
                                                           boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    String estado = (String) table.getValueAt(row, 6); // Columna de estado
                    
                    if ("Inactivo".equals(estado)) {
                        c.setForeground(Color.GRAY);
                        c.setFont(new Font(c.getFont().getName(), Font.ITALIC, c.getFont().getSize()));
                    } else {
                        c.setForeground(table.getForeground());
                        c.setFont(new Font(c.getFont().getName(), Font.PLAIN, c.getFont().getSize()));
                    }
                }
                
                return c;
            }
        });
    }
    
    /**
     * Muestra el formulario para agregar un nuevo usuario
     */
    private void mostrarFormularioAgregar() {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Agregar Usuario", 
                           Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(500, 500);
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
        formPanel.add(new JLabel("Apellido:"), gbc);
        
        gbc.gridx = 1;
        JTextField txtApellido = new JTextField(20);
        UIUtils.styleTextField(txtApellido);
        formPanel.add(txtApellido, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Email:"), gbc);
        
        gbc.gridx = 1;
        JTextField txtEmail = new JTextField(20);
        UIUtils.styleTextField(txtEmail);
        formPanel.add(txtEmail, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Nombre de Usuario:"), gbc);
        
        gbc.gridx = 1;
        JTextField txtNombreUsuario = new JTextField(20);
        UIUtils.styleTextField(txtNombreUsuario);
        formPanel.add(txtNombreUsuario, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Contraseña:"), gbc);
        
        gbc.gridx = 1;
        JPasswordField txtContrasena = new JPasswordField(20);
        UIUtils.styleTextField(txtContrasena);
        formPanel.add(txtContrasena, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 5;
        formPanel.add(new JLabel("Confirmar Contraseña:"), gbc);
        
        gbc.gridx = 1;
        JPasswordField txtConfirmContrasena = new JPasswordField(20);
        UIUtils.styleTextField(txtConfirmContrasena);
        formPanel.add(txtConfirmContrasena, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 6;
        formPanel.add(new JLabel("Rol:"), gbc);
        
        gbc.gridx = 1;
        JComboBox<String> cmbRol = new JComboBox<>(new String[] {"trabajador", "administrador"});
        UIUtils.styleComboBox(cmbRol);
        formPanel.add(cmbRol, gbc);
        
        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);
        
        RoundedButton btnGuardar = new RoundedButton("Guardar", UIConstants.SECONDARY_COLOR);
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.addActionListener(e -> {
            try {
                String nombre = txtNombre.getText().trim();
                String apellido = txtApellido.getText().trim();
                String email = txtEmail.getText().trim();
                String nombreUsuario = txtNombreUsuario.getText().trim();
                String contrasena = new String(txtContrasena.getPassword());
                String confirmContrasena = new String(txtConfirmContrasena.getPassword());
                String rol = (String) cmbRol.getSelectedItem();
                
                // Validaciones básicas
                if (nombre.isEmpty() || apellido.isEmpty() || email.isEmpty() || 
                    nombreUsuario.isEmpty() || contrasena.isEmpty()) {
                    UIUtils.showWarningMessage(dialog, "Todos los campos son obligatorios", "Campos incompletos");
                    return;
                }
                
                if (!contrasena.equals(confirmContrasena)) {
                    UIUtils.showWarningMessage(dialog, "Las contraseñas no coinciden", "Error de validación");
                    return;
                }
                
                if (controller.registrarUsuario(nombreUsuario, contrasena, rol, nombre, apellido, email)) {
                    UIUtils.showInfoMessage(dialog, "Usuario registrado con éxito", "Éxito");
                    dialog.dispose();
                    cargarDatos();
                } else {
                    UIUtils.showErrorMessage(dialog, "Error al registrar el usuario", "Error");
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
     * Muestra el formulario para modificar un usuario existente
     */
    private void mostrarFormularioModificar() {
        if (usuarioSeleccionado == null) {
            UIUtils.showWarningMessage(this, "Debe seleccionar un usuario para modificar", "Advertencia");
            return;
        }
        
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Modificar Usuario", 
                           Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(500, 550);
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
        
        // Campos del formulario con datos del usuario
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Nombre:"), gbc);
        
        gbc.gridx = 1;
        JTextField txtNombre = new JTextField(20);
        txtNombre.setText(usuarioSeleccionado.getNombre());
        UIUtils.styleTextField(txtNombre);
        formPanel.add(txtNombre, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Apellido:"), gbc);
        
        gbc.gridx = 1;
        JTextField txtApellido = new JTextField(20);
        txtApellido.setText(usuarioSeleccionado.getApellido());
        UIUtils.styleTextField(txtApellido);
        formPanel.add(txtApellido, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Email:"), gbc);
        
        gbc.gridx = 1;
        JTextField txtEmail = new JTextField(20);
        txtEmail.setText(usuarioSeleccionado.getEmail());
        UIUtils.styleTextField(txtEmail);
        formPanel.add(txtEmail, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Nombre de Usuario:"), gbc);
        
        gbc.gridx = 1;
        JTextField txtNombreUsuario = new JTextField(20);
        txtNombreUsuario.setText(usuarioSeleccionado.getNombreUsuario());
        UIUtils.styleTextField(txtNombreUsuario);
        formPanel.add(txtNombreUsuario, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Cambiar Contraseña:"), gbc);
        
        gbc.gridx = 1;
        JCheckBox chkCambiarContrasena = new JCheckBox("Actualizar contraseña");
        chkCambiarContrasena.setOpaque(false);
        formPanel.add(chkCambiarContrasena, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 5;
        formPanel.add(new JLabel("Nueva Contraseña:"), gbc);
        
        gbc.gridx = 1;
        JPasswordField txtContrasena = new JPasswordField(20);
        txtContrasena.setEnabled(false);
        UIUtils.styleTextField(txtContrasena);
        formPanel.add(txtContrasena, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 6;
        formPanel.add(new JLabel("Confirmar Contraseña:"), gbc);
        
        gbc.gridx = 1;
        JPasswordField txtConfirmContrasena = new JPasswordField(20);
        txtConfirmContrasena.setEnabled(false);
        UIUtils.styleTextField(txtConfirmContrasena);
        formPanel.add(txtConfirmContrasena, gbc);
        
        // Activar/desactivar campos de contraseña
        chkCambiarContrasena.addActionListener(e -> {
            boolean cambiar = chkCambiarContrasena.isSelected();
            txtContrasena.setEnabled(cambiar);
            txtConfirmContrasena.setEnabled(cambiar);
            if (!cambiar) {
                txtContrasena.setText("");
                txtConfirmContrasena.setText("");
            }
        });
        
        gbc.gridx = 0;
        gbc.gridy = 7;
        formPanel.add(new JLabel("Rol:"), gbc);
        
        gbc.gridx = 1;
        JComboBox<String> cmbRol = new JComboBox<>(new String[] {"trabajador", "administrador"});
        cmbRol.setSelectedItem(usuarioSeleccionado.getRol());
        UIUtils.styleComboBox(cmbRol);
        formPanel.add(cmbRol, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 8;
        formPanel.add(new JLabel("Estado:"), gbc);
        
        gbc.gridx = 1;
        JCheckBox chkActivo = new JCheckBox("Usuario activo");
        chkActivo.setSelected(usuarioSeleccionado.isActivo());
        chkActivo.setOpaque(false);
        formPanel.add(chkActivo, gbc);
        
        // Deshabilitar cambio de rol si es el usuario actual
        boolean esUsuarioActual = controller.getUsuarioActual().getIdUsuario() == usuarioSeleccionado.getIdUsuario();
        if (esUsuarioActual) {
            cmbRol.setEnabled(false);
            chkActivo.setEnabled(false);
            chkActivo.setSelected(true);
        }
        
        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);
        
        RoundedButton btnGuardar = new RoundedButton("Guardar Cambios", UIConstants.SECONDARY_COLOR);
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.addActionListener(e -> {
            try {
                String nombre = txtNombre.getText().trim();
                String apellido = txtApellido.getText().trim();
                String email = txtEmail.getText().trim();
                String nombreUsuario = txtNombreUsuario.getText().trim();
                String rol = (String) cmbRol.getSelectedItem();
                boolean activo = chkActivo.isSelected();
                
                // Validaciones básicas
                if (nombre.isEmpty() || apellido.isEmpty() || email.isEmpty() || nombreUsuario.isEmpty()) {
                    UIUtils.showWarningMessage(dialog, "Todos los campos son obligatorios", "Campos incompletos");
                    return;
                }
                
                // Obtener contraseña actual o nueva si se cambió
                String contrasena = usuarioSeleccionado.getContrasena();
                if (chkCambiarContrasena.isSelected()) {
                    String nuevaContrasena = new String(txtContrasena.getPassword());
                    String confirmContrasena = new String(txtConfirmContrasena.getPassword());
                    
                    if (nuevaContrasena.isEmpty()) {
                        UIUtils.showWarningMessage(dialog, "La nueva contraseña no puede estar vacía", "Error de validación");
                        return;
                    }
                    
                    if (!nuevaContrasena.equals(confirmContrasena)) {
                        UIUtils.showWarningMessage(dialog, "Las contraseñas no coinciden", "Error de validación");
                        return;
                    }
                    
                    contrasena = nuevaContrasena;
                }
                
                if (controller.actualizarUsuario(usuarioSeleccionado.getIdUsuario(), nombreUsuario, 
                                              contrasena, rol, nombre, apellido, email, activo)) {
                    UIUtils.showInfoMessage(dialog, "Usuario actualizado con éxito", "Éxito");
                    dialog.dispose();
                    cargarDatos();
                } else {
                    UIUtils.showErrorMessage(dialog, "Error al actualizar el usuario", "Error");
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
     * Elimina (desactiva) el usuario actualmente seleccionado
     */
    private void eliminarUsuarioSeleccionado() {
        if (usuarioSeleccionado == null) {
            UIUtils.showWarningMessage(this, "Debe seleccionar un usuario para eliminar", "Advertencia");
            return;
        }
        
        // No permitir eliminar el usuario actual
        if (usuarioSeleccionado.getIdUsuario() == controller.getUsuarioActual().getIdUsuario()) {
            UIUtils.showWarningMessage(this, "No puede eliminar su propio usuario", "Operación no permitida");
            return;
        }
        
        // Confirmar la eliminación
        int confirmacion = UIUtils.showConfirmDialog(
            this,
            "¿Está seguro de desactivar el usuario '" + usuarioSeleccionado.getNombreUsuario() + "'?\n" +
            "Esta acción no elimina el usuario de la base de datos, solo lo marca como inactivo.",
            "Confirmar desactivación"
        );
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                if (controller.eliminarUsuario(usuarioSeleccionado.getIdUsuario())) {
                    UIUtils.showInfoMessage(this, "Usuario desactivado con éxito", "Éxito");
                    cargarDatos();
                } else {
                    UIUtils.showErrorMessage(this, "Error al desactivar el usuario", "Error");
                }
            } catch (Exception ex) {
                UIUtils.showErrorMessage(this, "Error: " + ex.getMessage(), "Error");
            }
        }
    }
    
    /**
     * Muestra los detalles de un usuario seleccionado
     */
    private void mostrarDetallesUsuario(Usuario usuario) {
        if (usuario == null) return;
        
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Detalles del Usuario", 
                           Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        
        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);
        
        // Panel de información
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(Color.WHITE);
        
        // Encabezado con nombre y rol
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        JLabel nameLabel = new JLabel(usuario.getNombre() + " " + usuario.getApellido());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        nameLabel.setForeground(UIConstants.PRIMARY_COLOR);
        
        JPanel rolePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        rolePanel.setOpaque(false);
        
        String roleText = "administrador".equals(usuario.getRol()) ? "Administrador" : "Trabajador";
        Color roleColor = "administrador".equals(usuario.getRol()) ? 
                        new Color(70, 130, 180) : new Color(60, 179, 113);
        
        JPanel roleIndicator = StatusIndicator.createPillIndicator(roleText, roleColor);
        
        if (!usuario.isActivo()) {
            JPanel statusIndicator = StatusIndicator.createPillIndicator("Inactivo", Color.GRAY);
            rolePanel.add(statusIndicator);
        }
        
        rolePanel.add(roleIndicator);
        
        headerPanel.add(nameLabel, BorderLayout.NORTH);
        headerPanel.add(rolePanel, BorderLayout.CENTER);
        
        // Panel de detalles
        JPanel detailsPanel = new JPanel(new GridLayout(0, 2, 10, 15));
        detailsPanel.setOpaque(false);
        detailsPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        
        // Nombre de usuario
        JLabel userLabel = new JLabel("Usuario:");
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        JLabel userValueLabel = new JLabel(usuario.getNombreUsuario());
        userValueLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        // Email
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        JLabel emailValueLabel = new JLabel(usuario.getEmail());
        emailValueLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        // Fecha de creación
        JLabel creationLabel = new JLabel("Fecha de Creación:");
        creationLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        String fechaCreacion = usuario.getFechaCreacion() != null ?
                            new SimpleDateFormat("dd/MM/yyyy").format(usuario.getFechaCreacion()) : "N/A";
        JLabel creationValueLabel = new JLabel(fechaCreacion);
        creationValueLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        // Estado
        JLabel statusLabel = new JLabel("Estado:");
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        JLabel statusValueLabel = new JLabel(usuario.isActivo() ? "Activo" : "Inactivo");
        statusValueLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statusValueLabel.setForeground(usuario.isActivo() ? new Color(60, 179, 113) : Color.GRAY);
        
        // Agregar todo al panel de detalles
        detailsPanel.add(userLabel);
        detailsPanel.add(userValueLabel);
        detailsPanel.add(emailLabel);
        detailsPanel.add(emailValueLabel);
        detailsPanel.add(creationLabel);
        detailsPanel.add(creationValueLabel);
        detailsPanel.add(statusLabel);
        detailsPanel.add(statusValueLabel);
        
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
        mainPanel.add(infoPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.add(mainPanel);
        dialog.setVisible(true);
    }
}