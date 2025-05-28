package org.dbclient.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private Connection connection;

    private void loadDriver() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found.");
            e.printStackTrace();
        }
    }

    // koneksi awal ke server 
    public void connect(String host, String port, String user, String password) throws SQLException {
        loadDriver(); 
        String url = "jdbc:mysql://" + host + ":" + port + "/";
        this.connection = DriverManager.getConnection(url, user, password);
    }

    // koneksi ke database tertentu setelah dipilih
    public void connectToDatabase(String host, String port, String user, String password, String dbName) throws SQLException {
        loadDriver(); 
        String url = "jdbc:mysql://" + host + ":" + port + "/" + dbName;
        this.connection = DriverManager.getConnection(url, user, password);
    }

    public Connection getConnection() {
        return connection;
    }
}
