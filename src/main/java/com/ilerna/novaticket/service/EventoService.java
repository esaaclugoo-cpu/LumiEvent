package com.ilerna.novaticket.service;


import com.ilerna.novaticket.model.Evento;
import com.ilerna.novaticket.repository.EventoDAO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventoService {

    private final EventoDAO eventoDAO;

    public EventoService(@Qualifier("eventoDAOJdbc") EventoDAO eventoDAO) {
        this.eventoDAO = eventoDAO;
    }

    public void guardarEvento(Evento evento) {
        eventoDAO.guardar(evento);
    }

    public void actualizarEvento(Evento evento) {
        eventoDAO.actualizar(evento);
    }

    public void eliminarEvento(int id) {
        eventoDAO.eliminar(id);
    }

    public Evento obtenerEventoPorId(int id) {
        return eventoDAO.obtenerPorId(id);
    }

    public List<Evento> listarTodosLosEventos() {
        return eventoDAO.listarTodos();
    }


}
