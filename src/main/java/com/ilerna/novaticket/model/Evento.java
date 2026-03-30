package com.ilerna.novaticket.model;

import java.time.LocalDate;

public abstract class Evento {

    //Atributos

    private EventoEnum tipo_evento;
    private int id, id_lugar;
    private String nombre, descripcion, nombre_lugar, direccion, ciudad, ruta_imagen;
    private LocalDate fecha;

    //Constructores

    public Evento() {
    }

    public Evento(EventoEnum tipo_evento, int id, int id_lugar, String nombre, String descripcion, String nombre_lugar, String direccion, String ciudad, String ruta_imagen, LocalDate fecha) {
        this.tipo_evento = tipo_evento;
        this.id = id;
        this.id_lugar = id_lugar;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.nombre_lugar = nombre_lugar;
        this.direccion = direccion;
        this.ciudad = ciudad;
        this.ruta_imagen = ruta_imagen;
        this.fecha = fecha;
    }

    //Getters and Setters


    public EventoEnum getTipo_evento() {
        return tipo_evento;
    }

    public void setTipo_evento(EventoEnum tipo_evento) {
        this.tipo_evento = tipo_evento;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_lugar() {
        return id_lugar;
    }

    public void setId_lugar(int id_lugar) {
        this.id_lugar = id_lugar;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getNombre_lugar() {
        return nombre_lugar;
    }

    public void setNombre_lugar(String nombre_lugar) {
        this.nombre_lugar = nombre_lugar;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getRuta_imagen() {
        return ruta_imagen;
    }

    public void setRuta_imagen(String ruta_imagen) {
        this.ruta_imagen = ruta_imagen;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }
}
