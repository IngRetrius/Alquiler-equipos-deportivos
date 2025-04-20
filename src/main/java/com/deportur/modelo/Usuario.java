package com.deportur.modelo;

import java.util.Date;

public class Usuario {
    private int idUsuario;
    private String nombreUsuario;
    private String contrasena;
    private String rol;
    private String nombre;
    private String apellido;
    private String email;
    private boolean activo;
    private Date fechaCreacion;
    
    // Constructor vacío
    public Usuario() {
    }
    
    // Constructor con parámetros
    public Usuario(int idUsuario, String nombreUsuario, String contrasena, String rol, 
                  String nombre, String apellido, String email, boolean activo, Date fechaCreacion) {
        this.idUsuario = idUsuario;
        this.nombreUsuario = nombreUsuario;
        this.contrasena = contrasena;
        this.rol = rol;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.activo = activo;
        this.fechaCreacion = fechaCreacion;
    }
    
    // Getters y Setters
    public int getIdUsuario() {
        return idUsuario;
    }
    
    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }
    
    public String getNombreUsuario() {
        return nombreUsuario;
    }
    
    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }
    
    public String getContrasena() {
        return contrasena;
    }
    
    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }
    
    public String getRol() {
        return rol;
    }
    
    public void setRol(String rol) {
        this.rol = rol;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String getApellido() {
        return apellido;
    }
    
    public void setApellido(String apellido) {
        this.apellido = apellido;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public boolean isActivo() {
        return activo;
    }
    
    public void setActivo(boolean activo) {
        this.activo = activo;
    }
    
    public Date getFechaCreacion() {
        return fechaCreacion;
    }
    
    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
    
    @Override
    public String toString() {
        return nombre + " " + apellido + " (" + nombreUsuario + ")";
    }
    
    // Método para verificar si es administrador
    public boolean esAdministrador() {
        return "administrador".equals(this.rol);
    }
}