package com.ilerna.novaticket.model;

public class Usuario {

    private int id;
    private String nombre, email, password;
    private UsuarioEnum tipo_usuario;

    public Usuario() {
    }

    public Usuario(int id, String nombre, String email, String password, UsuarioEnum tipo_usuario) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.password = password;
        this.tipo_usuario = tipo_usuario;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UsuarioEnum getTipo_usuario() {
        return tipo_usuario;
    }

    public void setTipo_usuario(UsuarioEnum tipo_usuario) {
        this.tipo_usuario = tipo_usuario;
    }
}
