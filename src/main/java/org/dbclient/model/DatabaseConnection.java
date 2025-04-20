package org.dbclient.model;

// Import untuk koneksi JDBC
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    // Enkapsulasi: objek koneksi disimpan sebagai atribut private
    private Connection connection;

    // Method untuk membangun koneksi ke database MySQL
    // Menerapkan prinsip abstraksi: menyembunyikan detail teknis koneksi dari class lain
    public void connect(String host, String port, String user, String password) throws SQLException {
        try {
            // Pemanggilan driver secara eksplisit 
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found.");
            e.printStackTrace(); // Error handling sederhana
        }

        // URL JDBC dibentuk dinamis dari parameter method
        String url = "jdbc:mysql://" + host + ":" + port + "/";
        this.connection = DriverManager.getConnection(url, user, password);
    }

    // Getter untuk memberikan akses koneksi ke class lain
    // Memungkinkan pembagian koneksi secara terpusat, tanpa membuka detail implementasinya
    public Connection getConnection() {
        return connection;
    }
}

// - Class ini berfungsi sebagai komponen "Model" dalam pola MVC: menyimpan dan menyediakan akses data berupa objek koneksi.
// - Class ini menerapkan prinsip abstraksi dan enkapsulasi:
//   Detail koneksi disembunyikan dari class lain (misal: penggunaan 'DriverManager', pemrosesan URL).
//   Atribut 'connection' bersifat private dan hanya diakses melalui method publik 'getConnection()'.

