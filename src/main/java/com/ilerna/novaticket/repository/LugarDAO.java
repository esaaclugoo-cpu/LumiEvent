package com.ilerna.novaticket.repository;

import com.ilerna.novaticket.model.Lugar;
import java.util.List;

/**
 * Interfaz DAO para la entidad Lugar.
 * Define las operaciones CRUD básicas sobre la tabla lugar.
 */
public interface LugarDAO {

    /** Inserta un nuevo lugar y actualiza su id generado. */
    void guardar(Lugar lugar);

    /** Actualiza todos los campos de un lugar existente. */
    void actualizar(Lugar lugar);

    /** Elimina el lugar con el id indicado. */
    void eliminar(int id);

    /** Devuelve el lugar con el id indicado, o null si no existe. */
    Lugar obtenerPorId(int id);

    /** Devuelve la lista completa de lugares ordenada por nombre. */
    List<Lugar> listarTodos();
}
