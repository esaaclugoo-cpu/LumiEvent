package com.ilerna.novaticket.config;

import com.ilerna.novaticket.controller.AuthController;
import com.ilerna.novaticket.model.Usuario;
import com.ilerna.novaticket.model.UsuarioEnum;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Interceptor de seguridad que protege las rutas del panel admin y las rutas de compra del cliente.
 * Se ejecuta antes de cada petición HTTP para verificar si el usuario tiene los permisos necesarios.
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    /**
     * Se ejecuta antes de que el controlador procese la petición.
     * Comprueba si la ruta requiere autenticación o rol de administrador y redirige si no se cumple.
     *
     * @return true si la petición puede continuar, false si se redirige al usuario.
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Obtiene la ruta solicitada y el usuario de sesión actual
        String path = request.getRequestURI();
        HttpSession session = request.getSession(false);
        Usuario usuario = session == null ? null : (Usuario) session.getAttribute(AuthController.USUARIO_SESION_KEY);

        // Protege las rutas de administración: exige sesión activa con rol admin
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

        // Protege las rutas de compra: exige sesión activa de cualquier usuario
        if (esRutaCompraCliente(path) && usuario == null) {
            response.sendRedirect("/login?error=Inicia%20sesion%20para%20comprar%20tickets");
            return false;
        }

        return true;
    }

    /**
     * Determina si la ruta pertenece al panel de administración.
     * Incluye /admin y todos los CRUD de entidades gestionadas por el admin.
     */
    private boolean esRutaAdmin(String path) {
        return path.equals("/admin")
                || path.startsWith("/eventos")
                || path.startsWith("/asientos")
                || path.startsWith("/compras")
                || path.startsWith("/lugares")
                || path.startsWith("/usuarios")
                || path.startsWith("/tickets");
    }

    /**
     * Determina si la ruta corresponde a operaciones de compra del cliente.
     * Exige que el usuario haya iniciado sesión antes de acceder al carrito o añadir tickets.
     */
    private boolean esRutaCompraCliente(String path) {
        return path.startsWith("/cliente/carrito")
                || path.contains("/agregar-carrito");
    }
}
