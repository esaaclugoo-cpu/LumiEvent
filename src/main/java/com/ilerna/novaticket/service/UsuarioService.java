package com.ilerna.novaticket.service;

import com.ilerna.novaticket.model.Usuario;
import com.ilerna.novaticket.model.UsuarioEnum;
import com.ilerna.novaticket.repository.UsuarioDAO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio de lógica de negocio para los usuarios.
 * Gestiona el CRUD de usuarios, autenticación y registro de nuevos clientes.
 */
@Service
public class UsuarioService {

    private final UsuarioDAO usuarioDAO;

    /**
     * Constructor con inyección del DAO de usuarios.
     */
    public UsuarioService(@Qualifier("usuarioDAOJdbc") UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    /**
     * Persiste un nuevo usuario o actualiza el existente si ya tiene id asignado.
     */
    public void guardarUsuario(Usuario usuario) {
        usuarioDAO.guardar(usuario);
    }

    /**
     * Actualiza los datos de un usuario existente en la base de datos.
     */
    public void actualizarUsuario(Usuario usuario) {
        usuarioDAO.actualizar(usuario);
    }

    /**
     * Elimina el usuario con el id indicado de la base de datos.
     */
    public void eliminarUsuario(int id) {
        usuarioDAO.eliminar(id);
    }

    /**
     * Obtiene un usuario por su id. Devuelve null si no existe.
     */
    public Usuario obtenerUsuarioPorId(int id) {
        return usuarioDAO.obtenerPorId(id);
    }

    /**
     * Devuelve la lista completa de usuarios ordenada por nombre.
     */
    public List<Usuario> listarTodosLosUsuarios() {
        return usuarioDAO.listarTodos();
    }

    /**
     * Obtiene un usuario por su dirección de email.
     */
    public Usuario obtenerUsuarioPorEmail(String email) {
        return usuarioDAO.obtenerPorEmail(email);
    }

    /**
     * Autentica un usuario comprobando que el email y la contraseña coincidan.
     * Normaliza el email a minúsculas antes de buscarlo en la base de datos.
     * Devuelve null si las credenciales no son válidas.
     */
    public Usuario autenticar(String email, String password) {
        if (email == null || email.isBlank()) {
            return null;
        }
        // Normaliza el email para evitar problemas de mayúsculas/minúsculas
        String emailNormalizado = email.trim().toLowerCase();
        Usuario usuario = usuarioDAO.obtenerPorEmail(emailNormalizado);
        if (usuario == null || password == null) {
            return null;
        }
        // Compara la contraseña directamente (sin hash)
        return password.equals(usuario.getPassword()) ? usuario : null;
    }

    /**
     * Registra un nuevo cliente validando que todos los campos estén rellenos
     * y que el email no esté ya en uso.
     *
     * @return null si el registro fue exitoso, o un mensaje de error en caso contrario.
     */
    public String registrarCliente(String nombre, String email, String password) {
        if (nombre == null || nombre.isBlank() || email == null || email.isBlank() || password == null || password.isBlank()) {
            return "Debes completar todos los campos.";
        }

        String emailNormalizado = email.trim().toLowerCase();

        // Comprueba que el email no esté registrado ya por otro usuario
        for (Usuario existente : listarTodosLosUsuarios()) {
            if (existente.getEmail() != null && existente.getEmail().trim().equalsIgnoreCase(emailNormalizado)) {
                return "Ese email ya esta registrado.";
            }
        }

        // Crea y persiste el nuevo cliente con rol cliente
        Usuario usuario = new Usuario();
        usuario.setNombre(nombre.trim());
        usuario.setEmail(emailNormalizado);
        usuario.setPassword(password);
        usuario.setTipo_usuario(UsuarioEnum.cliente);
        usuarioDAO.guardar(usuario);
        return null;
    }

    /**
     * Devuelve el id del primer usuario con rol cliente encontrado en la base de datos.
     * Se usa como usuario por defecto al crear compras borradores para tickets de admin.
     * Devuelve null si no hay ningún cliente registrado.
     */
    public Integer obtenerIdClientePorDefecto() {
        for (Usuario usuario : listarTodosLosUsuarios()) {
            if (usuario.getTipo_usuario() == UsuarioEnum.cliente) {
                return usuario.getId();
            }
        }
        return null;
    }
}
