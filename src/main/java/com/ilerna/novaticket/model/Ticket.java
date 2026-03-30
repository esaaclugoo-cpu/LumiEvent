package com.ilerna.novaticket.model;

import java.math.BigDecimal;

public class Ticket {

    private int id, id_evento, id_asiento, id_compra, cantidad;
    private BigDecimal precio_unitario;

    public Ticket() {
    }

    public Ticket(int id, int id_evento, int id_asiento, int id_compra, int cantidad, BigDecimal precio_unitario) {
        this.id = id;
        this.id_evento = id_evento;
        this.id_asiento = id_asiento;
        this.id_compra = id_compra;
        this.cantidad = cantidad;
        this.precio_unitario = precio_unitario;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_evento() {
        return id_evento;
    }

    public void setId_evento(int id_evento) {
        this.id_evento = id_evento;
    }

    public int getId_asiento() {
        return id_asiento;
    }

    public void setId_asiento(int id_asiento) {
        this.id_asiento = id_asiento;
    }

    public int getId_compra() {
        return id_compra;
    }

    public void setId_compra(int id_compra) {
        this.id_compra = id_compra;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecio_unitario() {
        return precio_unitario;
    }

    public void setPrecio_unitario(BigDecimal precio_unitario) {
        this.precio_unitario = precio_unitario;
    }
}
