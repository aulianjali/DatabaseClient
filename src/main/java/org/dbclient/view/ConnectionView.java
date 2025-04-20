package org.dbclient.view;

import org.dbclient.controller.ConnectionController;

import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class ConnectionView {

    // Enkapsulasi: atribut hanya digunakan dalam class ini
    private Stage stage;
    private GridPane root;

    // Konstruktor: menerima stage utama, lalu membangun tampilan UI
    public ConnectionView(Stage stage) {
        this.stage = stage;
        this.root = buildUI(); // abstraksi: memisahkan pembuatan UI ke method tersendiri
    }

    // Method private untuk membangun UI (form koneksi)
    // Penerapan single responsibility: khusus menangani tampilan form
    private GridPane buildUI() {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setVgap(10);
        grid.setHgap(10);

        // Komponen input
        TextField hostField = new TextField("localhost");
        TextField portField = new TextField("3306");
        TextField userField = new TextField("root");
        PasswordField passField = new PasswordField();
        Button connectBtn = new Button("Connect");

        // Penyusunan komponen UI ke dalam grid
        grid.add(new Label("Host:"), 0, 0); grid.add(hostField, 1, 0);
        grid.add(new Label("Port:"), 0, 1); grid.add(portField, 1, 1);
        grid.add(new Label("Username:"), 0, 2); grid.add(userField, 1, 2);
        grid.add(new Label("Password:"), 0, 3); grid.add(passField, 1, 3);
        grid.add(connectBtn, 1, 4);

        // Delegasi logika koneksi ke controller
        // Penerapan pemisahan tanggung jawab antar objek (separation of concerns)
        connectBtn.setOnAction(e -> {
            ConnectionController controller = new ConnectionController();
            controller.handleConnection(stage, hostField.getText(), userField.getText(), passField.getText(), portField.getText());
        });

        return grid;
    }

    // Method untuk menampilkan view
    // Memisahkan logika tampilan dari logika kontrol & model
    public void show() {
        stage.setTitle("Database Connection");
        stage.setScene(new javafx.scene.Scene(root, 350, 250));
        stage.show();
    }

    // Getter untuk mengambil root node (bisa digunakan kalau scene dikelola dari luar)
    public Parent getView() {
        return root;
    }
}


// - Class ini merupakan bagian dari "View" dalam pola MVC, bertanggung jawab hanya pada antarmuka pengguna.
// - Menerapkan abstraksi melalui 'buildUI()', agar UI disusun terpisah dari konstruktor.
// - Menggunakan enkapsulasi, karena atribut hanya bisa diakses dalam class (tidak publik).
// - Memanfaatkan delegasi tanggung jawab ke controller ('ConnectionController') untuk menjaga separation of concern.
// - Penerapan event-driven programming dengan listener 'connectBtn.setOnAction()' yang memicu proses koneksi secara modular.
