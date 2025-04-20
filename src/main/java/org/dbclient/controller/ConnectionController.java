package org.dbclient.controller;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.dbclient.model.DatabaseConnection;

public class ConnectionController {

    // Enkapsulasi: objek dbConnection dibuat private dan static, akses lewat getter
    private static DatabaseConnection dbConnection = new DatabaseConnection();

    // Metode ini menjalankan logika koneksi dan membuka tampilan utama
    // menangani koneksi dan transisi ke tampilan utama
    public void handleConnection(javafx.stage.Stage stage, String host, String user, String pass, String port) {
        try {
            dbConnection.connect(host, port, user, pass);
            // Dependency Injection: mengoper objek Connection ke MainView
            new org.dbclient.view.MainView(dbConnection.getConnection()).start(stage);
        } catch (Exception e) {
            e.printStackTrace(); // Error handling sederhana
        }
    }

    // Getter untuk mengakses objek koneksi database secara global
    // Memungkinkan penggunaan koneksi yang sama oleh komponen lain
    public static DatabaseConnection getDbConnection() {
        return dbConnection;
    }

    // Mendapatkan daftar database dari koneksi aktif
    // Penerapan logika bisnis yang sesuai dengan perannya sebagai controller
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
            e.printStackTrace(); // Error handling 
        }
        return dbList;
    }
}

// - Class ini berperan sebagai "Controller" dalam pola MVC, menjembatani antara model (DatabaseConnection) dan view (MainView).
// - Menggunakan enkapsulasi dengan menyimpan objek 'dbConnection' secara privat dan mengaksesnya via getter.
// - Menerapkan abstraksi melalui method 'handleConnection' dan 'getDatabaseList' untuk menyederhanakan proses kompleks (koneksi & query).
// - Terdapat pemanfaatan objek model sebagai dependency, menjaga keterpisahan tanggung jawab antar class (prinsip low coupling).
// - Struktur class mendukung prinsip Single Responsibility, karena hanya fokus mengatur koneksi dan menyediakan data ke view.
