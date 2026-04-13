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

    public Usuario obtenerUsuarioPorEmail(String email) {
        return usuarioDAO.obtenerPorEmail(email);
    }

    public Usuario autenticar(String email, String password) {
        if (email == null || email.isBlank()) {
            return null;
        }
        String emailNormalizado = email.trim().toLowerCase();
        Usuario usuario = usuarioDAO.obtenerPorEmail(emailNormalizado);
        if (usuario == null || password == null) {
            return null;
        }
        return password.equals(usuario.getPassword()) ? usuario : null;
    }

    public String registrarCliente(String nombre, String email, String password) {
        if (nombre == null || nombre.isBlank() || email == null || email.isBlank() || password == null || password.isBlank()) {
            return "Debes completar todos los campos.";
        }

        String emailNormalizado = email.trim().toLowerCase();

        for (Usuario existente : listarTodosLosUsuarios()) {
            if (existente.getEmail() != null && existente.getEmail().trim().equalsIgnoreCase(emailNormalizado)) {
                return "Ese email ya esta registrado.";
            }
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(nombre.trim());
        usuario.setEmail(emailNormalizado);
        usuario.setPassword(password);
        usuario.setTipo_usuario(UsuarioEnum.cliente);
        usuarioDAO.guardar(usuario);
        return null;
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
