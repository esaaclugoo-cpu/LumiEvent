package com.ilerna.novaticket.model;

/**
 * Subtipo de Evento que representa una obra de teatro.
 * Añade los campos específicos: nombre de la obra y director.
 * Hereda todos los atributos comunes de la clase Evento.
 */
public class Teatro extends Evento {

    // id_evento es la FK que vincula este registro con la tabla evento
    private int id_evento;
    private String obra, director;

    /** Constructor vacío requerido por el DAO al mapear ResultSets. */
    public Teatro() {}

    /** Constructor con los campos específicos del teatro. */
    public Teatro(int id_evento, String obra, String director) {
        this.id_evento = id_evento;
        this.obra = obra;
        this.director = director;
    }

    /** Devuelve el id del evento padre al que pertenece esta obra. */
    public int getId_evento() { return id_evento; }
    public void setId_evento(int id_evento) { this.id_evento = id_evento; }

    /** Devuelve el título de la obra de teatro. */
    public String getObra() { return obra; }
    public void setObra(String obra) { this.obra = obra; }

    /** Devuelve el nombre del director de la obra. */
    public String getDirector() { return director; }
    public void setDirector(String director) { this.director = director; }
}
