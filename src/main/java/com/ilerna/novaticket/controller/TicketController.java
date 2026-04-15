package com.ilerna.novaticket.controller;

import com.ilerna.novaticket.model.Ticket;
import com.ilerna.novaticket.model.Asiento;
import com.ilerna.novaticket.model.Compra;
import com.ilerna.novaticket.model.Evento;
import com.ilerna.novaticket.model.Usuario;
import com.ilerna.novaticket.service.AsientoService;
import com.ilerna.novaticket.service.CompraService;
import com.ilerna.novaticket.service.EventoService;
import com.ilerna.novaticket.service.TicketService;
import com.ilerna.novaticket.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.List;

@Controller
public class TicketController {

    private final TicketService ticketService;
    private final EventoService eventoService;
    private final CompraService compraService;
    private final AsientoService asientoService;
    private final UsuarioService usuarioService;

    public TicketController(TicketService ticketService,
                            EventoService eventoService,
                            CompraService compraService,
                            AsientoService asientoService,
                            UsuarioService usuarioService) {
        this.ticketService = ticketService;
        this.eventoService = eventoService;
        this.compraService = compraService;
        this.asientoService = asientoService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/tickets")
    public String listarTickets(Model model) {
        List<Ticket> tickets = ticketService.listarTodosLosTickets();
        List<Evento> eventos = eventoService.listarTodosLosEventos();
        List<Compra> compras = compraService.listarTodasLasCompras();
        List<Asiento> asientos = asientoService.listarTodosLosAsientos();

        Map<Integer, String> eventosMap = new LinkedHashMap<>();
        eventos.forEach(evento -> eventosMap.put(evento.getId(), evento.getNombre()));

        Map<Integer, String> comprasMap = new LinkedHashMap<>();
        compras.forEach(compra -> comprasMap.put(compra.getId(), "Compra #" + compra.getId()));

        Map<Integer, String> asientosMap = new LinkedHashMap<>();
        asientos.forEach(asiento -> asientosMap.put(
                asiento.getId(),
                asiento.getFila() + "-" + asiento.getNumero_asiento() + " (" + asiento.getZona() + ")"
        ));

        model.addAttribute("tickets", tickets);
        model.addAttribute("eventos", eventos);
        model.addAttribute("compras", compras);
        model.addAttribute("asientos", asientos);
        model.addAttribute("eventosMap", eventosMap);
        model.addAttribute("comprasMap", comprasMap);
        model.addAttribute("asientosMap", asientosMap);
        return "crudTicket";
    }

    @GetMapping("/tickets/nuevo")
    public String mostrarFormulario(Model model) {
        cargarDatosFormulario(model, new Ticket());
        return "formTicket";
    }

    @PostMapping("/tickets/guardar")
    public String guardarTicket(@ModelAttribute Ticket ticket,
                                @RequestParam(value = "cantidadGeneral", defaultValue = "0") int cantidadGeneral,
                                @RequestParam(value = "cantidadVip", defaultValue = "0") int cantidadVip,
                                @RequestParam(value = "cantidadPremium", defaultValue = "0") int cantidadPremium,
                                @RequestParam(value = "precioGeneral", defaultValue = "25.00") BigDecimal precioGeneral,
                                @RequestParam(value = "precioVip", defaultValue = "50.00") BigDecimal precioVip,
                                @RequestParam(value = "precioPremium", defaultValue = "80.00") BigDecimal precioPremium,
                                Model model) {
        if (ticket.getId_asiento() != null && ticket.getId_asiento() <= 0) {
            ticket.setId_asiento(null);
        }

        if (ticket.getId_evento() <= 0) {
            recargarFormulario(model, ticket, "Debes seleccionar un evento válido.");
            return "formTicket";
        }

        Evento evento = eventoService.obtenerEventoPorId(ticket.getId_evento());
        if (evento == null) {
            recargarFormulario(model, ticket, "El evento seleccionado no existe.");
            return "formTicket";
        }

        if (precioGeneral.compareTo(BigDecimal.ZERO) <= 0 || precioVip.compareTo(BigDecimal.ZERO) <= 0 || precioPremium.compareTo(BigDecimal.ZERO) <= 0) {
            recargarFormulario(model, ticket, "Todos los precios deben ser mayores que 0.");
            return "formTicket";
        }

        int cantidadTotal = cantidadGeneral + cantidadVip + cantidadPremium;
        if (cantidadTotal <= 0) {
            recargarFormulario(model, ticket, "Debes ingresar al menos 1 ticket para algún tipo.");
            return "formTicket";
        }

        if (ticket.getId_asiento() != null && cantidadTotal > 1) {
            recargarFormulario(model, ticket, "Si seleccionas un asiento solo puedes crear 1 ticket.");
            return "formTicket";
        }


        if (ticket.getId_asiento() != null) {
            Asiento asiento = asientoService.obtenerAsientoPorId(ticket.getId_asiento());
            if (asiento == null) {
                recargarFormulario(model, ticket, "El asiento seleccionado no existe.");
                return "formTicket";
            }
            if (asiento.getId_lugar() != evento.getId_lugar()) {
                recargarFormulario(model, ticket, "El asiento no pertenece al lugar del evento.");
                return "formTicket";
            }
            if (asientoYaAsignado(ticket)) {
                recargarFormulario(model, ticket, "Ese asiento ya esta asignado para este evento.");
                return "formTicket";
            }
        }

        if (ticket.getId() > 0) {
            ticketService.actualizarTicket(ticket);
        } else {
            asegurarCompraAsociada(ticket);
            if (ticket.getId_compra() <= 0 || compraService.obtenerCompraPorId(ticket.getId_compra()) == null) {
                recargarFormulario(model, ticket, "No se pudo crear la compra base para los tickets.");
                return "formTicket";
            }
            String errorCreacion = crearTicketsMultiples(ticket, cantidadGeneral, cantidadVip, cantidadPremium, precioGeneral, precioVip, precioPremium);
            if (errorCreacion != null) {
                recargarFormulario(model, ticket, errorCreacion);
                return "formTicket";
            }
        }
        return "redirect:/tickets";
    }

    @GetMapping("/tickets/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable int id, Model model) {
        Ticket ticket = ticketService.obtenerTicketPorId(id);
        if (ticket == null) {
            return "redirect:/tickets";
        }
        cargarDatosFormulario(model, ticket);
        return "formTicket";
    }

    @GetMapping("/tickets/eliminar/{id}")
    public String eliminarTicket(@PathVariable int id) {
        ticketService.eliminarTicket(id);
        return "redirect:/tickets";
    }

    private boolean asientoYaAsignado(Ticket ticket) {
        for (Ticket existente : ticketService.listarTodosLosTickets()) {
            if (existente.getId_evento() == ticket.getId_evento()
                    && existente.getId_asiento() != null
                    && existente.getId_asiento().equals(ticket.getId_asiento())
                    && existente.getId() != ticket.getId()) {
                return true;
            }
        }
        return false;
    }

    private void recargarFormulario(Model model, Ticket ticket, String errorMensaje) {
        cargarDatosFormulario(model, ticket);
        model.addAttribute("errorMensaje", errorMensaje);
    }

    private String crearTicketsMultiples(Ticket ticketBase, int cantidadGeneral, int cantidadVip, int cantidadPremium, BigDecimal precioGeneral, BigDecimal precioVip, BigDecimal precioPremium) {
        String error = crearTicketsDelTipo(ticketBase, "General", cantidadGeneral, precioGeneral);
        if (error != null) {
            return error;
        }
        error = crearTicketsDelTipo(ticketBase, "VIP", cantidadVip, precioVip);
        if (error != null) {
            return error;
        }
        return crearTicketsDelTipo(ticketBase, "Premium", cantidadPremium, precioPremium);
    }

    private String crearTicketsDelTipo(Ticket ticketBase, String tipo, int cantidad, BigDecimal precio) {
        for (int i = 0; i < cantidad; i++) {
            Ticket nuevoTicket = new Ticket();
            nuevoTicket.setId_evento(ticketBase.getId_evento());
            nuevoTicket.setId_asiento(ticketBase.getId_asiento());
            nuevoTicket.setTipo(tipo);
            // La tabla ticket exige id_compra NOT NULL, reutilizamos la compra base creada en guardarTicket.
            nuevoTicket.setId_compra(ticketBase.getId_compra());
            nuevoTicket.setCantidad(1);
            nuevoTicket.setPrecio_unitario(precio);
            ticketService.guardarTicket(nuevoTicket);
            if (nuevoTicket.getId() <= 0) {
                return "No se pudo guardar el ticket de tipo " + tipo + ". Revisa la compra asociada y las relaciones con la base de datos.";
            }
        }
        return null;
    }

    private void cargarDatosFormulario(Model model, Ticket ticket) {
        model.addAttribute("ticket", ticket);
        model.addAttribute("eventos", eventoService.listarTodosLosEventos());
    }

    private void asegurarCompraAsociada(Ticket ticket) {
        if (ticket.getId_compra() > 0 && compraService.obtenerCompraPorId(ticket.getId_compra()) != null) {
            return;
        }

        Integer idUsuario = usuarioService.obtenerIdClientePorDefecto();
        if (idUsuario == null) {
            for (Usuario usuario : usuarioService.listarTodosLosUsuarios()) {
                if (usuario != null && usuario.getId() > 0) {
                    idUsuario = usuario.getId();
                    break;
                }
            }
        }
        if (idUsuario == null) {
            return;
        }

        Compra compra = new Compra();
        compra.setId_usuario(idUsuario);
        compra.setFecha(LocalDateTime.now());
        // Compra borrador para respetar FK de ticket sin marcarlo como vendido.
        compra.setTotal(BigDecimal.ZERO);
        compraService.guardarCompra(compra);

        if (compra.getId() > 0) {
            ticket.setId_compra(compra.getId());
        }
    }
}

