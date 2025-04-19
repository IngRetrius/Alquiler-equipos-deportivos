package com.deportur.dao;

import com.deportur.config.ConexionDB;
import com.deportur.modelo.DetalleReserva;
import com.deportur.modelo.Reserva;
import com.deportur.modelo.EquipoDeportivo;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DetalleReservaDAO {
    
    private EquipoDeportivoDAO equipoDAO = new EquipoDeportivoDAO();
    
    // Método para insertar un nuevo detalle de reserva
    public boolean insertar(DetalleReserva detalle) {
        String sql = "INSERT INTO detalle_reserva (id_reserva, id_equipo, precio_unitario) VALUES (?, ?, ?)";
        
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, detalle.getReserva().getIdReserva());
            stmt.setInt(2, detalle.getEquipo().getIdEquipo());
            stmt.setDouble(3, detalle.getPrecioUnitario());
            
            int filasAfectadas = stmt.executeUpdate();
            
            if (filasAfectadas > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        detalle.setIdDetalle(rs.getInt(1));
                    }
                }
                return true;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    // Método para eliminar un detalle de reserva
    public boolean eliminar(int idDetalle) {
        String sql = "DELETE FROM detalle_reserva WHERE id_detalle = ?";
        
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idDetalle);
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    // Método para eliminar todos los detalles de una reserva
    public boolean eliminarPorReserva(int idReserva) {
        String sql = "DELETE FROM detalle_reserva WHERE id_reserva = ?";
        
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idReserva);
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    // Método para buscar detalles por reserva
    public List<DetalleReserva> buscarPorReserva(int idReserva) {
        List<DetalleReserva> detalles = new ArrayList<>();
        String sql = "SELECT * FROM detalle_reserva WHERE id_reserva = ?";
        
        try {
            Connection conn = ConexionDB.getConexion();
            PreparedStatement stmt = conn.prepareStatement(sql);
            
            stmt.setInt(1, idReserva);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                DetalleReserva detalle = new DetalleReserva();
                detalle.setIdDetalle(rs.getInt("id_detalle"));
                
                // Crear objeto reserva con solo el ID
                Reserva reserva = new Reserva();
                reserva.setIdReserva(rs.getInt("id_reserva"));
                detalle.setReserva(reserva);
                
                // Guardar ID del equipo para consulta posterior
                int idEquipo = rs.getInt("id_equipo");
                detalle.setPrecioUnitario(rs.getDouble("precio_unitario"));
                
                // Cargar el equipo
                EquipoDeportivo equipo = equipoDAO.buscarPorId(idEquipo);
                detalle.setEquipo(equipo);
                
                detalles.add(detalle);
            }
            
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return detalles;
    }
    
    // Método para verificar si un equipo está reservado en un rango de fechas
    public boolean equipoReservadoEnFechas(int idEquipo, Date fechaInicio, Date fechaFin) {
        String sql = "SELECT COUNT(*) FROM detalle_reserva dr " +
                     "JOIN reserva r ON dr.id_reserva = r.id_reserva " +
                     "WHERE dr.id_equipo = ? " +
                     "AND r.estado IN ('Pendiente', 'Confirmada', 'En progreso') " +
                     "AND ((r.fecha_inicio <= ? AND r.fecha_fin >= ?) " +
                     "     OR (r.fecha_inicio <= ? AND r.fecha_fin >= ?) " +
                     "     OR (r.fecha_inicio >= ? AND r.fecha_fin <= ?))";
        
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idEquipo);
            stmt.setDate(2, fechaFin);
            stmt.setDate(3, fechaInicio);
            stmt.setDate(4, fechaInicio);
            stmt.setDate(5, fechaInicio);
            stmt.setDate(6, fechaInicio);
            stmt.setDate(7, fechaFin);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
}