package com.ilerna.novaticket.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

/**
 * Configuración MVC de la aplicación.
 * Registra el interceptor de autenticación y mapea el directorio de uploads
 * para que las imágenes de eventos sean accesibles desde la URL /uploads/.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    // Interceptor de autenticación inyectado por Spring
    private final AuthInterceptor authInterceptor;

    /**
     * Constructor que recibe el interceptor de autenticación por inyección de dependencias.
     */
    public WebConfig(AuthInterceptor authInterceptor) {
        this.authInterceptor = authInterceptor;
    }

    // Directorio de uploads configurado en application.properties (por defecto en static/uploads)
    @Value("${app.upload.dir:src/main/resources/static/uploads}")
    private String uploadDir;

    /**
     * Registra el manejador de recursos estáticos para servir imágenes subidas por el admin.
     * Mapea /uploads/** al directorio físico indicado en la propiedad app.upload.dir.
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Convierte la ruta relativa a absoluta y la normaliza para el sistema operativo
        String absolutePath = Paths.get(uploadDir).toAbsolutePath().toString().replace("\\", "/");
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + absolutePath + "/");
    }

    /**
     * Registra el interceptor de autenticación para todas las rutas del proyecto.
     * Excluye rutas públicas como login, registro, recursos estáticos y la home pública.
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/login",
                        "/registro",
                        "/logout",
                        "/",
                        "/home",
                        "/cliente/home",
                        "/uploads/**",
                        "/css/**",
                        "/js/**",
                        "/webjars/**",
                        "/error"
                );
    }
}
