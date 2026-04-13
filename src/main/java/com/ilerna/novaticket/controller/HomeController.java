package com.ilerna.novaticket.controller;

import com.ilerna.novaticket.model.Asiento;
import com.ilerna.novaticket.model.Compra;
import com.ilerna.novaticket.model.Evento;
import com.ilerna.novaticket.model.Ticket;
import com.ilerna.novaticket.model.Usuario;
import com.ilerna.novaticket.model.UsuarioEnum;
import com.ilerna.novaticket.service.AsientoService;
import com.ilerna.novaticket.service.CompraService;
import com.ilerna.novaticket.service.EventoService;
import com.ilerna.novaticket.service.TicketService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

@Controller
public class HomeController {

    private static final String CARRITO_SESSION_KEY = "carritoCliente";

    private final EventoService eventoService;
    private final TicketService ticketService;
    private final CompraService compraService;
    private final AsientoService asientoService;

    public HomeController(EventoService eventoService,
                          TicketService ticketService,
                          CompraService compraService,
                          AsientoService asientoService) {
        this.eventoService = eventoService;
        this.ticketService = ticketService;
        this.compraService = compraService;
        this.asientoService = asientoService;
    }

    @GetMapping({"/", "/home"})
    public String redirigirHomeCliente() {
        return "redirect:/cliente/home";
    }

    @GetMapping("/cliente/home")
    public String mostrarHomeCliente(@RequestParam(value = "error", required = false) String error,
                                     Model model,
                                     HttpSession session) {
        List<Evento> eventos = eventoService.listarTodosLosEventos();
        Map<Integer, Integer> disponibles = new LinkedHashMap<>();

        List<CarritoItem> carrito = obtenerCarrito(session);
        for (Evento evento : eventos) {
            int disponiblesEvento = obtenerDisponiblesTotalesPorEvento(carrito, evento);
            disponibles.put(evento.getId(), disponiblesEvento);
        }

        model.addAttribute("eventos", eventos);
        model.addAttribute("disponibles", disponibles);
        model.addAttribute("carritoCantidad", carrito.size());
        cargarDatosSesion(model, session);
        if (error != null && !error.isBlank()) {
            model.addAttribute("errorMensaje", error);
        }
        return "home";
    }

    @GetMapping("/cliente/evento/{id}")
    public String mostrarDetalleEventoCliente(@PathVariable int id,
                                              @RequestParam(value = "ok", required = false) Integer ok,
                                              Model model,
                                              HttpSession session) {
        Evento evento = eventoService.obtenerEventoPorId(id);
        if (evento == null) {
            return "redirect:/cliente/home";
        }

        cargarDetalleEvento(model, evento, new CompraEntrada(), session);
        if (ok != null && ok == 1) {
            model.addAttribute("okMensaje", "Se agrego al carrito correctamente.");
        }

        cargarDatosSesion(model, session);

        return "homeEvento";
    }

    @PostMapping("/cliente/evento/{id}/agregar-carrito")
    public String agregarAlCarrito(@PathVariable int id,
                                   @ModelAttribute("compraEntrada") CompraEntrada compraEntrada,
                                   Model model,
                                   HttpSession session) {
        Evento evento = eventoService.obtenerEventoPorId(id);
        if (evento == null) {
            return "redirect:/cliente/home";
        }

        List<CarritoItem> carrito = obtenerCarrito(session);
        String tipoNormalizado = normalizarTipo(compraEntrada.getTipo());
        compraEntrada.setTipo(tipoNormalizado);

        int stockTipo = obtenerStockDisponiblePorEventoYTipo(evento.getId(), tipoNormalizado);
        int enCarritoTipo = obtenerCantidadEnCarritoPorEventoYTipo(carrito, evento.getId(), tipoNormalizado);
        int disponiblesTipo = Math.max(stockTipo - enCarritoTipo, 0);

        if (compraEntrada.getCantidad() <= 0) {
            cargarDetalleEvento(model, evento, compraEntrada, session);
            model.addAttribute("errorMensaje", "Debes seleccionar al menos 1 boleto.");
            return "homeEvento";
        }

        if (compraEntrada.getCantidad() > disponiblesTipo) {
            cargarDetalleEvento(model, evento, compraEntrada, session);
            model.addAttribute("errorMensaje", "No hay disponibilidad suficiente para esa cantidad.");
            return "homeEvento";
        }

        BigDecimal precioUnitario = obtenerPrecioPorTipo(compraEntrada.getTipo());

        CarritoItem existente = buscarItem(carrito, evento.getId(), tipoNormalizado);
        if (existente != null) {
            existente.setCantidad(existente.getCantidad() + compraEntrada.getCantidad());
        } else {
            CarritoItem item = new CarritoItem();
            item.setIdEvento(evento.getId());
            item.setNombreEvento(evento.getNombre());
            item.setTipo(tipoNormalizado);
            item.setCantidad(compraEntrada.getCantidad());
            item.setPrecioUnitario(precioUnitario);
            carrito.add(item);
        }

        session.setAttribute(CARRITO_SESSION_KEY, carrito);
        return "redirect:/cliente/evento/" + id + "?ok=1";
    }

