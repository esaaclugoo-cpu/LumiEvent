package com.ilerna.novaticket.model;

import java.time.LocalDate;

public class EventoForm {

    private EventoEnum tipo_evento;
    private int id, id_lugar, aforo_maximo;
    private String nombre, descripcion, ruta_imagen;
    private LocalDate fecha;

    // Campos para Concierto
    private String artista_principal, genero_musical;
    private int duracion_minutos;

    // Campos para Teatro
    private String obra, director;

    // Campos para Museo
    private String nombre_exposicion, tipo_exposicion;
    private LocalDate fecha_fin;

    // Constructores
    public EventoForm() {}

    // Getters and Setters
    public EventoEnum getTipo_evento() {
        return tipo_evento;
    }

    public void setTipo_evento(EventoEnum tipo_evento) {
        this.tipo_evento = tipo_evento;
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

    public String getArtista_principal() {
        return artista_principal;
    }

    public void setArtista_principal(String artista_principal) {
        this.artista_principal = artista_principal;
    }

    public String getGenero_musical() {
        return genero_musical;
    }

    public void setGenero_musical(String genero_musical) {
        this.genero_musical = genero_musical;
    }

    public int getDuracion_minutos() {
        return duracion_minutos;
    }

    public void setDuracion_minutos(int duracion_minutos) {
        this.duracion_minutos = duracion_minutos;
    }

    public String getObra() {
        return obra;
    }

    public void setObra(String obra) {
        this.obra = obra;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAforo_maximo() {
        return aforo_maximo;
    }

    public void setAforo_maximo(int aforo_maximo) {
        this.aforo_maximo = aforo_maximo;
    }
}
