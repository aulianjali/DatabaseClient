package org.dbclient.view;

import java.sql.Connection;

import org.dbclient.controller.ConnectionController;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainView {

    private Connection connection;
    private QueryPanel queryPanel;
    private ResultPanel resultPanel;
    private NavigatorPanel navigatorPanel;

    // Label untuk menampilkan info koneksi di topbar
    private Label hostLabel;
    private Label portLabel;
    private Label userLabel;
    private Label passLabel;

    public MainView(Connection connection) {
        this.connection = connection;
    }

    public void start(Stage stage) {
        BorderPane root = new BorderPane();

        HBox topBar = new HBox(15);
        topBar.setPadding(new Insets(12));
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setStyle("-fx-background-color: #2c3e50;");

        // Label info koneksi yang dinamis
        hostLabel = new Label();
        portLabel = new Label();
        userLabel = new Label();
        passLabel = new Label();

        // Label statis (judul) dengan warna putih & bold
        Label hostLabelText = new Label("Host:");
        hostLabelText.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        Label portLabelText = new Label("Port:");
        portLabelText.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        Label userLabelText = new Label("User:");
        userLabelText.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        Label passLabelText = new Label("Pass:");
        passLabelText.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

        // Label info koneksi juga berwarna putih agar kontras
        hostLabel.setStyle("-fx-text-fill: white;");
        portLabel.setStyle("-fx-text-fill: white;");
        userLabel.setStyle("-fx-text-fill: white;");
        passLabel.setStyle("-fx-text-fill: white;");

        topBar.getChildren().addAll(
            hostLabelText, hostLabel,
            portLabelText, portLabel,
            userLabelText, userLabel,
            passLabelText, passLabel
        );

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

        queryPanel = new QueryPanel(connection);
        resultPanel = new ResultPanel();
        queryPanel.setResultPanel(resultPanel);

        centerPanel.getChildren().add(queryPanel.getView());
        root.setCenter(centerPanel);

        // Bagian bawah: hasil query
        root.setBottom(resultPanel.getView());

        // Jika koneksi awal ada, update info topbar dan panel navigator
        if (connection != null) {
            resultPanel.setConnection(connection);
            queryPanel.setConnection(connection);

            navigatorPanel = new NavigatorPanel(connection);
            navigatorPanel.setQueryPanel(queryPanel);
            queryPanel.setNavigatorPanel(navigatorPanel);
            leftPanel.getChildren().setAll(navigatorPanel.getView());

            updateConnectionInfo();
        } else {
            // Kalau belum koneksi, tampilkan tanda "-"
            hostLabel.setText("-");
            portLabel.setText("-");
            userLabel.setText("-");
            passLabel.setText("-");
        }

        Scene scene = new Scene(root, 1100, 650);
        stage.setTitle("JavaFX Database Client");
        stage.setScene(scene);
        stage.show();
    }

    // Update label info koneksi dari ConnectionController
    private void updateConnectionInfo() {
        String host = ConnectionController.getCurrentHost();
        String port = ConnectionController.getCurrentPort();
        String user = ConnectionController.getCurrentUser();
        String pass = ConnectionController.getCurrentPass();

        hostLabel.setText(host != null ? host : "-");
        portLabel.setText(port != null ? port : "-");
        userLabel.setText(user != null ? user : "-");
        if (pass != null && !pass.isEmpty()) {
            passLabel.setText("*".repeat(pass.length())); // sembunyikan password dengan asterik
        } else {
            passLabel.setText(" ");
        }
    }
}
