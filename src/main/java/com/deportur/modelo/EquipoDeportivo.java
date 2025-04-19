package com.deportur.modelo;

import java.util.Date;

public class EquipoDeportivo {
    private int idEquipo;
    private String nombre;
    private TipoEquipo tipo;
    private String marca;
    private String estado;
    private double precioAlquiler;
    private Date fechaAdquisicion;
    private DestinoTuristico destino;
    private boolean disponible;
    
    // Constructor vacío
    public EquipoDeportivo() {
    }
    
    // Constructor con parámetros
    public EquipoDeportivo(int idEquipo, String nombre, TipoEquipo tipo, String marca, 
                           String estado, double precioAlquiler, Date fechaAdquisicion, 
                           DestinoTuristico destino, boolean disponible) {
        this.idEquipo = idEquipo;
        this.nombre = nombre;
        this.tipo = tipo;
        this.marca = marca;
        this.estado = estado;
        this.precioAlquiler = precioAlquiler;
        this.fechaAdquisicion = fechaAdquisicion;
        this.destino = destino;
        this.disponible = disponible;
    }
    
    // Getters y Setters
    public int getIdEquipo() {
        return idEquipo;
    }
    
    public void setIdEquipo(int idEquipo) {
        this.idEquipo = idEquipo;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public TipoEquipo getTipo() {
        return tipo;
    }
    
    public void setTipo(TipoEquipo tipo) {
        this.tipo = tipo;
    }
    
    public String getMarca() {
        return marca;
    }
    
    public void setMarca(String marca) {
        this.marca = marca;
    }
    
    public String getEstado() {
        return estado;
    }
    
    public void setEstado(String estado) {
        this.estado = estado;
    }
    
    public double getPrecioAlquiler() {
        return precioAlquiler;
    }
    
    public void setPrecioAlquiler(double precioAlquiler) {
        this.precioAlquiler = precioAlquiler;
    }
    
    public Date getFechaAdquisicion() {
        return fechaAdquisicion;
    }
    
    public void setFechaAdquisicion(Date fechaAdquisicion) {
        this.fechaAdquisicion = fechaAdquisicion;
    }
    
    public DestinoTuristico getDestino() {
        return destino;
    }
    
    public void setDestino(DestinoTuristico destino) {
        this.destino = destino;
    }
    
    public boolean isDisponible() {
        return disponible;
    }
    
    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }
    
    @Override
    public String toString() {
        return nombre + " - " + marca + " (" + tipo.getNombre() + ")";
    }
}