    @GetMapping("/cliente/carrito")
    public String mostrarCarrito(@RequestParam(value = "ok", required = false) Integer ok,
                                 @RequestParam(value = "error", required = false) String error,
                                 Model model,
                                 HttpSession session) {
        List<CarritoItem> carrito = obtenerCarrito(session);
        model.addAttribute("carrito", carrito);
        model.addAttribute("total", calcularTotalCarrito(carrito));

        if (ok != null && ok == 1) {
            model.addAttribute("okMensaje", "Compra pagada correctamente.");
        }
        if (error != null && !error.isBlank()) {
            model.addAttribute("errorMensaje", error);
        }

        cargarDatosSesion(model, session);

        return "carrito";
    }

    @PostMapping("/cliente/carrito/eliminar")
    public String eliminarItemCarrito(@RequestParam("index") int index, HttpSession session) {
        List<CarritoItem> carrito = obtenerCarrito(session);
        if (index >= 0 && index < carrito.size()) {
            carrito.remove(index);
            session.setAttribute(CARRITO_SESSION_KEY, carrito);
        }
        return "redirect:/cliente/carrito";
    }

    @PostMapping("/cliente/carrito/pagar")
    public String pagarCarrito(HttpSession session) {
        List<CarritoItem> carrito = obtenerCarrito(session);
        if (carrito.isEmpty()) {
            return "redirect:/cliente/carrito?error=El carrito esta vacio.";
        }

        Usuario usuarioSesion = obtenerUsuarioSesion(session);
        if (usuarioSesion == null) {
            return "redirect:/login?error=Inicia%20sesion%20para%20comprar%20tickets";
        }

        Map<Integer, Map<String, Integer>> cantidadPorEventoTipo = new LinkedHashMap<>();
        for (CarritoItem item : carrito) {
            cantidadPorEventoTipo
                    .computeIfAbsent(item.getIdEvento(), k -> new LinkedHashMap<>())
                    .merge(normalizarTipo(item.getTipo()), item.getCantidad(), Integer::sum);
        }

        Map<Integer, Map<String, List<Asiento>>> asientosDisponiblesPorEventoTipo = new LinkedHashMap<>();
        for (Map.Entry<Integer, Map<String, Integer>> entry : cantidadPorEventoTipo.entrySet()) {
            Evento evento = eventoService.obtenerEventoPorId(entry.getKey());
            if (evento == null) {
                return "redirect:/cliente/carrito?error=Uno de los eventos ya no existe.";
            }

            Map<String, List<Asiento>> asientosLibresPorTipo = new LinkedHashMap<>();

            for (Map.Entry<String, Integer> porTipo : entry.getValue().entrySet()) {
                String tipo = porTipo.getKey();
                int cantidadTipo = porTipo.getValue();
                List<Ticket> stockTipo = obtenerTicketsStockPorEventoYTipo(evento.getId(), tipo);
                int disponiblesTipo = stockTipo.size();

                if (cantidadTipo > disponiblesTipo) {
                    String mensaje = "No hay disponibilidad suficiente para " + evento.getNombre() + " (" + tipo.toUpperCase(Locale.ROOT) + ").";
                    return "redirect:/cliente/carrito?error=" + URLEncoder.encode(mensaje, StandardCharsets.UTF_8);
                }

                int ticketsSinAsiento = 0;
                for (int i = 0; i < cantidadTipo; i++) {
                    if (stockTipo.get(i).getId_asiento() == null) {
                        ticketsSinAsiento++;
                    }
                }

                List<Asiento> asientosLibresTipo = obtenerAsientosLibresParaEventoYTipo(evento.getId(), tipo);
                asientosLibresPorTipo.put(tipo, asientosLibresTipo);

                if (ticketsSinAsiento > asientosLibresTipo.size()) {
                    String mensaje = "No hay asientos disponibles para " + evento.getNombre() + " (" + tipo.toUpperCase(Locale.ROOT) + ").";
                    return "redirect:/cliente/carrito?error=" + URLEncoder.encode(mensaje, StandardCharsets.UTF_8);
                }
            }
            asientosDisponiblesPorEventoTipo.put(evento.getId(), asientosLibresPorTipo);
        }

        Compra compra = new Compra();
        compra.setId_usuario(usuarioSesion.getId());
        compra.setFecha(LocalDateTime.now());
        compra.setTotal(calcularTotalCarrito(carrito));
        compraService.guardarCompra(compra);

        for (CarritoItem item : carrito) {
            List<Ticket> stock = obtenerTicketsStockPorEventoYTipo(item.getIdEvento(), item.getTipo());
            String tipoNormalizado = normalizarTipo(item.getTipo());
            List<Asiento> asientosDisponiblesTipo = asientosDisponiblesPorEventoTipo
                    .getOrDefault(item.getIdEvento(), new LinkedHashMap<>())
                    .getOrDefault(tipoNormalizado, new ArrayList<>());

            for (int i = 0; i < item.getCantidad(); i++) {
                Ticket ticket = stock.get(i);
                ticket.setId_compra(compra.getId());

                if (ticket.getId_asiento() == null) {
                    Asiento asientoAsignado = extraerAsientoAleatorio(asientosDisponiblesTipo);
                    if (asientoAsignado == null) {
                        String mensaje = "No se pudo asignar asiento para " + item.getNombreEvento() + " (" + tipoNormalizado.toUpperCase(Locale.ROOT) + ").";
                        return "redirect:/cliente/carrito?error=" + URLEncoder.encode(mensaje, StandardCharsets.UTF_8);
                    }
                    ticket.setId_asiento(asientoAsignado.getId());
                }

                ticketService.actualizarTicket(ticket);
            }
        }

        session.setAttribute(CARRITO_SESSION_KEY, new ArrayList<CarritoItem>());
        return "redirect:/cliente/carrito?ok=1";
    }

