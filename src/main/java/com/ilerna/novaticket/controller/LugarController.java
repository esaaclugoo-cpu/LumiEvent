package com.ilerna.novaticket.controller;

import com.ilerna.novaticket.model.Lugar;
import com.ilerna.novaticket.service.LugarService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class LugarController {

    private final LugarService lugarService;

    public LugarController(LugarService lugarService) {
        this.lugarService = lugarService;
    }

    @GetMapping("/lugares")
    public String listarLugares(Model model) {
        model.addAttribute("lugares", lugarService.listarTodosLosLugares());
        return "crudLugar";
    }

    @GetMapping("/lugares/nuevo")
    public String mostrarFormulario(Model model) {
        model.addAttribute("lugar", new Lugar());
        return "formLugar";
    }

    @PostMapping("/lugares/guardar")
    public String guardarLugar(@ModelAttribute Lugar lugar) {
        if (lugar.getId() > 0) {
            lugarService.actualizarLugar(lugar);
        } else {
            lugarService.guardarLugar(lugar);
        }
        return "redirect:/lugares";
    }

    @GetMapping("/lugares/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable int id, Model model) {
        Lugar lugar = lugarService.obtenerLugarPorId(id);
        if (lugar == null) {
            return "redirect:/lugares";
        }
        model.addAttribute("lugar", lugar);
        return "formLugar";
    }

    @GetMapping("/lugares/eliminar/{id}")
    public String eliminarLugar(@PathVariable int id) {
        lugarService.eliminarLugar(id);
        return "redirect:/lugares";
    }
}

