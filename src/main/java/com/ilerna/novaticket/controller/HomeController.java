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

    // Inicializa el controlador inyectando los servicios necesarios para eventos, tickets, compras y asientos.
    public HomeController(EventoService eventoService,
                          TicketService ticketService,
                          CompraService compraService,
                          AsientoService asientoService) {
        this.eventoService = eventoService;
        this.ticketService = ticketService;
        this.compraService = compraService;
        this.asientoService = asientoService;
    }

    // Redirige las rutas generales de entrada hacia la vista principal del cliente.
    @GetMapping({"/", "/home"})
    public String redirigirHomeCliente() {
        return "redirect:/cliente/home";
    }

    // Carga el home del cliente con todos los eventos, la disponibilidad calculada y los datos de sesión.
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

    // Muestra el detalle de un evento concreto y prepara el formulario para añadir entradas al carrito.
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

    // Valida la petición del usuario y añade al carrito la cantidad solicitada del tipo de entrada elegido.
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

        BigDecimal precioUnitario = obtenerPrecioDisponiblePorEventoYTipo(evento.getId(), compraEntrada.getTipo());
        if (precioUnitario.compareTo(BigDecimal.ZERO) <= 0) {
            cargarDetalleEvento(model, evento, compraEntrada, session);
            model.addAttribute("errorMensaje", "No se pudo determinar el precio para el tipo seleccionado.");
            return "homeEvento";
        }

        CarritoItem existente = buscarItem(carrito, evento.getId(), tipoNormalizado);
        if (existente != null) {
            existente.setCantidad(existente.getCantidad() + compraEntrada.getCantidad());
            existente.setPrecioUnitario(precioUnitario);
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

    // Muestra el contenido actual del carrito y calcula el total acumulado de la compra.
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

    // Construye el historial de compras del usuario autenticado o de todas las compras si el usuario es administrador.
    @GetMapping("/cliente/historial")
    public String mostrarHistorialCompras(Model model, HttpSession session) {
        Usuario usuarioSesion = obtenerUsuarioSesion(session);
        if (usuarioSesion == null) {
            return "redirect:/login?error=Inicia%20sesion%20para%20ver%20tu%20historial";
        }

        boolean esAdminHistorial = usuarioSesion.getTipo_usuario() == UsuarioEnum.admin;

        Map<Integer, Evento> eventosPorId = new LinkedHashMap<>();
        for (Evento evento : eventoService.listarTodosLosEventos()) {
            eventosPorId.put(evento.getId(), evento);
        }

        Map<Integer, Asiento> asientosPorId = new LinkedHashMap<>();
        for (Asiento asiento : asientoService.listarTodosLosAsientos()) {
            asientosPorId.put(asiento.getId(), asiento);
        }

        List<Ticket> tickets = ticketService.listarTodosLosTickets();
        List<HistorialCompraView> historial = new ArrayList<>();

        List<Compra> compras = new ArrayList<>(compraService.listarTodasLasCompras());
        compras.sort(Comparator.comparing(Compra::getFecha, Comparator.nullsLast(Comparator.naturalOrder())).reversed());

        for (Compra compra : compras) {
            if (compra == null || compra.getId() <= 0) {
                continue;
            }
            if (compra.getTotal() == null || compra.getTotal().compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }
            if (!esAdminHistorial && compra.getId_usuario() != usuarioSesion.getId()) {
                continue;
            }

            HistorialCompraView compraView = new HistorialCompraView();
            compraView.setId(compra.getId());
            compraView.setIdUsuario(compra.getId_usuario());
            compraView.setFecha(compra.getFecha());
            compraView.setTotal(compra.getTotal());

            List<HistorialDetalleView> detalles = new ArrayList<>();
            for (Ticket ticket : tickets) {
                if (ticket.getId_compra() != compra.getId()) {
                    continue;
                }

                HistorialDetalleView detalle = new HistorialDetalleView();
                detalle.setIdTicket(ticket.getId());
                detalle.setTipo(ticket.getTipo());
                detalle.setCantidad(ticket.getCantidad());
                detalle.setPrecioUnitario(ticket.getPrecio_unitario());

                BigDecimal precio = ticket.getPrecio_unitario() == null ? BigDecimal.ZERO : ticket.getPrecio_unitario();
                detalle.setSubtotal(precio.multiply(BigDecimal.valueOf(ticket.getCantidad())).setScale(2, RoundingMode.HALF_UP));

                Evento evento = eventosPorId.get(ticket.getId_evento());
                if (evento != null) {
                    detalle.setNombreEvento(evento.getNombre());
                    detalle.setFechaEvento(evento.getFecha());
                    detalle.setNombreLugar(evento.getNombre_lugar());
                    detalle.setCiudadLugar(evento.getCiudad());
                }

                if (ticket.getId_asiento() != null) {
                    Asiento asiento = asientosPorId.get(ticket.getId_asiento());
                    if (asiento != null) {
                        detalle.setFila(asiento.getFila());
                        detalle.setNumeroAsiento(asiento.getNumero_asiento());
                        detalle.setZona(asiento.getZona());
                    }
                }

                detalles.add(detalle);
            }

            compraView.setDetalles(detalles);
            historial.add(compraView);
        }

        model.addAttribute("historialCompras", historial);
        model.addAttribute("esAdminHistorial", esAdminHistorial);
        model.addAttribute("carritoCantidad", obtenerCarrito(session).size());
        cargarDatosSesion(model, session);
        return "historialCompras";
    }

    // Elimina del carrito el elemento indicado por su posición y vuelve a mostrar el listado actualizado.
    @PostMapping("/cliente/carrito/eliminar")
    public String eliminarItemCarrito(@RequestParam("index") int index, HttpSession session) {
        List<CarritoItem> carrito = obtenerCarrito(session);
        if (index >= 0 && index < carrito.size()) {
            carrito.remove(index);
            session.setAttribute(CARRITO_SESSION_KEY, carrito);
        }
        return "redirect:/cliente/carrito";
    }

    // Valida el carrito, registra la compra y asigna los tickets y asientos disponibles al usuario autenticado.
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

    // Calcula y carga en el modelo la disponibilidad y los precios por tipo para la ficha de un evento.
    private void cargarDetalleEvento(Model model, Evento evento, CompraEntrada compraEntrada, HttpSession session) {
        List<CarritoItem> carrito = obtenerCarrito(session);
        Map<String, Integer> disponiblesPorTipo = new LinkedHashMap<>();
        Map<String, BigDecimal> preciosPorTipo = new LinkedHashMap<>();
        int disponiblesReales = 0;

        for (String tipo : List.of("general", "vip", "premium")) {

            int stockTipo = obtenerStockDisponiblePorEventoYTipo(evento.getId(), tipo);
            int enCarritoTipo = obtenerCantidadEnCarritoPorEventoYTipo(carrito, evento.getId(), tipo);
            int disponiblesTipo = Math.max(stockTipo - enCarritoTipo, 0);

            disponiblesPorTipo.put(tipo, disponiblesTipo);
            disponiblesReales += disponiblesTipo;

            List<Ticket> tickets = obtenerTicketsStockPorEventoYTipo(evento.getId(), tipo);

            BigDecimal precio = tickets.stream().findFirst().map(Ticket::getPrecio_unitario).orElse(BigDecimal.ZERO);

            preciosPorTipo.put(tipo, precio);
        }

        int vendidosTotal = obtenerVendidosPorEvento(evento.getId());

        model.addAttribute("evento", evento);
        model.addAttribute("vendidos", vendidosTotal);
        model.addAttribute("disponibles", disponiblesReales);
        model.addAttribute("disponiblesPorTipo", disponiblesPorTipo);
        model.addAttribute("carritoCantidad", carrito.size());

        model.addAttribute("precios", preciosPorTipo);

        model.addAttribute("compraEntrada", compraEntrada);
    }


    // Devuelve cuántos tickets vendidos tiene un evento, evitando consultas innecesarias con identificadores no válidos.
    private int obtenerVendidosPorEvento(int idEvento) {
        if (idEvento <= 0) {
            return 0;
        }

        return ticketService.obtenerCantidadVendidaPorEvento(idEvento);
    }

    // Obtiene todos los asientos libres del lugar asociado a un evento, excluyendo los ya ocupados por tickets asignados.
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

    // Filtra los asientos libres de un evento para quedarse solo con los que pertenecen a la zona o tipo indicado.
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

    // Extrae de forma aleatoria uno de los asientos disponibles para repartir las asignaciones al confirmar la compra.
    private Asiento extraerAsientoAleatorio(List<Asiento> asientosDisponibles) {
        if (asientosDisponibles == null || asientosDisponibles.isEmpty()) {
            return null;
        }
        int indice = ThreadLocalRandom.current().nextInt(asientosDisponibles.size());
        return asientosDisponibles.remove(indice);
    }

    // Obtiene el precio real del stock disponible para un evento y tipo concreto.
    // Se usa para que el carrito y el total se calculen con el mismo precio mostrado en la ficha del evento.
    private BigDecimal obtenerPrecioDisponiblePorEventoYTipo(int idEvento, String tipo) {
        List<Ticket> stockTipo = obtenerTicketsStockPorEventoYTipo(idEvento, tipo);
        return stockTipo.stream()
                .map(Ticket::getPrecio_unitario)
                .filter(precio -> precio != null && precio.compareTo(BigDecimal.ZERO) > 0)
                .findFirst()
                .orElse(BigDecimal.ZERO);
    }

    // Normaliza el tipo de entrada o zona para poder comparar valores sin errores por mayúsculas o espacios.
    private String normalizarTipo(String tipo) {
        return tipo == null ? "general" : tipo.trim().toLowerCase(Locale.ROOT);
    }

    @SuppressWarnings("unchecked")
    // Recupera el carrito guardado en sesión o crea uno nuevo si todavía no existe.
    private List<CarritoItem> obtenerCarrito(HttpSession session) {
        Object carrito = session.getAttribute(CARRITO_SESSION_KEY);
        if (carrito instanceof List<?>) {
            return (List<CarritoItem>) carrito;
        }
        List<CarritoItem> nuevo = new ArrayList<>();
        session.setAttribute(CARRITO_SESSION_KEY, nuevo);
        return nuevo;
    }

    // Busca si ya existe en el carrito una línea correspondiente al mismo evento y tipo de entrada.
    private CarritoItem buscarItem(List<CarritoItem> carrito, int idEvento, String tipo) {
        for (CarritoItem item : carrito) {
            if (item.getIdEvento() == idEvento && item.getTipo().equals(tipo)) {
                return item;
            }
        }
        return null;
    }


    // Suma cuántas entradas de un evento y tipo concretos están ya reservadas temporalmente dentro del carrito.
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


    // Calcula la disponibilidad total visible de un evento descontando también las entradas apartadas en el carrito actual.
    private int obtenerDisponiblesTotalesPorEvento(List<CarritoItem> carrito, Evento evento) {
        int total = 0;
        for (String tipo : List.of("general", "vip", "premium")) {
            int stockTipo = obtenerStockDisponiblePorEventoYTipo(evento.getId(), tipo);
            int enCarritoTipo = obtenerCantidadEnCarritoPorEventoYTipo(carrito, evento.getId(), tipo);
            total += Math.max(stockTipo - enCarritoTipo, 0);
        }
        return total;
    }

    // Suma el stock disponible de tickets de un evento y tipo determinados teniendo en cuenta la cantidad guardada en cada ticket.
    private int obtenerStockDisponiblePorEventoYTipo(int idEvento, String tipo) {
        String tipoNormalizado = normalizarTipo(tipo);
        int total = 0;
        for (Ticket ticket : obtenerTicketsStockPorEventoYTipo(idEvento, tipoNormalizado)) {
            total += Math.max(ticket.getCantidad(), 0);
        }
        return total;
    }

    // Recupera los tickets que todavía forman parte del stock disponible para un evento y un tipo concretos.
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

    // Calcula el importe total del carrito sumando los subtotales de todos sus elementos.
    private BigDecimal calcularTotalCarrito(List<CarritoItem> carrito) {
        BigDecimal total = BigDecimal.ZERO;
        for (CarritoItem item : carrito) {
            total = total.add(item.getSubtotal());
        }
        return total.setScale(2, RoundingMode.HALF_UP);
    }

    // Obtiene de la sesión el usuario autenticado actualmente o devuelve null si no hay sesión iniciada.
    private Usuario obtenerUsuarioSesion(HttpSession session) {
        Object usuario = session.getAttribute(AuthController.USUARIO_SESION_KEY);
        return usuario instanceof Usuario ? (Usuario) usuario : null;
    }

    // Carga en el modelo la información básica de la sesión para que las vistas sepan si hay usuario y si es administrador.
    private void cargarDatosSesion(Model model, HttpSession session) {
        Usuario usuario = obtenerUsuarioSesion(session);
        model.addAttribute("usuarioSesion", usuario);
        model.addAttribute("esAdmin", usuario != null && usuario.getTipo_usuario() == UsuarioEnum.admin);
    }

    public static class CompraEntrada {
        private String tipo = "general";
        private int cantidad = 1;

        // Devuelve el tipo de entrada seleccionado en el formulario de compra.
        public String getTipo() {
            return tipo;
        }

        // Guarda el tipo de entrada elegido por el usuario antes de procesar la compra.
        public void setTipo(String tipo) {
            this.tipo = tipo;
        }

        // Devuelve la cantidad de entradas solicitadas en la operación actual.
        public int getCantidad() {
            return cantidad;
        }

        // Actualiza la cantidad de entradas que el usuario quiere añadir al carrito.
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

        // Devuelve el identificador del evento asociado a esta línea del carrito.
        public int getIdEvento() {
            return idEvento;
        }

        // Asigna el identificador del evento al que pertenece esta línea del carrito.
        public void setIdEvento(int idEvento) {
            this.idEvento = idEvento;
        }

        // Devuelve el nombre del evento mostrado al usuario dentro del carrito.
        public String getNombreEvento() {
            return nombreEvento;
        }

        // Guarda el nombre del evento para mostrarlo en la vista del carrito.
        public void setNombreEvento(String nombreEvento) {
            this.nombreEvento = nombreEvento;
        }

        // Devuelve el tipo de entrada almacenado en esta línea del carrito.
        public String getTipo() {
            return tipo;
        }

        // Actualiza el tipo de entrada asociado a esta línea del carrito.
        public void setTipo(String tipo) {
            this.tipo = tipo;
        }

        // Devuelve la cantidad de entradas acumuladas para esta línea del carrito.
        public int getCantidad() {
            return cantidad;
        }

        // Guarda la cantidad de entradas seleccionadas para esta línea del carrito.
        public void setCantidad(int cantidad) {
            this.cantidad = cantidad;
        }

        // Devuelve el precio unitario aplicado a cada entrada de esta línea del carrito.
        public BigDecimal getPrecioUnitario() {
            return precioUnitario;
        }

        // Guarda el precio unitario que se usará para calcular el subtotal del carrito.
        public void setPrecioUnitario(BigDecimal precioUnitario) {
            this.precioUnitario = precioUnitario;
        }

        // Calcula el subtotal multiplicando el precio unitario por la cantidad de entradas seleccionadas.
        public BigDecimal getSubtotal() {
            if (precioUnitario == null) {
                return BigDecimal.ZERO;
            }
            return precioUnitario.multiply(BigDecimal.valueOf(cantidad)).setScale(2, RoundingMode.HALF_UP);
        }
    }

    public static class HistorialCompraView {
        private int id;
        private int idUsuario;
        private LocalDateTime fecha;
        private BigDecimal total;
        private List<HistorialDetalleView> detalles = new ArrayList<>();

        // Devuelve el identificador de la compra mostrada en el historial.
        public int getId() {
            return id;
        }

        // Asigna el identificador de la compra representada en la vista de historial.
        public void setId(int id) {
            this.id = id;
        }

        // Devuelve el identificador del usuario propietario de la compra.
        public int getIdUsuario() {
            return idUsuario;
        }

        // Guarda el identificador del usuario asociado a la compra del historial.
        public void setIdUsuario(int idUsuario) {
            this.idUsuario = idUsuario;
        }

        // Devuelve la fecha y hora en la que se registró la compra.
        public LocalDateTime getFecha() {
            return fecha;
        }

        // Establece la fecha y hora que se mostrará para la compra del historial.
        public void setFecha(LocalDateTime fecha) {
            this.fecha = fecha;
        }

        // Devuelve el total pagado en la compra representada.
        public BigDecimal getTotal() {
            return total;
        }

        // Guarda el importe total pagado en la compra del historial.
        public void setTotal(BigDecimal total) {
            this.total = total;
        }

        // Devuelve el detalle de tickets asociado a la compra mostrada.
        public List<HistorialDetalleView> getDetalles() {
            return detalles;
        }

        // Asigna la lista de detalles que cuelga de una compra del historial.
        public void setDetalles(List<HistorialDetalleView> detalles) {
            this.detalles = detalles;
        }
    }

    public static class HistorialDetalleView {
        private int idTicket;
        private String nombreEvento;
        private java.time.LocalDate fechaEvento;
        private String nombreLugar;
        private String ciudadLugar;
        private String tipo;
        private int cantidad;
        private BigDecimal precioUnitario;
        private BigDecimal subtotal;
        private String fila;
        private Integer numeroAsiento;
        private String zona;

        // Devuelve el identificador del ticket que forma parte del detalle del historial.
        public int getIdTicket() {
            return idTicket;
        }

        // Asigna el identificador del ticket representado en el detalle del historial.
        public void setIdTicket(int idTicket) {
            this.idTicket = idTicket;
        }

        // Devuelve el nombre del evento asociado al ticket comprado.
        public String getNombreEvento() {
            return nombreEvento;
        }

        // Guarda el nombre del evento que se mostrará en el detalle del historial.
        public void setNombreEvento(String nombreEvento) {
            this.nombreEvento = nombreEvento;
        }

        // Devuelve la fecha del evento relacionado con el ticket.
        public java.time.LocalDate getFechaEvento() {
            return fechaEvento;
        }

        // Establece la fecha del evento para mostrarla dentro del historial.
        public void setFechaEvento(java.time.LocalDate fechaEvento) {
            this.fechaEvento = fechaEvento;
        }

        // Devuelve el nombre del lugar en el que se celebra el evento.
        public String getNombreLugar() {
            return nombreLugar;
        }

        // Guarda el nombre del lugar asociado al evento del ticket.
        public void setNombreLugar(String nombreLugar) {
            this.nombreLugar = nombreLugar;
        }

        // Devuelve la ciudad del lugar asociado al evento comprado.
        public String getCiudadLugar() {
            return ciudadLugar;
        }

        // Guarda la ciudad del lugar para mostrarla en el historial.
        public void setCiudadLugar(String ciudadLugar) {
            this.ciudadLugar = ciudadLugar;
        }

        // Devuelve el tipo de entrada comprado para ese ticket.
        public String getTipo() {
            return tipo;
        }

        // Establece el tipo de entrada que se mostrará en el detalle del historial.
        public void setTipo(String tipo) {
            this.tipo = tipo;
        }

        // Devuelve la cantidad de entradas agrupadas en este detalle del historial.
        public int getCantidad() {
            return cantidad;
        }

        // Guarda la cantidad de entradas correspondiente a este detalle del historial.
        public void setCantidad(int cantidad) {
            this.cantidad = cantidad;
        }

        // Devuelve el precio unitario aplicado a cada entrada del detalle.
        public BigDecimal getPrecioUnitario() {
            return precioUnitario;
        }

        // Guarda el precio unitario de las entradas incluidas en el detalle.
        public void setPrecioUnitario(BigDecimal precioUnitario) {
            this.precioUnitario = precioUnitario;
        }

        // Devuelve el subtotal calculado para el detalle del historial.
        public BigDecimal getSubtotal() {
            return subtotal;
        }

        // Establece el subtotal ya calculado que se mostrará en el detalle del historial.
        public void setSubtotal(BigDecimal subtotal) {
            this.subtotal = subtotal;
        }

        // Devuelve la fila del asiento asignado al ticket, si existe.
        public String getFila() {
            return fila;
        }

        // Guarda la fila del asiento asociado al ticket comprado.
        public void setFila(String fila) {
            this.fila = fila;
        }

        // Devuelve el número del asiento asignado al ticket, si existe.
        public Integer getNumeroAsiento() {
            return numeroAsiento;
        }

        // Guarda el número de asiento relacionado con el ticket del historial.
        public void setNumeroAsiento(Integer numeroAsiento) {
            this.numeroAsiento = numeroAsiento;
        }

        // Devuelve la zona del asiento asignado dentro del recinto.
        public String getZona() {
            return zona;
        }

        // Guarda la zona del asiento para mostrarla dentro del historial de compras.
        public void setZona(String zona) {
            this.zona = zona;
        }
    }
}
