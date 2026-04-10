package com.ilerna.novaticket.service;

import com.ilerna.novaticket.model.Ticket;
import com.ilerna.novaticket.repository.TicketDAO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TicketService {

    private final TicketDAO ticketDAO;

    public TicketService(@Qualifier("ticketDAOJdbc") TicketDAO ticketDAO) {
        this.ticketDAO = ticketDAO;
    }

    public void guardarTicket(Ticket ticket) {
        ticketDAO.guardar(ticket);
    }

    public void actualizarTicket(Ticket ticket) {
        ticketDAO.actualizar(ticket);
    }

    public void eliminarTicket(int id) {
        ticketDAO.eliminar(id);
    }

    public Ticket obtenerTicketPorId(int id) {
        return ticketDAO.obtenerPorId(id);
    }

    public List<Ticket> listarTodosLosTickets() {
        return ticketDAO.listarTodos();
    }

    public int obtenerCantidadVendidaPorEvento(int idEvento) {
        return ticketDAO.sumarCantidadPorEvento(idEvento);
    }

    public int obtenerCantidadVendidaPorEventoYTipo(int idEvento, String tipo) {
        if (tipo == null || tipo.isBlank()) {
            return 0;
        }
        return ticketDAO.sumarCantidadPorEventoYTipo(idEvento, tipo);
    }
}
