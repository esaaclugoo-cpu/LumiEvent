package com.ilerna.novaticket.model;

/**
 * Representa un lugar físico donde se celebran los eventos (sala, teatro, auditorio, etc.).
 * Contiene el nombre, la dirección y la ciudad del recinto.
 */
public class Lugar {

    // id único del lugar en la base de datos
    private int id;
    private String nombre;
    // Dirección postal del recinto
    private String direccion;
    // Ciudad donde está ubicado el lugar
    private String ciudad;

    /** Constructor vacío requerido por el DAO y por Spring MVC. */
    public Lugar() {}

    /** Constructor completo con todos los campos. */
    public Lugar(int id, String nombre, String direccion, String ciudad) {
        this.id = id;
        this.nombre = nombre;
        this.direccion = direccion;
        this.ciudad = ciudad;
    }

    /** Devuelve el identificador único del lugar. */
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    /** Devuelve el nombre del lugar (p.ej. "Palau Sant Jordi"). */
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    /** Devuelve la dirección postal del lugar. */
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    /** Devuelve la ciudad donde está ubicado el lugar. */
    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }
}
