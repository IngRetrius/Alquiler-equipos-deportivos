package com.deportur.vista;

import com.deportur.controlador.InventarioController;
import com.deportur.controlador.UsuarioController;
import com.deportur.modelo.DestinoTuristico;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PanelDestinos extends JPanel {
    
    private InventarioController controller;
    
    // Componentes de la interfaz
    private JTextField txtBuscar;
    private JButton btnBuscar;
    private JButton btnAgregar;
    private JButton btnModificar;
    private JButton btnEliminar;
    private JButton btnRefrescar;
    private JTable tblDestinos;
    private DefaultTableModel tableModel;
    private JScrollPane scrollPane;
    
    public PanelDestinos() {
        controller = new InventarioController();
        initComponents();
        cargarDatos();
    }
    
    private void initComponents() {
        // Configurar el layout
        setLayout(new BorderLayout());
        
        // Panel de búsqueda y filtros
        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        JLabel lblBuscar = new JLabel("Buscar por nombre o ubicación:");
        txtBuscar = new JTextField(20);
        btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(e -> buscarDestinos());
        
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
        btnEliminar.addActionListener(e -> eliminarDestinoSeleccionado());
        
        btnRefrescar = new JButton("Refrescar");
        btnRefrescar.addActionListener(e -> cargarDatos());
        
        // Verificar permisos de edición
        UsuarioController usuarioController = MainFrame.getUsuarioController();
        boolean puedeEditar = usuarioController.puedeEditarDestinos();
        
        btnAgregar.setEnabled(puedeEditar);
        btnModificar.setEnabled(puedeEditar);
        btnEliminar.setEnabled(puedeEditar);
        
        panelBotones.add(btnAgregar);
        panelBotones.add(btnModificar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnRefrescar);
        
        // Panel superior que contiene búsqueda y botones
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.add(panelBusqueda, BorderLayout.WEST);
        panelSuperior.add(panelBotones, BorderLayout.EAST);
        
        // Tabla de destinos
        String[] columnas = {"ID", "Nombre", "Ubicación", "Descripción"};
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Hacer que la tabla no sea editable
            }
        };
        
        tblDestinos = new JTable(tableModel);
        tblDestinos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblDestinos.getTableHeader().setReorderingAllowed(false);
        
        scrollPane = new JScrollPane(tblDestinos);
        
        // Añadir componentes al panel principal
        add(panelSuperior, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private void mostrarFormularioAgregar() {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Agregar Destino Turístico", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 300);
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
        panel.add(new JLabel("Ubicación:"), gbc);
        
        gbc.gridx = 1;
        JTextField txtUbicacion = new JTextField(20);
        panel.add(txtUbicacion, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Descripción:"), gbc);
        
        gbc.gridx = 1;
        JTextArea txtDescripcion = new JTextArea(5, 20);
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        JScrollPane scrollDescripcion = new JScrollPane(txtDescripcion);
        panel.add(scrollDescripcion, gbc);
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.addActionListener(e -> {
            try {
                String nombre = txtNombre.getText().trim();
                String ubicacion = txtUbicacion.getText().trim();
                String descripcion = txtDescripcion.getText().trim();
                
                if (controller.registrarDestino(nombre, ubicacion, descripcion)) {
                    JOptionPane.showMessageDialog(dialog, "Destino turístico registrado con éxito", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    cargarDatos();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Error al registrar el destino turístico", "Error", JOptionPane.ERROR_MESSAGE);
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
        int filaSeleccionada = tblDestinos.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un destino para modificar", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int idDestino = (int) tblDestinos.getValueAt(filaSeleccionada, 0);
        DestinoTuristico destino = controller.buscarDestinoPorId(idDestino);
        
        if (destino == null) {
            JOptionPane.showMessageDialog(this, "No se pudo cargar la información del destino", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Modificar Destino Turístico", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Campos del formulario con datos del destino
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Nombre:"), gbc);
        
        gbc.gridx = 1;
        JTextField txtNombre = new JTextField(20);
        txtNombre.setText(destino.getNombre());
        panel.add(txtNombre, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Ubicación:"), gbc);
        
        gbc.gridx = 1;
        JTextField txtUbicacion = new JTextField(20);
        txtUbicacion.setText(destino.getUbicacion());
        panel.add(txtUbicacion, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Descripción:"), gbc);
        
        gbc.gridx = 1;
        JTextArea txtDescripcion = new JTextArea(5, 20);
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        txtDescripcion.setText(destino.getDescripcion());
        JScrollPane scrollDescripcion = new JScrollPane(txtDescripcion);
        panel.add(scrollDescripcion, gbc);
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.addActionListener(e -> {
            try {
                String nombre = txtNombre.getText().trim();
                String ubicacion = txtUbicacion.getText().trim();
                String descripcion = txtDescripcion.getText().trim();
                
                if (controller.actualizarDestino(idDestino, nombre, ubicacion, descripcion)) {
                    JOptionPane.showMessageDialog(dialog, "Destino turístico actualizado con éxito", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    cargarDatos();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Error al actualizar el destino turístico", "Error", JOptionPane.ERROR_MESSAGE);
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
    
    private void eliminarDestinoSeleccionado() {
        int filaSeleccionada = tblDestinos.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un destino para eliminar", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int idDestino = (int) tblDestinos.getValueAt(filaSeleccionada, 0);
        String nombreDestino = (String) tblDestinos.getValueAt(filaSeleccionada, 1);
        
        int confirmacion = JOptionPane.showConfirmDialog(this,
            "¿Está seguro de eliminar el destino '" + nombreDestino + "'?",
            "Confirmar eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                if (controller.eliminarDestino(idDestino)) {
                    JOptionPane.showMessageDialog(this, "Destino eliminado con éxito", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    cargarDatos();
                } else {
                    JOptionPane.showMessageDialog(this, "Error al eliminar el destino", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void buscarDestinos() {
        String criterio = txtBuscar.getText().trim();
        
        if (criterio.isEmpty()) {
            cargarDatos();
            return;
        }
        
        List<DestinoTuristico> destinos = controller.buscarDestinosPorNombreOUbicacion(criterio);
        actualizarTabla(destinos);
    }
    
    private void cargarDatos() {
        List<DestinoTuristico> destinos = controller.listarTodosLosDestinos();
        actualizarTabla(destinos);
    }
    
    private void actualizarTabla(List<DestinoTuristico> destinos) {
        // Limpiar la tabla
        tableModel.setRowCount(0);
        
        if (destinos == null) {
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
}