package com.deportur.servicio;

import java.util.Date;
import java.util.List;

import com.deportur.dao.ClienteDAO;
import com.deportur.dao.DestinoTuristicoDAO;
import com.deportur.dao.DetalleReservaDAO;
import com.deportur.dao.EquipoDeportivoDAO;
import com.deportur.dao.ReservaDAO;
import com.deportur.modelo.Cliente;
import com.deportur.modelo.DestinoTuristico;
import com.deportur.modelo.DetalleReserva;
import com.deportur.modelo.EquipoDeportivo;
import com.deportur.modelo.Reserva;

public class GestionReservasService {
    
    private ReservaDAO reservaDAO;
    private ClienteDAO clienteDAO;
    private DetalleReservaDAO detalleReservaDAO;
    private EquipoDeportivoDAO equipoDAO;
    private DestinoTuristicoDAO destinoDAO;
    
    public GestionReservasService() {
        this.reservaDAO = new ReservaDAO();
        this.clienteDAO = new ClienteDAO();
        this.detalleReservaDAO = new DetalleReservaDAO();
        this.equipoDAO = new EquipoDeportivoDAO();
        this.destinoDAO = new DestinoTuristicoDAO();
    }
    
    // Métodos para gestionar reservas
    
    public boolean crearReserva(Reserva reserva) throws Exception {
        // Validar cliente
        if (reserva.getCliente() == null) {
            throw new Exception("El cliente es requerido para la reserva");
        }
        
        Cliente cliente = clienteDAO.buscarPorId(reserva.getCliente().getIdCliente());
        if (cliente == null) {
            throw new Exception("El cliente seleccionado no existe");
        }
        
        // Validar destino
        if (reserva.getDestino() == null) {
            throw new Exception("El destino turístico es requerido para la reserva");
        }
        
        DestinoTuristico destino = destinoDAO.buscarPorId(reserva.getDestino().getIdDestino());
        if (destino == null) {
            throw new Exception("El destino turístico seleccionado no existe");
        }
        
        // Validar fechas
        if (reserva.getFechaInicio() == null || reserva.getFechaFin() == null) {
            throw new Exception("Las fechas de inicio y fin son requeridas");
        }
        
        if (reserva.getFechaInicio().after(reserva.getFechaFin())) {
            throw new Exception("La fecha de inicio no puede ser posterior a la fecha de fin");
        }
        
        Date hoy = new Date();
        if (reserva.getFechaInicio().before(hoy)) {
            throw new Exception("La fecha de inicio no puede ser anterior a la fecha actual");
        }
        
        // Validar que haya al menos un equipo en la reserva
        if (reserva.getDetalles() == null || reserva.getDetalles().isEmpty()) {
            throw new Exception("La reserva debe incluir al menos un equipo");
        }
        
        // Establecer la fecha de creación y el estado inicial
        reserva.setFechaCreacion(new Date());
        reserva.setEstado("Pendiente");
        
        // Verificar disponibilidad de equipos
        for (DetalleReserva detalle : reserva.getDetalles()) {
            EquipoDeportivo equipo = equipoDAO.buscarPorId(detalle.getEquipo().getIdEquipo());
            
            if (equipo == null) {
                throw new Exception("El equipo seleccionado no existe");
            }
            
            if (!equipo.isDisponible()) {
                throw new Exception("El equipo " + equipo.getNombre() + " no está disponible");
            }
            
            // Verificar si el equipo ya está reservado en esas fechas
            java.sql.Date fechaInicio = new java.sql.Date(reserva.getFechaInicio().getTime());
            java.sql.Date fechaFin = new java.sql.Date(reserva.getFechaFin().getTime());
            
            if (detalleReservaDAO.equipoReservadoEnFechas(equipo.getIdEquipo(), fechaInicio, fechaFin)) {
                throw new Exception("El equipo " + equipo.getNombre() + " ya está reservado en las fechas seleccionadas");
            }
            
            // Establecer el precio unitario desde el equipo
            detalle.setPrecioUnitario(equipo.getPrecioAlquiler());
        }
        
        // Si pasa todas las validaciones, crear la reserva
        return reservaDAO.insertar(reserva);
    }
    
