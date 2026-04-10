package com.ilerna.novaticket.controller;

import com.ilerna.novaticket.model.Asiento;
import com.ilerna.novaticket.model.Lugar;
import com.ilerna.novaticket.service.AsientoService;
import com.ilerna.novaticket.service.LugarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.LinkedHashMap;
import java.util.Map;

@Controller
public class AsientoController {

    private final AsientoService asientoService;
    private final LugarService lugarService;

    @Autowired
    public AsientoController(AsientoService asientoService, LugarService lugarService) {
        this.asientoService = asientoService;
        this.lugarService = lugarService;
    }

    @GetMapping("/asientos")
    public String listarAsientos(Model model) {
        model.addAttribute("asientos", asientoService.listarTodosLosAsientos());
        model.addAttribute("lugares", obtenerLugaresRegistrados());
        return "crudAsiento";
    }

    @GetMapping("/asientos/nuevo")
    public String mostrarFormulario(Model model) {
        model.addAttribute("asiento", new Asiento());
        model.addAttribute("lugares", obtenerLugaresRegistrados());
        return "formAsiento";
    }

    @PostMapping("/asientos/guardar")
    public String guardarAsiento(@ModelAttribute Asiento asiento) {
        if (asiento.getId_lugar() <= 0) {
            return asiento.getId() > 0 ? "redirect:/asientos/editar/" + asiento.getId() : "redirect:/asientos/nuevo";
        }

        if (asiento.getId() > 0) {
            asientoService.actualizarAsiento(asiento);
        } else {
            asientoService.guardarAsiento(asiento);
        }
        return "redirect:/asientos";
    }

    @GetMapping("/asientos/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable int id, Model model) {
        Asiento asiento = asientoService.obtenerAsientoPorId(id);
        if (asiento == null) {
            return "redirect:/asientos";
        }
        model.addAttribute("asiento", asiento);
        model.addAttribute("lugares", obtenerLugaresRegistrados());
        return "formAsiento";
    }

    @PostMapping("/asientos/actualizar")
    public String actualizarAsiento(@ModelAttribute Asiento asiento) {
        if (asiento.getId_lugar() <= 0) {
            return "redirect:/asientos/editar/" + asiento.getId();
        }
        asientoService.actualizarAsiento(asiento);
        return "redirect:/asientos";
    }

    @GetMapping("/asientos/eliminar/{id}")
    public String eliminarAsiento(@PathVariable int id) {
        asientoService.eliminarAsiento(id);
        return "redirect:/asientos";
    }

    private Map<Integer, String> obtenerLugaresRegistrados() {
        Map<Integer, String> lugares = new LinkedHashMap<>();
        for (Lugar lugar : lugarService.listarTodosLosLugares()) {
            lugares.put(lugar.getId(), lugar.getNombre());
        }
        return lugares;
    }
}
