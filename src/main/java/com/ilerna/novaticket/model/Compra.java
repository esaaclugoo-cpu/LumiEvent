package com.ilerna.novaticket.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Representa una compra realizada por un usuario.
 * Agrupa múltiples tickets bajo un id de compra, con la fecha y el total pagado.
 * Una compra con total=0 es una compra borrador generada automáticamente para tickets de admin.
 */
public class Compra {

    // id único de la compra; id_usuario referencia al usuario que realizó la compra
    private int id, id_usuario;
    // Fecha y hora exactas en que se realizó la compra
    private LocalDateTime fecha;
    // Importe total de la compra; 0 indica compra borrador (sin pago real)
    private BigDecimal total;

    /** Constructor vacío requerido por el DAO y por Spring MVC. */
    public Compra() {}

    /** Constructor completo con todos los campos. */
    public Compra(int id, int id_usuario, LocalDateTime fecha, BigDecimal total) {
        this.id = id;
        this.id_usuario = id_usuario;
        this.fecha = fecha;
        this.total = total;
    }

    /** Devuelve el identificador único de la compra. */
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    /** Devuelve el id del usuario que realizó esta compra. */
    public int getId_usuario() { return id_usuario; }
    public void setId_usuario(int id_usuario) { this.id_usuario = id_usuario; }

    /** Devuelve la fecha y hora de la compra. */
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    /** Devuelve el importe total pagado en esta compra. */
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
}
