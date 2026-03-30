package com.ilerna.novaticket.repository;



import com.ilerna.novaticket.model.Evento;

import java.util.List;

public interface EventoDAO {

    void guardar(Evento evento);
    void actualizar(Evento evento);
    void eliminar(int id);
    Evento obtenerPorId(int id);
    List<Evento> listarTodos();
}
