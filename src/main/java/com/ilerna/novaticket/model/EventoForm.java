package com.ilerna.novaticket.model;

import java.time.LocalDate;

/**
 * Objeto de transferencia de datos (DTO) para el formulario de creación y edición de eventos.
 * Agrupa en una sola clase todos los campos posibles de los subtipos de evento
 * (Concierto, Teatro y Museo) para facilitar el binding con Spring MVC.
 * El controlador usa este objeto para construir la instancia concreta de Evento correspondiente.
 */
public class EventoForm {

    // Campos comunes a todos los tipos de evento
    private EventoEnum tipo_evento;
    private int id, id_lugar, aforo_maximo;
    private String nombre, descripcion, ruta_imagen;
    private LocalDate fecha;

    // Campos exclusivos de Concierto
    private String artista_principal, genero_musical;
    private int duracion_minutos;

    // Campos exclusivos de Teatro
    private String obra, director;

    // Campos exclusivos de Museo
    private String nombre_exposicion, tipo_exposicion;
    private LocalDate fecha_fin;

    /** Constructor vacío requerido por Spring MVC para el binding del formulario. */
    public EventoForm() {}

    // --- Getters y Setters ---

    /** Devuelve el tipo de evento seleccionado en el formulario. */
    public EventoEnum getTipo_evento() { return tipo_evento; }
    public void setTipo_evento(EventoEnum tipo_evento) { this.tipo_evento = tipo_evento; }

    /** Devuelve el id del lugar seleccionado en el formulario. */
    public int getId_lugar() { return id_lugar; }
    public void setId_lugar(int id_lugar) { this.id_lugar = id_lugar; }

    /** Devuelve el nombre del evento introducido en el formulario. */
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    /** Devuelve la descripción del evento. */
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    /** Devuelve el nombre del archivo de imagen del evento (UUID.extension). */
    public String getRuta_imagen() { return ruta_imagen; }
    public void setRuta_imagen(String ruta_imagen) { this.ruta_imagen = ruta_imagen; }

    /** Devuelve la fecha de celebración del evento. */
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    // Campos de Concierto

    /** Devuelve el artista o grupo principal (campo de concierto). */
    public String getArtista_principal() { return artista_principal; }
    public void setArtista_principal(String artista_principal) { this.artista_principal = artista_principal; }

    /** Devuelve el género musical (campo de concierto). */
    public String getGenero_musical() { return genero_musical; }
    public void setGenero_musical(String genero_musical) { this.genero_musical = genero_musical; }

    /** Devuelve la duración en minutos (campo de concierto). */
    public int getDuracion_minutos() { return duracion_minutos; }
    public void setDuracion_minutos(int duracion_minutos) { this.duracion_minutos = duracion_minutos; }

    // Campos de Teatro

    /** Devuelve el título de la obra (campo de teatro). */
    public String getObra() { return obra; }
    public void setObra(String obra) { this.obra = obra; }

    /** Devuelve el nombre del director (campo de teatro). */
    public String getDirector() { return director; }
    public void setDirector(String director) { this.director = director; }

    // Campos de Museo

    /** Devuelve el nombre de la exposición (campo de museo). */
    public String getNombre_exposicion() { return nombre_exposicion; }
    public void setNombre_exposicion(String nombre_exposicion) { this.nombre_exposicion = nombre_exposicion; }

    /** Devuelve el tipo de exposición (campo de museo). */
    public String getTipo_exposicion() { return tipo_exposicion; }
    public void setTipo_exposicion(String tipo_exposicion) { this.tipo_exposicion = tipo_exposicion; }

    /** Devuelve la fecha de fin de la exposición (campo de museo). */
    public LocalDate getFecha_fin() { return fecha_fin; }
    public void setFecha_fin(LocalDate fecha_fin) { this.fecha_fin = fecha_fin; }

    /** Devuelve el id del evento (usado al editar un evento existente). */
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    /** Devuelve el aforo máximo del evento. */
    public int getAforo_maximo() { return aforo_maximo; }
    public void setAforo_maximo(int aforo_maximo) { this.aforo_maximo = aforo_maximo; }
}
