package org.dbclient.view;

import java.sql.Connection;

import org.dbclient.controller.ConnectionController;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainView {

    // Enkapsulasi: setiap panel disimpan sebagai atribut private
    private Connection connection;
    private QueryPanel queryPanel;
    private ResultPanel resultPanel;
    private NavigatorPanel navigatorPanel;

    // Konstruktor menerima connection awal dan menyimpannya
    public MainView(Connection connection) {
        this.connection = connection;
    }

    // Method utama yang membangun dan menampilkan UI lengkap
    // Menerapkan prinsip abstraksi: detail UI dipisah ke blok kode terstruktur (top, left, center, bottom)
    public void start(Stage stage) {
        BorderPane root = new BorderPane(); // struktur layout utama JavaFX

        // Bagian atas: form input untuk koneksi
        HBox topBar = new HBox(15);
        topBar.setPadding(new Insets(12));
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setStyle("-fx-background-color: #2c3e50;");

        // Komponen input koneksi
        TextField hostField = new TextField("localhost");
        TextField portField = new TextField("3306");
        TextField userField = new TextField("root");
        PasswordField passField = new PasswordField();
        Button connectBtn = new Button("Connect");

        // Menambahkan label dan field ke dalam bar
        topBar.getChildren().addAll(
            new Label("Host:"), hostField,
            new Label("Port:"), portField,
            new Label("User:"), userField,
            new Label("Pass:"), passField,
            connectBtn
        );
        // Styling label-label
        topBar.getChildren().forEach(node -> {
            if (node instanceof Label label) {
                label.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
            }
        });
        root.setTop(topBar);

        // Bagian kiri: navigator panel
        VBox leftPanel = new VBox();
        leftPanel.setPadding(new Insets(5));
        leftPanel.setStyle("-fx-background-color: #ecf0f1;");
        leftPanel.setPrefWidth(220);
        root.setLeft(leftPanel);

        // Bagian tengah: query panel
        VBox centerPanel = new VBox(10);
        centerPanel.setPadding(new Insets(10));

        // Inisialisasi panel-panel utama
        queryPanel = new QueryPanel(connection);
        resultPanel = new ResultPanel();
        queryPanel.setResultPanel(resultPanel); // dependency antar panel

        centerPanel.getChildren().add(queryPanel.getView());
        root.setCenter(centerPanel);

        // Bagian bawah: hasil query
        root.setBottom(resultPanel.getView());

        // Jika koneksi awal sudah ada, panel navigator langsung diisi
        if (connection != null) {
            resultPanel.setConnection(connection);
            queryPanel.setConnection(connection);

            navigatorPanel = new NavigatorPanel(connection);
            navigatorPanel.setQueryPanel(queryPanel);
            queryPanel.setNavigatorPanel(navigatorPanel);
            leftPanel.getChildren().setAll(navigatorPanel.getView());
        }

        // Event tombol connect: menghubungkan ulang dan update semua panel
        connectBtn.setOnAction(e -> {
            try {
                String host = hostField.getText();
                String port = portField.getText();
                String user = userField.getText();
                String pass = passField.getText();

                // Delegasi proses koneksi ke controller
                ConnectionController.getDbConnection().connect(host, port, user, pass);
                connection = ConnectionController.getDbConnection().getConnection();

                // Update koneksi ke semua panel
                resultPanel.setConnection(connection);
                queryPanel.setConnection(connection);

                navigatorPanel = new NavigatorPanel(connection);
                navigatorPanel.setQueryPanel(queryPanel);
                queryPanel.setNavigatorPanel(navigatorPanel);
                leftPanel.getChildren().setAll(navigatorPanel.getView());

            } catch (Exception ex) {
                ex.printStackTrace();
                resultPanel.showError("Connection failed: " + ex.getMessage());
            }
        });

        // Menampilkan scene JavaFX
        Scene scene = new Scene(root, 1100, 650);
        stage.setTitle("JavaFX Database Client");
        stage.setScene(scene);
        stage.show();
    }
}

// - Class ini berperan sebagai View utama dalam pola MVC, mengatur layout dan integrasi UI secara keseluruhan.
// - Penerapan enkapsulasi: atribut panel dibuat private dan hanya digunakan di dalam class.
// - Penerapan komposisi: MainView mengatur dan menggabungkan beberapa objek view lain (NavigatorPanel, QueryPanel, ResultPanel).
// - Penerapan abstraksi: UI dibagi jadi beberapa area (topBar, leftPanel, centerPanel, bottom).
// - Delegasi logika ke controller ('ConnectionController') dan antar panel dilakukan secara modular.
// - Open for extension: struktur desain ini memungkinkan penambahan fitur di tiap panel tanpa perlu mengubah logika utama.

