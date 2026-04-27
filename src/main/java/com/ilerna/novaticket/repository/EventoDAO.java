package com.ilerna.novaticket.repository;

import com.ilerna.novaticket.model.Evento;
import java.util.List;

/**
 * Interfaz DAO (Data Access Object) para la entidad Evento.
 * Define las operaciones CRUD básicas que debe implementar cualquier repositorio de eventos.
 */
public interface EventoDAO {

    /** Inserta un nuevo evento en la base de datos y actualiza su id generado. */
    void guardar(Evento evento);

    /** Actualiza todos los campos de un evento existente identificado por su id. */
    void actualizar(Evento evento);

    /** Elimina el evento con el id indicado de la base de datos. */
    void eliminar(int id);

    /** Devuelve el evento con el id indicado, o null si no existe. */
    Evento obtenerPorId(int id);

    /** Devuelve la lista completa de eventos con los datos de lugar resueltos por JOIN. */
    List<Evento> listarTodos();
}
