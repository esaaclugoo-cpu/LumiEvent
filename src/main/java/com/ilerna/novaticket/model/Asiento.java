package com.ilerna.novaticket.model;

public class Asiento {

    private int id, id_lugar, numero_asiento;
    private String fila, zona;

    public Asiento() {
    }

    public Asiento(int id, int id_lugar, int numero_asiento, String fila, String zona) {
        this.id = id;
        this.id_lugar = id_lugar;
        this.numero_asiento = numero_asiento;
        this.fila = fila;
        this.zona = zona;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_lugar() {
        return id_lugar;
    }

    public void setId_lugar(int id_lugar) {
        this.id_lugar = id_lugar;
    }

    public int getNumero_asiento() {
        return numero_asiento;
    }

    public void setNumero_asiento(int numero_asiento) {
        this.numero_asiento = numero_asiento;
    }

    public String getFila() {
        return fila;
    }

    public void setFila(String fila) {
        this.fila = fila;
    }

    public String getZona() {
        return zona;
    }

    public void setZona(String zona) {
        this.zona = zona;
    }
}
