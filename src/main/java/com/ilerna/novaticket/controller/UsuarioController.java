package com.ilerna.novaticket.controller;

import com.ilerna.novaticket.model.Usuario;
import com.ilerna.novaticket.model.UsuarioEnum;
import com.ilerna.novaticket.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controlador CRUD de usuarios del panel de administración.
 * Permite al admin listar, crear, editar y eliminar usuarios con su rol (cliente o admin).
 */
@Controller
public class UsuarioController {

    private final UsuarioService usuarioService;

    /**
     * Constructor con inyección del servicio de usuarios.
     */
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     * Lista todos los usuarios registrados en el sistema.
     */
    @GetMapping("/usuarios")
    public String listarUsuarios(Model model) {
        model.addAttribute("usuarios", usuarioService.listarTodosLosUsuarios());
        return "crudUsuario";
    }

    /**
     * Muestra el formulario vacío para crear un nuevo usuario.
     * El tipo de usuario por defecto es cliente.
     */
    @GetMapping("/usuarios/nuevo")
    public String mostrarFormulario(Model model) {
        Usuario usuario = new Usuario();
        // Preselecciona el rol cliente como valor por defecto en el selector
        usuario.setTipo_usuario(UsuarioEnum.cliente);
        model.addAttribute("usuario", usuario);
        model.addAttribute("tiposUsuario", UsuarioEnum.values());
        return "formUsuario";
    }

    /**
     * Procesa el formulario de creación o actualización de un usuario.
     * Asigna tipo cliente si no se selecciona ninguno y valida que todos los campos obligatorios estén rellenos.
     */
    @PostMapping("/usuarios/guardar")
    public String guardarUsuario(@ModelAttribute Usuario usuario, Model model) {
        // Si no se seleccionó ningún tipo, asigna cliente por defecto
        if (usuario.getTipo_usuario() == null) {
            usuario.setTipo_usuario(UsuarioEnum.cliente);
        }

        // Valida que nombre, email y contraseña no estén vacíos
        if (usuario.getNombre() == null || usuario.getNombre().isBlank()
                || usuario.getEmail() == null || usuario.getEmail().isBlank()
                || usuario.getPassword() == null || usuario.getPassword().isBlank()) {
            model.addAttribute("usuario", usuario);
            model.addAttribute("tiposUsuario", UsuarioEnum.values());
            model.addAttribute("errorMensaje", "Todos los campos son obligatorios.");
            return "formUsuario";
        }

        // Si tiene id es una edición, si no es una inserción nueva
        if (usuario.getId() > 0) {
            usuarioService.actualizarUsuario(usuario);
        } else {
            usuarioService.guardarUsuario(usuario);
        }

        return "redirect:/usuarios";
    }

    /**
     * Muestra el formulario de edición de un usuario existente buscado por su id.
     * Redirige al listado si el usuario no existe.
     */
    @GetMapping("/usuarios/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable int id, Model model) {
        Usuario usuario = usuarioService.obtenerUsuarioPorId(id);
        if (usuario == null) {
            return "redirect:/usuarios";
        }
        model.addAttribute("usuario", usuario);
        model.addAttribute("tiposUsuario", UsuarioEnum.values());
        return "formUsuario";
    }

    /**
     * Elimina el usuario con el id indicado y redirige al listado.
     */
    @GetMapping("/usuarios/eliminar/{id}")
    public String eliminarUsuario(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            usuarioService.eliminarUsuario(id);
            redirectAttributes.addFlashAttribute("okMensaje", "Usuario eliminado correctamente.");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMensaje", e.getMessage());
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMensaje", "No se pudo eliminar el usuario.");
        }
        return "redirect:/usuarios";
    }
}
