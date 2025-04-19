package com.deportur.dao;

import com.deportur.config.ConexionDB;
import com.deportur.modelo.TipoEquipo;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TipoEquipoDAO {
    
    // Método para insertar un nuevo tipo de equipo
    public boolean insertar(TipoEquipo tipoEquipo) {
        String sql = "INSERT INTO tipo_equipo (nombre, descripcion) VALUES (?, ?)";
        
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, tipoEquipo.getNombre());
            stmt.setString(2, tipoEquipo.getDescripcion());
            
            int filasAfectadas = stmt.executeUpdate();
            
            if (filasAfectadas > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        tipoEquipo.setIdTipo(rs.getInt(1));
                    }
                }
                return true;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    // Método para actualizar un tipo de equipo existente
    public boolean actualizar(TipoEquipo tipoEquipo) {
        String sql = "UPDATE tipo_equipo SET nombre = ?, descripcion = ? WHERE id_tipo = ?";
        
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, tipoEquipo.getNombre());
            stmt.setString(2, tipoEquipo.getDescripcion());
            stmt.setInt(3, tipoEquipo.getIdTipo());
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    // Método para eliminar un tipo de equipo
    public boolean eliminar(int idTipo) {
        String sql = "DELETE FROM tipo_equipo WHERE id_tipo = ?";
        
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idTipo);
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    // Método para buscar un tipo de equipo por ID
    public TipoEquipo buscarPorId(int idTipo) {
        String sql = "SELECT * FROM tipo_equipo WHERE id_tipo = ?";
        
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idTipo);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    TipoEquipo tipo = new TipoEquipo();
                    tipo.setIdTipo(rs.getInt("id_tipo"));
                    tipo.setNombre(rs.getString("nombre"));
                    tipo.setDescripcion(rs.getString("descripcion"));
                    return tipo;
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    // Método para listar todos los tipos de equipo
    public List<TipoEquipo> listarTodos() {
        List<TipoEquipo> tipos = new ArrayList<>();
        String sql = "SELECT * FROM tipo_equipo ORDER BY nombre";
        
        try (Connection conn = ConexionDB.getConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                TipoEquipo tipo = new TipoEquipo();
                tipo.setIdTipo(rs.getInt("id_tipo"));
                tipo.setNombre(rs.getString("nombre"));
                tipo.setDescripcion(rs.getString("descripcion"));
                tipos.add(tipo);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return tipos;
    }
}