package com.deportur.modelo;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

public class Reserva {
    private int idReserva;
    private Cliente cliente;
    private Date fechaCreacion;
    private Date fechaInicio;
    private Date fechaFin;
    private DestinoTuristico destino;
    private String estado;
    private List<DetalleReserva> detalles;
    
    // Constructor vacío
    public Reserva() {
        this.detalles = new ArrayList<>();
    }
    
    // Constructor con parámetros
    public Reserva(int idReserva, Cliente cliente, Date fechaCreacion, 
                   Date fechaInicio, Date fechaFin, DestinoTuristico destino, 
                   String estado) {
        this.idReserva = idReserva;
        this.cliente = cliente;
        this.fechaCreacion = fechaCreacion;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.destino = destino;
        this.estado = estado;
        this.detalles = new ArrayList<>();
    }
    
    // Getters y Setters
    public int getIdReserva() {
        return idReserva;
    }
    
    public void setIdReserva(int idReserva) {
        this.idReserva = idReserva;
    }
    
    public Cliente getCliente() {
        return cliente;
    }
    
    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }
    
    public Date getFechaCreacion() {
        return fechaCreacion;
    }
    
    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
    
    public Date getFechaInicio() {
        return fechaInicio;
    }
    
    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }
    
    public Date getFechaFin() {
        return fechaFin;
    }
    
    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }
    
    public DestinoTuristico getDestino() {
        return destino;
    }
    
    public void setDestino(DestinoTuristico destino) {
        this.destino = destino;
    }
    
    public String getEstado() {
        return estado;
    }
    
    public void setEstado(String estado) {
        this.estado = estado;
    }
    
    public List<DetalleReserva> getDetalles() {
        return detalles;
    }
    
    public void setDetalles(List<DetalleReserva> detalles) {
        this.detalles = detalles;
    }
    
    public void agregarDetalle(DetalleReserva detalle) {
        this.detalles.add(detalle);
    }
    
    public void eliminarDetalle(DetalleReserva detalle) {
        this.detalles.remove(detalle);
    }
    
    // Métodos para calcular el total de la reserva
    public double calcularTotal() {
        double total = 0;
        for (DetalleReserva detalle : detalles) {
            total += detalle.getPrecioUnitario();
        }
        return total;
    }
    
    @Override
    public String toString() {
        return "Reserva #" + idReserva + " - Cliente: " + cliente.getNombre() + " " + 
               cliente.getApellido() + " - Destino: " + destino.getNombre();
    }
}