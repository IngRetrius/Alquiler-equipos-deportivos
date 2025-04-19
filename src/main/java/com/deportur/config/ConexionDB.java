package com.deportur.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConexionDB {
    private static String url;
    private static String usuario;
    private static String password;
    
    // Usar ThreadLocal para mantener una conexión por hilo
    private static ThreadLocal<Connection> threadConnection = new ThreadLocal<>();
    
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
        Connection conn = threadConnection.get();
        
        if (conn == null || conn.isClosed()) {
            conn = DriverManager.getConnection(url, usuario, password);
            threadConnection.set(conn);
        }
        
        return conn;
    }
    
    // Método para cerrar la conexión
    public static void cerrarConexion() {
        Connection conn = threadConnection.get();
        if (conn != null) {
            try {
                conn.close();
                threadConnection.remove();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    // Método para iniciar una transacción
    public static void iniciarTransaccion() throws SQLException {
        Connection conn = getConexion();
        if (conn != null) {
            conn.setAutoCommit(false);
        }
    }
    
    // Método para confirmar una transacción
    public static void confirmarTransaccion() throws SQLException {
        Connection conn = threadConnection.get();
        if (conn != null) {
            conn.commit();
            conn.setAutoCommit(true);
        }
    }
    
    // Método para revertir una transacción
    public static void revertirTransaccion() throws SQLException {
        Connection conn = threadConnection.get();
        if (conn != null) {
            conn.rollback();
            conn.setAutoCommit(true);
        }
    }
}