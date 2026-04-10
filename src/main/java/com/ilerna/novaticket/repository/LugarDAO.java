package com.ilerna.novaticket.repository;

import com.ilerna.novaticket.model.Lugar;

import java.util.List;

public interface LugarDAO {

    void guardar(Lugar lugar);

    void actualizar(Lugar lugar);

    void eliminar(int id);

    Lugar obtenerPorId(int id);

    List<Lugar> listarTodos();
}

