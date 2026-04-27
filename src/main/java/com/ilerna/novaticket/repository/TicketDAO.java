package com.ilerna.novaticket.repository;

import com.ilerna.novaticket.model.Ticket;
import java.util.List;

/**
 * Interfaz DAO para la entidad Ticket.
 * Además del CRUD básico, define consultas agregadas para contabilizar tickets vendidos por evento.
 */
public interface TicketDAO {

    /** Inserta un nuevo ticket y actualiza su id generado. */
    void guardar(Ticket ticket);

    /** Actualiza todos los campos de un ticket existente. */
    void actualizar(Ticket ticket);

    /** Elimina el ticket con el id indicado. */
    void eliminar(int id);

    /** Devuelve el ticket con el id indicado, o null si no existe. */
    Ticket obtenerPorId(int id);

    /** Devuelve la lista completa de tickets ordenada por id descendente. */
    List<Ticket> listarTodos();

    /**
     * Suma la cantidad total de tickets vendidos (compra.total > 0) para un evento dado.
     * Se usa para calcular el aforo disponible restante.
     */
    int sumarCantidadPorEvento(int idEvento);

    /**
     * Suma la cantidad de tickets vendidos para un evento filtrando por tipo (General, VIP, Premium).
     * Permite conocer la disponibilidad por zona.
     */
    int sumarCantidadPorEventoYTipo(int idEvento, String tipo);
}
