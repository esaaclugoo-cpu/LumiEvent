package com.ilerna.novaticket.model;

import java.math.BigDecimal;

/**
 * Representa un ticket que da derecho a asistir a un evento.
 * Cada ticket pertenece a un evento y a una compra, y opcionalmente tiene asignado un asiento.
 * El tipo (General, VIP, Premium) determina el precio y la zona del asiento.
 */
public class Ticket {

    // id único; id_evento referencia al evento; id_compra referencia a la compra; cantidad siempre es 1 por ticket
    private int id, id_evento, id_compra, cantidad;
    // id_asiento es nullable: null indica ticket sin asiento numerado asignado
    private Integer id_asiento;
    // tipo: "General", "VIP" o "Premium"
    private String tipo;
    // Precio unitario de venta de este ticket
    private BigDecimal precio_unitario;

    /** Constructor vacío requerido por el DAO y por Spring MVC. */
    public Ticket() {}

    /** Constructor completo con todos los campos. */
    public Ticket(int id, int id_evento, Integer id_asiento, String tipo, int id_compra, int cantidad, BigDecimal precio_unitario) {
        this.id = id;
        this.id_evento = id_evento;
        this.id_asiento = id_asiento;
        this.tipo = tipo;
        this.id_compra = id_compra;
        this.cantidad = cantidad;
        this.precio_unitario = precio_unitario;
    }

    /** Devuelve el identificador único del ticket. */
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    /** Devuelve el id del evento al que da acceso este ticket. */
    public int getId_evento() { return id_evento; }
    public void setId_evento(int id_evento) { this.id_evento = id_evento; }

    /** Devuelve el id del asiento asignado, o null si no tiene asiento numerado. */
    public Integer getId_asiento() { return id_asiento; }
    public void setId_asiento(Integer id_asiento) { this.id_asiento = id_asiento; }

    /** Devuelve el tipo de ticket: General, VIP o Premium. */
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    /** Devuelve el id de la compra a la que pertenece este ticket. */
    public int getId_compra() { return id_compra; }
    public void setId_compra(int id_compra) { this.id_compra = id_compra; }

    /** Devuelve la cantidad de entradas (siempre 1 por ticket individual). */
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    /** Devuelve el precio unitario de este ticket. */
    public BigDecimal getPrecio_unitario() { return precio_unitario; }
    public void setPrecio_unitario(BigDecimal precio_unitario) { this.precio_unitario = precio_unitario; }
}
