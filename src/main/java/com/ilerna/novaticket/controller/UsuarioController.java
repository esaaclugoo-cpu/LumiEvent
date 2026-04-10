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

@Controller
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/usuarios")
    public String listarUsuarios(Model model) {
        model.addAttribute("usuarios", usuarioService.listarTodosLosUsuarios());
        return "crudUsuario";
    }

    @GetMapping("/usuarios/nuevo")
    public String mostrarFormulario(Model model) {
        Usuario usuario = new Usuario();
        usuario.setTipo_usuario(UsuarioEnum.cliente);
        model.addAttribute("usuario", usuario);
        model.addAttribute("tiposUsuario", UsuarioEnum.values());
        return "formUsuario";
    }

    @PostMapping("/usuarios/guardar")
    public String guardarUsuario(@ModelAttribute Usuario usuario, Model model) {
        if (usuario.getTipo_usuario() == null) {
            usuario.setTipo_usuario(UsuarioEnum.cliente);
        }

        if (usuario.getNombre() == null || usuario.getNombre().isBlank()
                || usuario.getEmail() == null || usuario.getEmail().isBlank()
                || usuario.getPassword() == null || usuario.getPassword().isBlank()) {
            model.addAttribute("usuario", usuario);
            model.addAttribute("tiposUsuario", UsuarioEnum.values());
            model.addAttribute("errorMensaje", "Todos los campos son obligatorios.");
            return "formUsuario";
        }

        if (usuario.getId() > 0) {
            usuarioService.actualizarUsuario(usuario);
        } else {
            usuarioService.guardarUsuario(usuario);
        }

        return "redirect:/usuarios";
    }

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

    @GetMapping("/usuarios/eliminar/{id}")
    public String eliminarUsuario(@PathVariable int id) {
        usuarioService.eliminarUsuario(id);
        return "redirect:/usuarios";
    }
}

