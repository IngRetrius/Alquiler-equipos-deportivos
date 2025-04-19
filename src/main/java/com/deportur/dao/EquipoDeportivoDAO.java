package com.deportur.dao;

import com.deportur.config.ConexionDB;
import com.deportur.modelo.EquipoDeportivo;
import com.deportur.modelo.TipoEquipo;
import com.deportur.modelo.DestinoTuristico;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EquipoDeportivoDAO {
    
    private TipoEquipoDAO tipoEquipoDAO = new TipoEquipoDAO();
    private DestinoTuristicoDAO destinoDAO = new DestinoTuristicoDAO();
    
    // Método para insertar un nuevo equipo deportivo
    public boolean insertar(EquipoDeportivo equipo) {
        String sql = "INSERT INTO equipo_deportivo (nombre, id_tipo, marca, estado, " +
                     "precio_alquiler, fecha_adquisicion, id_destino, disponible) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, equipo.getNombre());
            stmt.setInt(2, equipo.getTipo().getIdTipo());
            stmt.setString(3, equipo.getMarca());
            stmt.setString(4, equipo.getEstado());
            stmt.setDouble(5, equipo.getPrecioAlquiler());
            stmt.setDate(6, new java.sql.Date(equipo.getFechaAdquisicion().getTime()));
            stmt.setInt(7, equipo.getDestino().getIdDestino());
            stmt.setBoolean(8, equipo.isDisponible());
            
            int filasAfectadas = stmt.executeUpdate();
            
            if (filasAfectadas > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        equipo.setIdEquipo(rs.getInt(1));
                    }
                }
                return true;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    // Método para actualizar un equipo deportivo existente
    public boolean actualizar(EquipoDeportivo equipo) {
        String sql = "UPDATE equipo_deportivo SET nombre = ?, id_tipo = ?, marca = ?, " +
                     "estado = ?, precio_alquiler = ?, fecha_adquisicion = ?, " +
                     "id_destino = ?, disponible = ? WHERE id_equipo = ?";
        
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, equipo.getNombre());
            stmt.setInt(2, equipo.getTipo().getIdTipo());
            stmt.setString(3, equipo.getMarca());
            stmt.setString(4, equipo.getEstado());
            stmt.setDouble(5, equipo.getPrecioAlquiler());
            stmt.setDate(6, new java.sql.Date(equipo.getFechaAdquisicion().getTime()));
            stmt.setInt(7, equipo.getDestino().getIdDestino());
            stmt.setBoolean(8, equipo.isDisponible());
            stmt.setInt(9, equipo.getIdEquipo());
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    // Método para eliminar un equipo deportivo
    public boolean eliminar(int idEquipo) {
        String sql = "DELETE FROM equipo_deportivo WHERE id_equipo = ?";
        
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idEquipo);
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    // Método para buscar un equipo deportivo por ID
    public EquipoDeportivo buscarPorId(int idEquipo) {
        String sql = "SELECT * FROM equipo_deportivo WHERE id_equipo = ?";
        EquipoDeportivo equipo = null;
        
        try {
            Connection conn = ConexionDB.getConexion();
            PreparedStatement stmt = conn.prepareStatement(sql);
            
            stmt.setInt(1, idEquipo);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                equipo = new EquipoDeportivo();
                equipo.setIdEquipo(rs.getInt("id_equipo"));
                equipo.setNombre(rs.getString("nombre"));
                
                // Guardar IDs para consultas posteriores
                int idTipo = rs.getInt("id_tipo");
                int idDestino = rs.getInt("id_destino");
                
                equipo.setMarca(rs.getString("marca"));
                equipo.setEstado(rs.getString("estado"));
                equipo.setPrecioAlquiler(rs.getDouble("precio_alquiler"));
                equipo.setFechaAdquisicion(rs.getDate("fecha_adquisicion"));
                equipo.setDisponible(rs.getBoolean("disponible"));
                
                // Obtener objetos relacionados
                TipoEquipo tipo = tipoEquipoDAO.buscarPorId(idTipo);
                equipo.setTipo(tipo);
                
                DestinoTuristico destino = destinoDAO.buscarPorId(idDestino);
                equipo.setDestino(destino);
            }
            
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return equipo;
    }
    
    // Método para listar todos los equipos deportivos
    public List<EquipoDeportivo> listarTodos() {
        List<EquipoDeportivo> equipos = new ArrayList<>();
        String sql = "SELECT * FROM equipo_deportivo ORDER BY nombre";
        
        try {
            Connection conn = ConexionDB.getConexion();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                EquipoDeportivo equipo = new EquipoDeportivo();
                equipo.setIdEquipo(rs.getInt("id_equipo"));
                equipo.setNombre(rs.getString("nombre"));
                
                // Guardar IDs para consultas posteriores
                int idTipo = rs.getInt("id_tipo");
                int idDestino = rs.getInt("id_destino");
                
                equipo.setMarca(rs.getString("marca"));
                equipo.setEstado(rs.getString("estado"));
                equipo.setPrecioAlquiler(rs.getDouble("precio_alquiler"));
                equipo.setFechaAdquisicion(rs.getDate("fecha_adquisicion"));
                equipo.setDisponible(rs.getBoolean("disponible"));
                
                // Obtener objetos relacionados
                TipoEquipo tipo = tipoEquipoDAO.buscarPorId(idTipo);
                equipo.setTipo(tipo);
                
                DestinoTuristico destino = destinoDAO.buscarPorId(idDestino);
                equipo.setDestino(destino);
                
                equipos.add(equipo);
            }
            
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return equipos;
    }
    
    // Método para buscar equipos por tipo
    public List<EquipoDeportivo> buscarPorTipo(int idTipo) {
        List<EquipoDeportivo> equipos = new ArrayList<>();
        String sql = "SELECT * FROM equipo_deportivo WHERE id_tipo = ? ORDER BY nombre";
        
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idTipo);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    equipos.add(crearEquipoDesdeResultSet(rs));
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return equipos;
    }
    
    // Método para buscar equipos por destino
    public List<EquipoDeportivo> buscarPorDestino(int idDestino) {
        List<EquipoDeportivo> equipos = new ArrayList<>();
        String sql = "SELECT * FROM equipo_deportivo WHERE id_destino = ? ORDER BY nombre";
        
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idDestino);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    equipos.add(crearEquipoDesdeResultSet(rs));
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return equipos;
    }
    
    // Método para buscar equipos disponibles por destino y fechas
    public List<EquipoDeportivo> buscarDisponiblesPorDestinoYFechas(int idDestino, Date fechaInicio, Date fechaFin) {
        List<EquipoDeportivo> equipos = new ArrayList<>();
        
        // Consulta para buscar equipos disponibles que no estén en reservas activas en el rango de fechas
        String sql = "SELECT e.* FROM equipo_deportivo e " +
                     "WHERE e.id_destino = ? " +
                     "AND e.disponible = true " +
                     "AND e.id_equipo NOT IN (" +
                     "  SELECT dr.id_equipo FROM detalle_reserva dr " +
                     "  JOIN reserva r ON dr.id_reserva = r.id_reserva " +
                     "  WHERE r.estado IN ('Pendiente', 'Confirmada', 'En progreso') " +
                     "  AND ((r.fecha_inicio <= ? AND r.fecha_fin >= ?) " +
                     "       OR (r.fecha_inicio <= ? AND r.fecha_fin >= ?) " +
                     "       OR (r.fecha_inicio >= ? AND r.fecha_fin <= ?)))";
        
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idDestino);
            stmt.setDate(2, fechaFin);
            stmt.setDate(3, fechaInicio);
            stmt.setDate(4, fechaInicio);
            stmt.setDate(5, fechaInicio);
            stmt.setDate(6, fechaInicio);
            stmt.setDate(7, fechaFin);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    equipos.add(crearEquipoDesdeResultSet(rs));
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return equipos;
    }
    
    // Método auxiliar para crear un objeto EquipoDeportivo a partir de un ResultSet
    private EquipoDeportivo crearEquipoDesdeResultSet(ResultSet rs) throws SQLException {
        EquipoDeportivo equipo = new EquipoDeportivo();
        equipo.setIdEquipo(rs.getInt("id_equipo"));
        equipo.setNombre(rs.getString("nombre"));
        
        // Obtener el tipo de equipo
        TipoEquipo tipo = tipoEquipoDAO.buscarPorId(rs.getInt("id_tipo"));
        equipo.setTipo(tipo);
        
        equipo.setMarca(rs.getString("marca"));
        equipo.setEstado(rs.getString("estado"));
        equipo.setPrecioAlquiler(rs.getDouble("precio_alquiler"));
        equipo.setFechaAdquisicion(rs.getDate("fecha_adquisicion"));
        
        // Obtener el destino turístico
        DestinoTuristico destino = destinoDAO.buscarPorId(rs.getInt("id_destino"));
        equipo.setDestino(destino);
        
        equipo.setDisponible(rs.getBoolean("disponible"));
        
        return equipo;
    }
}