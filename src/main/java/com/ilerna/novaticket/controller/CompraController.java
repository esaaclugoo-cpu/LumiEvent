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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Controlador CRUD de compras del panel de administración.
 * Permite al admin listar, crear, editar y eliminar registros de compra.
 */
@Controller
public class CompraController {

    private final CompraService compraService;
    private final UsuarioService usuarioService;

    /**
     * Constructor con inyección de los servicios de compras y usuarios.
     */
    @Autowired
    public CompraController(CompraService compraService, UsuarioService usuarioService) {
        this.compraService = compraService;
        this.usuarioService = usuarioService;
    }

    /**
     * Lista todas las compras de la base de datos junto con un mapa id -> nombre del usuario comprador.
     */
    @GetMapping("/compras")
    public String listarCompras(Model model) {
        model.addAttribute("compras", compraService.listarTodasLasCompras());
        // Construye mapa de usuarios para mostrar nombre y email en la tabla
        Map<Integer, String> usuarios = new LinkedHashMap<>();
        usuarioService.listarTodosLosUsuarios().forEach(usuario ->
                usuarios.put(usuario.getId(), usuario.getNombre() + " (" + usuario.getEmail() + ")"));
        model.addAttribute("usuariosMap", usuarios);
        return "crudCompra";
    }

    /**
     * Muestra el formulario vacío para crear una nueva compra.
     * Carga la lista de usuarios disponibles para el selector.
     */
    @GetMapping("/compras/nuevo")
    public String mostrarFormulario(Model model) {
        model.addAttribute("compra", new Compra());
        model.addAttribute("usuarios", usuarioService.listarTodosLosUsuarios());
        return "formCompra";
    }

    /**
     * Procesa el formulario de creación o actualización de una compra.
     * Valida que no haya errores de binding y que el usuario sea válido antes de persistir.
     */
    @PostMapping("/compras/guardar")
    public String guardarCompra(@ModelAttribute Compra compra, BindingResult bindingResult, Model model) {
        // Verifica errores de validación de Spring MVC y que el id de usuario sea positivo
        if (bindingResult.hasErrors() || compra.getId_usuario() <= 0) {
            model.addAttribute("usuarios", usuarioService.listarTodosLosUsuarios());
            model.addAttribute("errorMensaje", "Revisa los datos del formulario. La fecha debe tener formato valido (yyyy-MM-ddTHH:mm).");
            return "formCompra";
        }

        // Si la compra tiene id, es una actualización; si no, es una inserción nueva
        if (compra.getId() > 0) {
            compraService.actualizarCompra(compra);
        } else {
            compraService.guardarCompra(compra);
        }
        return "redirect:/compras";
    }

    /**
     * Muestra el formulario de edición de una compra existente buscada por su id.
     * Redirige al listado si la compra no existe.
     */
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

    /**
     * Procesa la actualización de una compra existente.
     * Valida que los ids de compra y usuario sean válidos y que no haya errores de binding.
     */
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

    /**
     * Elimina la compra con el id indicado y redirige al listado.
     */
    @GetMapping("/compras/eliminar/{id}")
    public String eliminarCompra(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            compraService.eliminarCompra(id);
            redirectAttributes.addFlashAttribute("okMensaje", "Compra eliminada correctamente.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMensaje", "No se pudo eliminar la compra.");
        }
        return "redirect:/compras";
    }
}
