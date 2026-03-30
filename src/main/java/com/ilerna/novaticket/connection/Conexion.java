package com.ilerna.novaticket.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {

    private static final String URL = "jdbc:mysql://localhost:3306/novaticket";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    private static Conexion instancia;
    private Connection connection;

    // Constructor privado para evitar la creación externa de instancias
    private Conexion() {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Conexión a la base de datos establecida.");
        } catch (SQLException e) {
            System.err.println("❌ Error al conectar a la base de datos.");
            e.printStackTrace();
        }
    }

    // Método para obtener la instancia del Singleton
    public static synchronized Conexion getInstancia() {
        if (instancia == null || instancia.connection == null) {
            instancia = new Conexion();
        }
        return instancia;
    }

    // Método para obtener la conexión activa
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("✅ Nueva conexión establecida.");
            }
            return connection;
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener la conexión: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // Método para cerrar la conexión manualmente
    public void cerrarConexion() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("✅ Conexión cerrada correctamente.");
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al cerrar la conexión.");
            e.printStackTrace();
        }
    }
}
