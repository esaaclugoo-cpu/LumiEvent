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

@Repository
@Qualifier("lugarDAOJdbc")
public class LugarDAOJdbc implements LugarDAO {

    private Connection getConnection() {
        return Conexion.getInstancia().getConnection();
    }

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

    @Override
    public void eliminar(int id) {
        Connection conn = getConnection();
        if (conn == null) {
            return;
        }

        String sql = "DELETE FROM lugar WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

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

    private Lugar mapearLugar(ResultSet rs) throws SQLException {
        Lugar lugar = new Lugar();
        lugar.setId(rs.getInt("id"));
        lugar.setNombre(rs.getString("nombre"));
        lugar.setDireccion(rs.getString("direccion"));
        lugar.setCiudad(rs.getString("ciudad"));
        return lugar;
    }
}

