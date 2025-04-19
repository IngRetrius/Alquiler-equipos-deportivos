package com.deportur.servicio;

import com.deportur.dao.EquipoDeportivoDAO;
import com.deportur.dao.TipoEquipoDAO;
import com.deportur.dao.DestinoTuristicoDAO;
import com.deportur.modelo.EquipoDeportivo;
import com.deportur.modelo.TipoEquipo;
import com.deportur.modelo.DestinoTuristico;

import java.util.Date;
import java.util.List;

public class GestionInventarioService {
    
    private EquipoDeportivoDAO equipoDAO;
    private TipoEquipoDAO tipoEquipoDAO;
    private DestinoTuristicoDAO destinoDAO;
    
    public GestionInventarioService() {
        this.equipoDAO = new EquipoDeportivoDAO();
        this.tipoEquipoDAO = new TipoEquipoDAO();
        this.destinoDAO = new DestinoTuristicoDAO();
    }
    
    // Métodos para Equipos Deportivos
    
    public boolean registrarEquipo(EquipoDeportivo equipo) throws Exception {
        // Validar datos del equipo
        if (equipo.getNombre() == null || equipo.getNombre().trim().isEmpty()) {
            throw new Exception("El nombre del equipo es requerido");
        }
        
        if (equipo.getTipo() == null) {
            throw new Exception("El tipo de equipo es requerido");
        }
        
        if (equipo.getMarca() == null || equipo.getMarca().trim().isEmpty()) {
            throw new Exception("La marca del equipo es requerida");
        }
        
        if (equipo.getEstado() == null || equipo.getEstado().trim().isEmpty()) {
            throw new Exception("El estado del equipo es requerido");
        }
        
        if (equipo.getPrecioAlquiler() <= 0) {
            throw new Exception("El precio de alquiler debe ser mayor a cero");
        }
        
        if (equipo.getFechaAdquisicion() == null) {
            throw new Exception("La fecha de adquisición es requerida");
        }
        
        if (equipo.getFechaAdquisicion().after(new Date())) {
            throw new Exception("La fecha de adquisición no puede ser futura");
        }
        
        if (equipo.getDestino() == null) {
            throw new Exception("El destino turístico es requerido");
        }
        
        // Si pasa todas las validaciones, registrar el equipo
        return equipoDAO.insertar(equipo);
    }
    
    public boolean actualizarEquipo(EquipoDeportivo equipo) throws Exception {
        // Validar que el equipo exista
        EquipoDeportivo equipoExistente = equipoDAO.buscarPorId(equipo.getIdEquipo());
        if (equipoExistente == null) {
            throw new Exception("El equipo que intenta actualizar no existe");
        }
        
        // Validar datos del equipo (similar a registrarEquipo)
        if (equipo.getNombre() == null || equipo.getNombre().trim().isEmpty()) {
            throw new Exception("El nombre del equipo es requerido");
        }
        
        if (equipo.getTipo() == null) {
            throw new Exception("El tipo de equipo es requerido");
        }
        
        if (equipo.getMarca() == null || equipo.getMarca().trim().isEmpty()) {
            throw new Exception("La marca del equipo es requerida");
        }
        
        if (equipo.getEstado() == null || equipo.getEstado().trim().isEmpty()) {
            throw new Exception("El estado del equipo es requerido");
        }
        
        if (equipo.getPrecioAlquiler() <= 0) {
            throw new Exception("El precio de alquiler debe ser mayor a cero");
        }
        
        if (equipo.getFechaAdquisicion() == null) {
            throw new Exception("La fecha de adquisición es requerida");
        }
        
        if (equipo.getFechaAdquisicion().after(new Date())) {
            throw new Exception("La fecha de adquisición no puede ser futura");
        }
        
        if (equipo.getDestino() == null) {
            throw new Exception("El destino turístico es requerido");
        }
        
        // Si pasa todas las validaciones, actualizar el equipo
        return equipoDAO.actualizar(equipo);
    }
    
    public boolean eliminarEquipo(int idEquipo) throws Exception {
        // Verificar si el equipo existe
        EquipoDeportivo equipo = equipoDAO.buscarPorId(idEquipo);
        if (equipo == null) {
            throw new Exception("El equipo que intenta eliminar no existe");
        }
        
        // Aquí podríamos verificar si el equipo está en alguna reserva activa
        // y prevenir su eliminación en ese caso
        
        return equipoDAO.eliminar(idEquipo);
    }
    
    public EquipoDeportivo buscarEquipoPorId(int idEquipo) {
        return equipoDAO.buscarPorId(idEquipo);
    }
    
    public List<EquipoDeportivo> listarTodosLosEquipos() {
        return equipoDAO.listarTodos();
    }
    
    public List<EquipoDeportivo> buscarEquiposPorTipo(int idTipo) {
        return equipoDAO.buscarPorTipo(idTipo);
    }
    
    public List<EquipoDeportivo> buscarEquiposPorDestino(int idDestino) {
        return equipoDAO.buscarPorDestino(idDestino);
    }
    
