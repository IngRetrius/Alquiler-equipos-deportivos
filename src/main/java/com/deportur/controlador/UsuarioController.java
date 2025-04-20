package com.deportur.controlador;

import com.deportur.modelo.Usuario;
import com.deportur.servicio.GestionUsuariosService;

import java.util.List;
import javax.swing.JOptionPane;

public class UsuarioController {
    
    private GestionUsuariosService usuarioService;
    private Usuario usuarioActual; // Usuario que ha iniciado sesión
    
    public UsuarioController() {
        this.usuarioService = new GestionUsuariosService();
        this.usuarioActual = null;
    }
    
    // Método para autenticar usuario
    public boolean autenticar(String nombreUsuario, String contrasena) {
        try {
            usuarioActual = usuarioService.autenticar(nombreUsuario, contrasena);
            return true;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error de autenticación: " + e.getMessage(),
                                         "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    // Método para cerrar sesión
    public void cerrarSesion() {
        usuarioActual = null;
    }
    
    // Método para obtener el usuario actual
    public Usuario getUsuarioActual() {
        return usuarioActual;
    }
    
    // Método para verificar si hay un usuario autenticado
    public boolean estaAutenticado() {
        return usuarioActual != null;
    }
    
    // Método para verificar si el usuario actual es administrador
    public boolean esAdministrador() {
        return usuarioActual != null && usuarioActual.esAdministrador();
    }
    
    // Método para registrar un nuevo usuario
    public boolean registrarUsuario(String nombreUsuario, String contrasena, String rol,
                                   String nombre, String apellido, String email) {
        try {
            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setNombreUsuario(nombreUsuario);
            nuevoUsuario.setContrasena(contrasena);
            nuevoUsuario.setRol(rol);
            nuevoUsuario.setNombre(nombre);
            nuevoUsuario.setApellido(apellido);
            nuevoUsuario.setEmail(email);
            nuevoUsuario.setActivo(true);
            
            return usuarioService.registrarUsuario(nuevoUsuario, usuarioActual);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al registrar usuario: " + e.getMessage(),
                                         "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    // Método para actualizar un usuario existente
    public boolean actualizarUsuario(int idUsuario, String nombreUsuario, String contrasena, String rol,
                                    String nombre, String apellido, String email, boolean activo) {
        try {
            Usuario usuario = new Usuario();
            usuario.setIdUsuario(idUsuario);
            usuario.setNombreUsuario(nombreUsuario);
            usuario.setContrasena(contrasena);
            usuario.setRol(rol);
            usuario.setNombre(nombre);
            usuario.setApellido(apellido);
            usuario.setEmail(email);
            usuario.setActivo(activo);
            
            return usuarioService.actualizarUsuario(usuario, usuarioActual);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al actualizar usuario: " + e.getMessage(),
                                         "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    // Método para eliminar un usuario
    public boolean eliminarUsuario(int idUsuario) {
        try {
            return usuarioService.eliminarUsuario(idUsuario, usuarioActual);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al eliminar usuario: " + e.getMessage(),
                                         "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    // Método para buscar un usuario por ID
    public Usuario buscarUsuarioPorId(int idUsuario) {
        try {
            return usuarioService.buscarUsuarioPorId(idUsuario, usuarioActual);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al buscar usuario: " + e.getMessage(),
                                         "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
    
    // Método para listar todos los usuarios
    public List<Usuario> listarTodosLosUsuarios() {
        try {
            return usuarioService.listarTodosLosUsuarios(usuarioActual);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al listar usuarios: " + e.getMessage(),
                                         "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
    
    // Método para listar solo usuarios activos
    public List<Usuario> listarUsuariosActivos() {
        try {
            return usuarioService.listarUsuariosActivos(usuarioActual);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al listar usuarios activos: " + e.getMessage(),
                                         "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
}