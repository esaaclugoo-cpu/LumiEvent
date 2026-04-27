package com.ilerna.novaticket.model;

/**
 * Subtipo de Evento que representa un concierto musical.
 * Añade los campos específicos: artista principal, género musical y duración.
 * Hereda todos los atributos comunes de la clase Evento.
 */
public class Concierto extends Evento {

    // id_evento es la FK que vincula este registro con la tabla evento
    private int id_evento, duracion_minutos;
    private String artista_principal, genero_musical;

    /** Constructor vacío requerido por el DAO al mapear ResultSets. */
    public Concierto() {}

    /** Constructor con los campos específicos del concierto. */
    public Concierto(int id_evento, int duracion_minutos, String artista_principal, String genero_musical) {
        this.id_evento = id_evento;
        this.duracion_minutos = duracion_minutos;
        this.artista_principal = artista_principal;
        this.genero_musical = genero_musical;
    }

    /** Devuelve el id del evento padre al que pertenece este concierto. */
    public int getId_evento() { return id_evento; }
    public void setId_evento(int id_evento) { this.id_evento = id_evento; }

    /** Devuelve la duración del concierto en minutos. */
    public int getDuracion_minutos() { return duracion_minutos; }
    public void setDuracion_minutos(int duracion_minutos) { this.duracion_minutos = duracion_minutos; }

    /** Devuelve el nombre del artista o grupo principal del concierto. */
    public String getArtista_principal() { return artista_principal; }
    public void setArtista_principal(String artista_principal) { this.artista_principal = artista_principal; }

    /** Devuelve el género musical del concierto (rock, pop, clásica, etc.). */
    public String getGenero_musical() { return genero_musical; }
    public void setGenero_musical(String genero_musical) { this.genero_musical = genero_musical; }
}
