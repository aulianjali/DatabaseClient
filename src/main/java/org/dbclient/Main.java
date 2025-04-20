package org.dbclient;

import org.dbclient.view.ConnectionView;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) {
        ConnectionView connectionView = new ConnectionView(stage);

        // ambil layout dari connectionview
        Scene scene = new Scene(connectionView.getView(), 350, 250);

        // css global
        scene.getStylesheets().add(getClass().getClassLoader().getResource("style.css").toExternalForm());

        stage.setTitle("DB Client");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
