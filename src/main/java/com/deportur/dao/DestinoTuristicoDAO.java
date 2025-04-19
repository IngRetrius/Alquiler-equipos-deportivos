package com.deportur.dao;

import com.deportur.config.ConexionDB;
import com.deportur.modelo.DestinoTuristico;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DestinoTuristicoDAO {
    
    // Método para insertar un nuevo destino turístico
    public boolean insertar(DestinoTuristico destino) {
        String sql = "INSERT INTO destino_turistico (nombre, ubicacion, descripcion) VALUES (?, ?, ?)";
        
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, destino.getNombre());
            stmt.setString(2, destino.getUbicacion());
            stmt.setString(3, destino.getDescripcion());
            
            int filasAfectadas = stmt.executeUpdate();
            
            if (filasAfectadas > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        destino.setIdDestino(rs.getInt(1));
                    }
                }
                return true;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    // Método para actualizar un destino turístico existente
    public boolean actualizar(DestinoTuristico destino) {
        String sql = "UPDATE destino_turistico SET nombre = ?, ubicacion = ?, descripcion = ? WHERE id_destino = ?";
        
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, destino.getNombre());
            stmt.setString(2, destino.getUbicacion());
            stmt.setString(3, destino.getDescripcion());
            stmt.setInt(4, destino.getIdDestino());
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    // Método para eliminar un destino turístico
    public boolean eliminar(int idDestino) {
        String sql = "DELETE FROM destino_turistico WHERE id_destino = ?";
        
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idDestino);
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    // Método para buscar un destino turístico por ID
    public DestinoTuristico buscarPorId(int idDestino) {
        String sql = "SELECT * FROM destino_turistico WHERE id_destino = ?";
        
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idDestino);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    DestinoTuristico destino = new DestinoTuristico();
                    destino.setIdDestino(rs.getInt("id_destino"));
                    destino.setNombre(rs.getString("nombre"));
                    destino.setUbicacion(rs.getString("ubicacion"));
                    destino.setDescripcion(rs.getString("descripcion"));
                    return destino;
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    // Método para listar todos los destinos turísticos
    public List<DestinoTuristico> listarTodos() {
        List<DestinoTuristico> destinos = new ArrayList<>();
        String sql = "SELECT * FROM destino_turistico ORDER BY nombre";
        
        try (Connection conn = ConexionDB.getConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                DestinoTuristico destino = new DestinoTuristico();
                destino.setIdDestino(rs.getInt("id_destino"));
                destino.setNombre(rs.getString("nombre"));
                destino.setUbicacion(rs.getString("ubicacion"));
                destino.setDescripcion(rs.getString("descripcion"));
                destinos.add(destino);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return destinos;
    }
    
    // Método para buscar destinos por nombre o ubicación
    public List<DestinoTuristico> buscarPorNombreOUbicacion(String criterio) {
        List<DestinoTuristico> destinos = new ArrayList<>();
        String sql = "SELECT * FROM destino_turistico WHERE nombre LIKE ? OR ubicacion LIKE ? ORDER BY nombre";
        
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String criterioLike = "%" + criterio + "%";
            stmt.setString(1, criterioLike);
            stmt.setString(2, criterioLike);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    DestinoTuristico destino = new DestinoTuristico();
                    destino.setIdDestino(rs.getInt("id_destino"));
                    destino.setNombre(rs.getString("nombre"));
                    destino.setUbicacion(rs.getString("ubicacion"));
                    destino.setDescripcion(rs.getString("descripcion"));
                    destinos.add(destino);
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return destinos;
    }
}