package com.ilerna.novaticket.repository;

import com.ilerna.novaticket.model.Compra;
import java.util.List;

/**
 * Interfaz DAO para la entidad Compra.
 * Define las operaciones CRUD básicas sobre la tabla compra.
 */
public interface CompraDAO {

    /** Inserta una nueva compra y actualiza su id generado. */
    void guardar(Compra compra);

    /** Actualiza todos los campos de una compra existente. */
    void actualizar(Compra compra);

    /** Elimina la compra con el id indicado. */
    void eliminar(int id);

    /** Devuelve la compra con el id indicado, o null si no existe. */
    Compra obtenerPorId(int id);

    /** Devuelve la lista completa de compras. */
    List<Compra> listarTodos();
}
