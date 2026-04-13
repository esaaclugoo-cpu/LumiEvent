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

@Controller
public class AuthController {

    public static final String USUARIO_SESION_KEY = "usuarioSesion";

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/login")
    public String mostrarLogin(@RequestParam(value = "error", required = false) String error,
                               @RequestParam(value = "ok", required = false) String ok,
                               Model model,
                               HttpSession session) {
        if (session.getAttribute(USUARIO_SESION_KEY) != null) {
            return "redirect:/cliente/home";
        }
        model.addAttribute("errorMensaje", error);
        model.addAttribute("okMensaje", ok);
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam("email") String email,
                        @RequestParam("password") String password,
                        HttpSession session) {
        Usuario usuario = usuarioService.autenticar(email, password);
        if (usuario == null) {
            return "redirect:/login?error=Credenciales%20invalidas";
        }

        session.setAttribute(USUARIO_SESION_KEY, usuario);
        if (usuario.getTipo_usuario() != null && "admin".equalsIgnoreCase(usuario.getTipo_usuario().name())) {
            return "redirect:/admin";
        }
        return "redirect:/cliente/home";
    }

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

    @PostMapping("/registro")
    public String registro(@RequestParam("nombre") String nombre,
                           @RequestParam("email") String email,
                           @RequestParam("password") String password) {
        String error = usuarioService.registrarCliente(nombre, email, password);
        if (error != null) {
            return "redirect:/registro?error=" + URLEncoder.encode(error, StandardCharsets.UTF_8);
        }
        return "redirect:/login?ok=Cuenta%20creada%20correctamente";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/cliente/home";
    }
}

