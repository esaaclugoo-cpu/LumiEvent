package com.ilerna.novaticket.service;

import com.ilerna.novaticket.model.Lugar;
import com.ilerna.novaticket.repository.LugarDAO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LugarService {

    private final LugarDAO lugarDAO;

    public LugarService(@Qualifier("lugarDAOJdbc") LugarDAO lugarDAO) {
        this.lugarDAO = lugarDAO;
    }

    public void guardarLugar(Lugar lugar) {
        lugarDAO.guardar(lugar);
    }

    public void actualizarLugar(Lugar lugar) {
        lugarDAO.actualizar(lugar);
    }

    public void eliminarLugar(int id) {
        lugarDAO.eliminar(id);
    }

    public Lugar obtenerLugarPorId(int id) {
        return lugarDAO.obtenerPorId(id);
    }

    public List<Lugar> listarTodosLosLugares() {
        return lugarDAO.listarTodos();
    }
}

