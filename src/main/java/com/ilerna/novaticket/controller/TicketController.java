package com.ilerna.novaticket.controller;

import com.ilerna.novaticket.model.Ticket;
import com.ilerna.novaticket.model.Asiento;
import com.ilerna.novaticket.model.Compra;
import com.ilerna.novaticket.model.Evento;
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
import java.util.HashMap;
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
        model.addAttribute("tickets", ticketService.listarTodosLosTickets());
        model.addAttribute("eventos", eventoService.listarTodosLosEventos());
        model.addAttribute("compras", compraService.listarTodasLasCompras());
        model.addAttribute("asientos", asientoService.listarTodosLosAsientos());
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
            crearTicketsMultiples(ticket, cantidadGeneral, cantidadVip, cantidadPremium, precioGeneral, precioVip, precioPremium);
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

    private boolean esTicketValido(Ticket ticket) {
        return ticket.getId_evento() > 0
                && ticket.getTipo() != null
                && !ticket.getTipo().isBlank()
                && ticket.getCantidad() > 0
                && ticket.getPrecio_unitario() != null
                && ticket.getPrecio_unitario().compareTo(BigDecimal.ZERO) > 0;
    }

    private String normalizarTipo(String tipo) {
        if (tipo == null) {
            return null;
        }
        String base = tipo.trim().toLowerCase(Locale.ROOT);
        return switch (base) {
            case "general" -> "General";
            case "vip" -> "VIP";
            case "premium" -> "Premium";
            default -> null;
        };
    }

    private BigDecimal obtenerPrecioPorTipo(String tipo) {
        return switch (tipo) {
            case "VIP" -> new BigDecimal("50.00");
            case "Premium" -> new BigDecimal("80.00");
            default -> new BigDecimal("25.00");
        };
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

    private void crearTicketsMultiples(Ticket ticketBase, int cantidadGeneral, int cantidadVip, int cantidadPremium, BigDecimal precioGeneral, BigDecimal precioVip, BigDecimal precioPremium) {
        crearTicketsDelTipo(ticketBase, "General", cantidadGeneral, precioGeneral);
        crearTicketsDelTipo(ticketBase, "VIP", cantidadVip, precioVip);
        crearTicketsDelTipo(ticketBase, "Premium", cantidadPremium, precioPremium);
    }

    private void crearTicketsDelTipo(Ticket ticketBase, String tipo, int cantidad, BigDecimal precio) {
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
        }
    }

    private void cargarDatosFormulario(Model model, Ticket ticket) {
        model.addAttribute("ticket", ticket);
        model.addAttribute("eventos", eventoService.listarTodosLosEventos());
        model.addAttribute("asientosDisponiblesPorEvento", construirAsientosDisponiblesPorEvento(ticket));
        model.addAttribute("disponiblesPorEventoTipo", construirDisponiblesPorEventoTipo(ticket));
    }

    private Map<Integer, List<Map<String, Object>>> construirAsientosDisponiblesPorEvento(Ticket ticketActual) {
        Map<Integer, List<Map<String, Object>>> resultado = new LinkedHashMap<>();
        List<Asiento> todosAsientos = asientoService.listarTodosLosAsientos();

        for (Evento evento : eventoService.listarTodosLosEventos()) {
            List<Map<String, Object>> asientosEvento = new ArrayList<>();
            for (Asiento asiento : todosAsientos) {
                if (asiento.getId_lugar() != evento.getId_lugar()) {
                    continue;
                }
                if (!asientoDisponibleParaEvento(asiento.getId(), evento.getId(), ticketActual)) {
                    continue;
                }

                Map<String, Object> item = new HashMap<>();
                item.put("id", asiento.getId());
                item.put("label", "ID " + asiento.getId() + " - " + asiento.getFila() + "-" + asiento.getNumero_asiento() + " (" + asiento.getZona() + ")");
                asientosEvento.add(item);
            }
            resultado.put(evento.getId(), asientosEvento);
        }

        return resultado;
    }

    private boolean asientoDisponibleParaEvento(int idAsiento, int idEvento, Ticket ticketActual) {
        for (Ticket existente : ticketService.listarTodosLosTickets()) {
            if (existente.getId_evento() != idEvento || existente.getId_asiento() == null || existente.getId_asiento() != idAsiento) {
                continue;
            }

            if (ticketActual != null && ticketActual.getId() > 0 && existente.getId() == ticketActual.getId()) {
                return true;
            }
            return false;
        }
        return true;
    }

    private void asegurarCompraAsociada(Ticket ticket) {
        if (ticket.getId_compra() > 0 && compraService.obtenerCompraPorId(ticket.getId_compra()) != null) {
            return;
        }

        Integer idUsuario = usuarioService.obtenerIdClientePorDefecto();
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

    private Map<Integer, Map<String, Integer>> construirDisponiblesPorEventoTipo(Ticket ticketActual) {
        Map<Integer, Map<String, Integer>> disponibles = new LinkedHashMap<>();
        for (Evento evento : eventoService.listarTodosLosEventos()) {
            Map<String, Integer> porTipo = new LinkedHashMap<>();
            porTipo.put("general", calcularDisponiblesPorTipo(ticketActual, evento, "General"));
            porTipo.put("vip", calcularDisponiblesPorTipo(ticketActual, evento, "VIP"));
            porTipo.put("premium", calcularDisponiblesPorTipo(ticketActual, evento, "Premium"));
            disponibles.put(evento.getId(), porTipo);
        }
        return disponibles;
    }

    private int calcularDisponiblesPorTipo(Ticket ticketActual, Evento evento, String tipoNormalizado) {
        int vendidosTotal = ticketService.obtenerCantidadVendidaPorEvento(evento.getId());

        if (ticketActual != null && ticketActual.getId() > 0) {
            Ticket original = ticketService.obtenerTicketPorId(ticketActual.getId());
            if (original != null && original.getId_evento() == evento.getId()) {
                vendidosTotal = Math.max(vendidosTotal - Math.max(original.getCantidad(), 0), 0);
            }
        }

        return Math.max(evento.getAforo_maximo() - vendidosTotal, 0);
    }
}

