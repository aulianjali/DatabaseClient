package org.dbclient.controller;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.dbclient.model.DatabaseConnection;

public class ConnectionController {

    private static DatabaseConnection dbConnection = new DatabaseConnection();

    // simpan info koneksi terakhir
    private static String currentHost;
    private static String currentPort;
    private static String currentUser;
    private static String currentPass;

    // logika koneksi dan membuka tampilan utama
    public void handleConnection(javafx.stage.Stage stage, String host, String user, String pass, String port) {
        try {
            dbConnection.connect(host, port, user, pass);
            
            // info koneksi terakhir
            currentHost = host;
            currentPort = port;
            currentUser = user;
            currentPass = pass;
            
            // mengoper objek Connection ke MainView
            new org.dbclient.view.MainView(dbConnection.getConnection()).start(stage);
        } catch (Exception e) {
            e.printStackTrace(); 
        }
    }

    // koneksi ulang menggunakan info koneksi terakhir
    public void reconnect() throws Exception {
        if (currentHost == null || currentPort == null || currentUser == null || currentPass == null) {
            throw new Exception("No previous connection info available.");
        }
        dbConnection.connect(currentHost, currentPort, currentUser, currentPass);
    }

    // getter info koneksi terakhir
    public static String getCurrentHost() {
        return currentHost;
    }

    public static String getCurrentPort() {
        return currentPort;
    }

    public static String getCurrentUser() {
        return currentUser;
    }

    public static String getCurrentPass() {
        return currentPass;
    }

    // mengizinkan penggunaan koneksi yang sama oleh komponen lain
    public static DatabaseConnection getDbConnection() {
        return dbConnection;
    }

    // mendapatkan daftar database dari koneksi aktif
    public List<String> getDatabaseList() {
        List<String> dbList = new ArrayList<>();
        try {
            Connection conn = dbConnection.getConnection();
            if (conn != null && !conn.isClosed()) {
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SHOW DATABASES");

                while (rs.next()) {
                    dbList.add(rs.getString(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dbList;
    }
}
