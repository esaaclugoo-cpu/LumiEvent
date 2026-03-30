package com.ilerna.novaticket.model;

public class Concierto extends Evento {

    private int id_evento, duracion_minutos;
    private String artista_principal, genero_musical;

    public Concierto() {
    }

    public Concierto(int id_evento, int duracion_minutos, String artista_principal, String genero_musical) {
        this.id_evento = id_evento;
        this.duracion_minutos = duracion_minutos;
        this.artista_principal = artista_principal;
        this.genero_musical = genero_musical;
    }

    public int getId_evento() {
        return id_evento;
    }

    public void setId_evento(int id_evento) {
        this.id_evento = id_evento;
    }

    public int getDuracion_minutos() {
        return duracion_minutos;
    }

    public void setDuracion_minutos(int duracion_minutos) {
        this.duracion_minutos = duracion_minutos;
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
}
