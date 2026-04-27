package com.ilerna.novaticket;

import com.ilerna.novaticket.model.Concierto;
import com.ilerna.novaticket.repository.EventoDAO;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDate;

/**
 * Clase principal de la aplicación NovaTicket.
 * Arranca el contexto de Spring Boot con todas las configuraciones automáticas.
 */
@SpringBootApplication
public class NovaTicketApplication {

	/**
	 * Punto de entrada de la aplicación.
	 * Inicia el servidor embebido y registra todos los beans de Spring.
	 */
	public static void main(String[] args) {
		SpringApplication.run(NovaTicketApplication.class, args);
	}
}
