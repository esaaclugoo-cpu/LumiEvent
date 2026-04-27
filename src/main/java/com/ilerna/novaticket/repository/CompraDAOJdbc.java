package com.ilerna.novaticket.repository;

import com.ilerna.novaticket.connection.Conexion;
import com.ilerna.novaticket.model.Compra;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación JDBC del repositorio de compras.
 * Accede directamente a la tabla compra de MySQL usando la conexión Singleton de Conexion.
 */
@Repository
@Qualifier("compraDAOJdbc")
public class CompraDAOJdbc implements CompraDAO {

    /** Obtiene la conexión activa desde el Singleton Conexion. */
    private Connection getConnection() {
        return Conexion.getInstancia().getConnection();
    }

    /**
     * Inserta una nueva compra en la base de datos y actualiza el id generado en el objeto.
     */
    @Override
    public void guardar(Compra compra) {

        Connection conn = getConnection();
        if (conn == null) {
            System.err.println("No se pudo obtener conexion a la base de datos.");
            return;
        }

        String sql = "INSERT INTO compra (id_usuario, fecha, total) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, compra.getId_usuario());
            pstmt.setTimestamp(2, Timestamp.valueOf(compra.getFecha()));
            pstmt.setBigDecimal(3, compra.getTotal());

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        compra.setId(rs.getInt(1));
                    }
                }
                System.out.println("Compra guardado correctamente.");
            }
        } catch (SQLException e) {
            System.err.println("Error al guardar la compra.");
            e.printStackTrace();
        }

    }

    /**
     * Actualiza id_usuario, fecha y total de una compra existente identificada por su id.
     */
    @Override
    public void actualizar(Compra compra) {

        Connection conn = getConnection();
        if (conn == null) {
            System.err.println("❌ No se pudo obtener conexión a la base de datos.");
            return;
        }

        String sql = "UPDATE compra SET id_usuario = ?, fecha = ?, total = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, compra.getId_usuario());
            pstmt.setTimestamp(2, Timestamp.valueOf(compra.getFecha()));
            pstmt.setBigDecimal(3, compra.getTotal());
            pstmt.setInt(4, compra.getId());

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("✅ Compra actualizado correctamente.");
            } else {
                System.err.println("❌ No se encontró la compra para actualizar.");
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar la compra.");
            e.printStackTrace();
        }

    }

    /**
     * Elimina la compra con el id indicado de la base de datos.
     */
    @Override
    public void eliminar(int id) {

        Connection conn = getConnection();
        if (conn == null) {
            throw new RuntimeException("No hay conexión disponible para eliminar compra.");
        }

        String sql = "DELETE FROM compra WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("✅ Compra eliminado correctamente.");
            } else {
                System.err.println("❌ No se encontró la compra para eliminar.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("No se pudo eliminar la compra con id " + id, e);
        }

    }

    /**
     * Obtiene una compra por su id. Devuelve null si no existe o hay error de conexión.
     */
    @Override
    public Compra obtenerPorId(int id) {

        Compra compra = null;
        String sql = "SELECT * FROM compra WHERE id = ?";

        Connection conn = getConnection();
        if (conn == null) {
            System.err.println("❌ No se pudo obtener conexión a la base de datos.");
            return compra;
        }

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    compra = mapearCompra(rs);
                }
            }
            System.out.println("✅ Asiento obtenido correctamente.");
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener el asiento.");
            e.printStackTrace();
        }
        return compra;
    }

    /**
     * Devuelve la lista completa de compras de la tabla compra.
     */
    @Override
    public List<Compra> listarTodos() {

        List<Compra> compras = new ArrayList<>();
        String sql = "SELECT * FROM compra";

        Connection conn = getConnection();
        if (conn == null) {
            System.err.println("❌ No se pudo obtener conexión a la base de datos.");
            return compras;
        }

        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Compra compra = mapearCompra(rs);
                compras.add(compra);
            }
            System.out.println("✅ Listado de compras recuperado correctamente.");
        } catch (SQLException e) {
            System.err.println("❌ Error al listar las compras.");
            e.printStackTrace();
        }
        return compras;
    }

    /**
     * Mapea una fila del ResultSet al objeto Compra correspondiente.
     * Convierte el Timestamp de MySQL a LocalDateTime de Java.
     */
    private Compra mapearCompra(ResultSet rs) throws SQLException {
        Compra compra = new Compra();
        compra.setId(rs.getInt("id"));
        compra.setId_usuario(rs.getInt("id_usuario"));
        Timestamp fechaSql = rs.getTimestamp("fecha");
        compra.setFecha(fechaSql != null ? fechaSql.toLocalDateTime() : null);
        compra.setTotal(rs.getBigDecimal("total"));
        return compra;
    }
}
