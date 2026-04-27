package com.ilerna.novaticket.service;

import com.ilerna.novaticket.model.Compra;
import com.ilerna.novaticket.repository.CompraDAO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio de lógica de negocio para las compras.
 * Delega todas las operaciones CRUD al repositorio CompraDAO.
 */
@Service
public class CompraService {

    private final CompraDAO compraDAO;

    /**
     * Constructor con inyección del DAO de compras.
     */
    public CompraService(@Qualifier("compraDAOJdbc") CompraDAO compraDAO) {
        this.compraDAO = compraDAO;
    }

    /**
     * Persiste una nueva compra en la base de datos.
     * Actualiza el id de la compra con el generado por la BD tras la inserción.
     */
    public void guardarCompra(Compra compra) {compraDAO.guardar(compra);}

    /**
     * Actualiza los datos de una compra existente en la base de datos.
     */
    public void actualizarCompra(Compra compra) {
        compraDAO.actualizar(compra);
    }

    /**
     * Elimina la compra con el id indicado de la base de datos.
     */
    public void eliminarCompra(int id) {compraDAO.eliminar(id);}

    /**
     * Obtiene una compra por su id. Devuelve null si no existe.
     */
    public Compra obtenerCompraPorId(int id) {
        return compraDAO.obtenerPorId(id);
    }

    /**
     * Devuelve la lista completa de compras almacenadas en la base de datos.
     */
    public List<Compra> listarTodasLasCompras() {
        return compraDAO.listarTodos();
    }
}
