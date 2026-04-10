package com.ilerna.novaticket.repository;

import com.ilerna.novaticket.connection.Conexion;
import com.ilerna.novaticket.model.Ticket;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Repository
@Qualifier("ticketDAOJdbc")
public class TicketDAOJdbc implements TicketDAO {

    private Connection getConnection() {
        return Conexion.getInstancia().getConnection();
    }

    @Override
    public void guardar(Ticket ticket) {
        Connection conn = getConnection();
        if (conn == null) {
            return;
        }

        String sql = "INSERT INTO ticket (id_evento, id_asiento, tipo, id_compra, cantidad, precio_unitario) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, ticket.getId_evento());
            if (ticket.getId_asiento() == null || ticket.getId_asiento() <= 0) {
                pstmt.setNull(2, java.sql.Types.INTEGER);
            } else {
                pstmt.setInt(2, ticket.getId_asiento());
            }
            pstmt.setString(3, ticket.getTipo());
            if (ticket.getId_compra() <= 0) {
                pstmt.setNull(4, java.sql.Types.INTEGER);
            } else {
                pstmt.setInt(4, ticket.getId_compra());
            }
            pstmt.setInt(5, ticket.getCantidad());
            pstmt.setBigDecimal(6, ticket.getPrecio_unitario());

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        ticket.setId(rs.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void actualizar(Ticket ticket) {
        Connection conn = getConnection();
        if (conn == null) {
            return;
        }

        String sql = "UPDATE ticket SET id_evento = ?, id_asiento = ?, tipo = ?, id_compra = ?, cantidad = ?, precio_unitario = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, ticket.getId_evento());
            if (ticket.getId_asiento() == null || ticket.getId_asiento() <= 0) {
                pstmt.setNull(2, java.sql.Types.INTEGER);
            } else {
                pstmt.setInt(2, ticket.getId_asiento());
            }
            pstmt.setString(3, ticket.getTipo());
            if (ticket.getId_compra() <= 0) {
                pstmt.setNull(4, java.sql.Types.INTEGER);
            } else {
                pstmt.setInt(4, ticket.getId_compra());
            }
            pstmt.setInt(5, ticket.getCantidad());
            pstmt.setBigDecimal(6, ticket.getPrecio_unitario());
            pstmt.setInt(7, ticket.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void eliminar(int id) {
        Connection conn = getConnection();
        if (conn == null) {
            return;
        }

        String sql = "DELETE FROM ticket WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Ticket obtenerPorId(int id) {
        Connection conn = getConnection();
        if (conn == null) {
            return null;
        }

        String sql = "SELECT * FROM ticket WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapearTicket(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Ticket> listarTodos() {
        Connection conn = getConnection();
        List<Ticket> tickets = new ArrayList<>();
        if (conn == null) {
            return tickets;
        }

        String sql = "SELECT * FROM ticket ORDER BY id DESC";
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                tickets.add(mapearTicket(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tickets;
    }

    @Override
    public int sumarCantidadPorEvento(int idEvento) {
        Connection conn = getConnection();
        if (conn == null) {
            return 0;
        }

        String sql = "SELECT COALESCE(SUM(t.cantidad), 0) AS vendidos "
                + "FROM ticket t "
                + "JOIN compra c ON c.id = t.id_compra "
                + "WHERE t.id_evento = ? AND c.total > 0";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idEvento);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("vendidos");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    @Override
    public int sumarCantidadPorEventoYTipo(int idEvento, String tipo) {
        Connection conn = getConnection();
        if (conn == null) {
            return 0;
        }

        String sql = "SELECT COALESCE(SUM(t.cantidad), 0) AS vendidos "
                + "FROM ticket t "
                + "JOIN compra c ON c.id = t.id_compra "
                + "WHERE t.id_evento = ? AND LOWER(TRIM(t.tipo)) = LOWER(TRIM(?)) AND c.total > 0";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idEvento);
            pstmt.setString(2, tipo);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("vendidos");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    private Ticket mapearTicket(ResultSet rs) throws SQLException {
        Ticket ticket = new Ticket();
        ticket.setId(rs.getInt("id"));
        ticket.setId_evento(rs.getInt("id_evento"));
        Object idAsientoValue = rs.getObject("id_asiento");
        ticket.setId_asiento(idAsientoValue != null ? rs.getInt("id_asiento") : null);
        ticket.setTipo(rs.getString("tipo"));
        ticket.setId_compra(rs.getInt("id_compra"));
        ticket.setCantidad(rs.getInt("cantidad"));
        ticket.setPrecio_unitario(rs.getBigDecimal("precio_unitario"));
        return ticket;
    }
}
