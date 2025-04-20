package org.dbclient.view;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javafx.geometry.Insets;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

public class NavigatorPanel {

    // Enkapsulasi: field disimpan private agar hanya bisa diakses melalui method
    private Connection connection;
    private VBox panel;
    private TreeView<String> treeView;
    private QueryPanel queryPanel;

    // Konstruktor menerima connection dan langsung membangun UI tree-nya
    public NavigatorPanel(Connection connection) {
        this.connection = connection;

        panel = new VBox();
        panel.setPadding(new Insets(10));
        panel.setMinWidth(200);

        // Styling panel
        panel.setStyle("""
            -fx-background-color: #f5f5f5;
            -fx-border-color: #cccccc;
            -fx-border-width: 0 1 0 0;
        """);

        treeView = new TreeView<>();
        treeView.setStyle("""
            -fx-font-family: 'Segoe UI';
            -fx-font-size: 13px;
        """);

        refresh(); // Abstraksi: data langsung dimuat pada inisialisasi

        treeView.setOnMouseClicked(this::handleMouseClick); // Delegasi ke method handler
        panel.getChildren().add(treeView);
    }

    // Abstraksi interaksi mouse: single click untuk preview, double click untuk load tabel
    private void handleMouseClick(MouseEvent event) {
        TreeItem<String> selectedItem = treeView.getSelectionModel().getSelectedItem();
        if (selectedItem == null) return;

        // Double click pada database: load semua tabelnya
        if (event.getClickCount() == 2 && event.getButton() == MouseButton.PRIMARY) {
            if (selectedItem.getParent() != null && selectedItem.getParent().getValue().equals("Databases")) {
                loadTablesForDatabase(selectedItem);
            }
        }

        // Single click pada tabel: preview isi tabel
        if (event.getClickCount() == 1 && event.getButton() == MouseButton.PRIMARY) {
            if (selectedItem.getParent() != null &&
                selectedItem.getParent().getParent() != null &&
                queryPanel != null) {

                String dbName = selectedItem.getParent().getValue();
                String tableName = selectedItem.getValue();
                String query = "SELECT * FROM " + dbName + "." + tableName + " LIMIT 100;";
                queryPanel.setQueryAndExecute(query); // Delegasi ke QueryPanel
            }
        }
    }

    // Method private khusus untuk memuat tabel dari database tertentu
    private void loadTablesForDatabase(TreeItem<String> dbItem) {
        try {
            dbItem.getChildren().clear(); // Reset isi
            String dbName = dbItem.getValue();

            Statement stmt = connection.createStatement();
            stmt.execute("USE " + dbName);
            ResultSet rs = stmt.executeQuery("SHOW TABLES");

            while (rs.next()) {
                String tableName = rs.getString(1);
                TreeItem<String> tableItem = new TreeItem<>(tableName);
                dbItem.getChildren().add(tableItem);
            }

            dbItem.setExpanded(true); // Expand setelah dimuat
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method publik untuk me-refresh seluruh struktur database dari awal
    public void refresh() {
        try {
            TreeItem<String> rootItem = new TreeItem<>("Databases");

            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SHOW DATABASES");

            while (rs.next()) {
                String dbName = rs.getString(1);
                TreeItem<String> dbItem = new TreeItem<>(dbName);
                rootItem.getChildren().add(dbItem);
            }

            rootItem.setExpanded(true);
            treeView.setRoot(rootItem);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Getter view panel-nya, untuk ditampilkan di UI utama
    public VBox getView() {
        return panel;
    }

    // Setter untuk injeksi QueryPanel dari luar (komposisi antar view)
    public void setQueryPanel(QueryPanel queryPanel) {
        this.queryPanel = queryPanel;
    }
}


// - Class ini menerapkan prinsip enkapsulasi: hanya method publik yang dibuka ('refresh()', 'getView()', 'setQueryPanel()').
// - Menerapkan abstraksi: pemisahan logika UI (TreeView) dan logika interaksi database (loadTablesForDatabase, refresh).
// - Ada komposisi dengan QueryPanel untuk menjalankan query dari TreeView secara langsung.
// - Gunakan delegasi event handler ke method khusus (handleMouseClick), memisahkan logika klik dan responsnya.


