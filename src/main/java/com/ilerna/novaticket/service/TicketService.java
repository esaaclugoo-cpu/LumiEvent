package com.ilerna.novaticket.service;

import com.ilerna.novaticket.model.Ticket;
import com.ilerna.novaticket.repository.TicketDAO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio de lógica de negocio para los tickets.
 * Ofrece operaciones CRUD y consultas agregadas para calcular la cantidad vendida por evento y tipo.
 */
@Service
public class TicketService {

    private final TicketDAO ticketDAO;

    /**
     * Constructor con inyección del DAO de tickets.
     */
    public TicketService(@Qualifier("ticketDAOJdbc") TicketDAO ticketDAO) {
        this.ticketDAO = ticketDAO;
    }

    /**
     * Persiste un nuevo ticket en la base de datos.
     * Actualiza el id del ticket con el generado por la BD tras la inserción.
     */
    public void guardarTicket(Ticket ticket) {
        ticketDAO.guardar(ticket);
    }

    /**
     * Actualiza los datos de un ticket existente en la base de datos.
     */
    public void actualizarTicket(Ticket ticket) {
        ticketDAO.actualizar(ticket);
    }

    /**
     * Elimina el ticket con el id indicado de la base de datos.
     */
    public void eliminarTicket(int id) {
        ticketDAO.eliminar(id);
    }

    /**
     * Obtiene un ticket por su id. Devuelve null si no existe.
     */
    public Ticket obtenerTicketPorId(int id) {
        return ticketDAO.obtenerPorId(id);
    }

    /**
     * Devuelve la lista completa de tickets ordenada por id descendente.
     */
    public List<Ticket> listarTodosLosTickets() {
        return ticketDAO.listarTodos();
    }

    /**
     * Suma la cantidad total de tickets vendidos (compra con total > 0) para un evento.
     * Se usa para calcular el aforo disponible restante.
     */
    public int obtenerCantidadVendidaPorEvento(int idEvento) {
        return ticketDAO.sumarCantidadPorEvento(idEvento);
    }

    /**
     * Suma la cantidad de tickets vendidos para un evento filtrando además por tipo de ticket (General, VIP, Premium).
     * Devuelve 0 si el tipo es null o vacío.
     */
    public int obtenerCantidadVendidaPorEventoYTipo(int idEvento, String tipo) {
        if (tipo == null || tipo.isBlank()) {
            return 0;
        }
        return ticketDAO.sumarCantidadPorEventoYTipo(idEvento, tipo);
    }
}
