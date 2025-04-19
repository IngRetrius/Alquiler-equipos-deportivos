package com.deportur.controlador;

import com.deportur.modelo.Reserva;
import com.deportur.modelo.Cliente;
import com.deportur.modelo.DetalleReserva;
import com.deportur.modelo.EquipoDeportivo;
import com.deportur.modelo.DestinoTuristico;
import com.deportur.servicio.GestionReservasService;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import javax.swing.JOptionPane;

public class ReservasController {
    
    private GestionReservasService reservasService;
    
    public ReservasController() {
        this.reservasService = new GestionReservasService();
    }
    
    // Métodos para Reservas
    
    public boolean crearReserva(Cliente cliente, Date fechaInicio, Date fechaFin, 
                               DestinoTuristico destino, List<EquipoDeportivo> equipos) {
        try {
            Reserva reserva = new Reserva();
            reserva.setCliente(cliente);
            reserva.setFechaCreacion(new Date()); // Fecha actual
            reserva.setFechaInicio(fechaInicio);
            reserva.setFechaFin(fechaFin);
            reserva.setDestino(destino);
            reserva.setEstado("Pendiente");
            
            List<DetalleReserva> detalles = new ArrayList<>();
            for (EquipoDeportivo equipo : equipos) {
                DetalleReserva detalle = new DetalleReserva();
                detalle.setEquipo(equipo);
                detalle.setPrecioUnitario(equipo.getPrecioAlquiler());
                detalle.setReserva(reserva);
                detalles.add(detalle);
            }
            
            reserva.setDetalles(detalles);
            
            return reservasService.crearReserva(reserva);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al crear reserva: " + e.getMessage(),
                                         "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    public boolean modificarReserva(int idReserva, Cliente cliente, Date fechaInicio, 
                                   Date fechaFin, DestinoTuristico destino, 
                                   String estado, List<EquipoDeportivo> equipos) {
        try {
            Reserva reserva = reservasService.consultarReserva(idReserva);
            if (reserva == null) {
                JOptionPane.showMessageDialog(null, "La reserva no existe",
                                             "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            reserva.setCliente(cliente);
            reserva.setFechaInicio(fechaInicio);
            reserva.setFechaFin(fechaFin);
            reserva.setDestino(destino);
            reserva.setEstado(estado);
            
            List<DetalleReserva> detalles = new ArrayList<>();
            for (EquipoDeportivo equipo : equipos) {
                DetalleReserva detalle = new DetalleReserva();
                detalle.setEquipo(equipo);
                detalle.setPrecioUnitario(equipo.getPrecioAlquiler());
                detalle.setReserva(reserva);
                detalles.add(detalle);
            }
            
            reserva.setDetalles(detalles);
            
            return reservasService.modificarReserva(reserva);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al modificar reserva: " + e.getMessage(),
                                         "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    public boolean cancelarReserva(int idReserva) {
        try {
            return reservasService.cancelarReserva(idReserva);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al cancelar reserva: " + e.getMessage(),
                                         "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    public Reserva consultarReserva(int idReserva) {
        return reservasService.consultarReserva(idReserva);
    }
    
    public List<Reserva> listarTodasLasReservas() {
        return reservasService.listarTodasLasReservas();
    }
    
    public List<Reserva> buscarReservasPorCliente(int idCliente) {
        try {
            return reservasService.buscarReservasPorCliente(idCliente);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al buscar reservas por cliente: " + e.getMessage(),
                                         "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
    
    public List<Reserva> buscarReservasPorDestino(int idDestino) {
        try {
            return reservasService.buscarReservasPorDestino(idDestino);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al buscar reservas por destino: " + e.getMessage(),
                                         "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
    
    // Métodos para Clientes
    
    public boolean registrarCliente(String nombre, String apellido, String documento, 
                                   String tipoDocumento, String telefono, 
                                   String email, String direccion) {
        try {
            Cliente cliente = new Cliente();
            cliente.setNombre(nombre);
            cliente.setApellido(apellido);
            cliente.setDocumento(documento);
            cliente.setTipoDocumento(tipoDocumento);
            cliente.setTelefono(telefono);
            cliente.setEmail(email);
            cliente.setDireccion(direccion);
            
            return reservasService.registrarCliente(cliente);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al registrar cliente: " + e.getMessage(),
                                         "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    public boolean actualizarCliente(int idCliente, String nombre, String apellido, 
                                    String documento, String tipoDocumento, 
                                    String telefono, String email, String direccion) {
        try {
            Cliente cliente = new Cliente();
            cliente.setIdCliente(idCliente);
            cliente.setNombre(nombre);
            cliente.setApellido(apellido);
            cliente.setDocumento(documento);
            cliente.setTipoDocumento(tipoDocumento);
            cliente.setTelefono(telefono);
            cliente.setEmail(email);
            cliente.setDireccion(direccion);
            
            return reservasService.actualizarCliente(cliente);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al actualizar cliente: " + e.getMessage(),
                                         "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    public boolean eliminarCliente(int idCliente) {
        try {
            return reservasService.eliminarCliente(idCliente);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al eliminar cliente: " + e.getMessage(),
                                         "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    public Cliente buscarClientePorId(int idCliente) {
        return reservasService.buscarClientePorId(idCliente);
    }
    
    public Cliente buscarClientePorDocumento(String documento) {
        return reservasService.buscarClientePorDocumento(documento);
    }
    
    public List<Cliente> listarTodosLosClientes() {
        return reservasService.listarTodosLosClientes();
    }
    
    public List<Cliente> buscarClientesPorNombreOApellido(String criterio) {
        return reservasService.buscarClientesPorNombreOApellido(criterio);
    }
    
    // Método para verificar disponibilidad de equipos
    public boolean verificarDisponibilidadEquipo(int idEquipo, Date fechaInicio, Date fechaFin) {
        try {
            return reservasService.verificarDisponibilidadEquipo(idEquipo, fechaInicio, fechaFin);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al verificar disponibilidad: " + e.getMessage(),
                                         "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
}