    private void cargarDetalleEvento(Model model, Evento evento, CompraEntrada compraEntrada, HttpSession session) {
        List<CarritoItem> carrito = obtenerCarrito(session);
        Map<String, Integer> disponiblesPorTipo = new LinkedHashMap<>();
        int disponiblesReales = 0;

        for (String tipo : List.of("general", "vip", "premium")) {
            int stockTipo = obtenerStockDisponiblePorEventoYTipo(evento.getId(), tipo);
            int enCarritoTipo = obtenerCantidadEnCarritoPorEventoYTipo(carrito, evento.getId(), tipo);
            int disponiblesTipo = Math.max(stockTipo - enCarritoTipo, 0);
            disponiblesPorTipo.put(tipo, disponiblesTipo);
            disponiblesReales += disponiblesTipo;
        }

        int vendidosTotal = obtenerVendidosPorEvento(evento.getId());
        model.addAttribute("evento", evento);
        model.addAttribute("vendidos", vendidosTotal);
        model.addAttribute("disponibles", disponiblesReales);
        model.addAttribute("disponiblesPorTipo", disponiblesPorTipo);
        model.addAttribute("carritoCantidad", carrito.size());
        model.addAttribute("precios", Map.of(
                "general", new BigDecimal("25.00"),
                "vip", new BigDecimal("50.00"),
                "premium", new BigDecimal("80.00")
        ));
        model.addAttribute("compraEntrada", compraEntrada);
    }


