package com.ilerna.novaticket.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Compra {

    private int id, id_usuario;
    private LocalDateTime fecha;
    private BigDecimal total;

    public Compra() {
    }

    public Compra(int id, int id_usuario, LocalDateTime fecha, BigDecimal total) {
        this.id = id;
        this.id_usuario = id_usuario;
        this.fecha = fecha;
        this.total = total;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(int id_usuario) {
        this.id_usuario = id_usuario;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}
