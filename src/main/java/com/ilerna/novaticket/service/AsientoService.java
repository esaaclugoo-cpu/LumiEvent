package com.ilerna.novaticket.service;

import com.ilerna.novaticket.model.Asiento;
import com.ilerna.novaticket.repository.AsientoDAO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class AsientoService {

    private final AsientoDAO asientoDAO;

    public AsientoService(@Qualifier("asientoDAOJdbc") AsientoDAO asientoDAO) {
        this.asientoDAO = asientoDAO;
    }

    public void guardarAsiento(Asiento asiento) {
        asientoDAO.guardar(asiento);
    }

    public void guardarAsientosMasivos(Asiento base, int cantidadGeneral, int cantidadVip, int cantidadPremium) {
        if (base == null || base.getId_lugar() <= 0 || base.getFila() == null || base.getFila().isBlank()) {
            return;
        }

        int siguienteNumero = calcularSiguienteNumero(base.getId_lugar(), base.getFila());
        Map<String, Integer> cantidadesPorTipo = new LinkedHashMap<>();
        cantidadesPorTipo.put("general", Math.max(cantidadGeneral, 0));
        cantidadesPorTipo.put("vip", Math.max(cantidadVip, 0));
        cantidadesPorTipo.put("premium", Math.max(cantidadPremium, 0));

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

    public void actualizarAsiento(Asiento asiento) {
        asientoDAO.actualizar(asiento);
    }

    public void eliminarAsiento(int id) {
        asientoDAO.eliminar(id);
    }

    public Asiento obtenerAsientoPorId(int id) {
        return asientoDAO.obtenerPorId(id);
    }

    public List<Asiento> listarTodosLosAsientos() {
        return asientoDAO.listarTodos();
    }

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
