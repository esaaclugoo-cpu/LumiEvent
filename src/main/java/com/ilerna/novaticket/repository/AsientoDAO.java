package com.ilerna.novaticket.repository;

import com.ilerna.novaticket.model.Asiento;
import java.util.List;

/**
 * Interfaz DAO para la entidad Asiento.
 * Define las operaciones CRUD básicas sobre la tabla asiento.
 */
public interface AsientoDAO {

    /** Inserta un nuevo asiento y actualiza su id generado. */
    void guardar(Asiento asiento);

    /** Actualiza todos los campos de un asiento existente. */
    void actualizar(Asiento asiento);

    /** Elimina el asiento con el id indicado. */
    void eliminar(int id);

    /** Devuelve el asiento con el id indicado, o null si no existe. */
    Asiento obtenerPorId(int id);

    /** Devuelve la lista completa de asientos. */
    List<Asiento> listarTodos();
}
