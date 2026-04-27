package com.ilerna.novaticket.repository;

import com.ilerna.novaticket.connection.Conexion;
import com.ilerna.novaticket.model.Lugar;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación JDBC del repositorio de lugares.
 * Accede directamente a la tabla lugar de MySQL usando la conexión Singleton de Conexion.
 */
@Repository
@Qualifier("lugarDAOJdbc")
public class LugarDAOJdbc implements LugarDAO {

    /** Obtiene la conexión activa desde el Singleton Conexion. */
    private Connection getConnection() {
        return Conexion.getInstancia().getConnection();
    }

    /**
     * Inserta un nuevo lugar en la base de datos y actualiza el id generado en el objeto.
     */
    @Override
    public void guardar(Lugar lugar) {
        Connection conn = getConnection();
        if (conn == null) {
            return;
        }

        String sql = "INSERT INTO lugar (nombre, direccion, ciudad) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, lugar.getNombre());
            pstmt.setString(2, lugar.getDireccion());
            pstmt.setString(3, lugar.getCiudad());

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        lugar.setId(rs.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Actualiza nombre, dirección y ciudad de un lugar existente identificado por su id.
     */
    @Override
    public void actualizar(Lugar lugar) {
        Connection conn = getConnection();
        if (conn == null) {
            return;
        }

        String sql = "UPDATE lugar SET nombre = ?, direccion = ?, ciudad = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, lugar.getNombre());
            pstmt.setString(2, lugar.getDireccion());
            pstmt.setString(3, lugar.getCiudad());
            pstmt.setInt(4, lugar.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Elimina el lugar con el id indicado de la base de datos.
     *
     * Antes de borrar el lugar elimina en transacción:
     * 1) Tickets de eventos celebrados en ese lugar.
     * 2) Asientos del lugar.
     *
     * Luego borra el lugar; los eventos y subtablas de evento se eliminan por ON DELETE CASCADE.
     */
    @Override
    public void eliminar(int id) {
        Connection conn = getConnection();
        if (conn == null) {
            throw new RuntimeException("No hay conexión disponible para eliminar lugar.");
        }

        String sqlEliminarTicketsDelLugar =
                "DELETE t FROM ticket t JOIN evento e ON t.id_evento = e.id WHERE e.id_lugar = ?";
        String sqlEliminarAsientos = "DELETE FROM asiento WHERE id_lugar = ?";
        String sqlEliminarLugar = "DELETE FROM lugar WHERE id = ?";

        boolean autoCommitAnterior = true;
        try {
            autoCommitAnterior = conn.getAutoCommit();
            conn.setAutoCommit(false);

            try (PreparedStatement pstmtTickets = conn.prepareStatement(sqlEliminarTicketsDelLugar);
                 PreparedStatement pstmtAsientos = conn.prepareStatement(sqlEliminarAsientos);
                 PreparedStatement pstmtLugar = conn.prepareStatement(sqlEliminarLugar)) {

                pstmtTickets.setInt(1, id);
                pstmtTickets.executeUpdate();

                pstmtAsientos.setInt(1, id);
                pstmtAsientos.executeUpdate();

                pstmtLugar.setInt(1, id);
                pstmtLugar.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ignored) {
            }
            throw new RuntimeException("No se pudo eliminar el lugar con id " + id, e);
        } finally {
            try {
                conn.setAutoCommit(autoCommitAnterior);
            } catch (SQLException ignored) {
            }
        }
    }

    /**
     * Obtiene un lugar por su id. Devuelve null si no existe o hay error de conexión.
     */
    @Override
    public Lugar obtenerPorId(int id) {
        Connection conn = getConnection();
        if (conn == null) {
            return null;
        }

        String sql = "SELECT * FROM lugar WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapearLugar(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Devuelve la lista completa de lugares ordenada por nombre.
     */
    @Override
    public List<Lugar> listarTodos() {
        Connection conn = getConnection();
        List<Lugar> lugares = new ArrayList<>();
        if (conn == null) {
            return lugares;
        }

        String sql = "SELECT * FROM lugar ORDER BY nombre";
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                lugares.add(mapearLugar(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lugares;
    }

    /**
     * Mapea una fila del ResultSet al objeto Lugar correspondiente.
     */
    private Lugar mapearLugar(ResultSet rs) throws SQLException {
        Lugar lugar = new Lugar();
        lugar.setId(rs.getInt("id"));
        lugar.setNombre(rs.getString("nombre"));
        lugar.setDireccion(rs.getString("direccion"));
        lugar.setCiudad(rs.getString("ciudad"));
        return lugar;
    }
}
