package com.ilerna.novaticket.service;


import com.ilerna.novaticket.model.Evento;
import com.ilerna.novaticket.repository.EventoDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

/**
 * Servicio de lógica de negocio para los eventos.
 * Delega las operaciones CRUD al repositorio EventoDAO y gestiona las imágenes de eventos
 * (subida, reemplazo y eliminación) en el directorio de uploads configurado.
 */
@Service
public class EventoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventoService.class);

    private final EventoDAO eventoDAO;
    // Directorio físico donde se guardan las imágenes de los eventos
    private final String uploadDir;

    /**
     * Constructor con inyección del DAO de eventos y la ruta de uploads desde application.properties.
     */
    public EventoService(@Qualifier("eventoDAOJdbc") EventoDAO eventoDAO,
                        @Value("${app.upload.dir:src/main/resources/static/uploads}") String uploadDir) {
        this.eventoDAO = eventoDAO;
        this.uploadDir = uploadDir;
    }

    /**
     * Persiste un nuevo evento en la base de datos.
     */
    public void guardarEvento(Evento evento) {
        eventoDAO.guardar(evento);
    }

    /**
     * Actualiza los datos de un evento existente en la base de datos.
     */
    public void actualizarEvento(Evento evento) {
        eventoDAO.actualizar(evento);
    }

    /**
     * Elimina un evento por su id y borra también su imagen del disco si existe.
     */
    public void eliminarEvento(int id) {
        // Obtiene el evento antes de eliminarlo para recuperar la ruta de su imagen
        Evento evento = eventoDAO.obtenerPorId(id);
        eventoDAO.eliminar(id);
        if (evento != null) {
            eliminarImagenSiExiste(evento.getRuta_imagen());
        }
    }

    /**
     * Obtiene un evento por su id. Devuelve null si no existe.
     */
    public Evento obtenerEventoPorId(int id) {
        return eventoDAO.obtenerPorId(id);
    }

    /**
     * Devuelve la lista completa de eventos con sus datos de lugar resueltos mediante JOIN.
     */
    public List<Evento> listarTodosLosEventos() {
        return eventoDAO.listarTodos();
    }

    /**
     * Guarda la imagen subida en el directorio de uploads con un nombre UUID único.
     * Si no se sube archivo nuevo, devuelve la ruta actual sin cambios.
     * Valida que el archivo sea una imagen antes de guardarlo.
     * Si se sube una nueva imagen, elimina la anterior para evitar residuos.
     *
     * @param imagenFile       Archivo subido desde el formulario (puede ser null o vacío).
     * @param rutaImagenActual Ruta de la imagen anterior, se conserva si no se sube una nueva.
     * @return Nombre del archivo guardado, o la ruta actual si no se subió ninguno.
     * @throws IllegalArgumentException Si el archivo no es una imagen válida.
     * @throws RuntimeException         Si ocurre un error de E/S al guardar la imagen.
     */
    public String guardarImagen(MultipartFile imagenFile, String rutaImagenActual) {
        // Si no se sube ningún archivo, mantiene la imagen existente
        if (imagenFile == null || imagenFile.isEmpty()) {
            return rutaImagenActual;
        }

        // Valida que el tipo MIME sea de imagen
        String tipoContenido = imagenFile.getContentType();
        if (tipoContenido == null || !tipoContenido.startsWith("image/")) {
            throw new IllegalArgumentException("El archivo debe ser una imagen valida.");
        }

        try {
            // Crea el directorio si no existe y genera un nombre único con UUID
            Path directorio = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(directorio);

            String nombreOriginal = imagenFile.getOriginalFilename();
            String extension = obtenerExtension(nombreOriginal);
            String nombreNuevo = UUID.randomUUID() + extension;
            Path destino = directorio.resolve(nombreNuevo);

            Files.copy(imagenFile.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);

            // Si se sube una nueva imagen, se elimina la anterior para no dejar residuos.
            eliminarImagenSiExiste(rutaImagenActual);

            return nombreNuevo;
        } catch (IOException e) {
            throw new RuntimeException("No se pudo guardar la imagen del evento.", e);
        }
    }

    /**
     * Extrae la extensión de un nombre de archivo (incluyendo el punto).
     * Devuelve cadena vacía si no tiene extensión.
     */
    private String obtenerExtension(String nombreArchivo) {
        if (nombreArchivo == null || !nombreArchivo.contains(".")) {
            return "";
        }

        int ultimoPunto = nombreArchivo.lastIndexOf('.');
        return nombreArchivo.substring(ultimoPunto);
    }

    /**
     * Elimina el archivo de imagen del disco si existe y la ruta no está vacía.
     * Verifica que la ruta resultante esté dentro del directorio de uploads para evitar path traversal.
     */
    private void eliminarImagenSiExiste(String rutaImagen) {
        if (rutaImagen == null || rutaImagen.isBlank()) {
            return;
        }

        try {
            Path directorio = Paths.get(uploadDir).toAbsolutePath().normalize();
            Path archivoImagen = directorio.resolve(rutaImagen).normalize();
            // Comprueba que la ruta resuelta sigue dentro del directorio permitido
            if (!archivoImagen.startsWith(directorio)) {
                LOGGER.warn("Se intento eliminar una imagen fuera del directorio permitido: {}", rutaImagen);
                return;
            }
            Files.deleteIfExists(archivoImagen);
        } catch (IOException e) {
            LOGGER.warn("No se pudo eliminar la imagen del evento: {}", rutaImagen, e);
        }
    }
}
