package com.ilerna.novaticket.repository;

import com.ilerna.novaticket.model.Usuario;

import java.util.List;

public interface UsuarioDAO {

    void guardar(Usuario usuario);

    void actualizar(Usuario usuario);

    void eliminar(int id);

    Usuario obtenerPorId(int id);

    List<Usuario> listarTodos();
}


