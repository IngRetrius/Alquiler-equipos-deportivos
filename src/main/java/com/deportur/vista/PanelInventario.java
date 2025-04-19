package com.deportur.vista;

import com.deportur.controlador.InventarioController;
import com.deportur.modelo.EquipoDeportivo;
import com.deportur.modelo.TipoEquipo;
import com.deportur.modelo.DestinoTuristico;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PanelInventario extends JPanel {
    
    private InventarioController controller;
    
    // Componentes de la interfaz
    private JTextField txtBuscar;
    private JComboBox<String> cmbFiltro;
    private JButton btnBuscar;
    private JButton btnAgregar;
    private JButton btnModificar;
    private JButton btnEliminar;
    private JButton btnRefrescar;
    private JTable tblEquipos;
    private DefaultTableModel tableModel;
    private JScrollPane scrollPane;
    
    public PanelInventario() {
        controller = new InventarioController();
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
        cmbFiltro = new JComboBox<>(new String[] {"Todos", "Por Tipo", "Por Destino", "Por Disponibilidad"});
        btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(e -> buscarEquipos());
        
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
        btnEliminar.addActionListener(e -> eliminarEquipoSeleccionado());
        
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
        
        // Tabla de equipos
        String[] columnas = {"ID", "Nombre", "Tipo", "Marca", "Estado", "Precio Alquiler", "Fecha Adquisición", "Destino", "Disponible"};
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Hacer que la tabla no sea editable
            }
        };
        
        tblEquipos = new JTable(tableModel);
        tblEquipos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblEquipos.getTableHeader().setReorderingAllowed(false);
        
        scrollPane = new JScrollPane(tblEquipos);
        
        // Añadir componentes al panel principal
        add(panelSuperior, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    public void mostrarFormularioAgregar() {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Agregar Equipo", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(500, 400);
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
        panel.add(new JLabel("Tipo:"), gbc);
        
        gbc.gridx = 1;
        JComboBox<TipoEquipo> cmbTipo = new JComboBox<>();
        cargarTiposEquipo(cmbTipo);
        panel.add(cmbTipo, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Marca:"), gbc);
        
        gbc.gridx = 1;
        JTextField txtMarca = new JTextField(20);
        panel.add(txtMarca, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Estado:"), gbc);
        
        gbc.gridx = 1;
        JComboBox<String> cmbEstado = new JComboBox<>(new String[] {"Nuevo", "Bueno", "Regular", "Mantenimiento", "Fuera de servicio"});
        panel.add(cmbEstado, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("Precio Alquiler:"), gbc);
        
        gbc.gridx = 1;
        JTextField txtPrecio = new JTextField(20);
        panel.add(txtPrecio, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(new JLabel("Fecha Adquisición:"), gbc);
        
        gbc.gridx = 1;
        JFormattedTextField txtFecha = new JFormattedTextField(new SimpleDateFormat("dd/MM/yyyy"));
        txtFecha.setValue(new Date());
        panel.add(txtFecha, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 6;
        panel.add(new JLabel("Destino:"), gbc);
        
        gbc.gridx = 1;
        JComboBox<DestinoTuristico> cmbDestino = new JComboBox<>();
        cargarDestinosTuristicos(cmbDestino);
        panel.add(cmbDestino, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 7;
        panel.add(new JLabel("Disponible:"), gbc);
        
        gbc.gridx = 1;
        JCheckBox chkDisponible = new JCheckBox();
        chkDisponible.setSelected(true);
        panel.add(chkDisponible, gbc);
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton btnGuardar = new JButton("Guardar");
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
                
                if (controller.registrarEquipo(nombre, tipo, marca, estado, precio, fechaAdq, destino, disponible)) {
                    JOptionPane.showMessageDialog(dialog, "Equipo registrado con éxito", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    cargarDatos();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Error al registrar el equipo", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "El precio debe ser un valor numérico", "Error", JOptionPane.ERROR_MESSAGE);
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
    
    public void mostrarFormularioModificar() {
        int filaSeleccionada = tblEquipos.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un equipo para modificar", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int idEquipo = (int) tblEquipos.getValueAt(filaSeleccionada, 0);
        EquipoDeportivo equipo = controller.buscarEquipoPorId(idEquipo);
        
        if (equipo == null) {
            JOptionPane.showMessageDialog(this, "No se pudo cargar la información del equipo", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Modificar Equipo", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Campos del formulario con datos del equipo
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Nombre:"), gbc);
        
        gbc.gridx = 1;
        JTextField txtNombre = new JTextField(20);
        txtNombre.setText(equipo.getNombre());
        panel.add(txtNombre, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Tipo:"), gbc);
        
        gbc.gridx = 1;
        JComboBox<TipoEquipo> cmbTipo = new JComboBox<>();
        cargarTiposEquipo(cmbTipo);
        seleccionarTipoEquipo(cmbTipo, equipo.getTipo());
        panel.add(cmbTipo, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Marca:"), gbc);
        
        gbc.gridx = 1;
        JTextField txtMarca = new JTextField(20);
        txtMarca.setText(equipo.getMarca());
        panel.add(txtMarca, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Estado:"), gbc);
        
        gbc.gridx = 1;
        JComboBox<String> cmbEstado = new JComboBox<>(new String[] {"Nuevo", "Bueno", "Regular", "Mantenimiento", "Fuera de servicio"});
        cmbEstado.setSelectedItem(equipo.getEstado());
        panel.add(cmbEstado, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("Precio Alquiler:"), gbc);
        
        gbc.gridx = 1;
        JTextField txtPrecio = new JTextField(20);
        txtPrecio.setText(String.valueOf(equipo.getPrecioAlquiler()));
        panel.add(txtPrecio, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(new JLabel("Fecha Adquisición:"), gbc);
        
        gbc.gridx = 1;
        JFormattedTextField txtFecha = new JFormattedTextField(new SimpleDateFormat("dd/MM/yyyy"));
        txtFecha.setValue(equipo.getFechaAdquisicion());
        panel.add(txtFecha, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 6;
        panel.add(new JLabel("Destino:"), gbc);
        
        gbc.gridx = 1;
        JComboBox<DestinoTuristico> cmbDestino = new JComboBox<>();
        cargarDestinosTuristicos(cmbDestino);
        seleccionarDestinoTuristico(cmbDestino, equipo.getDestino());
        panel.add(cmbDestino, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 7;
        panel.add(new JLabel("Disponible:"), gbc);
        
        gbc.gridx = 1;
        JCheckBox chkDisponible = new JCheckBox();
        chkDisponible.setSelected(equipo.isDisponible());
        panel.add(chkDisponible, gbc);
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton btnGuardar = new JButton("Guardar");
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
                
                if (controller.actualizarEquipo(idEquipo, nombre, tipo, marca, estado, precio, fechaAdq, destino, disponible)) {
                    JOptionPane.showMessageDialog(dialog, "Equipo actualizado con éxito", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    cargarDatos();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Error al actualizar el equipo", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "El precio debe ser un valor numérico", "Error", JOptionPane.ERROR_MESSAGE);
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
    
    private void eliminarEquipoSeleccionado() {
        int filaSeleccionada = tblEquipos.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un equipo para eliminar", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int idEquipo = (int) tblEquipos.getValueAt(filaSeleccionada, 0);
        String nombreEquipo = (String) tblEquipos.getValueAt(filaSeleccionada, 1);
        
        int confirmacion = JOptionPane.showConfirmDialog(this,
            "¿Está seguro de eliminar el equipo '" + nombreEquipo + "'?",
            "Confirmar eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            if (controller.eliminarEquipo(idEquipo)) {
                JOptionPane.showMessageDialog(this, "Equipo eliminado con éxito", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarDatos();
            } else {
                JOptionPane.showMessageDialog(this, "Error al eliminar el equipo", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void buscarEquipos() {
        // Implementar la búsqueda según los criterios seleccionados
        // Esta es una implementación básica que podría mejorarse
        String filtro = (String) cmbFiltro.getSelectedItem();
        String criterio = txtBuscar.getText().trim();
        
        cargarDatos(); // Refrescar la tabla completa
        
        if (criterio.isEmpty()) {
            return; // No hay criterio de búsqueda, mostrar todo
        }
        
        // Filtrar resultados
        DefaultTableModel modeloFiltrado = new DefaultTableModel(
            new String[] {"ID", "Nombre", "Tipo", "Marca", "Estado", "Precio Alquiler", "Fecha Adquisición", "Destino", "Disponible"}, 0);
        
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            boolean agregar = false;
            
            switch (filtro) {
                case "Todos":
                    agregar = true;
                    break;
                case "Por Tipo":
                    String tipo = (String) tableModel.getValueAt(i, 2);
                    agregar = tipo.toLowerCase().contains(criterio.toLowerCase());
                    break;
                case "Por Destino":
                    String destino = (String) tableModel.getValueAt(i, 7);
                    agregar = destino.toLowerCase().contains(criterio.toLowerCase());
                    break;
                case "Por Disponibilidad":
                    boolean disponible = (boolean) tableModel.getValueAt(i, 8);
                    agregar = disponible == Boolean.parseBoolean(criterio);
                    break;
            }
            
            if (agregar) {
                Object[] row = new Object[9];
                for (int j = 0; j < 9; j++) {
                    row[j] = tableModel.getValueAt(i, j);
                }
                modeloFiltrado.addRow(row);
            }
        }
        
        tblEquipos.setModel(modeloFiltrado);
    }
    
    private void cargarDatos() {
        // Limpiar la tabla
        tableModel.setRowCount(0);
        
        // Obtener todos los equipos
        List<EquipoDeportivo> equipos = controller.listarTodosLosEquipos();
        
        // Llenar la tabla con los datos
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        
        for (EquipoDeportivo equipo : equipos) {
            Object[] row = {
                equipo.getIdEquipo(),
                equipo.getNombre(),
                equipo.getTipo().getNombre(),
                equipo.getMarca(),
                equipo.getEstado(),
                equipo.getPrecioAlquiler(),
                sdf.format(equipo.getFechaAdquisicion()),
                equipo.getDestino().getNombre(),
                equipo.isDisponible()
            };
            tableModel.addRow(row);
        }
    }
    
    private void cargarTiposEquipo(JComboBox<TipoEquipo> comboBox) {
        comboBox.removeAllItems();
        List<TipoEquipo> tipos = controller.listarTodosLosTiposEquipo();
        for (TipoEquipo tipo : tipos) {
            comboBox.addItem(tipo);
        }
    }
    
    private void cargarDestinosTuristicos(JComboBox<DestinoTuristico> comboBox) {
        comboBox.removeAllItems();
        List<DestinoTuristico> destinos = controller.listarTodosLosDestinos();
        for (DestinoTuristico destino : destinos) {
            comboBox.addItem(destino);
        }
    }
    
    private void seleccionarTipoEquipo(JComboBox<TipoEquipo> comboBox, TipoEquipo tipo) {
        for (int i = 0; i < comboBox.getItemCount(); i++) {
            TipoEquipo item = comboBox.getItemAt(i);
            if (item.getIdTipo() == tipo.getIdTipo()) {
                comboBox.setSelectedIndex(i);
                break;
            }
        }
    }
    
    private void seleccionarDestinoTuristico(JComboBox<DestinoTuristico> comboBox, DestinoTuristico destino) {
        for (int i = 0; i < comboBox.getItemCount(); i++) {
            DestinoTuristico item = comboBox.getItemAt(i);
            if (item.getIdDestino() == destino.getIdDestino()) {
                comboBox.setSelectedIndex(i);
                break;
            }
        }
    }
}