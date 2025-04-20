package com.deportur.dao;

import com.deportur.config.ConexionDB;
import com.deportur.modelo.Usuario;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {
    
    // Método para insertar un nuevo usuario
    public boolean insertar(Usuario usuario) {
        String sql = "INSERT INTO usuario (nombre_usuario, contrasena, rol, nombre, apellido, email, activo) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, usuario.getNombreUsuario());
            stmt.setString(2, usuario.getContrasena());
            stmt.setString(3, usuario.getRol());
            stmt.setString(4, usuario.getNombre());
            stmt.setString(5, usuario.getApellido());
            stmt.setString(6, usuario.getEmail());
            stmt.setBoolean(7, usuario.isActivo());
            
            int filasAfectadas = stmt.executeUpdate();
            
            if (filasAfectadas > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        usuario.setIdUsuario(rs.getInt(1));
                    }
                }
                return true;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    // Método para actualizar un usuario existente
    public boolean actualizar(Usuario usuario) {
        String sql = "UPDATE usuario SET nombre_usuario = ?, contrasena = ?, rol = ?, " +
                     "nombre = ?, apellido = ?, email = ?, activo = ? WHERE id_usuario = ?";
        
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, usuario.getNombreUsuario());
            stmt.setString(2, usuario.getContrasena());
            stmt.setString(3, usuario.getRol());
            stmt.setString(4, usuario.getNombre());
            stmt.setString(5, usuario.getApellido());
            stmt.setString(6, usuario.getEmail());
            stmt.setBoolean(7, usuario.isActivo());
            stmt.setInt(8, usuario.getIdUsuario());
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    // Método para eliminar un usuario (desactivarlo)
    public boolean eliminar(int idUsuario) {
        // En lugar de eliminar realmente, marcamos como inactivo
        String sql = "UPDATE usuario SET activo = 0 WHERE id_usuario = ?";
        
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idUsuario);
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    // Método para buscar un usuario por ID
    public Usuario buscarPorId(int idUsuario) {
        String sql = "SELECT * FROM usuario WHERE id_usuario = ?";
        
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idUsuario);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return crearUsuarioDesdeResultSet(rs);
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    // Método para buscar un usuario por nombre de usuario
    public Usuario buscarPorNombreUsuario(String nombreUsuario) {
        String sql = "SELECT * FROM usuario WHERE nombre_usuario = ?";
        
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, nombreUsuario);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return crearUsuarioDesdeResultSet(rs);
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    // Método para autenticar usuario
    public Usuario autenticar(String nombreUsuario, String contrasena) {
        String sql = "SELECT * FROM usuario WHERE nombre_usuario = ? AND contrasena = ? AND activo = 1";
        
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, nombreUsuario);
            stmt.setString(2, contrasena);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return crearUsuarioDesdeResultSet(rs);
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    // Método para listar todos los usuarios
    public List<Usuario> listarTodos() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuario ORDER BY nombre, apellido";
        
        try (Connection conn = ConexionDB.getConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                usuarios.add(crearUsuarioDesdeResultSet(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return usuarios;
    }
    
    // Método para listar solo usuarios activos
    public List<Usuario> listarActivos() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuario WHERE activo = 1 ORDER BY nombre, apellido";
        
        try (Connection conn = ConexionDB.getConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                usuarios.add(crearUsuarioDesdeResultSet(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return usuarios;
    }
    
    // Método para verificar si hay al menos un administrador
    public boolean existeAlMenosUnAdministrador(int exceptoIdUsuario) {
        String sql = "SELECT COUNT(*) FROM usuario WHERE rol = 'administrador' AND activo = 1 AND id_usuario != ?";
        
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, exceptoIdUsuario);
            
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
    
    // Método auxiliar para crear un objeto Usuario a partir de un ResultSet
    private Usuario crearUsuarioDesdeResultSet(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(rs.getInt("id_usuario"));
        usuario.setNombreUsuario(rs.getString("nombre_usuario"));
        usuario.setContrasena(rs.getString("contrasena"));
        usuario.setRol(rs.getString("rol"));
        usuario.setNombre(rs.getString("nombre"));
        usuario.setApellido(rs.getString("apellido"));
        usuario.setEmail(rs.getString("email"));
        usuario.setActivo(rs.getBoolean("activo"));
        usuario.setFechaCreacion(rs.getTimestamp("fecha_creacion"));
        return usuario;
    }
}