package com.ilerna.novaticket.model;

import java.time.LocalDate;

public class Museo extends Evento{

    private int id_evento;
    private String nombre_exposicion, tipo_exposicion;
    private LocalDate fecha_fin;

    public Museo() {
    }

    public Museo(int id_evento, String nombre_exposicion, String tipo_exposicion, LocalDate fecha_fin) {
        this.id_evento = id_evento;
        this.nombre_exposicion = nombre_exposicion;
        this.tipo_exposicion = tipo_exposicion;
        this.fecha_fin = fecha_fin;
    }

    public int getId_evento() {
        return id_evento;
    }

    public void setId_evento(int id_evento) {
        this.id_evento = id_evento;
    }

    public String getNombre_exposicion() {
        return nombre_exposicion;
    }

    public void setNombre_exposicion(String nombre_exposicion) {
        this.nombre_exposicion = nombre_exposicion;
    }

    public String getTipo_exposicion() {
        return tipo_exposicion;
    }

    public void setTipo_exposicion(String tipo_exposicion) {
        this.tipo_exposicion = tipo_exposicion;
    }

    public LocalDate getFecha_fin() {
        return fecha_fin;
    }

    public void setFecha_fin(LocalDate fecha_fin) {
        this.fecha_fin = fecha_fin;
    }
}
