package com.deportur.dao;

import com.deportur.config.ConexionDB;
import com.deportur.modelo.Reserva;
import com.deportur.modelo.Cliente;
import com.deportur.modelo.DestinoTuristico;
import com.deportur.modelo.DetalleReserva;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservaDAO {
    
    private ClienteDAO clienteDAO = new ClienteDAO();
    private DestinoTuristicoDAO destinoDAO = new DestinoTuristicoDAO();
    private DetalleReservaDAO detalleReservaDAO = new DetalleReservaDAO();
    
    // Método para insertar una nueva reserva
    public boolean insertar(Reserva reserva) {
        String sql = "INSERT INTO reserva (id_cliente, fecha_creacion, fecha_inicio, fecha_fin, id_destino, estado) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, reserva.getCliente().getIdCliente());
            stmt.setTimestamp(2, new java.sql.Timestamp(reserva.getFechaCreacion().getTime()));
            stmt.setDate(3, new java.sql.Date(reserva.getFechaInicio().getTime()));
            stmt.setDate(4, new java.sql.Date(reserva.getFechaFin().getTime()));
            stmt.setInt(5, reserva.getDestino().getIdDestino());
            stmt.setString(6, reserva.getEstado());
            
            int filasAfectadas = stmt.executeUpdate();
            
            if (filasAfectadas > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        reserva.setIdReserva(rs.getInt(1));
                        
                        // Insertar los detalles de la reserva
                        if (reserva.getDetalles() != null && !reserva.getDetalles().isEmpty()) {
                            for (DetalleReserva detalle : reserva.getDetalles()) {
                                detalle.setReserva(reserva);
                                detalleReservaDAO.insertar(detalle);
                            }
                        }
                        
                        return true;
                    }
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    // Método para actualizar una reserva existente
    public boolean actualizar(Reserva reserva) {
        String sql = "UPDATE reserva SET id_cliente = ?, fecha_inicio = ?, fecha_fin = ?, " +
                     "id_destino = ?, estado = ? WHERE id_reserva = ?";
        
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, reserva.getCliente().getIdCliente());
            stmt.setDate(2, new java.sql.Date(reserva.getFechaInicio().getTime()));
            stmt.setDate(3, new java.sql.Date(reserva.getFechaFin().getTime()));
            stmt.setInt(4, reserva.getDestino().getIdDestino());
            stmt.setString(5, reserva.getEstado());
            stmt.setInt(6, reserva.getIdReserva());
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    // Método para cancelar una reserva
    public boolean cancelarReserva(int idReserva) {
        String sql = "UPDATE reserva SET estado = 'Cancelada' WHERE id_reserva = ?";
        
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idReserva);
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    // Método para eliminar una reserva
    public boolean eliminar(int idReserva) {
        // Primero eliminar los detalles de la reserva
        detalleReservaDAO.eliminarPorReserva(idReserva);
        
        // Luego eliminar la reserva
        String sql = "DELETE FROM reserva WHERE id_reserva = ?";
        
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idReserva);
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    // Método para buscar una reserva por ID
    public Reserva buscarPorId(int idReserva) {
        String sql = "SELECT * FROM reserva WHERE id_reserva = ?";
        
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idReserva);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Reserva reserva = crearReservaDesdeResultSet(rs);
                    
                    // Cargar los detalles de la reserva
                    List<DetalleReserva> detalles = detalleReservaDAO.buscarPorReserva(idReserva);
                    reserva.setDetalles(detalles);
                    
                    return reserva;
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    // Método para listar todas las reservas
    public List<Reserva> listarTodas() {
        List<Reserva> reservas = new ArrayList<>();
        String sql = "SELECT * FROM reserva ORDER BY fecha_creacion DESC";
        
        try {
            Connection conn = ConexionDB.getConexion();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                Reserva reserva = new Reserva();
                reserva.setIdReserva(rs.getInt("id_reserva"));
                
                // Guardar IDs para consultas posteriores
                int idCliente = rs.getInt("id_cliente");
                int idDestino = rs.getInt("id_destino");
                int idReserva = rs.getInt("id_reserva");
                
                reserva.setFechaCreacion(rs.getTimestamp("fecha_creacion"));
                reserva.setFechaInicio(rs.getDate("fecha_inicio"));
                reserva.setFechaFin(rs.getDate("fecha_fin"));
                reserva.setEstado(rs.getString("estado"));
                
                // Obtener objetos relacionados
                Cliente cliente = clienteDAO.buscarPorId(idCliente);
                reserva.setCliente(cliente);
                
                DestinoTuristico destino = destinoDAO.buscarPorId(idDestino);
                reserva.setDestino(destino);
                
                // Cargar los detalles de la reserva
                List<DetalleReserva> detalles = detalleReservaDAO.buscarPorReserva(idReserva);
                reserva.setDetalles(detalles);
                
                reservas.add(reserva);
            }
            
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return reservas;
    }
    
    // Método para buscar reservas por cliente
    public List<Reserva> buscarPorCliente(int idCliente) {
        List<Reserva> reservas = new ArrayList<>();
        String sql = "SELECT * FROM reserva WHERE id_cliente = ? ORDER BY fecha_creacion DESC";
        
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idCliente);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Reserva reserva = crearReservaDesdeResultSet(rs);
                    
                    // Cargar los detalles de la reserva
                    List<DetalleReserva> detalles = detalleReservaDAO.buscarPorReserva(reserva.getIdReserva());
                    reserva.setDetalles(detalles);
                    
                    reservas.add(reserva);
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return reservas;
    }
    
    // Método para buscar reservas por destino
    public List<Reserva> buscarPorDestino(int idDestino) {
        List<Reserva> reservas = new ArrayList<>();
        String sql = "SELECT * FROM reserva WHERE id_destino = ? ORDER BY fecha_inicio";
        
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idDestino);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Reserva reserva = crearReservaDesdeResultSet(rs);
                    
                    // Cargar los detalles de la reserva
                    List<DetalleReserva> detalles = detalleReservaDAO.buscarPorReserva(reserva.getIdReserva());
                    reserva.setDetalles(detalles);
                    
                    reservas.add(reserva);
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return reservas;
    }
    
    // Método auxiliar para crear un objeto Reserva a partir de un ResultSet
    private Reserva crearReservaDesdeResultSet(ResultSet rs) throws SQLException {
        Reserva reserva = new Reserva();
        reserva.setIdReserva(rs.getInt("id_reserva"));
        
        // Cargar el cliente
        Cliente cliente = clienteDAO.buscarPorId(rs.getInt("id_cliente"));
        reserva.setCliente(cliente);
        
        reserva.setFechaCreacion(rs.getTimestamp("fecha_creacion"));
        reserva.setFechaInicio(rs.getDate("fecha_inicio"));
        reserva.setFechaFin(rs.getDate("fecha_fin"));
        
        // Cargar el destino
        DestinoTuristico destino = destinoDAO.buscarPorId(rs.getInt("id_destino"));
        reserva.setDestino(destino);
        
        reserva.setEstado(rs.getString("estado"));
        
        return reserva;
    }
}