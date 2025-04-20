package com.deportur.vista;

import com.deportur.controlador.UsuarioController;
import com.deportur.modelo.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PanelUsuarios extends JPanel {
    
    private UsuarioController controller;
    
    // Componentes de la interfaz
    private JTextField txtBuscar;
    private JButton btnBuscar;
    private JButton btnAgregar;
    private JButton btnModificar;
    private JButton btnEliminar;
    private JButton btnRefrescar;
    private JTable tblUsuarios;
    private DefaultTableModel tableModel;
    private JScrollPane scrollPane;
    
    public PanelUsuarios(UsuarioController controller) {
        this.controller = controller;
        initComponents();
        cargarDatos();
    }
    
    private void initComponents() {
        // Configurar el layout
        setLayout(new BorderLayout());
        
        // Panel de búsqueda y filtros
        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        JLabel lblBuscar = new JLabel("Buscar:");
        txtBuscar = new JTextField(20);
        btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(e -> buscarUsuarios());
        
        panelBusqueda.add(lblBuscar);
        panelBusqueda.add(txtBuscar);
        panelBusqueda.add(btnBuscar);
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        btnAgregar = new JButton("Agregar");
        btnAgregar.addActionListener(e -> mostrarFormularioAgregar());
        
        btnModificar = new JButton("Modificar");
        btnModificar.addActionListener(e -> mostrarFormularioModificar());
        
        btnEliminar = new JButton("Eliminar");
        btnEliminar.addActionListener(e -> eliminarUsuarioSeleccionado());
        
        btnRefrescar = new JButton("Refrescar");
        btnRefrescar.addActionListener(e -> cargarDatos());
        
        panelBotones.add(btnAgregar);
        panelBotones.add(btnModificar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnRefrescar);
        
        // Panel superior que contiene búsqueda y botones
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.add(panelBusqueda, BorderLayout.WEST);
        panelSuperior.add(panelBotones, BorderLayout.EAST);
        
        // Tabla de usuarios
        String[] columnas = {"ID", "Usuario", "Nombre", "Apellido", "Email", "Rol", "Activo"};
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Hacer que la tabla no sea editable
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 6) { // Columna "Activo"
                    return Boolean.class;
                }
                return String.class;
            }
        };
        
        tblUsuarios = new JTable(tableModel);
        tblUsuarios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblUsuarios.getTableHeader().setReorderingAllowed(false);
        
        scrollPane = new JScrollPane(tblUsuarios);
        
        // Añadir componentes al panel principal
        add(panelSuperior, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private void mostrarFormularioAgregar() {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Agregar Usuario", true);
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
        panel.add(new JLabel("Nombre de Usuario:"), gbc);
        
        gbc.gridx = 1;
        JTextField txtNombreUsuario = new JTextField(20);
        panel.add(txtNombreUsuario, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Contraseña:"), gbc);
        
        gbc.gridx = 1;
        JPasswordField txtContrasena = new JPasswordField(20);
        panel.add(txtContrasena, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Nombre:"), gbc);
        
        gbc.gridx = 1;
        JTextField txtNombre = new JTextField(20);
        panel.add(txtNombre, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Apellido:"), gbc);
        
        gbc.gridx = 1;
        JTextField txtApellido = new JTextField(20);
        panel.add(txtApellido, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("Email:"), gbc);
        
        gbc.gridx = 1;
        JTextField txtEmail = new JTextField(20);
        panel.add(txtEmail, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(new JLabel("Rol:"), gbc);
        
        gbc.gridx = 1;
        JComboBox<String> cmbRol = new JComboBox<>(new String[] {"administrador", "trabajador"});
        panel.add(cmbRol, gbc);
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.addActionListener(e -> {
            try {
                String nombreUsuario = txtNombreUsuario.getText().trim();
                String contrasena = new String(txtContrasena.getPassword());
                String nombre = txtNombre.getText().trim();
                String apellido = txtApellido.getText().trim();
                String email = txtEmail.getText().trim();
                String rol = (String) cmbRol.getSelectedItem();
                
                if (controller.registrarUsuario(nombreUsuario, contrasena, rol, nombre, apellido, email)) {
                    JOptionPane.showMessageDialog(dialog, "Usuario registrado con éxito", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    cargarDatos();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error al registrar usuario: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.addActionListener(e -> dialog.dispose());
        
        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);
        
        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(panelBotones, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    private void mostrarFormularioModificar() {
        int filaSeleccionada = tblUsuarios.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un usuario para modificar", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int idUsuario = (int) tblUsuarios.getValueAt(filaSeleccionada, 0);
        Usuario usuario = controller.buscarUsuarioPorId(idUsuario);
        
        if (usuario == null) {
            JOptionPane.showMessageDialog(this, "No se pudo cargar la información del usuario", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Modificar Usuario", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Campos del formulario con datos del usuario
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Nombre de Usuario:"), gbc);
        
        gbc.gridx = 1;
        JTextField txtNombreUsuario = new JTextField(20);
        txtNombreUsuario.setText(usuario.getNombreUsuario());
        panel.add(txtNombreUsuario, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Contraseña:"), gbc);
        
        gbc.gridx = 1;
        JPasswordField txtContrasena = new JPasswordField(20);
        txtContrasena.setText(usuario.getContrasena());
        panel.add(txtContrasena, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Nombre:"), gbc);
        
        gbc.gridx = 1;
        JTextField txtNombre = new JTextField(20);
        txtNombre.setText(usuario.getNombre());
        panel.add(txtNombre, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Apellido:"), gbc);
        
        gbc.gridx = 1;
        JTextField txtApellido = new JTextField(20);
        txtApellido.setText(usuario.getApellido());
        panel.add(txtApellido, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("Email:"), gbc);
        
        gbc.gridx = 1;
        JTextField txtEmail = new JTextField(20);
        txtEmail.setText(usuario.getEmail());
        panel.add(txtEmail, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(new JLabel("Rol:"), gbc);
        
        gbc.gridx = 1;
        JComboBox<String> cmbRol = new JComboBox<>(new String[] {"administrador", "trabajador"});
        cmbRol.setSelectedItem(usuario.getRol());
        panel.add(cmbRol, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 6;
        panel.add(new JLabel("Activo:"), gbc);
        
        gbc.gridx = 1;
        JCheckBox chkActivo = new JCheckBox();
        chkActivo.setSelected(usuario.isActivo());
        panel.add(chkActivo, gbc);
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.addActionListener(e -> {
            try {
                String nombreUsuario = txtNombreUsuario.getText().trim();
                String contrasena = new String(txtContrasena.getPassword());
                String nombre = txtNombre.getText().trim();
                String apellido = txtApellido.getText().trim();
                String email = txtEmail.getText().trim();
                String rol = (String) cmbRol.getSelectedItem();
                boolean activo = chkActivo.isSelected();
                
                if (controller.actualizarUsuario(idUsuario, nombreUsuario, contrasena, rol, nombre, apellido, email, activo)) {
                    JOptionPane.showMessageDialog(dialog, "Usuario actualizado con éxito", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    cargarDatos();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error al actualizar usuario: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.addActionListener(e -> dialog.dispose());
        
        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);
        
        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(panelBotones, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    private void eliminarUsuarioSeleccionado() {
        int filaSeleccionada = tblUsuarios.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un usuario para eliminar", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int idUsuario = (int) tblUsuarios.getValueAt(filaSeleccionada, 0);
        String nombreUsuario = (String) tblUsuarios.getValueAt(filaSeleccionada, 1);
        
        int confirmacion = JOptionPane.showConfirmDialog(this,
            "¿Está seguro de desactivar al usuario '" + nombreUsuario + "'?",
            "Confirmar eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            if (controller.eliminarUsuario(idUsuario)) {
                JOptionPane.showMessageDialog(this, "Usuario desactivado con éxito", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarDatos();
            }
        }
    }
    
    private void buscarUsuarios() {
        String criterio = txtBuscar.getText().trim();
        
        if (criterio.isEmpty()) {
            cargarDatos();
            return;
        }
        
        // Implementación simple: filtra la tabla existente
        List<Usuario> usuarios = controller.listarTodosLosUsuarios();
        tableModel.setRowCount(0);
        
        if (usuarios == null) {
            return;
        }
        
        for (Usuario usuario : usuarios) {
            if (usuario.getNombreUsuario().toLowerCase().contains(criterio.toLowerCase()) ||
                usuario.getNombre().toLowerCase().contains(criterio.toLowerCase()) ||
                usuario.getApellido().toLowerCase().contains(criterio.toLowerCase())) {
                
                Object[] row = {
                    usuario.getIdUsuario(),
                    usuario.getNombreUsuario(),
                    usuario.getNombre(),
                    usuario.getApellido(),
                    usuario.getEmail(),
                    usuario.getRol(),
                    usuario.isActivo()
                };
                tableModel.addRow(row);
            }
        }
    }
    
    private void cargarDatos() {
        // Limpiar la tabla
        tableModel.setRowCount(0);
        
        // Obtener todos los usuarios
        List<Usuario> usuarios = controller.listarTodosLosUsuarios();
        
        if (usuarios == null) {
            return;
        }
        
        // Llenar la tabla con los datos
        for (Usuario usuario : usuarios) {
            Object[] row = {
                usuario.getIdUsuario(),
                usuario.getNombreUsuario(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getEmail(),
                usuario.getRol(),
                usuario.isActivo()
            };
            tableModel.addRow(row);
        }
    }
}