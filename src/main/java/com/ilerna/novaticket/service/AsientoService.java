package com.ilerna.novaticket.service;

import com.ilerna.novaticket.model.Asiento;
import com.ilerna.novaticket.repository.AsientoDAO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Servicio de lógica de negocio para los asientos.
 * Ofrece operaciones CRUD y la creación masiva de asientos por fila y zona.
 */
@Service
public class AsientoService {

    private final AsientoDAO asientoDAO;

    /**
     * Constructor con inyección del DAO de asientos.
     */
    public AsientoService(@Qualifier("asientoDAOJdbc") AsientoDAO asientoDAO) {
        this.asientoDAO = asientoDAO;
    }

    /**
     * Persiste un único asiento en la base de datos.
     */
    public void guardarAsiento(Asiento asiento) {
        asientoDAO.guardar(asiento);
    }

    /**
     * Crea en masa múltiples asientos para una fila y lugar dados.
     * Genera asientos de tipo General, VIP y Premium según las cantidades indicadas,
     * numerándolos consecutivamente a partir del número más alto ya existente en esa fila.
     *
     * @param base             Asiento base con id_lugar y fila ya rellenados.
     * @param cantidadGeneral  Número de asientos de zona general a crear.
     * @param cantidadVip      Número de asientos de zona VIP a crear.
     * @param cantidadPremium  Número de asientos de zona Premium a crear.
     */
    public void guardarAsientosMasivos(Asiento base, int cantidadGeneral, int cantidadVip, int cantidadPremium) {
        if (base == null || base.getId_lugar() <= 0 || base.getFila() == null || base.getFila().isBlank()) {
            return;
        }

        // Calcula el siguiente número libre en la fila para evitar duplicados
        int siguienteNumero = calcularSiguienteNumero(base.getId_lugar(), base.getFila());
        Map<String, Integer> cantidadesPorTipo = new LinkedHashMap<>();
        cantidadesPorTipo.put("general", Math.max(cantidadGeneral, 0));
        cantidadesPorTipo.put("vip", Math.max(cantidadVip, 0));
        cantidadesPorTipo.put("premium", Math.max(cantidadPremium, 0));

        // Crea y persiste cada asiento según tipo y cantidad
        for (Map.Entry<String, Integer> entry : cantidadesPorTipo.entrySet()) {
            String tipo = entry.getKey();
            int cantidad = entry.getValue();
            for (int i = 0; i < cantidad; i++) {
                Asiento asiento = new Asiento();
                asiento.setId_lugar(base.getId_lugar());
                asiento.setFila(base.getFila().trim());
                asiento.setNumero_asiento(siguienteNumero++);
                asiento.setZona(normalizarZona(tipo));
                asientoDAO.guardar(asiento);
            }
        }
    }

    /**
     * Actualiza los datos de un asiento existente en la base de datos.
     */
    public void actualizarAsiento(Asiento asiento) {
        asientoDAO.actualizar(asiento);
    }

    /**
     * Elimina el asiento con el id indicado.
     */
    public void eliminarAsiento(int id) {
        asientoDAO.eliminar(id);
    }

    /**
     * Obtiene un asiento por su id. Devuelve null si no existe.
     */
    public Asiento obtenerAsientoPorId(int id) {
        return asientoDAO.obtenerPorId(id);
    }

    /**
     * Devuelve la lista completa de asientos almacenados en la base de datos.
     */
    public List<Asiento> listarTodosLosAsientos() {
        return asientoDAO.listarTodos();
    }

    /**
     * Calcula el siguiente número libre de asiento en una fila y lugar concretos.
     * Recorre todos los asientos del lugar y devuelve el máximo encontrado + 1.
     */
    private int calcularSiguienteNumero(int idLugar, String fila) {
        int max = 0;
        String filaNormalizada = fila == null ? "" : fila.trim().toLowerCase(Locale.ROOT);
        for (Asiento asiento : asientoDAO.listarTodos()) {
            if (asiento.getId_lugar() != idLugar) {
                continue;
            }
            String filaAsiento = asiento.getFila() == null ? "" : asiento.getFila().trim().toLowerCase(Locale.ROOT);
            if (!filaAsiento.equals(filaNormalizada)) {
                continue;
            }
            max = Math.max(max, asiento.getNumero_asiento());
        }
        return max + 1;
    }

    /**
     * Normaliza el nombre de zona a uno de los valores permitidos: general, vip o premium.
     * Cualquier valor desconocido se trata como general.
     */
    private String normalizarZona(String zona) {
        if (zona == null) {
            return "general";
        }
        String valor = zona.trim().toLowerCase(Locale.ROOT);
        if ("vip".equals(valor) || "premium".equals(valor)) {
            return valor;
        }
        return "general";
    }
}