    private int obtenerVendidosPorEvento(int idEvento) {
        if (idEvento <= 0) {
            return 0;
        }

        return ticketService.obtenerCantidadVendidaPorEvento(idEvento);
    }

    private List<Asiento> obtenerAsientosLibresParaEvento(int idEvento) {
        Evento evento = eventoService.obtenerEventoPorId(idEvento);
        if (evento == null) {
            return new ArrayList<>();
        }

        List<Asiento> asientosLugar = new ArrayList<>();
        for (Asiento asiento : asientoService.listarTodosLosAsientos()) {
            if (asiento.getId_lugar() == evento.getId_lugar()) {
                asientosLugar.add(asiento);
            }
        }

        Set<Integer> asientosOcupados = new HashSet<>();
        for (Ticket ticket : ticketService.listarTodosLosTickets()) {
            if (ticket.getId_evento() == idEvento && ticket.getId_asiento() != null) {
                asientosOcupados.add(ticket.getId_asiento());
            }
        }

        List<Asiento> libres = new ArrayList<>();
        for (Asiento asiento : asientosLugar) {
            if (!asientosOcupados.contains(asiento.getId())) {
                libres.add(asiento);
            }
        }

        libres.sort(Comparator.comparing(Asiento::getFila).thenComparingInt(Asiento::getNumero_asiento));
        return libres;
    }

    private List<Asiento> obtenerAsientosLibresParaEventoYTipo(int idEvento, String tipo) {
        String tipoNormalizado = normalizarTipo(tipo);
        List<Asiento> libresPorTipo = new ArrayList<>();
        for (Asiento asiento : obtenerAsientosLibresParaEvento(idEvento)) {
            if (normalizarTipo(asiento.getZona()).equals(tipoNormalizado)) {
                libresPorTipo.add(asiento);
            }
        }
        return libresPorTipo;
    }

    private Asiento extraerAsientoAleatorio(List<Asiento> asientosDisponibles) {
        if (asientosDisponibles == null || asientosDisponibles.isEmpty()) {
            return null;
        }
        int indice = ThreadLocalRandom.current().nextInt(asientosDisponibles.size());
        return asientosDisponibles.remove(indice);
    }

    private BigDecimal obtenerPrecioPorTipo(String tipo) {
        String tipoNormalizado = normalizarTipo(tipo);
        if ("vip".equals(tipoNormalizado)) {
            return new BigDecimal("50.00");
        }
        if ("premium".equals(tipoNormalizado)) {
            return new BigDecimal("80.00");
        }
        return new BigDecimal("25.00");
    }

    private String normalizarTipo(String tipo) {
        return tipo == null ? "general" : tipo.trim().toLowerCase(Locale.ROOT);
    }

    @SuppressWarnings("unchecked")
    private List<CarritoItem> obtenerCarrito(HttpSession session) {
        Object carrito = session.getAttribute(CARRITO_SESSION_KEY);
        if (carrito instanceof List<?>) {
            return (List<CarritoItem>) carrito;
        }
        List<CarritoItem> nuevo = new ArrayList<>();
        session.setAttribute(CARRITO_SESSION_KEY, nuevo);
        return nuevo;
    }

    private CarritoItem buscarItem(List<CarritoItem> carrito, int idEvento, String tipo) {
        for (CarritoItem item : carrito) {
            if (item.getIdEvento() == idEvento && item.getTipo().equals(tipo)) {
                return item;
            }
        }
        return null;
    }


    private int obtenerCantidadEnCarritoPorEventoYTipo(List<CarritoItem> carrito, int idEvento, String tipo) {
        int total = 0;
        String tipoNormalizado = normalizarTipo(tipo);
        for (CarritoItem item : carrito) {
            if (item.getIdEvento() == idEvento && normalizarTipo(item.getTipo()).equals(tipoNormalizado)) {
                total += item.getCantidad();
            }
        }
        return total;
    }


