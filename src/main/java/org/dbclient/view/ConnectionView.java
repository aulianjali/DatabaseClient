package org.dbclient.view;

import org.dbclient.controller.ConnectionController;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ConnectionView {

    private Stage stage;
    private BorderPane root;

    public ConnectionView(Stage stage) {
        this.stage = stage;
        this.root = buildUI();
    }

    private BorderPane buildUI() {
        BorderPane layout = new BorderPane();

        // form bagian tengah
        VBox form = new VBox(12);
        form.setPadding(new Insets(30));
        form.setAlignment(Pos.CENTER);

        // input field
        TextField hostField = new TextField();
        TextField portField = new TextField();
        TextField userField = new TextField();
        PasswordField passField = new PasswordField();
        Button connectBtn = new Button("Connect");
        connectBtn.setPrefWidth(100);

        // styling label
        form.getChildren().addAll(
            createLabeledField("Host", hostField),
            createLabeledField("Port", portField),
            createLabeledField("Username", userField),
            createLabeledField("Password", passField),
            connectBtn
        );

        layout.setCenter(form);

        // action connect
        connectBtn.setOnAction(e -> {
            ConnectionController controller = new ConnectionController();
            controller.handleConnection(stage, hostField.getText(), userField.getText(), passField.getText(), portField.getText());
        });

        return layout;
    }

    private HBox createLabeledField(String labelText, Control input) {
        Label label = new Label(labelText + ":");
        label.setPrefWidth(100);
        label.setStyle("-fx-font-weight: bold;");
        HBox field = new HBox(10, label, input);
        field.setAlignment(Pos.CENTER_LEFT);
        return field;
    }

    public void show() {
        stage.initStyle(javafx.stage.StageStyle.UNDECORATED);
        stage.setTitle("Access Database");
        stage.setScene(new Scene(root, 400, 320));
        stage.setResizable(false);
        stage.centerOnScreen();
        stage.show();
    }

    public Parent getView() {
        return root;
    }
}