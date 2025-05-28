package org.dbclient.view;

import java.sql.Connection;

import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

public class QueryPanel {

    // Enkapsulasi: semua atribut dibuat private
    private Connection connection;
    private VBox panel;
    private ResultPanel resultPanel;
    private NavigatorPanel navigatorPanel;

    private TextArea queryArea;

    // Konstruktor utama dengan parameter connection
    public QueryPanel(Connection connection) {
        this.connection = connection;
        panel = new VBox();

        queryArea = new TextArea("SELECT * FROM ...;");
        Button executeBtn = new Button("Execute");

        // Event handling: delegasi ke method eksekusi
        executeBtn.setOnAction(e -> executeQuery());

        // Styling panel dan komponen
        panel.setStyle("""
            -fx-background-color: #f5f5f5;
            -fx-padding: 12;
            -fx-spacing: 12;
        """);

        queryArea.setStyle("""
            -fx-font-family: 'Consolas';
            -fx-font-size: 13px;
            -fx-background-radius: 8;
            -fx-border-radius: 8;
            -fx-border-color: #ccc;
            -fx-background-color: #ffffff;
            -fx-padding: 8;
        """);

        executeBtn.setStyle("""
            -fx-background-color: #4CAF50;
            -fx-text-fill: white;
            -fx-font-weight: bold;
            -fx-background-radius: 6;
            -fx-padding: 6 14 6 14;
        """);

        panel.getChildren().addAll(queryArea, executeBtn);
    }

    // Enkapsulasi dan Dependency Injection
    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public void setResultPanel(ResultPanel resultPanel) {
        this.resultPanel = resultPanel;
    }

    public void setNavigatorPanel(NavigatorPanel navigatorPanel) {
        this.navigatorPanel = navigatorPanel;
    }

    // Abstraksi tampilan
    public VBox getView() {
        return panel;
    }

    // Digunakan saat TreeView memilih tabel, langsung set query dan eksekusi
    public void setQueryAndExecute(String query) {
        queryArea.setText(query);
        executeQuery();
    }

    // Abstraksi proses eksekusi query
    public void executeQuery() {
        String query = queryArea.getText().trim();

        // Polimorfisme implisit: resultPanel tahu cara menampilkan query
        if (resultPanel != null) {
            resultPanel.showResult(query);
        } else {
            System.err.println("ResultPanel belum diset!");
        }

        // Jika query DDL, navigator otomatis refresh
        if (navigatorPanel != null && isDDL(query)) {
            navigatorPanel.refresh(); 
        }
    }

    // method untuk deteksi perintah DDL
    private boolean isDDL(String query) {
        String lower = query.toLowerCase().trim();
        return lower.startsWith("create ") ||
               lower.startsWith("drop ")   ||
               lower.startsWith("alter ")  ||
               lower.startsWith("truncate ") ||
               lower.startsWith("rename ");
    }
}

// - Menerapkan prinsip enkapsulasi (semua properti dan logic disimpan private).
// - Menerapkan komposisi: bekerja sama dengan 'ResultPanel' dan 'NavigatorPanel' untuk menyusun alur UI.
// - Terdapat abstraksi: metode 'executeQuery' memisahkan logika eksekusi dari UI langsung.
// - Dependency injection: dependency eksternal (resultPanel & navigatorPanel) disediakan lewat setter.


