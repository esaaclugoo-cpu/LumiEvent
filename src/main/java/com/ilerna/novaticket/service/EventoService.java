package com.ilerna.novaticket.service;


import com.ilerna.novaticket.model.Evento;
import com.ilerna.novaticket.repository.EventoDAO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
public class EventoService {

    private final EventoDAO eventoDAO;
    private final String uploadDir;

    public EventoService(@Qualifier("eventoDAOJdbc") EventoDAO eventoDAO,
                        @Value("${app.upload.dir:src/main/resources/static/uploads}") String uploadDir) {
        this.eventoDAO = eventoDAO;
        this.uploadDir = uploadDir;
    }

    public void guardarEvento(Evento evento) {
        eventoDAO.guardar(evento);
    }

    public void actualizarEvento(Evento evento) {
        eventoDAO.actualizar(evento);
    }

    public void eliminarEvento(int id) {
        eventoDAO.eliminar(id);
    }

    public Evento obtenerEventoPorId(int id) {
        return eventoDAO.obtenerPorId(id);
    }

    public List<Evento> listarTodosLosEventos() {
        return eventoDAO.listarTodos();
    }

    public String guardarImagen(MultipartFile imagenFile, String rutaImagenActual) {
        if (imagenFile == null || imagenFile.isEmpty()) {
            return rutaImagenActual;
        }

        String tipoContenido = imagenFile.getContentType();
        if (tipoContenido == null || !tipoContenido.startsWith("image/")) {
            throw new IllegalArgumentException("El archivo debe ser una imagen valida.");
        }

        try {
            Path directorio = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(directorio);

            String nombreOriginal = imagenFile.getOriginalFilename();
            String extension = obtenerExtension(nombreOriginal);
            String nombreNuevo = UUID.randomUUID() + extension;
            Path destino = directorio.resolve(nombreNuevo);

            Files.copy(imagenFile.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);

            // Si se sube una nueva imagen, se elimina la anterior para no dejar residuos.
            if (rutaImagenActual != null && !rutaImagenActual.isBlank()) {
                Files.deleteIfExists(directorio.resolve(rutaImagenActual));
            }

            return nombreNuevo;
        } catch (IOException e) {
            throw new RuntimeException("No se pudo guardar la imagen del evento.", e);
        }
    }

    private String obtenerExtension(String nombreArchivo) {
        if (nombreArchivo == null || !nombreArchivo.contains(".")) {
            return "";
        }

        int ultimoPunto = nombreArchivo.lastIndexOf('.');
        return nombreArchivo.substring(ultimoPunto);
    }


}
