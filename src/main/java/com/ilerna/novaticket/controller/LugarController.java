package com.ilerna.novaticket.controller;

import com.ilerna.novaticket.model.Lugar;
import com.ilerna.novaticket.service.LugarService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controlador CRUD de lugares del panel de administración.
 * Gestiona el listado, creación, edición y eliminación de lugares donde se celebran eventos.
 */
@Controller
public class LugarController {

    private final LugarService lugarService;

    /**
     * Constructor con inyección del servicio de lugares.
     */
    public LugarController(LugarService lugarService) {
        this.lugarService = lugarService;
    }

    /**
     * Lista todos los lugares almacenados en la base de datos.
     */
    @GetMapping("/lugares")
    public String listarLugares(Model model) {
        model.addAttribute("lugares", lugarService.listarTodosLosLugares());
        return "crudLugar";
    }

    /**
     * Muestra el formulario vacío para crear un nuevo lugar.
     */
    @GetMapping("/lugares/nuevo")
    public String mostrarFormulario(Model model) {
        model.addAttribute("lugar", new Lugar());
        return "formLugar";
    }

    /**
     * Procesa el formulario de creación o actualización de un lugar.
     * Si el lugar tiene id, lo actualiza; si no, lo inserta como nuevo.
     */
    @PostMapping("/lugares/guardar")
    public String guardarLugar(@ModelAttribute Lugar lugar) {
        if (lugar.getId() > 0) {
            lugarService.actualizarLugar(lugar);
        } else {
            lugarService.guardarLugar(lugar);
        }
        return "redirect:/lugares";
    }

    /**
     * Muestra el formulario de edición de un lugar existente buscado por su id.
     * Redirige al listado si el lugar no existe.
     */
    @GetMapping("/lugares/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable int id, Model model) {
        Lugar lugar = lugarService.obtenerLugarPorId(id);
        if (lugar == null) {
            return "redirect:/lugares";
        }
        model.addAttribute("lugar", lugar);
        return "formLugar";
    }

    /**
     * Elimina el lugar con el id indicado y redirige al listado.
     */
    @GetMapping("/lugares/eliminar/{id}")
    public String eliminarLugar(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            lugarService.eliminarLugar(id);
            redirectAttributes.addFlashAttribute("okMensaje", "Lugar eliminado correctamente.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMensaje", "No se pudo eliminar el lugar. Puede tener datos relacionados.");
        }
        return "redirect:/lugares";
    }
}
