package com.ilerna.novaticket.service;

import com.ilerna.novaticket.model.Usuario;
import com.ilerna.novaticket.model.UsuarioEnum;
import com.ilerna.novaticket.repository.UsuarioDAO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioDAO usuarioDAO;

    public UsuarioService(@Qualifier("usuarioDAOJdbc") UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    public void guardarUsuario(Usuario usuario) {
        usuarioDAO.guardar(usuario);
    }

    public void actualizarUsuario(Usuario usuario) {
        usuarioDAO.actualizar(usuario);
    }

    public void eliminarUsuario(int id) {
        usuarioDAO.eliminar(id);
    }

    public Usuario obtenerUsuarioPorId(int id) {
        return usuarioDAO.obtenerPorId(id);
    }

    public List<Usuario> listarTodosLosUsuarios() {
        return usuarioDAO.listarTodos();
    }

    public Integer obtenerIdClientePorDefecto() {
        for (Usuario usuario : listarTodosLosUsuarios()) {
            if (usuario.getTipo_usuario() == UsuarioEnum.cliente) {
                return usuario.getId();
            }
        }
        return null;
    }
}
