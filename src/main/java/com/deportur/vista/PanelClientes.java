package com.deportur.vista;

import com.deportur.controlador.ReservasController;
import com.deportur.controlador.UsuarioController;
import com.deportur.modelo.Cliente;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PanelClientes extends JPanel {
    
    private ReservasController controller;
    
    // Componentes de la interfaz
    private JTextField txtBuscar;
    private JComboBox<String> cmbFiltro;
    private JButton btnBuscar;
    private JButton btnAgregar;
    private JButton btnModificar;
    private JButton btnEliminar;
    private JButton btnRefrescar;
    private JTable tblClientes;
    private DefaultTableModel tableModel;
    private JScrollPane scrollPane;
    
    public PanelClientes() {
        controller = new ReservasController();
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
        cmbFiltro = new JComboBox<>(new String[] {"Todos", "Por Nombre", "Por Documento"});
        btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(e -> buscarClientes());
        
        panelBusqueda.add(lblBuscar);
        panelBusqueda.add(txtBuscar);
        panelBusqueda.add(new JLabel("Filtrar:"));
        panelBusqueda.add(cmbFiltro);
        panelBusqueda.add(btnBuscar);
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        btnAgregar = new JButton("Agregar");
        btnAgregar.addActionListener(e -> mostrarFormularioAgregar());
        
        btnModificar = new JButton("Modificar");
        btnModificar.addActionListener(e -> mostrarFormularioModificar());
        
        btnEliminar = new JButton("Eliminar");
        btnEliminar.addActionListener(e -> eliminarClienteSeleccionado());
        
        btnRefrescar = new JButton("Refrescar");
        btnRefrescar.addActionListener(e -> cargarDatos());
        
        // Verificar permisos de edición
        UsuarioController usuarioController = MainFrame.getUsuarioController();
        btnEliminar.setEnabled(usuarioController.puedeEliminarClientes());
    
        panelBotones.add(btnAgregar);
        panelBotones.add(btnModificar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnRefrescar);
        
        // Panel superior que contiene búsqueda y botones
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.add(panelBusqueda, BorderLayout.WEST);
        panelSuperior.add(panelBotones, BorderLayout.EAST);
        
        // Tabla de clientes
        String[] columnas = {"ID", "Nombre", "Apellido", "Documento", "Tipo Doc", "Teléfono", "Email", "Dirección"};
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Hacer que la tabla no sea editable
            }
        };
        
        tblClientes = new JTable(tableModel);
        tblClientes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblClientes.getTableHeader().setReorderingAllowed(false);
        
        scrollPane = new JScrollPane(tblClientes);
        
        // Añadir componentes al panel principal
        add(panelSuperior, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    public void mostrarFormularioAgregar() {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Agregar Cliente", true);
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
        panel.add(txtNombre, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Apellido:"), gbc);
        
        gbc.gridx = 1;
        JTextField txtApellido = new JTextField(20);
        panel.add(txtApellido, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Documento:"), gbc);
        
        gbc.gridx = 1;
        JTextField txtDocumento = new JTextField(20);
        panel.add(txtDocumento, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Tipo Documento:"), gbc);
        
        gbc.gridx = 1;
        JComboBox<String> cmbTipoDocumento = new JComboBox<>(new String[] {"CC", "CE", "Pasaporte"});
        panel.add(cmbTipoDocumento, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("Teléfono:"), gbc);
        
        gbc.gridx = 1;
        JTextField txtTelefono = new JTextField(20);
        panel.add(txtTelefono, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(new JLabel("Email:"), gbc);
        
        gbc.gridx = 1;
        JTextField txtEmail = new JTextField(20);
        panel.add(txtEmail, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 6;
        panel.add(new JLabel("Dirección:"), gbc);
        
        gbc.gridx = 1;
        JTextField txtDireccion = new JTextField(20);
        panel.add(txtDireccion, gbc);
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton btnGuardar = new JButton("Guardar");
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
                    JOptionPane.showMessageDialog(dialog, "Cliente registrado con éxito", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    cargarDatos();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Error al registrar el cliente", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
        int filaSeleccionada = tblClientes.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un cliente para modificar", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int idCliente = (int) tblClientes.getValueAt(filaSeleccionada, 0);
        Cliente cliente = controller.buscarClientePorId(idCliente);
        
        if (cliente == null) {
            JOptionPane.showMessageDialog(this, "No se pudo cargar la información del cliente", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Modificar Cliente", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Campos del formulario con datos del cliente
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Nombre:"), gbc);
        
        gbc.gridx = 1;
        JTextField txtNombre = new JTextField(20);
        txtNombre.setText(cliente.getNombre());
        panel.add(txtNombre, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Apellido:"), gbc);
        
        gbc.gridx = 1;
        JTextField txtApellido = new JTextField(20);
        txtApellido.setText(cliente.getApellido());
        panel.add(txtApellido, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Documento:"), gbc);
        
        gbc.gridx = 1;
        JTextField txtDocumento = new JTextField(20);
        txtDocumento.setText(cliente.getDocumento());
        panel.add(txtDocumento, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Tipo Documento:"), gbc);
        
        gbc.gridx = 1;
        JComboBox<String> cmbTipoDocumento = new JComboBox<>(new String[] {"CC", "CE", "Pasaporte"});
        cmbTipoDocumento.setSelectedItem(cliente.getTipoDocumento());
        panel.add(cmbTipoDocumento, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("Teléfono:"), gbc);
        
        gbc.gridx = 1;
        JTextField txtTelefono = new JTextField(20);
        txtTelefono.setText(cliente.getTelefono());
        panel.add(txtTelefono, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(new JLabel("Email:"), gbc);
        
        gbc.gridx = 1;
        JTextField txtEmail = new JTextField(20);
        txtEmail.setText(cliente.getEmail());
        panel.add(txtEmail, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 6;
        panel.add(new JLabel("Dirección:"), gbc);
        
        gbc.gridx = 1;
        JTextField txtDireccion = new JTextField(20);
        txtDireccion.setText(cliente.getDireccion());
        panel.add(txtDireccion, gbc);
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.addActionListener(e -> {
            try {
                String nombre = txtNombre.getText().trim();
                String apellido = txtApellido.getText().trim();
                String documento = txtDocumento.getText().trim();
                String tipoDocumento = (String) cmbTipoDocumento.getSelectedItem();
                String telefono = txtTelefono.getText().trim();
                String email = txtEmail.getText().trim();
                String direccion = txtDireccion.getText().trim();
                
                if (controller.actualizarCliente(idCliente, nombre, apellido, documento, tipoDocumento, telefono, email, direccion)) {
                    JOptionPane.showMessageDialog(dialog, "Cliente actualizado con éxito", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    cargarDatos();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Error al actualizar el cliente", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
    
    private void eliminarClienteSeleccionado() {
        int filaSeleccionada = tblClientes.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un cliente para eliminar", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int idCliente = (int) tblClientes.getValueAt(filaSeleccionada, 0);
        String nombreCliente = tblClientes.getValueAt(filaSeleccionada, 1) + " " + tblClientes.getValueAt(filaSeleccionada, 2);
        
        int confirmacion = JOptionPane.showConfirmDialog(this,
            "¿Está seguro de eliminar al cliente '" + nombreCliente + "'?",
            "Confirmar eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            if (controller.eliminarCliente(idCliente)) {
                JOptionPane.showMessageDialog(this, "Cliente eliminado con éxito", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarDatos();
            } else {
                JOptionPane.showMessageDialog(this, "Error al eliminar el cliente", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void buscarClientes() {
        String filtro = (String) cmbFiltro.getSelectedItem();
        String criterio = txtBuscar.getText().trim();
        
        if (criterio.isEmpty()) {
            cargarDatos();
            return;
        }
        
        List<Cliente> clientes = null;
        
        switch(filtro) {
            case "Por Nombre":
                clientes = controller.buscarClientesPorNombreOApellido(criterio);
                break;
            case "Por Documento":
                Cliente cliente = controller.buscarClientePorDocumento(criterio);
                if (cliente != null) {
                    clientes = new java.util.ArrayList<>();
                    clientes.add(cliente);
                }
                break;
            default:
                clientes = controller.listarTodosLosClientes();
                break;
        }
        
        actualizarTabla(clientes);
    }
    
    private void cargarDatos() {
        List<Cliente> clientes = controller.listarTodosLosClientes();
        actualizarTabla(clientes);
    }
    
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
}