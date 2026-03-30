package com.ilerna.novaticket.repository;
import com.ilerna.novaticket.connection.Conexion;
import com.ilerna.novaticket.model.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
@Qualifier("eventoDAOJdbc")
public class EventoDAOJdbc implements EventoDAO{

    private Connection getConnection() {
        return Conexion.getInstancia().getConnection();
    }


    @Override
    public void guardar(Evento evento) {

    }

    @Override
    public void actualizar(Evento evento) {

    }

    @Override
    public void eliminar(int id) {

    }

    @Override
    public Evento obtenerPorId(int id) {
        return null;
    }

    @Override
    public List<Evento> listarTodos() {;
        List<Evento> eventos = new ArrayList<>();
        String sql = "SELECT * FROM evento";
        
        Connection conn = getConnection();
        if (conn == null) {
            System.err.println("❌ No se pudo obtener conexión a la base de datos.");
            return eventos;
        }
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                eventos.add(mapearEvento(rs));
            }
            System.out.println("✅ Listado de eventos recuperado correctamente.");
        } catch (SQLException e) {
            System.err.println("❌ Error al listar los eventos.");
            e.printStackTrace();
        }
        return eventos;
    }

    private Evento mapearEvento(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String nombre = rs.getString("nombre");
        String descripcion = rs.getString("descripcion");
        LocalDate fecha = rs.getDate("fecha").toLocalDate();
        int id_lugar = rs.getInt("id_lugar");
        String nombre_lugar = rs.getString("nombre_lugar");
        String direccion = rs.getString("direccion");
        String ciudad = rs.getString("ciudad");
        String ruta_imagen = rs.getString("ruta_imagen");
        EventoEnum tipo = EventoEnum.valueOf(rs.getString("tipo_evento"));

        Evento evento = null;
        switch (tipo) {
            case concierto:
                evento = new Concierto();
                break;
            case teatro:
                evento = new Teatro();
                break;
            case museo:
                evento = new Museo();
                break;
        }
        if (evento != null) {
            evento.setId(id);
            evento.setNombre(nombre);
            evento.setDescripcion(descripcion);
            evento.setFecha(fecha);
            evento.setId_lugar(id_lugar);
            evento.setNombre_lugar(nombre_lugar);
            evento.setDireccion(direccion);
            evento.setCiudad(ciudad);
            evento.setRuta_imagen(ruta_imagen);
            evento.setTipo_evento(tipo);
        }
        return evento;
    }
}
