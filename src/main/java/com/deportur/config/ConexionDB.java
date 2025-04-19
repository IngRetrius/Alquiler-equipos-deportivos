package com.deportur.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConexionDB {
    private static Connection conexion = null;
    private static String url;
    private static String usuario;
    private static String password;
    
    static {
        try {
            Properties props = new Properties();
            props.load(new FileInputStream("config/config.properties"));
            
            url = props.getProperty("db.url");
            usuario = props.getProperty("db.user");
            password = props.getProperty("db.password");
            
            // Registrar el driver de MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    // Método para obtener la conexión
    public static Connection getConexion() throws SQLException {
        if (conexion == null || conexion.isClosed()) {
            conexion = DriverManager.getConnection(url, usuario, password);
        }
        return conexion;
    }
    
    // Método para cerrar la conexión
    public static void cerrarConexion() {
        if (conexion != null) {
            try {
                conexion.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}