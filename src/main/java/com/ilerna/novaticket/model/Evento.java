package com.ilerna.novaticket.model;

import java.time.LocalDate;

/**
 * Clase abstracta base para todos los tipos de evento del sistema.
 * Define los atributos comunes (nombre, fecha, aforo, lugar, imagen) y
 * obliga a las subclases (Concierto, Teatro, Museo) a implementar su comportamiento específico.
 * El campo nombre_lugar, direccion y ciudad se rellenan mediante JOIN con la tabla lugar en el DAO.
 */
public abstract class Evento {

    // Tipo de evento para discriminar el subtipo concreto
    private EventoEnum tipo_evento;
    // Identificadores de la entidad y del lugar asociado
    private int id, id_lugar, aforo_maximo;
    // Datos propios del evento; nombre_lugar, direccion y ciudad vienen del JOIN con lugar
    private String nombre, descripcion, nombre_lugar, direccion, ciudad, ruta_imagen;
    // Fecha de celebración del evento
    private LocalDate fecha;

    /** Constructor vacío requerido por Spring y los DAOs. */
    public Evento() {
    }

    /** Constructor completo con todos los campos del evento base. */
    public Evento(EventoEnum tipo_evento, int id, int id_lugar, int aforo_maximo, String nombre, String descripcion, String nombre_lugar, String direccion, String ciudad, String ruta_imagen, LocalDate fecha) {
        this.tipo_evento = tipo_evento;
        this.id = id;
        this.id_lugar = id_lugar;
        this.aforo_maximo = aforo_maximo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.nombre_lugar = nombre_lugar;
        this.direccion = direccion;
        this.ciudad = ciudad;
        this.ruta_imagen = ruta_imagen;
        this.fecha = fecha;
    }

    // --- Getters y Setters ---

    /** Devuelve la ruta (nombre de archivo) de la imagen del evento en la carpeta uploads. */
    public String getRuta_imagen() { return ruta_imagen; }
    public void setRuta_imagen(String ruta_imagen) { this.ruta_imagen = ruta_imagen; }

    /** Devuelve el tipo de evento (concierto, teatro o museo). */
    public EventoEnum getTipo_evento() { return tipo_evento; }
    public void setTipo_evento(EventoEnum tipo_evento) { this.tipo_evento = tipo_evento; }

    /** Devuelve el identificador único del evento. */
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    /** Devuelve el id del lugar donde se celebra el evento. */
    public int getId_lugar() { return id_lugar; }
    public void setId_lugar(int id_lugar) { this.id_lugar = id_lugar; }

    /** Devuelve el aforo máximo del evento. */
    public int getAforo_maximo() { return aforo_maximo; }
    public void setAforo_maximo(int aforo_maximo) { this.aforo_maximo = aforo_maximo; }

    /** Devuelve el nombre del evento. */
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    /** Devuelve la descripción del evento. */
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    /** Devuelve el nombre del lugar obtenido mediante JOIN con la tabla lugar. */
    public String getNombre_lugar() { return nombre_lugar; }
    public void setNombre_lugar(String nombre_lugar) { this.nombre_lugar = nombre_lugar; }

    /** Devuelve la dirección del lugar obtenida mediante JOIN. */
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    /** Devuelve la ciudad del lugar obtenida mediante JOIN. */
    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    /** Devuelve la fecha de celebración del evento. */
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
}
