package com.ilerna.novaticket.repository;

import com.ilerna.novaticket.connection.Conexion;
import com.ilerna.novaticket.model.Usuario;
import com.ilerna.novaticket.model.UsuarioEnum;
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
 * Implementación JDBC del repositorio de usuarios.
 * Accede directamente a la tabla usuario de MySQL usando la conexión Singleton de Conexion.
 */
@Repository
@Qualifier("usuarioDAOJdbc")
public class UsuarioDAOJdbc implements UsuarioDAO {

    /** Obtiene la conexión activa desde el Singleton Conexion. */
    private Connection getConnection() {
        return Conexion.getInstancia().getConnection();
    }

    /**
     * Inserta un nuevo usuario en la base de datos y actualiza el id generado en el objeto.
     */
    @Override
    public void guardar(Usuario usuario) {
        Connection conn = getConnection();
        if (conn == null) {
            return;
        }

        String sql = "INSERT INTO usuario (nombre, email, password, tipo_usuario) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, usuario.getNombre());
            pstmt.setString(2, usuario.getEmail());
            pstmt.setString(3, usuario.getPassword());
            pstmt.setString(4, usuario.getTipo_usuario().name());

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        usuario.setId(rs.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Actualiza nombre, email, contraseña y tipo_usuario de un usuario existente por su id.
     */
    @Override
    public void actualizar(Usuario usuario) {
        Connection conn = getConnection();
        if (conn == null) {
            return;
        }

        String sql = "UPDATE usuario SET nombre = ?, email = ?, password = ?, tipo_usuario = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, usuario.getNombre());
            pstmt.setString(2, usuario.getEmail());
            pstmt.setString(3, usuario.getPassword());
            pstmt.setString(4, usuario.getTipo_usuario().name());
            pstmt.setInt(5, usuario.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Elimina el usuario con el id indicado.
     * Antes elimina sus compras asociadas en la misma transacción
     * (los tickets de esas compras se eliminan por cascada en BD).
     */
    @Override
    public void eliminar(int id) {
        Connection conn = getConnection();
        if (conn == null) {
            return;
        }

        String sqlEliminarCompras = "DELETE FROM compra WHERE id_usuario = ?";
        String sqlEliminarUsuario = "DELETE FROM usuario WHERE id = ?";

        boolean autoCommitAnterior = true;
        try {
            autoCommitAnterior = conn.getAutoCommit();
            conn.setAutoCommit(false);

            try (PreparedStatement pstmtCompras = conn.prepareStatement(sqlEliminarCompras);
                 PreparedStatement pstmtUsuario = conn.prepareStatement(sqlEliminarUsuario)) {
                // Primero borra compras del usuario; ticket se borra por ON DELETE CASCADE en compra
                pstmtCompras.setInt(1, id);
                pstmtCompras.executeUpdate();

                // Luego borra el usuario
                pstmtUsuario.setInt(1, id);
                pstmtUsuario.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ignored) {
            }
            throw new RuntimeException("No se pudo eliminar el usuario con id " + id, e);
        } finally {
            try {
                conn.setAutoCommit(autoCommitAnterior);
            } catch (SQLException ignored) {
            }
        }
    }

    /**
     * Obtiene un usuario por su id. Devuelve null si no existe o hay error de conexión.
     */
    @Override
    public Usuario obtenerPorId(int id) {
        Connection conn = getConnection();
        if (conn == null) {
            return null;
        }

        String sql = "SELECT * FROM usuario WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapearUsuario(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Obtiene un usuario buscando por email de forma insensible a mayúsculas.
     * Devuelve null si no existe, el email es nulo/vacío o hay error de conexión.
     * Se usa en el proceso de autenticación y validación de registro duplicado.
     */
    @Override
    public Usuario obtenerPorEmail(String email) {
        Connection conn = getConnection();
        if (conn == null || email == null || email.isBlank()) {
            return null;
        }

        String sql = "SELECT * FROM usuario WHERE LOWER(TRIM(email)) = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email.trim().toLowerCase());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapearUsuario(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Devuelve la lista completa de usuarios ordenada por nombre.
     */
    @Override
    public List<Usuario> listarTodos() {
        Connection conn = getConnection();
        List<Usuario> usuarios = new ArrayList<>();
        if (conn == null) {
            return usuarios;
        }

        String sql = "SELECT * FROM usuario ORDER BY nombre";
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                usuarios.add(mapearUsuario(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return usuarios;
    }

    /**
     * Mapea una fila del ResultSet al objeto Usuario correspondiente.
     * Convierte el campo tipo_usuario de String a UsuarioEnum.
     */
    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setId(rs.getInt("id"));
        usuario.setNombre(rs.getString("nombre"));
        usuario.setEmail(rs.getString("email"));
        usuario.setPassword(rs.getString("password"));
        usuario.setTipo_usuario(UsuarioEnum.valueOf(rs.getString("tipo_usuario")));
        return usuario;
    }
}

