package com.ilerna.novaticket.model;

/**
 * Representa un asiento físico dentro de un lugar.
 * Cada asiento pertenece a un lugar (id_lugar), tiene una fila, número y zona (general, vip o premium).
 */
public class Asiento {

    // id único del asiento; id_lugar referencia a la tabla lugar
    private int id, id_lugar, numero_asiento;
    // fila: letra o código de la fila (p.ej. "A", "B"); zona: tipo de asiento
    private String fila, zona;

    /** Constructor vacío requerido por el DAO y por Spring MVC. */
    public Asiento() {}

    /** Constructor completo con todos los campos. */
    public Asiento(int id, int id_lugar, int numero_asiento, String fila, String zona) {
        this.id = id;
        this.id_lugar = id_lugar;
        this.numero_asiento = numero_asiento;
        this.fila = fila;
        this.zona = zona;
    }

    /** Devuelve el identificador único del asiento. */
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    /** Devuelve el id del lugar al que pertenece el asiento. */
    public int getId_lugar() { return id_lugar; }
    public void setId_lugar(int id_lugar) { this.id_lugar = id_lugar; }

    /** Devuelve el número de asiento dentro de la fila. */
    public int getNumero_asiento() { return numero_asiento; }
    public void setNumero_asiento(int numero_asiento) { this.numero_asiento = numero_asiento; }

    /** Devuelve la fila del asiento (letra o código). */
    public String getFila() { return fila; }
    public void setFila(String fila) { this.fila = fila; }

    /** Devuelve la zona del asiento: general, vip o premium. */
    public String getZona() { return zona; }
    public void setZona(String zona) { this.zona = zona; }
}