    public List<EquipoDeportivo> buscarEquiposDisponiblesPorDestinoYFechas(
            int idDestino, java.sql.Date fechaInicio, java.sql.Date fechaFin) throws Exception {
        
        // Validar fechas
        if (fechaInicio == null || fechaFin == null) {
            throw new Exception("Las fechas de inicio y fin son requeridas");
        }
        
        if (fechaInicio.after(fechaFin)) {
            throw new Exception("La fecha de inicio no puede ser posterior a la fecha de fin");
        }
        
        Date hoy = new Date();
        java.sql.Date fechaActual = new java.sql.Date(hoy.getTime());
        if (fechaInicio.before(fechaActual)) {
            throw new Exception("La fecha de inicio no puede ser anterior a la fecha actual");
        }
        
        // Verificar que el destino exista
        DestinoTuristico destino = destinoDAO.buscarPorId(idDestino);
        if (destino == null) {
            throw new Exception("El destino turístico especificado no existe");
        }
        
        return equipoDAO.buscarDisponiblesPorDestinoYFechas(idDestino, fechaInicio, fechaFin);
    }
    
    // Métodos para Tipos de Equipo
    
    public boolean registrarTipoEquipo(TipoEquipo tipoEquipo) throws Exception {
        // Validar datos
        if (tipoEquipo.getNombre() == null || tipoEquipo.getNombre().trim().isEmpty()) {
            throw new Exception("El nombre del tipo de equipo es requerido");
        }
        
        return tipoEquipoDAO.insertar(tipoEquipo);
    }
    
    public boolean actualizarTipoEquipo(TipoEquipo tipoEquipo) throws Exception {
        // Validar que exista
        TipoEquipo tipo = tipoEquipoDAO.buscarPorId(tipoEquipo.getIdTipo());
        if (tipo == null) {
            throw new Exception("El tipo de equipo que intenta actualizar no existe");
        }
        
        // Validar datos
        if (tipoEquipo.getNombre() == null || tipoEquipo.getNombre().trim().isEmpty()) {
            throw new Exception("El nombre del tipo de equipo es requerido");
        }
        
        return tipoEquipoDAO.actualizar(tipoEquipo);
    }
    
    public boolean eliminarTipoEquipo(int idTipo) throws Exception {
        // Verificar si existen equipos con este tipo
        List<EquipoDeportivo> equipos = equipoDAO.buscarPorTipo(idTipo);
        if (equipos != null && !equipos.isEmpty()) {
            throw new Exception("No se puede eliminar el tipo de equipo porque existen equipos asociados");
        }
        
        return tipoEquipoDAO.eliminar(idTipo);
    }
    
    public TipoEquipo buscarTipoEquipoPorId(int idTipo) {
        return tipoEquipoDAO.buscarPorId(idTipo);
    }
    
    public List<TipoEquipo> listarTodosLosTiposEquipo() {
        return tipoEquipoDAO.listarTodos();
    }
    
    // Métodos para Destinos Turísticos
    
    public boolean registrarDestino(DestinoTuristico destino) throws Exception {
        // Validar datos
        if (destino.getNombre() == null || destino.getNombre().trim().isEmpty()) {
            throw new Exception("El nombre del destino turístico es requerido");
        }
        
        if (destino.getUbicacion() == null || destino.getUbicacion().trim().isEmpty()) {
            throw new Exception("La ubicación del destino turístico es requerida");
        }
        
        return destinoDAO.insertar(destino);
    }
    
    public boolean actualizarDestino(DestinoTuristico destino) throws Exception {
        // Validar que exista
        DestinoTuristico destinoExistente = destinoDAO.buscarPorId(destino.getIdDestino());
        if (destinoExistente == null) {
            throw new Exception("El destino turístico que intenta actualizar no existe");
        }
        
        // Validar datos
        if (destino.getNombre() == null || destino.getNombre().trim().isEmpty()) {
            throw new Exception("El nombre del destino turístico es requerido");
        }
        
        if (destino.getUbicacion() == null || destino.getUbicacion().trim().isEmpty()) {
            throw new Exception("La ubicación del destino turístico es requerida");
        }
        
        return destinoDAO.actualizar(destino);
    }
    
    public boolean eliminarDestino(int idDestino) throws Exception {
        // Verificar si existen equipos en este destino
        List<EquipoDeportivo> equipos = equipoDAO.buscarPorDestino(idDestino);
        if (equipos != null && !equipos.isEmpty()) {
            throw new Exception("No se puede eliminar el destino turístico porque existen equipos asociados");
        }
        
        return destinoDAO.eliminar(idDestino);
    }
    
    public DestinoTuristico buscarDestinoPorId(int idDestino) {
        return destinoDAO.buscarPorId(idDestino);
    }
    
    public List<DestinoTuristico> listarTodosLosDestinos() {
        return destinoDAO.listarTodos();
    }
    
    public List<DestinoTuristico> buscarDestinosPorNombreOUbicacion(String criterio) {
        return destinoDAO.buscarPorNombreOUbicacion(criterio);
    }
}