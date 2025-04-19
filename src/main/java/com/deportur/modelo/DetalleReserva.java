package com.deportur.modelo;

public class DetalleReserva {
    private int idDetalle;
    private Reserva reserva;
    private EquipoDeportivo equipo;
    private double precioUnitario;
    
    // Constructor vacío
    public DetalleReserva() {
    }
    
    // Constructor con parámetros
    public DetalleReserva(int idDetalle, Reserva reserva, EquipoDeportivo equipo, double precioUnitario) {
        this.idDetalle = idDetalle;
        this.reserva = reserva;
        this.equipo = equipo;
        this.precioUnitario = precioUnitario;
    }
    
    // Getters y Setters
    public int getIdDetalle() {
        return idDetalle;
    }
    
    public void setIdDetalle(int idDetalle) {
        this.idDetalle = idDetalle;
    }
    
    public Reserva getReserva() {
        return reserva;
    }
    
    public void setReserva(Reserva reserva) {
        this.reserva = reserva;
    }
    
    public EquipoDeportivo getEquipo() {
        return equipo;
    }
    
    public void setEquipo(EquipoDeportivo equipo) {
        this.equipo = equipo;
    }
    
    public double getPrecioUnitario() {
        return precioUnitario;
    }
    
    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }
    
    @Override
    public String toString() {
        return equipo.getNombre() + " - $" + precioUnitario;
    }
}