package com.deportur.servicio;

import com.deportur.dao.UsuarioDAO;
import com.deportur.modelo.Usuario;
import java.util.List;
import java.util.regex.Pattern;

public class GestionUsuariosService {
    
    private UsuarioDAO usuarioDAO;
    
    public GestionUsuariosService() {
        this.usuarioDAO = new UsuarioDAO();
    }
    
    // Método para autenticar usuario
    public Usuario autenticar(String nombreUsuario, String contrasena) throws Exception {
        if (nombreUsuario == null || nombreUsuario.trim().isEmpty()) {
            throw new Exception("El nombre de usuario es requerido");
        }
        
        if (contrasena == null || contrasena.trim().isEmpty()) {
            throw new Exception("La contraseña es requerida");
        }
        
        Usuario usuario = usuarioDAO.autenticar(nombreUsuario, contrasena);
        
        if (usuario == null) {
            throw new Exception("Credenciales incorrectas o usuario inactivo");
        }
        
        return usuario;
    }
    
    // Método para registrar un nuevo usuario
    public boolean registrarUsuario(Usuario usuario, Usuario usuarioActual) throws Exception {
        // Validar que el usuario actual sea administrador
        if (usuarioActual == null || !usuarioActual.esAdministrador()) {
            throw new Exception("Solo los administradores pueden registrar usuarios");
        }
        
        // Validar datos obligatorios
        if (usuario.getNombreUsuario() == null || usuario.getNombreUsuario().trim().isEmpty()) {
            throw new Exception("El nombre de usuario es requerido");
        }
        
        if (usuario.getContrasena() == null || usuario.getContrasena().trim().isEmpty()) {
            throw new Exception("La contraseña es requerida");
        }
        
        // Validar requisitos de contraseña
        validarContrasena(usuario.getContrasena());
        
        if (usuario.getNombre() == null || usuario.getNombre().trim().isEmpty()) {
            throw new Exception("El nombre es requerido");
        }
        
        if (usuario.getApellido() == null || usuario.getApellido().trim().isEmpty()) {
            throw new Exception("El apellido es requerido");
        }
        
        // Verificar que el nombre de usuario no exista
        Usuario usuarioExistente = usuarioDAO.buscarPorNombreUsuario(usuario.getNombreUsuario());
        if (usuarioExistente != null) {
            throw new Exception("El nombre de usuario ya existe");
        }
        
        // Si pasa todas las validaciones, registrar el usuario
        return usuarioDAO.insertar(usuario);
    }
    
    // Método para actualizar un usuario existente
    public boolean actualizarUsuario(Usuario usuario, Usuario usuarioActual) throws Exception {
        // Validar que el usuario actual sea administrador
        if (usuarioActual == null || !usuarioActual.esAdministrador()) {
            throw new Exception("Solo los administradores pueden actualizar usuarios");
        }
        
        // Verificar que el usuario exista
        Usuario usuarioExistente = usuarioDAO.buscarPorId(usuario.getIdUsuario());
        if (usuarioExistente == null) {
            throw new Exception("El usuario que intenta actualizar no existe");
        }
        
        // Validar datos obligatorios
        if (usuario.getNombreUsuario() == null || usuario.getNombreUsuario().trim().isEmpty()) {
            throw new Exception("El nombre de usuario es requerido");
        }
        
        // Si la contraseña ha cambiado, validarla
        if (!usuario.getContrasena().equals(usuarioExistente.getContrasena())) {
            validarContrasena(usuario.getContrasena());
        }
        
        if (usuario.getNombre() == null || usuario.getNombre().trim().isEmpty()) {
            throw new Exception("El nombre es requerido");
        }
        
        if (usuario.getApellido() == null || usuario.getApellido().trim().isEmpty()) {
            throw new Exception("El apellido es requerido");
        }
        
        // Verificar que el nombre de usuario no esté duplicado
        Usuario otroUsuario = usuarioDAO.buscarPorNombreUsuario(usuario.getNombreUsuario());
        if (otroUsuario != null && otroUsuario.getIdUsuario() != usuario.getIdUsuario()) {
            throw new Exception("El nombre de usuario ya está en uso por otro usuario");
        }
        
        // Si está cambiando el rol de administrador a trabajador, verificar que quede al menos un admin
        if (usuarioExistente.esAdministrador() && !usuario.esAdministrador()) {
            if (!usuarioDAO.existeAlMenosUnAdministrador(usuario.getIdUsuario())) {
                throw new Exception("No se puede cambiar el rol. Debe permanecer al menos un administrador en el sistema");
            }
        }
        
        // Si pasa todas las validaciones, actualizar el usuario
        return usuarioDAO.actualizar(usuario);
    }
    
    // Método para eliminar (desactivar) un usuario
    public boolean eliminarUsuario(int idUsuario, Usuario usuarioActual) throws Exception {
        // Validar que el usuario actual sea administrador
        if (usuarioActual == null || !usuarioActual.esAdministrador()) {
            throw new Exception("Solo los administradores pueden eliminar usuarios");
        }
        
        // Verificar que el usuario exista
        Usuario usuario = usuarioDAO.buscarPorId(idUsuario);
        if (usuario == null) {
            throw new Exception("El usuario que intenta eliminar no existe");
        }
        
        // Evitar que un administrador se elimine a sí mismo
        if (usuario.getIdUsuario() == usuarioActual.getIdUsuario()) {
            throw new Exception("No puede eliminar su propio usuario");
        }
        
        // Si es un administrador, verificar que quede al menos un admin
        if (usuario.esAdministrador()) {
            if (!usuarioDAO.existeAlMenosUnAdministrador(idUsuario)) {
                throw new Exception("No se puede eliminar el usuario. Debe permanecer al menos un administrador en el sistema");
            }
        }
        
        // Si pasa todas las validaciones, eliminar (desactivar) el usuario
        return usuarioDAO.eliminar(idUsuario);
    }
    
    // Método para buscar un usuario por ID
    public Usuario buscarUsuarioPorId(int idUsuario, Usuario usuarioActual) throws Exception {
        // Validar que el usuario actual sea administrador
        if (usuarioActual == null || !usuarioActual.esAdministrador()) {
            throw new Exception("Solo los administradores pueden consultar usuarios");
        }
        
        return usuarioDAO.buscarPorId(idUsuario);
    }
    
    // Método para listar todos los usuarios
    public List<Usuario> listarTodosLosUsuarios(Usuario usuarioActual) throws Exception {
        // Validar que el usuario actual sea administrador
        if (usuarioActual == null || !usuarioActual.esAdministrador()) {
            throw new Exception("Solo los administradores pueden listar usuarios");
        }
        
        return usuarioDAO.listarTodos();
    }
    
    // Método para listar solo usuarios activos
    public List<Usuario> listarUsuariosActivos(Usuario usuarioActual) throws Exception {
        // Validar que el usuario actual sea administrador
        if (usuarioActual == null || !usuarioActual.esAdministrador()) {
            throw new Exception("Solo los administradores pueden listar usuarios");
        }
        
        return usuarioDAO.listarActivos();
    }
    
    // Método privado para validar la contraseña
    private void validarContrasena(String contrasena) throws Exception {
        if (contrasena == null || contrasena.length() < 8) {
            throw new Exception("La contraseña debe tener al menos 8 caracteres");
        }
        
        // Verificar que contenga al menos un carácter especial
        Pattern pattern = Pattern.compile("[!@#$%^&*(),.?\":{}|<>]");
        if (!pattern.matcher(contrasena).find()) {
            throw new Exception("La contraseña debe contener al menos un carácter especial");
        }
    }
}