    private int obtenerDisponiblesTotalesPorEvento(List<CarritoItem> carrito, Evento evento) {
        int total = 0;
        for (String tipo : List.of("general", "vip", "premium")) {
            int stockTipo = obtenerStockDisponiblePorEventoYTipo(evento.getId(), tipo);
            int enCarritoTipo = obtenerCantidadEnCarritoPorEventoYTipo(carrito, evento.getId(), tipo);
            total += Math.max(stockTipo - enCarritoTipo, 0);
        }
        return total;
    }

    private int obtenerStockDisponiblePorEventoYTipo(int idEvento, String tipo) {
        String tipoNormalizado = normalizarTipo(tipo);
        int total = 0;
        for (Ticket ticket : obtenerTicketsStockPorEventoYTipo(idEvento, tipoNormalizado)) {
            total += Math.max(ticket.getCantidad(), 0);
        }
        return total;
    }

    private List<Ticket> obtenerTicketsStockPorEventoYTipo(int idEvento, String tipo) {
        String tipoNormalizado = normalizarTipo(tipo);
        Map<Integer, Compra> comprasPorId = new LinkedHashMap<>();
        for (Compra compra : compraService.listarTodasLasCompras()) {
            comprasPorId.put(compra.getId(), compra);
        }

        List<Ticket> stock = new ArrayList<>();
        for (Ticket ticket : ticketService.listarTodosLosTickets()) {
            if (ticket.getId_evento() != idEvento) {
                continue;
            }
            if (!normalizarTipo(ticket.getTipo()).equals(tipoNormalizado)) {
                continue;
            }
            Compra compra = comprasPorId.get(ticket.getId_compra());
            if (compra != null && compra.getTotal() != null && compra.getTotal().compareTo(BigDecimal.ZERO) <= 0) {
                stock.add(ticket);
            }
        }
        return stock;
    }

    private BigDecimal calcularTotalCarrito(List<CarritoItem> carrito) {
        BigDecimal total = BigDecimal.ZERO;
        for (CarritoItem item : carrito) {
            total = total.add(item.getSubtotal());
        }
        return total.setScale(2, RoundingMode.HALF_UP);
    }

    private Usuario obtenerUsuarioSesion(HttpSession session) {
        Object usuario = session.getAttribute(AuthController.USUARIO_SESION_KEY);
        return usuario instanceof Usuario ? (Usuario) usuario : null;
    }

    private void cargarDatosSesion(Model model, HttpSession session) {
        Usuario usuario = obtenerUsuarioSesion(session);
        model.addAttribute("usuarioSesion", usuario);
        model.addAttribute("esAdmin", usuario != null && usuario.getTipo_usuario() == UsuarioEnum.admin);
    }

    public static class CompraEntrada {
        private String tipo = "general";
        private int cantidad = 1;

        public String getTipo() {
            return tipo;
        }

        public void setTipo(String tipo) {
            this.tipo = tipo;
        }

        public int getCantidad() {
            return cantidad;
        }

        public void setCantidad(int cantidad) {
            this.cantidad = cantidad;
        }
    }

    public static class CarritoItem {
        private int idEvento;
        private String nombreEvento;
        private String tipo;
        private int cantidad;
        private BigDecimal precioUnitario;

        public int getIdEvento() {
            return idEvento;
        }

        public void setIdEvento(int idEvento) {
            this.idEvento = idEvento;
        }

        public String getNombreEvento() {
            return nombreEvento;
        }

        public void setNombreEvento(String nombreEvento) {
            this.nombreEvento = nombreEvento;
        }

        public String getTipo() {
            return tipo;
        }

        public void setTipo(String tipo) {
            this.tipo = tipo;
        }

        public int getCantidad() {
            return cantidad;
        }

        public void setCantidad(int cantidad) {
            this.cantidad = cantidad;
        }

        public BigDecimal getPrecioUnitario() {
            return precioUnitario;
        }

        public void setPrecioUnitario(BigDecimal precioUnitario) {
            this.precioUnitario = precioUnitario;
        }

        public BigDecimal getSubtotal() {
            if (precioUnitario == null) {
                return BigDecimal.ZERO;
            }
            return precioUnitario.multiply(BigDecimal.valueOf(cantidad)).setScale(2, RoundingMode.HALF_UP);
        }
    }
}
