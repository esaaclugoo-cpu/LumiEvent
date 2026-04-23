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
        Connection conn = getConnection();
        if (conn == null) {
            System.err.println("❌ No se pudo obtener conexión a la base de datos.");
            return;
        }

        String sql = "INSERT INTO evento (nombre, descripcion, fecha, aforo_maximo, tipo_evento, id_lugar, ruta_imagen) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, evento.getNombre());
            pstmt.setString(2, evento.getDescripcion());
            pstmt.setDate(3, Date.valueOf(evento.getFecha()));
            pstmt.setInt(4, evento.getAforo_maximo());
            pstmt.setString(5, evento.getTipo_evento().name());
            pstmt.setInt(6, evento.getId_lugar());
            pstmt.setString(7, evento.getRuta_imagen());

            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    evento.setId(id);

                    switch (evento.getTipo_evento()) {
                        case concierto:
                            Concierto c = (Concierto) evento;
                            String sqlC = "INSERT INTO concierto (id_evento, artista_principal, genero_musical, duracion_minutos) VALUES (?, ?, ?, ?)";
                            try (PreparedStatement pstmtC = conn.prepareStatement(sqlC)) {
                                pstmtC.setInt(1, id);
                                pstmtC.setString(2, c.getArtista_principal());
                                pstmtC.setString(3, c.getGenero_musical());
                                pstmtC.setInt(4, c.getDuracion_minutos());
                                pstmtC.executeUpdate();
                            }
                            break;
                        case teatro:
                            Teatro t = (Teatro) evento;
                            String sqlT = "INSERT INTO teatro (id_evento, obra, director) VALUES (?, ?, ?)";
                            try (PreparedStatement pstmtT = conn.prepareStatement(sqlT)) {
                                pstmtT.setInt(1, id);
                                pstmtT.setString(2, t.getObra());
                                pstmtT.setString(3, t.getDirector());
                                pstmtT.executeUpdate();
                            }
                            break;
                        case museo:
                            Museo m = (Museo) evento;
                            String sqlM = "INSERT INTO museo (id_evento, nombre_exposicion, tipo_exposicion, fecha_fin) VALUES (?, ?, ?, ?)";
                            try (PreparedStatement pstmtM = conn.prepareStatement(sqlM)) {
                                pstmtM.setInt(1, id);
                                pstmtM.setString(2, m.getNombre_exposicion());
                                pstmtM.setString(3, m.getTipo_exposicion());
                                pstmtM.setDate(4, m.getFecha_fin() != null ? Date.valueOf(m.getFecha_fin()) : null);
                                pstmtM.executeUpdate();
                            }
                            break;
                    }
                }
            }
            System.out.println("✅ Evento guardado correctamente.");
        } catch (SQLException e) {
            System.err.println("❌ Error al guardar el evento.");
            e.printStackTrace();
        }
    }

    @Override
    public void actualizar(Evento evento) {
         Connection conn = getConnection();
        if (conn == null) {
            System.err.println("❌ No se pudo obtener conexión a la base de datos.");
            return;
        }

        String sql = "UPDATE evento SET nombre = ?, descripcion = ?, fecha = ?, aforo_maximo = ?, tipo_evento = ?, id_lugar = ?, ruta_imagen = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, evento.getNombre());
            pstmt.setString(2, evento.getDescripcion());
            pstmt.setDate(3, Date.valueOf(evento.getFecha()));
            pstmt.setInt(4, evento.getAforo_maximo());
            pstmt.setString(5, evento.getTipo_evento().name());
            pstmt.setInt(6, evento.getId_lugar());
            pstmt.setString(7, evento.getRuta_imagen());
            pstmt.setInt(8, evento.getId());

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                // Update subclass table
                updateSubclass(evento);
                System.out.println("✅ Evento actualizado correctamente.");
            } else {
                System.err.println("❌ No se encontró el evento para actualizar.");
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar el evento.");
            e.printStackTrace();
        }
    }

    private void updateSubclass(Evento evento) {
        Connection conn = getConnection();
        if (conn == null) return;

        try {
            switch (evento.getTipo_evento()) {
                case concierto:
                    Concierto c = (Concierto) evento;
                    String sqlC = "UPDATE concierto SET artista_principal = ?, genero_musical = ?, duracion_minutos = ? WHERE id_evento = ?";
                    try (PreparedStatement pstmt = conn.prepareStatement(sqlC)) {
                        pstmt.setString(1, c.getArtista_principal());
                        pstmt.setString(2, c.getGenero_musical());
                        pstmt.setInt(3, c.getDuracion_minutos());
                        pstmt.setInt(4, evento.getId());
                        pstmt.executeUpdate();
                    }
                    break;
                case teatro:
                    Teatro t = (Teatro) evento;
                    String sqlT = "UPDATE teatro SET obra = ?, director = ? WHERE id_evento = ?";
                    try (PreparedStatement pstmt = conn.prepareStatement(sqlT)) {
                        pstmt.setString(1, t.getObra());
                        pstmt.setString(2, t.getDirector());
                        pstmt.setInt(3, evento.getId());
                        pstmt.executeUpdate();
                    }
                    break;
                case museo:
                    Museo m = (Museo) evento;
                    String sqlM = "UPDATE museo SET nombre_exposicion = ?, tipo_exposicion = ?, fecha_fin = ? WHERE id_evento = ?";
                    try (PreparedStatement pstmt = conn.prepareStatement(sqlM)) {
                        pstmt.setString(1, m.getNombre_exposicion());
                        pstmt.setString(2, m.getTipo_exposicion());
                        pstmt.setDate(3, m.getFecha_fin() != null ? Date.valueOf(m.getFecha_fin()) : null);
                        pstmt.setInt(4, evento.getId());
                        pstmt.executeUpdate();
                    }
                    break;
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar la subclase.");
            e.printStackTrace();
        }
    }

    @Override
    public void eliminar(int id) {
        Connection conn = getConnection();
        if (conn == null) {
            System.err.println("❌ No se pudo obtener conexión a la base de datos.");
            return;
        }

        String sql = "DELETE FROM evento WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("✅ Evento eliminado correctamente.");
            } else {
                System.err.println("❌ No se encontró el evento para eliminar.");
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al eliminar el evento.");
            e.printStackTrace();
        }
    }

    @Override
    public Evento obtenerPorId(int id) {
        Evento evento = null;
        String sql = "SELECT e.*, l.nombre AS nombre_lugar_fk, l.direccion AS direccion_fk, l.ciudad AS ciudad_fk " +
                "FROM evento e INNER JOIN lugar l ON e.id_lugar = l.id WHERE e.id = ?";

        Connection conn = getConnection();
        if (conn == null) {
            System.err.println("❌ No se pudo obtener conexión a la base de datos.");
            return evento;
        }

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    evento = mapearEvento(rs);
                    // Load subclass data
                    if (evento != null) {
                        loadSubclassData(evento);
                    }
                }
            }
            System.out.println("✅ Evento obtenido correctamente.");
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener el evento.");
            e.printStackTrace();
        }
        return evento;
    }

    private void loadSubclassData(Evento evento) {
        Connection conn = getConnection();
        if (conn == null) return;

        try {
            switch (evento.getTipo_evento()) {
                case concierto:
                    String sqlC = "SELECT * FROM concierto WHERE id_evento = ?";
                    try (PreparedStatement pstmt = conn.prepareStatement(sqlC)) {
                        pstmt.setInt(1, evento.getId());
                        try (ResultSet rs = pstmt.executeQuery()) {
                            if (rs.next()) {
                                Concierto c = (Concierto) evento;
                                c.setArtista_principal(rs.getString("artista_principal"));
                                c.setGenero_musical(rs.getString("genero_musical"));
                                c.setDuracion_minutos(rs.getInt("duracion_minutos"));
                            }
                        }
                    }
                    break;
                case teatro:
                    String sqlT = "SELECT * FROM teatro WHERE id_evento = ?";
                    try (PreparedStatement pstmt = conn.prepareStatement(sqlT)) {
                        pstmt.setInt(1, evento.getId());
                        try (ResultSet rs = pstmt.executeQuery()) {
                            if (rs.next()) {
                                Teatro t = (Teatro) evento;
                                t.setObra(rs.getString("obra"));
                                t.setDirector(rs.getString("director"));
                            }
                        }
                    }
                    break;
                case museo:
                    String sqlM = "SELECT * FROM museo WHERE id_evento = ?";
                    try (PreparedStatement pstmt = conn.prepareStatement(sqlM)) {
                        pstmt.setInt(1, evento.getId());
                        try (ResultSet rs = pstmt.executeQuery()) {
                            if (rs.next()) {
                                Museo m = (Museo) evento;
                                m.setNombre_exposicion(rs.getString("nombre_exposicion"));
                                m.setTipo_exposicion(rs.getString("tipo_exposicion"));
                                Date fechaFin = rs.getDate("fecha_fin");
                                m.setFecha_fin(fechaFin != null ? fechaFin.toLocalDate() : null);
                            }
                        }
                    }
                    break;
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al cargar datos de subclase.");
            e.printStackTrace();
        }
    }

    @Override
    public List<Evento> listarTodos() {;
        List<Evento> eventos = new ArrayList<>();
        String sql = "SELECT e.*, l.nombre AS nombre_lugar_fk, l.direccion AS direccion_fk, l.ciudad AS ciudad_fk " +
                "FROM evento e INNER JOIN lugar l ON e.id_lugar = l.id";

        Connection conn = getConnection();
        if (conn == null) {
            System.err.println("❌ No se pudo obtener conexión a la base de datos.");
            return eventos;
        }
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Evento evento = mapearEvento(rs);
                if (evento != null) {
                    loadSubclassData(evento);
                }
                eventos.add(evento);
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
        int aforo_maximo = rs.getInt("aforo_maximo");
        int id_lugar = rs.getInt("id_lugar");
        String nombre_lugar = rs.getString("nombre_lugar_fk");
        String direccion = rs.getString("direccion_fk");
        String ciudad = rs.getString("ciudad_fk");
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
            evento.setAforo_maximo(aforo_maximo);
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
