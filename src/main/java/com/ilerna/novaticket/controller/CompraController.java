package com.ilerna.novaticket.controller;

import com.ilerna.novaticket.model.Compra;
import com.ilerna.novaticket.service.CompraService;
import com.ilerna.novaticket.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.LinkedHashMap;
import java.util.Map;

@Controller
public class CompraController {

    private final CompraService compraService;
    private final UsuarioService usuarioService;

    @Autowired
    public CompraController(CompraService compraService, UsuarioService usuarioService) {
        this.compraService = compraService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/compras")
    public String listarCompras(Model model) {
        model.addAttribute("compras", compraService.listarTodasLasCompras());
        Map<Integer, String> usuarios = new LinkedHashMap<>();
        usuarioService.listarTodosLosUsuarios().forEach(usuario ->
                usuarios.put(usuario.getId(), usuario.getNombre() + " (" + usuario.getEmail() + ")"));
        model.addAttribute("usuariosMap", usuarios);
        return "crudCompra";
    }

    @GetMapping("/compras/nuevo")
    public String mostrarFormulario(Model model) {
        model.addAttribute("compra", new Compra());
        model.addAttribute("usuarios", usuarioService.listarTodosLosUsuarios());
        return "formCompra";
    }

    @PostMapping("/compras/guardar")
    public String guardarCompra(@ModelAttribute Compra compra, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors() || compra.getId_usuario() <= 0) {
            model.addAttribute("usuarios", usuarioService.listarTodosLosUsuarios());
            model.addAttribute("errorMensaje", "Revisa los datos del formulario. La fecha debe tener formato valido (yyyy-MM-ddTHH:mm).");
            return "formCompra";
        }

        if (compra.getId() > 0) {
            compraService.actualizarCompra(compra);
        } else {
            compraService.guardarCompra(compra);
        }
        return "redirect:/compras";
    }

    @GetMapping("/compras/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable int id, Model model) {
        Compra compra = compraService.obtenerCompraPorId(id);
        if (compra == null) {
            return "redirect:/compras";
        }
        model.addAttribute("compra", compra);
        model.addAttribute("usuarios", usuarioService.listarTodosLosUsuarios());
        return "formCompra";
    }

    @PostMapping("/compras/actualizar")
    public String actualizarCompra(@ModelAttribute Compra compra, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors() || compra.getId() <= 0 || compra.getId_usuario() <= 0) {
            model.addAttribute("usuarios", usuarioService.listarTodosLosUsuarios());
            model.addAttribute("errorMensaje", "No se pudo actualizar. Verifica la fecha/hora y los campos obligatorios.");
            return "formCompra";
        }
        compraService.actualizarCompra(compra);
        return "redirect:/compras";
    }

    @GetMapping("/compras/eliminar/{id}")
    public String eliminarCompra(@PathVariable int id) {
        compraService.eliminarCompra(id);
        return "redirect:/compras";
    }
}
