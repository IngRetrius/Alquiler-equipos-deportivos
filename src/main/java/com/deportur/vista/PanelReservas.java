package com.deportur.vista;

import com.deportur.controlador.ReservasController;
import com.deportur.controlador.InventarioController;
import com.deportur.modelo.Reserva;
import com.deportur.modelo.Cliente;
import com.deportur.modelo.DestinoTuristico;
import com.deportur.modelo.EquipoDeportivo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

public class PanelReservas extends JPanel {
    
    private ReservasController controller;
    private InventarioController inventarioController;
    
    // Componentes de la interfaz
    private JTextField txtBuscar;
    private JComboBox<String> cmbFiltro;
    private JButton btnBuscar;
    private JButton btnCrear;
    private JButton btnModificar;
    private JButton btnCancelar;
    private JButton btnConsultar;
    private JButton btnRefrescar;
    private JTable tblReservas;
    private DefaultTableModel tableModel;
    private JScrollPane scrollPane;
    
    public PanelReservas() {
        controller = new ReservasController();
        inventarioController = new InventarioController();
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
        cmbFiltro = new JComboBox<>(new String[] {"Todas", "Por Cliente", "Por Destino", "Por Estado"});
        btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(e -> buscarReservas());
        
        panelBusqueda.add(lblBuscar);
        panelBusqueda.add(txtBuscar);
        panelBusqueda.add(new JLabel("Filtrar:"));
        panelBusqueda.add(cmbFiltro);
        panelBusqueda.add(btnBuscar);
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        btnCrear = new JButton("Crear Reserva");
        btnCrear.addActionListener(e -> mostrarFormularioCrear());
        
        btnModificar = new JButton("Modificar");
        btnModificar.addActionListener(e -> mostrarFormularioModificar());
        
        btnCancelar = new JButton("Cancelar Reserva");
        btnCancelar.addActionListener(e -> cancelarReservaSeleccionada());
        
        btnConsultar = new JButton("Consultar");
        btnConsultar.addActionListener(e -> consultarReservaSeleccionada());
        
        btnRefrescar = new JButton("Refrescar");
        btnRefrescar.addActionListener(e -> cargarDatos());
        
        panelBotones.add(btnCrear);
        panelBotones.add(btnModificar);
        panelBotones.add(btnCancelar);
        panelBotones.add(btnConsultar);
        panelBotones.add(btnRefrescar);
        
        // Panel superior que contiene búsqueda y botones
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.add(panelBusqueda, BorderLayout.WEST);
        panelSuperior.add(panelBotones, BorderLayout.EAST);
        
        // Tabla de reservas
        String[] columnas = {"ID", "Cliente", "Fecha Creación", "Fecha Inicio", "Fecha Fin", "Destino", "Estado"};
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Hacer que la tabla no sea editable
            }
        };
        
        tblReservas = new JTable(tableModel);
        tblReservas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblReservas.getTableHeader().setReorderingAllowed(false);
        
        scrollPane = new JScrollPane(tblReservas);
        
        // Añadir componentes al panel principal
        add(panelSuperior, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    public void mostrarFormularioCrear() {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Crear Reserva", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(600, 500);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Campos del formulario
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Cliente:"), gbc);
        
        gbc.gridx = 1;
        JComboBox<Cliente> cmbCliente = new JComboBox<>();
        cargarClientes(cmbCliente);
        panel.add(cmbCliente, gbc);
        
        gbc.gridx = 2;
        JButton btnNuevoCliente = new JButton("Nuevo Cliente");
        btnNuevoCliente.addActionListener(e -> mostrarFormularioCliente(cmbCliente));
        panel.add(btnNuevoCliente, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Destino:"), gbc);
        
        gbc.gridx = 1;
        JComboBox<DestinoTuristico> cmbDestino = new JComboBox<>();
        cargarDestinos(cmbDestino);
        panel.add(cmbDestino, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Fecha Inicio:"), gbc);
        
        gbc.gridx = 1;
        JFormattedTextField txtFechaInicio = new JFormattedTextField(new SimpleDateFormat("dd/MM/yyyy"));
        txtFechaInicio.setValue(new Date());
        panel.add(txtFechaInicio, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Fecha Fin:"), gbc);
        
        gbc.gridx = 1;
        JFormattedTextField txtFechaFin = new JFormattedTextField(new SimpleDateFormat("dd/MM/yyyy"));
        Date fechaFin = new Date();
        fechaFin.setTime(fechaFin.getTime() + (7 * 24 * 60 * 60 * 1000)); // Una semana después
        txtFechaFin.setValue(fechaFin);
        
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("Equipos Disponibles:"), gbc);
        
        // Botón para buscar equipos disponibles
        gbc.gridx = 1;
        JButton btnBuscarEquipos = new JButton("Buscar Equipos Disponibles");
        panel.add(btnBuscarEquipos, gbc);
        
        // Lista de equipos disponibles y seleccionados
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 3;
        
        JPanel panelEquipos = new JPanel(new BorderLayout());
        
        DefaultListModel<EquipoDeportivo> modeloDisponibles = new DefaultListModel<>();
        JList<EquipoDeportivo> listaDisponibles = new JList<>(modeloDisponibles);
        JScrollPane scrollDisponibles = new JScrollPane(listaDisponibles);
        scrollDisponibles.setBorder(BorderFactory.createTitledBorder("Equipos Disponibles"));
        
        DefaultListModel<EquipoDeportivo> modeloSeleccionados = new DefaultListModel<>();
        JList<EquipoDeportivo> listaSeleccionados = new JList<>(modeloSeleccionados);
        JScrollPane scrollSeleccionados = new JScrollPane(listaSeleccionados);
        scrollSeleccionados.setBorder(BorderFactory.createTitledBorder("Equipos Seleccionados"));
        
        JPanel panelBotonesEquipos = new JPanel(new GridLayout(2, 1, 0, 10));
        JButton btnAgregar = new JButton(">>");
        JButton btnQuitar = new JButton("<<");
        
        panelBotonesEquipos.add(btnAgregar);
        panelBotonesEquipos.add(btnQuitar);
        
        JPanel panelCentro = new JPanel(new BorderLayout());
        panelCentro.add(panelBotonesEquipos, BorderLayout.CENTER);
        
        panelEquipos.add(scrollDisponibles, BorderLayout.WEST);
        panelEquipos.add(panelCentro, BorderLayout.CENTER);
        panelEquipos.add(scrollSeleccionados, BorderLayout.EAST);
        
        scrollDisponibles.setPreferredSize(new Dimension(200, 200));
        scrollSeleccionados.setPreferredSize(new Dimension(200, 200));
        
        panel.add(panelEquipos, gbc);
        
        // Eventos para los botones de equipos
        btnBuscarEquipos.addActionListener(e -> {
            try {
                modeloDisponibles.clear();
                
                if (cmbDestino.getSelectedItem() == null) {
                    JOptionPane.showMessageDialog(dialog, "Debe seleccionar un destino", "Advertencia", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                DestinoTuristico destino = (DestinoTuristico) cmbDestino.getSelectedItem();
                Date fechaInicio = (Date) txtFechaInicio.getValue();
                Date fechaFinBusqueda = (Date) txtFechaFin.getValue(); // Cambiado el nombre
                
                if (fechaInicio == null || fechaFinBusqueda == null) {
                    JOptionPane.showMessageDialog(dialog, "Debe especificar las fechas de inicio y fin", "Advertencia", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                if (fechaInicio.after(fechaFinBusqueda)) {
                    JOptionPane.showMessageDialog(dialog, "La fecha de inicio no puede ser posterior a la fecha de fin", "Advertencia", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                java.sql.Date sqlFechaInicio = new java.sql.Date(fechaInicio.getTime());
                java.sql.Date sqlFechaFin = new java.sql.Date(fechaFinBusqueda.getTime());
                
                List<EquipoDeportivo> equiposDisponibles = inventarioController.buscarEquiposDisponibles(
                    destino.getIdDestino(), sqlFechaInicio, sqlFechaFin);
                
                for (EquipoDeportivo equipo : equiposDisponibles) {
                    modeloDisponibles.addElement(equipo);
                }
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error al buscar equipos: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        btnAgregar.addActionListener(e -> {
            List<EquipoDeportivo> seleccionados = listaDisponibles.getSelectedValuesList();
            for (EquipoDeportivo equipo : seleccionados) {
                modeloDisponibles.removeElement(equipo);
                modeloSeleccionados.addElement(equipo);
            }
        });
        
        btnQuitar.addActionListener(e -> {
            List<EquipoDeportivo> seleccionados = listaSeleccionados.getSelectedValuesList();
            for (EquipoDeportivo equipo : seleccionados) {
                modeloSeleccionados.removeElement(equipo);
                modeloDisponibles.addElement(equipo);
            }
        });
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.addActionListener(e -> {
            try {
                if (cmbCliente.getSelectedItem() == null) {
                    JOptionPane.showMessageDialog(dialog, "Debe seleccionar un cliente", "Advertencia", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                if (cmbDestino.getSelectedItem() == null) {
                    JOptionPane.showMessageDialog(dialog, "Debe seleccionar un destino", "Advertencia", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                Date fechaInicio = (Date) txtFechaInicio.getValue();
                if (fechaInicio == null || fechaFin == null) {
                    JOptionPane.showMessageDialog(dialog, "Debe especificar las fechas de inicio y fin", "Advertencia", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                if (modeloSeleccionados.size() == 0) {
                    JOptionPane.showMessageDialog(dialog, "Debe seleccionar al menos un equipo", "Advertencia", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                Cliente cliente = (Cliente) cmbCliente.getSelectedItem();
                DestinoTuristico destino = (DestinoTuristico) cmbDestino.getSelectedItem();
                
                List<EquipoDeportivo> equipos = new ArrayList<>();
                for (int i = 0; i < modeloSeleccionados.size(); i++) {
                    equipos.add(modeloSeleccionados.getElementAt(i));
                }
                
                if (controller.crearReserva(cliente, fechaInicio, fechaFin, destino, equipos)) {
                    JOptionPane.showMessageDialog(dialog, "Reserva creada con éxito", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    cargarDatos();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Error al crear la reserva", "Error", JOptionPane.ERROR_MESSAGE);
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
        int filaSeleccionada = tblReservas.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar una reserva para modificar", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int idReserva = (int) tblReservas.getValueAt(filaSeleccionada, 0);
        Reserva reserva = controller.consultarReserva(idReserva);
        
        if (reserva == null) {
            JOptionPane.showMessageDialog(this, "No se pudo cargar la información de la reserva", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Verificar si la reserva puede ser modificada
        if ("Finalizada".equals(reserva.getEstado()) || "Cancelada".equals(reserva.getEstado())) {
            JOptionPane.showMessageDialog(this, "No se puede modificar una reserva " + reserva.getEstado().toLowerCase(), "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Crear diálogo de modificación (similar a mostrarFormularioCrear pero con datos de la reserva)
        // Aquí solo se muestra un ejemplo simplificado:
        JOptionPane.showMessageDialog(this, "Funcionalidad de modificar reserva en desarrollo", "Información", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void cancelarReservaSeleccionada() {
        int filaSeleccionada = tblReservas.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar una reserva para cancelar", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int idReserva = (int) tblReservas.getValueAt(filaSeleccionada, 0);
        String nombreCliente = (String) tblReservas.getValueAt(filaSeleccionada, 1);
        String estado = (String) tblReservas.getValueAt(filaSeleccionada, 6);
        
        // Verificar si la reserva puede ser cancelada
        if ("Finalizada".equals(estado) || "Cancelada".equals(estado)) {
            JOptionPane.showMessageDialog(this, "No se puede cancelar una reserva " + estado.toLowerCase(), "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirmacion = JOptionPane.showConfirmDialog(this,
            "¿Está seguro de cancelar la reserva #" + idReserva + " del cliente " + nombreCliente + "?",
            "Confirmar cancelación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            if (controller.cancelarReserva(idReserva)) {
                JOptionPane.showMessageDialog(this, "Reserva cancelada con éxito", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarDatos();
            } else {
                JOptionPane.showMessageDialog(this, "Error al cancelar la reserva", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void consultarReservaSeleccionada() {
        int filaSeleccionada = tblReservas.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar una reserva para consultar", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int idReserva = (int) tblReservas.getValueAt(filaSeleccionada, 0);
        Reserva reserva = controller.consultarReserva(idReserva);
        
        if (reserva == null) {
            JOptionPane.showMessageDialog(this, "No se pudo cargar la información de la reserva", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Mostrar detalles de la reserva
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        StringBuilder mensaje = new StringBuilder();
        mensaje.append("Reserva #").append(reserva.getIdReserva()).append("\n\n");
        mensaje.append("Cliente: ").append(reserva.getCliente().getNombre()).append(" ").append(reserva.getCliente().getApellido()).append("\n");
        mensaje.append("Documento: ").append(reserva.getCliente().getDocumento()).append("\n");
        mensaje.append("Fecha de Creación: ").append(sdf.format(reserva.getFechaCreacion())).append("\n");
        mensaje.append("Fecha de Inicio: ").append(sdf.format(reserva.getFechaInicio())).append("\n");
        mensaje.append("Fecha de Fin: ").append(sdf.format(reserva.getFechaFin())).append("\n");
        mensaje.append("Destino: ").append(reserva.getDestino().getNombre()).append("\n");
        mensaje.append("Estado: ").append(reserva.getEstado()).append("\n\n");
        
        mensaje.append("Equipos Reservados:\n");
        if (reserva.getDetalles() != null && !reserva.getDetalles().isEmpty()) {
            double total = 0;
            for (int i = 0; i < reserva.getDetalles().size(); i++) {
                mensaje.append(i + 1).append(". ");
                mensaje.append(reserva.getDetalles().get(i).getEquipo().getNombre());
                mensaje.append(" - $").append(reserva.getDetalles().get(i).getPrecioUnitario());
                mensaje.append("\n");
                total += reserva.getDetalles().get(i).getPrecioUnitario();
            }
            mensaje.append("\nTotal: $").append(total);
        } else {
            mensaje.append("No hay equipos registrados en esta reserva.");
        }
        
        JOptionPane.showMessageDialog(this, mensaje.toString(), "Detalles de la Reserva", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void buscarReservas() {
        // Implementar la búsqueda según los criterios seleccionados
        // Esta es una implementación básica que podría mejorarse
        JOptionPane.showMessageDialog(this, "Funcionalidad de búsqueda en desarrollo", "Información", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void cargarDatos() {
        // Limpiar la tabla
        tableModel.setRowCount(0);
        
        // Obtener todas las reservas
        List<Reserva> reservas = controller.listarTodasLasReservas();
        
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
    }
    
    private void mostrarFormularioCliente(JComboBox<Cliente> cmbCliente) {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Nuevo Cliente", true);
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
    
    private void cargarClientes(JComboBox<Cliente> comboBox) {
        comboBox.removeAllItems();
        List<Cliente> clientes = controller.listarTodosLosClientes();
        for (Cliente cliente : clientes) {
            comboBox.addItem(cliente);
        }
    }
    
    private void cargarDestinos(JComboBox<DestinoTuristico> comboBox) {
        comboBox.removeAllItems();
        List<DestinoTuristico> destinos = inventarioController.listarTodosLosDestinos();
        for (DestinoTuristico destino : destinos) {
            comboBox.addItem(destino);
        }
    }
}