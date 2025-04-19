package com.deportur.controlador;

import com.deportur.modelo.EquipoDeportivo;
import com.deportur.modelo.TipoEquipo;
import com.deportur.modelo.DestinoTuristico;
import com.deportur.servicio.GestionInventarioService;

import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;

public class InventarioController {
    
    private GestionInventarioService inventarioService;
    
    public InventarioController() {
        this.inventarioService = new GestionInventarioService();
    }
    
    // Métodos para Equipos Deportivos
    
    public boolean registrarEquipo(String nombre, TipoEquipo tipo, String marca, 
                                  String estado, double precioAlquiler, 
                                  Date fechaAdquisicion, DestinoTuristico destino, 
                                  boolean disponible) {
        try {
            EquipoDeportivo equipo = new EquipoDeportivo();
            equipo.setNombre(nombre);
            equipo.setTipo(tipo);
            equipo.setMarca(marca);
            equipo.setEstado(estado);
            equipo.setPrecioAlquiler(precioAlquiler);
            equipo.setFechaAdquisicion(fechaAdquisicion);
            equipo.setDestino(destino);
            equipo.setDisponible(disponible);
            
            return inventarioService.registrarEquipo(equipo);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al registrar equipo: " + e.getMessage(),
                                         "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    public boolean actualizarEquipo(int idEquipo, String nombre, TipoEquipo tipo, 
                                   String marca, String estado, double precioAlquiler, 
                                   Date fechaAdquisicion, DestinoTuristico destino, 
                                   boolean disponible) {
        try {
            EquipoDeportivo equipo = new EquipoDeportivo();
            equipo.setIdEquipo(idEquipo);
            equipo.setNombre(nombre);
            equipo.setTipo(tipo);
            equipo.setMarca(marca);
            equipo.setEstado(estado);
            equipo.setPrecioAlquiler(precioAlquiler);
            equipo.setFechaAdquisicion(fechaAdquisicion);
            equipo.setDestino(destino);
            equipo.setDisponible(disponible);
            
            return inventarioService.actualizarEquipo(equipo);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al actualizar equipo: " + e.getMessage(),
                                         "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    public boolean eliminarEquipo(int idEquipo) {
        try {
            return inventarioService.eliminarEquipo(idEquipo);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al eliminar equipo: " + e.getMessage(),
                                         "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    public EquipoDeportivo buscarEquipoPorId(int idEquipo) {
        return inventarioService.buscarEquipoPorId(idEquipo);
    }
    
    public List<EquipoDeportivo> listarTodosLosEquipos() {
        return inventarioService.listarTodosLosEquipos();
    }
    
    public List<EquipoDeportivo> buscarEquiposPorTipo(int idTipo) {
        return inventarioService.buscarEquiposPorTipo(idTipo);
    }
    
    public List<EquipoDeportivo> buscarEquiposPorDestino(int idDestino) {
        return inventarioService.buscarEquiposPorDestino(idDestino);
    }
    
    public List<EquipoDeportivo> buscarEquiposDisponibles(int idDestino, java.sql.Date fechaInicio, java.sql.Date fechaFin) {
        try {
            return inventarioService.buscarEquiposDisponiblesPorDestinoYFechas(idDestino, fechaInicio, fechaFin);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al buscar equipos disponibles: " + e.getMessage(),
                                         "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
    
    // Métodos para Tipos de Equipo
    
    public boolean registrarTipoEquipo(String nombre, String descripcion) {
        try {
            TipoEquipo tipoEquipo = new TipoEquipo();
            tipoEquipo.setNombre(nombre);
            tipoEquipo.setDescripcion(descripcion);
            
            return inventarioService.registrarTipoEquipo(tipoEquipo);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al registrar tipo de equipo: " + e.getMessage(),
                                         "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    public boolean actualizarTipoEquipo(int idTipo, String nombre, String descripcion) {
        try {
            TipoEquipo tipoEquipo = new TipoEquipo();
            tipoEquipo.setIdTipo(idTipo);
            tipoEquipo.setNombre(nombre);
            tipoEquipo.setDescripcion(descripcion);
            
            return inventarioService.actualizarTipoEquipo(tipoEquipo);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al actualizar tipo de equipo: " + e.getMessage(),
                                         "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    public boolean eliminarTipoEquipo(int idTipo) {
        try {
            return inventarioService.eliminarTipoEquipo(idTipo);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al eliminar tipo de equipo: " + e.getMessage(),
                                         "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    public TipoEquipo buscarTipoEquipoPorId(int idTipo) {
        return inventarioService.buscarTipoEquipoPorId(idTipo);
    }
    
    public List<TipoEquipo> listarTodosLosTiposEquipo() {
        return inventarioService.listarTodosLosTiposEquipo();
    }
    
    // Métodos para Destinos Turísticos
    
    public boolean registrarDestino(String nombre, String ubicacion, String descripcion) {
        try {
            DestinoTuristico destino = new DestinoTuristico();
            destino.setNombre(nombre);
            destino.setUbicacion(ubicacion);
            destino.setDescripcion(descripcion);
            
            return inventarioService.registrarDestino(destino);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al registrar destino turístico: " + e.getMessage(),
                                         "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    public boolean actualizarDestino(int idDestino, String nombre, String ubicacion, String descripcion) {
        try {
            DestinoTuristico destino = new DestinoTuristico();
            destino.setIdDestino(idDestino);
            destino.setNombre(nombre);
            destino.setUbicacion(ubicacion);
            destino.setDescripcion(descripcion);
            
            return inventarioService.actualizarDestino(destino);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al actualizar destino turístico: " + e.getMessage(),
                                         "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    public boolean eliminarDestino(int idDestino) {
        try {
            return inventarioService.eliminarDestino(idDestino);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al eliminar destino turístico: " + e.getMessage(),
                                         "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    public DestinoTuristico buscarDestinoPorId(int idDestino) {
        return inventarioService.buscarDestinoPorId(idDestino);
    }
    
    public List<DestinoTuristico> listarTodosLosDestinos() {
        return inventarioService.listarTodosLosDestinos();
    }
    
    public List<DestinoTuristico> buscarDestinosPorNombreOUbicacion(String criterio) {
        return inventarioService.buscarDestinosPorNombreOUbicacion(criterio);
    }
}