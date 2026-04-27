package com.ilerna.novaticket.service;

import com.ilerna.novaticket.model.Lugar;
import com.ilerna.novaticket.repository.LugarDAO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio de lógica de negocio para los lugares.
 * Delega todas las operaciones CRUD al repositorio LugarDAO.
 */
@Service
public class LugarService {

    private final LugarDAO lugarDAO;

    /**
     * Constructor con inyección del DAO de lugares.
     */
    public LugarService(@Qualifier("lugarDAOJdbc") LugarDAO lugarDAO) {
        this.lugarDAO = lugarDAO;
    }

    /**
     * Persiste un nuevo lugar en la base de datos.
     */
    public void guardarLugar(Lugar lugar) {
        lugarDAO.guardar(lugar);
    }

    /**
     * Actualiza los datos de un lugar existente en la base de datos.
     */
    public void actualizarLugar(Lugar lugar) {
        lugarDAO.actualizar(lugar);
    }

    /**
     * Elimina el lugar con el id indicado de la base de datos.
     */
    public void eliminarLugar(int id) {
        lugarDAO.eliminar(id);
    }

    /**
     * Obtiene un lugar por su id. Devuelve null si no existe.
     */
    public Lugar obtenerLugarPorId(int id) {
        return lugarDAO.obtenerPorId(id);
    }

    /**
     * Devuelve la lista completa de lugares ordenada por nombre.
     */
    public List<Lugar> listarTodosLosLugares() {
        return lugarDAO.listarTodos();
    }
}