    public boolean modificarReserva(Reserva reserva) throws Exception {
        // Verificar que la reserva exista
        Reserva reservaExistente = reservaDAO.buscarPorId(reserva.getIdReserva());
        if (reservaExistente == null) {
            throw new Exception("La reserva que intenta modificar no existe");
        }
        
        // Verificar que la reserva no esté en estado "Finalizada" o "Cancelada"
        if ("Finalizada".equals(reservaExistente.getEstado()) || "Cancelada".equals(reservaExistente.getEstado())) {
            throw new Exception("No se puede modificar una reserva finalizada o cancelada");
        }
        
        // Validaciones similares a crearReserva
        // Validar cliente
        if (reserva.getCliente() == null) {
            throw new Exception("El cliente es requerido para la reserva");
        }
        
        Cliente cliente = clienteDAO.buscarPorId(reserva.getCliente().getIdCliente());
        if (cliente == null) {
            throw new Exception("El cliente seleccionado no existe");
        }
        
        // Validar destino
        if (reserva.getDestino() == null) {
            throw new Exception("El destino turístico es requerido para la reserva");
        }
        
        DestinoTuristico destino = destinoDAO.buscarPorId(reserva.getDestino().getIdDestino());
        if (destino == null) {
            throw new Exception("El destino turístico seleccionado no existe");
        }
        
        // Validar fechas
        if (reserva.getFechaInicio() == null || reserva.getFechaFin() == null) {
            throw new Exception("Las fechas de inicio y fin son requeridas");
        }
        
        if (reserva.getFechaInicio().after(reserva.getFechaFin())) {
            throw new Exception("La fecha de inicio no puede ser posterior a la fecha de fin");
        }
        
        Date hoy = new Date();
        if (reserva.getFechaInicio().before(hoy)) {
            throw new Exception("La fecha de inicio no puede ser anterior a la fecha actual");
        }
        
        // Al modificar, mantener la fecha de creación original
        reserva.setFechaCreacion(reservaExistente.getFechaCreacion());
        
        // Si cambian los detalles, eliminar los anteriores y agregar los nuevos
        if (reserva.getDetalles() != null && !reserva.getDetalles().isEmpty()) {
            // Eliminar detalles anteriores
            detalleReservaDAO.eliminarPorReserva(reserva.getIdReserva());
            
            // Verificar nuevos equipos
            for (DetalleReserva detalle : reserva.getDetalles()) {
                EquipoDeportivo equipo = equipoDAO.buscarPorId(detalle.getEquipo().getIdEquipo());
                
                if (equipo == null) {
                    throw new Exception("El equipo seleccionado no existe");
                }
                
                if (!equipo.isDisponible()) {
                    throw new Exception("El equipo " + equipo.getNombre() + " no está disponible");
                }
                
                // Verificar si el equipo ya está reservado en esas fechas (excluyendo esta reserva)
                //java.sql.Date fechaInicio = new java.sql.Date(reserva.getFechaInicio().getTime());
                //java.sql.Date fechaFin = new java.sql.Date(reserva.getFechaFin().getTime());
                
                // Lógica para verificar disponibilidad excluyendo la reserva actual
                // (aquí necesitaríamos una consulta más específica en el DAO)
                
                // Establecer el precio unitario desde el equipo
                detalle.setPrecioUnitario(equipo.getPrecioAlquiler());
                detalle.setReserva(reserva);
                
                // Insertar nuevo detalle
                detalleReservaDAO.insertar(detalle);
            }
        }
        
        // Actualizar la reserva
        return reservaDAO.actualizar(reserva);
    }
    
    public boolean cancelarReserva(int idReserva) throws Exception {
        // Verificar que la reserva exista
        Reserva reserva = reservaDAO.buscarPorId(idReserva);
        if (reserva == null) {
            throw new Exception("La reserva que intenta cancelar no existe");
        }
        
        // Verificar que la reserva no esté en estado "Finalizada" o ya "Cancelada"
        if ("Finalizada".equals(reserva.getEstado())) {
            throw new Exception("No se puede cancelar una reserva finalizada");
        }
        
        if ("Cancelada".equals(reserva.getEstado())) {
            throw new Exception("La reserva ya está cancelada");
        }
        
        return reservaDAO.cancelarReserva(idReserva);
    }
    
    public Reserva consultarReserva(int idReserva) {
        return reservaDAO.buscarPorId(idReserva);
    }
    
    public List<Reserva> listarTodasLasReservas() {
        return reservaDAO.listarTodas();
    }
    
    public List<Reserva> buscarReservasPorCliente(int idCliente) throws Exception {
        // Verificar que el cliente exista
        Cliente cliente = clienteDAO.buscarPorId(idCliente);
        if (cliente == null) {
            throw new Exception("El cliente especificado no existe");
        }
        
        return reservaDAO.buscarPorCliente(idCliente);
    }
    
    public List<Reserva> buscarReservasPorDestino(int idDestino) throws Exception {
        // Verificar que el destino exista
        DestinoTuristico destino = destinoDAO.buscarPorId(idDestino);
        if (destino == null) {
            throw new Exception("El destino turístico especificado no existe");
        }
        
        return reservaDAO.buscarPorDestino(idDestino);
    }
    
    // Métodos para gestionar clientes
    
