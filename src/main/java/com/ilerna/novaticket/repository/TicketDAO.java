package com.ilerna.novaticket.repository;

import com.ilerna.novaticket.model.Ticket;

import java.util.List;

public interface TicketDAO {

    void guardar(Ticket ticket);

    void actualizar(Ticket ticket);

    void eliminar(int id);

    Ticket obtenerPorId(int id);

    List<Ticket> listarTodos();

    int sumarCantidadPorEvento(int idEvento);

    int sumarCantidadPorEventoYTipo(int idEvento, String tipo);
}
