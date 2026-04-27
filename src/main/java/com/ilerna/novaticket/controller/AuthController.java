package com.ilerna.novaticket.controller;

import com.ilerna.novaticket.model.Usuario;
import com.ilerna.novaticket.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Controlador de autenticación y gestión de perfil.
 * Gestiona el inicio de sesión, registro, cierre de sesión y actualización de datos del usuario.
 */
@Controller
public class AuthController {

    // Clave usada para almacenar el usuario autenticado en la sesión HTTP
    public static final String USUARIO_SESION_KEY = "usuarioSesion";

    private final UsuarioService usuarioService;

    /**
     * Constructor con inyección del servicio de usuarios.
     */
    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     * Muestra el formulario de inicio de sesión.
     * Si el usuario ya tiene sesión activa, lo redirige directamente al home del cliente.
     *
     * @param error   Mensaje de error opcional a mostrar (credenciales incorrectas, etc.)
     * @param ok      Mensaje de éxito opcional (p. ej. cuenta creada correctamente)
     */
    @GetMapping("/login")
    public String mostrarLogin(@RequestParam(value = "error", required = false) String error,
                               @RequestParam(value = "ok", required = false) String ok,
                               Model model,
                               HttpSession session) {
        // Si ya hay sesión iniciada, no tiene sentido mostrar el login
        if (session.getAttribute(USUARIO_SESION_KEY) != null) {
            return "redirect:/cliente/home";
        }
        model.addAttribute("errorMensaje", error);
        model.addAttribute("okMensaje", ok);
        return "login";
    }

    /**
     * Procesa el formulario de inicio de sesión.
     * Autentica al usuario con email y contraseña; si es admin lo redirige al panel admin.
     */
    @PostMapping("/login")
    public String login(@RequestParam("email") String email,
                        @RequestParam("password") String password,
                        HttpSession session) {
        // Valida las credenciales contra la base de datos
        Usuario usuario = usuarioService.autenticar(email, password);
        if (usuario == null) {
            return "redirect:/login?error=Credenciales%20invalidas";
        }

        // Guarda el usuario en sesión y redirige según su rol
        session.setAttribute(USUARIO_SESION_KEY, usuario);
        if (usuario.getTipo_usuario() != null && "admin".equalsIgnoreCase(usuario.getTipo_usuario().name())) {
            return "redirect:/admin";
        }
        return "redirect:/cliente/home";
    }

    /**
     * Muestra el formulario de registro de nuevo cliente.
     * Si el usuario ya tiene sesión activa, lo redirige al home.
     */
    @GetMapping("/registro")
    public String mostrarRegistro(@RequestParam(value = "error", required = false) String error,
                                  Model model,
                                  HttpSession session) {
        if (session.getAttribute(USUARIO_SESION_KEY) != null) {
            return "redirect:/cliente/home";
        }
        model.addAttribute("errorMensaje", error);
        return "registro";
    }

    /**
     * Procesa el formulario de registro.
     * Delega la validación y creación del usuario al servicio; si hay error lo muestra en la vista.
     */
    @PostMapping("/registro")
    public String registro(@RequestParam("nombre") String nombre,
                           @RequestParam("email") String email,
                           @RequestParam("password") String password) {
        // Intenta registrar el cliente; si hay error lo codifica en la URL de redirección
        String error = usuarioService.registrarCliente(nombre, email, password);
        if (error != null) {
            return "redirect:/registro?error=" + URLEncoder.encode(error, StandardCharsets.UTF_8);
        }
        return "redirect:/login?ok=Cuenta%20creada%20correctamente";
    }

    /**
     * Muestra la página de perfil del usuario autenticado con sus datos actuales.
     */
    @GetMapping("/cliente/perfil")
    public String mostrarPerfil(Model model, HttpSession session) {
        Usuario usuarioSesion = obtenerUsuarioSesion(session);
        if (usuarioSesion == null) {
            return "redirect:/login";
        }
        model.addAttribute("usuario", usuarioSesion);
        return "perfil";
    }

    /**
     * Procesa la actualización de datos del perfil (nombre, email y contraseña opcional).
     * Solo actualiza la contraseña si se ha introducido un valor no vacío.
     */
    @PostMapping("/cliente/perfil/actualizar")
    public String actualizarPerfil(@RequestParam("nombre") String nombre,
                                   @RequestParam("email") String email,
                                   @RequestParam("password") String password,
                                   HttpSession session) {
        Usuario usuarioSesion = obtenerUsuarioSesion(session);
        if (usuarioSesion == null) {
            return "redirect:/login";
        }

        // Actualiza los campos del usuario en sesión
        usuarioSesion.setNombre(nombre);
        usuarioSesion.setEmail(email);
        // La contraseña solo se cambia si el usuario introduce un valor nuevo
        if (password != null && !password.isBlank()) {
            usuarioSesion.setPassword(password);
        }

        // Persiste los cambios y refresca el objeto de sesión
        usuarioService.guardarUsuario(usuarioSesion);
        session.setAttribute(USUARIO_SESION_KEY, usuarioSesion);

        return "redirect:/cliente/perfil?ok=Perfil%20actualizado%20correctamente";
    }

    /**
     * Cierra la sesión del usuario invalidando todos los datos almacenados en ella.
     * Redirige a la home pública tras el logout.
     */
    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/cliente/home";
    }

    /**
     * Extrae el objeto Usuario de la sesión HTTP de forma segura.
     * Devuelve null si no hay sesión activa o el atributo no es de tipo Usuario.
     */
    private Usuario obtenerUsuarioSesion(HttpSession session) {
        Object usuario = session.getAttribute(USUARIO_SESION_KEY);
        return usuario instanceof Usuario ? (Usuario) usuario : null;
    }
}

