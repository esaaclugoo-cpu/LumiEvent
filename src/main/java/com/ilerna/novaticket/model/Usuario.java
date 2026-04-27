package com.ilerna.novaticket.model;

/**
 * Representa un usuario del sistema, tanto clientes como administradores.
 * El rol se determina por el campo tipo_usuario (UsuarioEnum).
 */
public class Usuario {

    // id único del usuario en la base de datos
    private int id;
    private String nombre, email, password;
    // Rol del usuario: cliente o admin
    private UsuarioEnum tipo_usuario;

    /** Constructor vacío requerido por el DAO y por Spring MVC. */
    public Usuario() {}

    /** Constructor completo con todos los campos. */
    public Usuario(int id, String nombre, String email, String password, UsuarioEnum tipo_usuario) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.password = password;
        this.tipo_usuario = tipo_usuario;
    }

    /** Devuelve el identificador único del usuario. */
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    /** Devuelve el nombre visible del usuario. */
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    /** Devuelve el email del usuario (usado como identificador de login). */
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    /** Devuelve la contraseña del usuario (almacenada en texto plano). */
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    /** Devuelve el rol del usuario: cliente o admin. */
    public UsuarioEnum getTipo_usuario() { return tipo_usuario; }
    public void setTipo_usuario(UsuarioEnum tipo_usuario) { this.tipo_usuario = tipo_usuario; }
}
