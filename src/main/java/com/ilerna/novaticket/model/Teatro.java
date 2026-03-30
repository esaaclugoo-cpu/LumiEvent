package com.ilerna.novaticket.model;

public class Teatro extends Evento {

    private int id_evento;
    private String obra, director;

    public Teatro() {
    }

    public Teatro(int id_evento, String obra, String director) {
        this.id_evento = id_evento;
        this.obra = obra;
        this.director = director;
    }

    public int getId_evento() {
        return id_evento;
    }

    public void setId_evento(int id_evento) {
        this.id_evento = id_evento;
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
}
