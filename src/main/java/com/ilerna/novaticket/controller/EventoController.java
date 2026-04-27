package com.ilerna.novaticket.controller;

import com.ilerna.novaticket.model.Concierto;
import com.ilerna.novaticket.model.Evento;
import com.ilerna.novaticket.model.EventoForm;
import com.ilerna.novaticket.model.Lugar;
import com.ilerna.novaticket.model.Museo;
import com.ilerna.novaticket.model.Teatro;
import com.ilerna.novaticket.service.EventoService;
import com.ilerna.novaticket.service.LugarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDate;

/**
 * Controlador CRUD de eventos del panel de administración.
 * Gestiona el listado, creación, edición y eliminación de eventos.
 * Soporta tres subtipos de evento mediante herencia: Concierto, Teatro y Museo.
 */
@Controller
public class EventoController {

    private final EventoService eventoService;
    private final LugarService lugarService;

    /**
     * Constructor con inyección de los servicios de eventos y lugares.
     */
    @Autowired
    public EventoController(EventoService eventoService, LugarService lugarService) {
        this.eventoService = eventoService;
        this.lugarService = lugarService;
    }

    /**
     * Muestra el panel de administración principal.
     */
    @GetMapping("/admin")
    public String mostrarAdmin() {
        return "admin";
    }

    /**
     * Lista todos los eventos con sus datos de lugar obtenidos mediante JOIN.
     */
    @GetMapping("/eventos")
    public String listarEvento(Model model) {
        model.addAttribute("eventos", eventoService.listarTodosLosEventos());
        return "crudEvento";
    }

    /**
     * Muestra el formulario vacío para crear un nuevo evento.
     * Carga la lista de lugares registrados para el selector del formulario.
     */
    @GetMapping("/eventos/nuevo")
    public String mostrarFormulario(Model model) {
        model.addAttribute("evento", new EventoForm());
        model.addAttribute("lugares", lugarService.listarTodosLosLugares());
        return "formEvento";
    }

    /**
     * Procesa el formulario de creación de un evento.
     * Valida la fecha, guarda la imagen si se sube una, construye el subtipo correcto
     * y verifica que el lugar seleccionado exista antes de persistir.
     */
    @PostMapping("/eventos/guardar")
    public String guardarEvento(@ModelAttribute EventoForm eventoForm,
                                @RequestParam(value = "imagenFile", required = false) MultipartFile imagenFile,
                                Model model) {
        if (eventoForm.getFecha() == null || eventoForm.getFecha().isBefore(LocalDate.now())) {
            model.addAttribute("evento", eventoForm);
            model.addAttribute("lugares", lugarService.listarTodosLosLugares());
            model.addAttribute("errorMensaje", "La fecha del evento debe ser hoy o posterior.");
            return "formEvento";
        }

        try {
            eventoForm.setRuta_imagen(eventoService.guardarImagen(imagenFile, eventoForm.getRuta_imagen()));
        } catch (IllegalArgumentException e) {
            model.addAttribute("evento", eventoForm);
            model.addAttribute("lugares", lugarService.listarTodosLosLugares());
            model.addAttribute("errorMensaje", e.getMessage());
            return "formEvento";
        } catch (RuntimeException e) {
            model.addAttribute("evento", eventoForm);
            model.addAttribute("lugares", lugarService.listarTodosLosLugares());
            model.addAttribute("errorMensaje", "No se pudo subir la imagen del evento.");
            return "formEvento";
        }

        Evento evento = construirEventoDesdeFormulario(eventoForm);
        if (evento == null) {
            model.addAttribute("evento", eventoForm);
            model.addAttribute("lugares", lugarService.listarTodosLosLugares());
            model.addAttribute("errorMensaje", "Debes seleccionar un tipo de evento valido.");
            return "formEvento";
        }

        if (!aplicarLugarSeleccionado(eventoForm.getId_lugar(), evento, model, eventoForm)) {
            return "formEvento";
        }

        eventoService.guardarEvento(evento);
        return "redirect:/eventos";
    }

    /**
     * Muestra el formulario de edición precargado con los datos del evento existente.
     * Rellena los campos específicos del subtipo (Concierto, Teatro o Museo).
     */
    @GetMapping("/eventos/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable int id, Model model) {
        Evento evento = eventoService.obtenerEventoPorId(id);
        if (evento == null) {
            return "redirect:/eventos";
        }

        EventoForm form = new EventoForm();
        form.setId(evento.getId());
        form.setNombre(evento.getNombre());
        form.setDescripcion(evento.getDescripcion());
        form.setFecha(evento.getFecha());
        form.setAforo_maximo(evento.getAforo_maximo());
        form.setTipo_evento(evento.getTipo_evento());
        form.setId_lugar(evento.getId_lugar());
        form.setRuta_imagen(evento.getRuta_imagen());

        if (evento instanceof Concierto) {
            Concierto c = (Concierto) evento;
            form.setArtista_principal(c.getArtista_principal());
            form.setGenero_musical(c.getGenero_musical());
            form.setDuracion_minutos(c.getDuracion_minutos());
        } else if (evento instanceof Teatro) {
            Teatro t = (Teatro) evento;
            form.setObra(t.getObra());
            form.setDirector(t.getDirector());
        } else if (evento instanceof Museo) {
            Museo m = (Museo) evento;
            form.setNombre_exposicion(m.getNombre_exposicion());
            form.setTipo_exposicion(m.getTipo_exposicion());
            form.setFecha_fin(m.getFecha_fin());
        }

        model.addAttribute("evento", form);
        model.addAttribute("lugares", lugarService.listarTodosLosLugares());
        return "formEvento";
    }

