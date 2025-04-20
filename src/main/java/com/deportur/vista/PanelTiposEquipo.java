package com.deportur.vista;

import com.deportur.controlador.InventarioController;
import com.deportur.controlador.UsuarioController;
import com.deportur.modelo.TipoEquipo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PanelTiposEquipo extends JPanel {
    
    private InventarioController controller;
    
    // Componentes de la interfaz
    private JTextField txtBuscar;
    private JButton btnBuscar;
    private JButton btnAgregar;
    private JButton btnModificar;
    private JButton btnEliminar;
    private JButton btnRefrescar;
    private JTable tblTipos;
    private DefaultTableModel tableModel;
    private JScrollPane scrollPane;
    
    public PanelTiposEquipo() {
        controller = new InventarioController();
        initComponents();
        cargarDatos();
    }
    
    private void initComponents() {
        // Configurar el layout
        setLayout(new BorderLayout());
        
        // Panel de búsqueda
        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        JLabel lblBuscar = new JLabel("Buscar por nombre:");
        txtBuscar = new JTextField(20);
        btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(e -> buscarTipos());
        
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
        btnEliminar.addActionListener(e -> eliminarTipoSeleccionado());
        
        btnRefrescar = new JButton("Refrescar");
        btnRefrescar.addActionListener(e -> cargarDatos());
        
        // Verificar permisos de edición
        UsuarioController usuarioController = MainFrame.getUsuarioController();
        boolean puedeEditar = usuarioController.puedeEditarTiposEquipo();
        
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
        
        // Tabla de tipos de equipo
        String[] columnas = {"ID", "Nombre", "Descripción"};
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Hacer que la tabla no sea editable
            }
        };
        
        tblTipos = new JTable(tableModel);
        tblTipos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblTipos.getTableHeader().setReorderingAllowed(false);
        
        scrollPane = new JScrollPane(tblTipos);
        
        // Añadir componentes al panel principal
        add(panelSuperior, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private void mostrarFormularioAgregar() {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Agregar Tipo de Equipo", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 250);
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
        panel.add(new JLabel("Descripción:"), gbc);
        
        gbc.gridx = 1;
        JTextArea txtDescripcion = new JTextArea(4, 20);
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
                String descripcion = txtDescripcion.getText().trim();
                
                if (controller.registrarTipoEquipo(nombre, descripcion)) {
                    JOptionPane.showMessageDialog(dialog, "Tipo de equipo registrado con éxito", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    cargarDatos();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Error al registrar el tipo de equipo", "Error", JOptionPane.ERROR_MESSAGE);
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
        int filaSeleccionada = tblTipos.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un tipo de equipo para modificar", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int idTipo = (int) tblTipos.getValueAt(filaSeleccionada, 0);
        TipoEquipo tipo = controller.buscarTipoEquipoPorId(idTipo);
        
        if (tipo == null) {
            JOptionPane.showMessageDialog(this, "No se pudo cargar la información del tipo de equipo", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Modificar Tipo de Equipo", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Campos del formulario con datos del tipo
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Nombre:"), gbc);
        
        gbc.gridx = 1;
        JTextField txtNombre = new JTextField(20);
        txtNombre.setText(tipo.getNombre());
        panel.add(txtNombre, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Descripción:"), gbc);
        
        gbc.gridx = 1;
        JTextArea txtDescripcion = new JTextArea(4, 20);
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        txtDescripcion.setText(tipo.getDescripcion());
        JScrollPane scrollDescripcion = new JScrollPane(txtDescripcion);
        panel.add(scrollDescripcion, gbc);
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.addActionListener(e -> {
            try {
                String nombre = txtNombre.getText().trim();
                String descripcion = txtDescripcion.getText().trim();
                
                if (controller.actualizarTipoEquipo(idTipo, nombre, descripcion)) {
                    JOptionPane.showMessageDialog(dialog, "Tipo de equipo actualizado con éxito", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    cargarDatos();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Error al actualizar el tipo de equipo", "Error", JOptionPane.ERROR_MESSAGE);
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
    
    private void eliminarTipoSeleccionado() {
        int filaSeleccionada = tblTipos.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un tipo de equipo para eliminar", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int idTipo = (int) tblTipos.getValueAt(filaSeleccionada, 0);
        String nombreTipo = (String) tblTipos.getValueAt(filaSeleccionada, 1);
        
        int confirmacion = JOptionPane.showConfirmDialog(this,
            "¿Está seguro de eliminar el tipo de equipo '" + nombreTipo + "'?",
            "Confirmar eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                if (controller.eliminarTipoEquipo(idTipo)) {
                    JOptionPane.showMessageDialog(this, "Tipo de equipo eliminado con éxito", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    cargarDatos();
                } else {
                    JOptionPane.showMessageDialog(this, "Error al eliminar el tipo de equipo", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void buscarTipos() {
        String criterio = txtBuscar.getText().trim();
        
        cargarDatos(); // Refrescar la tabla completa
        
        if (criterio.isEmpty()) {
            return; // No hay criterio de búsqueda, mostrar todo
        }
        
        // Filtrar resultados (implementación simple)
        DefaultTableModel modeloFiltrado = new DefaultTableModel(
            new String[] {"ID", "Nombre", "Descripción"}, 0);
        
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String nombre = (String) tableModel.getValueAt(i, 1);
            if (nombre.toLowerCase().contains(criterio.toLowerCase())) {
                Object[] row = new Object[3];
                for (int j = 0; j < 3; j++) {
                    row[j] = tableModel.getValueAt(i, j);
                }
                modeloFiltrado.addRow(row);
            }
        }
        
        tblTipos.setModel(modeloFiltrado);
    }
    
    private void cargarDatos() {
        // Limpiar la tabla
        tableModel.setRowCount(0);
        
        // Obtener todos los tipos de equipo
        List<TipoEquipo> tipos = controller.listarTodosLosTiposEquipo();
        
        if (tipos == null) {
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
}