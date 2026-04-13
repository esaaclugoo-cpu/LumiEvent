package com.ilerna.novaticket.config;

import com.ilerna.novaticket.controller.AuthController;
import com.ilerna.novaticket.model.Usuario;
import com.ilerna.novaticket.model.UsuarioEnum;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getRequestURI();
        HttpSession session = request.getSession(false);
        Usuario usuario = session == null ? null : (Usuario) session.getAttribute(AuthController.USUARIO_SESION_KEY);

        if (esRutaAdmin(path)) {
            if (usuario == null) {
                response.sendRedirect("/login?error=Debes%20iniciar%20sesion");
                return false;
            }
            if (usuario.getTipo_usuario() != UsuarioEnum.admin) {
                response.sendRedirect("/cliente/home?error=No%20tienes%20permiso%20para%20entrar%20a%20admin");
                return false;
            }
        }

        if (esRutaCompraCliente(path) && usuario == null) {
            response.sendRedirect("/login?error=Inicia%20sesion%20para%20comprar%20tickets");
            return false;
        }

        return true;
    }

    private boolean esRutaAdmin(String path) {
        return path.equals("/admin")
                || path.startsWith("/eventos")
                || path.startsWith("/asientos")
                || path.startsWith("/compras")
                || path.startsWith("/lugares")
                || path.startsWith("/usuarios")
                || path.startsWith("/tickets");
    }

    private boolean esRutaCompraCliente(String path) {
        return path.startsWith("/cliente/carrito")
                || path.contains("/agregar-carrito");
    }
}