    public boolean registrarCliente(Cliente cliente) throws Exception {
        // Validar datos del cliente
        if (cliente.getNombre() == null || cliente.getNombre().trim().isEmpty()) {
            throw new Exception("El nombre del cliente es requerido");
        }
        
        if (cliente.getApellido() == null || cliente.getApellido().trim().isEmpty()) {
            throw new Exception("El apellido del cliente es requerido");
        }
        
        if (cliente.getDocumento() == null || cliente.getDocumento().trim().isEmpty()) {
            throw new Exception("El documento de identidad del cliente es requerido");
        }
        
        if (cliente.getTipoDocumento() == null || cliente.getTipoDocumento().trim().isEmpty()) {
            throw new Exception("El tipo de documento es requerido");
        }
        
        // Verificar si ya existe un cliente con el mismo documento
        Cliente clienteExistente = clienteDAO.buscarPorDocumento(cliente.getDocumento());
        if (clienteExistente != null) {
            throw new Exception("Ya existe un cliente registrado con el mismo documento de identidad");
        }
        
        return clienteDAO.insertar(cliente);
    }
    
    public boolean actualizarCliente(Cliente cliente) throws Exception {
        // Verificar que el cliente exista
        Cliente clienteExistente = clienteDAO.buscarPorId(cliente.getIdCliente());
        if (clienteExistente == null) {
            throw new Exception("El cliente que intenta actualizar no existe");
        }
        
        // Validar datos del cliente
        if (cliente.getNombre() == null || cliente.getNombre().trim().isEmpty()) {
            throw new Exception("El nombre del cliente es requerido");
        }
        
        if (cliente.getApellido() == null || cliente.getApellido().trim().isEmpty()) {
            throw new Exception("El apellido del cliente es requerido");
        }
        
        if (cliente.getDocumento() == null || cliente.getDocumento().trim().isEmpty()) {
            throw new Exception("El documento de identidad del cliente es requerido");
        }
        
        if (cliente.getTipoDocumento() == null || cliente.getTipoDocumento().trim().isEmpty()) {
            throw new Exception("El tipo de documento es requerido");
        }
        
        // Verificar si ya existe otro cliente con el mismo documento (diferente al actual)
        Cliente otroCliente = clienteDAO.buscarPorDocumento(cliente.getDocumento());
        if (otroCliente != null && otroCliente.getIdCliente() != cliente.getIdCliente()) {
            throw new Exception("Ya existe otro cliente registrado con el mismo documento de identidad");
        }
        
        return clienteDAO.actualizar(cliente);
    }
    
    public boolean eliminarCliente(int idCliente) throws Exception {
        // Verificar que el cliente exista
        Cliente cliente = clienteDAO.buscarPorId(idCliente);
        if (cliente == null) {
            throw new Exception("El cliente que intenta eliminar no existe");
        }
        
        // Verificar si el cliente tiene reservas
        List<Reserva> reservas = reservaDAO.buscarPorCliente(idCliente);
        if (reservas != null && !reservas.isEmpty()) {
            throw new Exception("No se puede eliminar el cliente porque tiene reservas asociadas");
        }
        
        return clienteDAO.eliminar(idCliente);
    }
    
    public Cliente buscarClientePorId(int idCliente) {
        return clienteDAO.buscarPorId(idCliente);
    }
    
    public Cliente buscarClientePorDocumento(String documento) {
        return clienteDAO.buscarPorDocumento(documento);
    }
    
    public List<Cliente> listarTodosLosClientes() {
        return clienteDAO.listarTodos();
    }
    
    public List<Cliente> buscarClientesPorNombreOApellido(String criterio) {
        return clienteDAO.buscarPorNombreOApellido(criterio);
    }
    
    // Método para verificar disponibilidad de equipos
    public boolean verificarDisponibilidadEquipo(int idEquipo, Date fechaInicio, Date fechaFin) throws Exception {
        // Validar fechas
        if (fechaInicio == null || fechaFin == null) {
            throw new Exception("Las fechas de inicio y fin son requeridas");
        }
        
        if (fechaInicio.after(fechaFin)) {
            throw new Exception("La fecha de inicio no puede ser posterior a la fecha de fin");
        }
        
        Date hoy = new Date();
        if (fechaInicio.before(hoy)) {
            throw new Exception("La fecha de inicio no puede ser anterior a la fecha actual");
        }
        
        // Verificar que el equipo exista
        EquipoDeportivo equipo = equipoDAO.buscarPorId(idEquipo);
        if (equipo == null) {
            throw new Exception("El equipo especificado no existe");
        }
        
        // Verificar si el equipo está disponible (estado general)
        if (!equipo.isDisponible()) {
            return false;
        }
        
        // Verificar si el equipo ya está reservado en esas fechas
        java.sql.Date sqlFechaInicio = new java.sql.Date(fechaInicio.getTime());
        java.sql.Date sqlFechaFin = new java.sql.Date(fechaFin.getTime());
        
        return !detalleReservaDAO.equipoReservadoEnFechas(idEquipo, sqlFechaInicio, sqlFechaFin);
    }
}