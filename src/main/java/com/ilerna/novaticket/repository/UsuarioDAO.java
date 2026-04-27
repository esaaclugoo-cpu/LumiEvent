package com.ilerna.novaticket.repository;

import com.ilerna.novaticket.model.Usuario;
import java.util.List;

/**
 * Interfaz DAO para la entidad Usuario.
 * Define las operaciones CRUD básicas y la búsqueda por email para la autenticación.
 */
public interface UsuarioDAO {

    /** Inserta un nuevo usuario y actualiza su id generado. */
    void guardar(Usuario usuario);

    /** Actualiza todos los campos de un usuario existente. */
    void actualizar(Usuario usuario);

    /** Elimina el usuario con el id indicado. */
    void eliminar(int id);

    /** Devuelve el usuario con el id indicado, o null si no existe. */
    Usuario obtenerPorId(int id);

    /**
     * Devuelve el usuario cuyo email coincida (sin distinguir mayúsculas).
     * Se usa en el proceso de autenticación y validación de registro.
     */
    Usuario obtenerPorEmail(String email);

    /** Devuelve la lista completa de usuarios ordenada por nombre. */
    List<Usuario> listarTodos();

}