    /**
     * Procesa la actualización de un evento existente.
     * Mantiene la imagen anterior si no se sube una nueva, actualiza el subtipo y el lugar.
     */
    @PostMapping("/eventos/actualizar")
    public String actualizarEvento(@ModelAttribute EventoForm eventoForm,
                                   @RequestParam(value = "imagenFile", required = false) MultipartFile imagenFile,
                                   Model model) {
        Evento eventoActual = eventoService.obtenerEventoPorId(eventoForm.getId());
        if (eventoActual == null) {
            return "redirect:/eventos";
        }

        try {
            eventoForm.setRuta_imagen(eventoService.guardarImagen(imagenFile, eventoActual.getRuta_imagen()));
        } catch (IllegalArgumentException e) {
            model.addAttribute("evento", eventoForm);
            model.addAttribute("lugares", lugarService.listarTodosLosLugares());
            model.addAttribute("errorMensaje", e.getMessage());
            return "formEvento";
        } catch (RuntimeException e) {
            model.addAttribute("evento", eventoForm);
            model.addAttribute("lugares", lugarService.listarTodosLosLugares());
            model.addAttribute("errorMensaje", "No se pudo actualizar la imagen del evento.");
            return "formEvento";
        }

        Evento evento = construirEventoDesdeFormulario(eventoForm);
        if (evento == null) {
            model.addAttribute("evento", eventoForm);
            model.addAttribute("lugares", lugarService.listarTodosLosLugares());
            model.addAttribute("errorMensaje", "Debes seleccionar un tipo de evento valido.");
            return "formEvento";
        }

        evento.setId(eventoForm.getId());
        if (!aplicarLugarSeleccionado(eventoForm.getId_lugar(), evento, model, eventoForm)) {
            return "formEvento";
        }

        eventoService.actualizarEvento(evento);
        return "redirect:/eventos";
    }

    /**
     * Elimina el evento con el id indicado (incluyendo su imagen del disco) y redirige al listado.
     */
    @GetMapping("/eventos/eliminar/{id}")
    public String eliminarEvento(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            eventoService.eliminarEvento(id);
            redirectAttributes.addFlashAttribute("okMensaje", "Evento eliminado correctamente.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMensaje", "No se pudo eliminar el evento. Puede tener datos relacionados.");
        }
        return "redirect:/eventos";
    }

    /**
     * Crea la instancia concreta de Evento (Concierto, Teatro o Museo) a partir del formulario.
     * Rellena los atributos comunes y los específicos del subtipo seleccionado.
     *
     * @return El objeto Evento listo para persistir, o null si el tipo no es válido.
     */
    private Evento construirEventoDesdeFormulario(EventoForm eventoForm) {
        Evento evento = null;

        if (eventoForm.getTipo_evento() == null) {
            return null;
        }

        switch (eventoForm.getTipo_evento()) {
            case concierto:
                Concierto concierto = new Concierto();
                concierto.setArtista_principal(eventoForm.getArtista_principal());
                concierto.setGenero_musical(eventoForm.getGenero_musical());
                concierto.setDuracion_minutos(eventoForm.getDuracion_minutos());
                evento = concierto;
                break;
            case teatro:
                Teatro teatro = new Teatro();
                teatro.setObra(eventoForm.getObra());
                teatro.setDirector(eventoForm.getDirector());
                evento = teatro;
                break;
            case museo:
                Museo museo = new Museo();
                museo.setNombre_exposicion(eventoForm.getNombre_exposicion());
                museo.setTipo_exposicion(eventoForm.getTipo_exposicion());
                museo.setFecha_fin(eventoForm.getFecha_fin());
                evento = museo;
                break;
            default:
                return null;
        }

        evento.setTipo_evento(eventoForm.getTipo_evento());
        evento.setNombre(eventoForm.getNombre());
        evento.setDescripcion(eventoForm.getDescripcion());
        evento.setFecha(eventoForm.getFecha());
        evento.setAforo_maximo(eventoForm.getAforo_maximo());
        evento.setRuta_imagen(eventoForm.getRuta_imagen());
        return evento;
    }

    /**
     * Busca el lugar por id, lo asigna al evento y devuelve false si no se encuentra.
     * En caso de error añade el mensaje al modelo y recarga la lista de lugares.
     */
    private boolean aplicarLugarSeleccionado(int idLugar, Evento evento, Model model, EventoForm eventoForm) {
        Lugar lugar = lugarService.obtenerLugarPorId(idLugar);
        if (lugar == null) {
            model.addAttribute("evento", eventoForm);
            model.addAttribute("lugares", lugarService.listarTodosLosLugares());
            model.addAttribute("errorMensaje", "Debes seleccionar un lugar valido.");
            return false;
        }

        evento.setId_lugar(lugar.getId());
        return true;
    }

}
