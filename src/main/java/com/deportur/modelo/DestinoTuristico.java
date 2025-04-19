package com.deportur.modelo;

public class DestinoTuristico {
    private int idDestino;
    private String nombre;
    private String ubicacion;
    private String descripcion;
    
    // Constructor vacío
    public DestinoTuristico() {
    }
    
    // Constructor con parámetros
    public DestinoTuristico(int idDestino, String nombre, String ubicacion, String descripcion) {
        this.idDestino = idDestino;
        this.nombre = nombre;
        this.ubicacion = ubicacion;
        this.descripcion = descripcion;
    }
    
    // Getters y Setters
    public int getIdDestino() {
        return idDestino;
    }
    
    public void setIdDestino(int idDestino) {
        this.idDestino = idDestino;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String getUbicacion() {
        return ubicacion;
    }
    
    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    @Override
    public String toString() {
        return nombre + " (" + ubicacion + ")";
    }
}