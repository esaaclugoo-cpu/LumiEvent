package com.ilerna.novaticket.model;

import java.time.LocalDate;

/**
 * Subtipo de Evento que representa una exposición de museo.
 * Añade los campos específicos: nombre de la exposición, tipo y fecha de fin.
 * Hereda todos los atributos comunes de la clase Evento.
 */
public class Museo extends Evento {

    // id_evento es la FK que vincula este registro con la tabla evento
    private int id_evento;
    private String nombre_exposicion, tipo_exposicion;
    // Fecha en la que finaliza la exposición (puede ser null si es permanente)
    private LocalDate fecha_fin;

    /** Constructor vacío requerido por el DAO al mapear ResultSets. */
    public Museo() {}

    /** Constructor con los campos específicos del museo. */
    public Museo(int id_evento, String nombre_exposicion, String tipo_exposicion, LocalDate fecha_fin) {
        this.id_evento = id_evento;
        this.nombre_exposicion = nombre_exposicion;
        this.tipo_exposicion = tipo_exposicion;
        this.fecha_fin = fecha_fin;
    }

    /** Devuelve el id del evento padre al que pertenece esta exposición. */
    public int getId_evento() { return id_evento; }
    public void setId_evento(int id_evento) { this.id_evento = id_evento; }

    /** Devuelve el nombre de la exposición del museo. */
    public String getNombre_exposicion() { return nombre_exposicion; }
    public void setNombre_exposicion(String nombre_exposicion) { this.nombre_exposicion = nombre_exposicion; }

    /** Devuelve el tipo de exposición (fotográfica, pictórica, escultura, etc.). */
    public String getTipo_exposicion() { return tipo_exposicion; }
    public void setTipo_exposicion(String tipo_exposicion) { this.tipo_exposicion = tipo_exposicion; }

    /** Devuelve la fecha de fin de la exposición, o null si no tiene fecha de cierre. */
    public LocalDate getFecha_fin() { return fecha_fin; }
    public void setFecha_fin(LocalDate fecha_fin) { this.fecha_fin = fecha_fin; }
}
