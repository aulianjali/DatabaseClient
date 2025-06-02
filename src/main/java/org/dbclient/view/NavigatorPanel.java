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

    private Connection connection;
    private VBox panel;
    private TreeView<String> treeView;
    private QueryPanel queryPanel;
    private String currentDatabase; 

    public NavigatorPanel(Connection connection) {
        this.connection = connection;

        panel = new VBox();
        panel.setPadding(new Insets(10));
        panel.setMinWidth(200);

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

        refresh();

        treeView.setOnMouseClicked(this::handleMouseClick);
        panel.getChildren().add(treeView);
    }

    private void handleMouseClick(MouseEvent event) {
        TreeItem<String> selectedItem = treeView.getSelectionModel().getSelectedItem();
        if (selectedItem == null) return;

        if (event.getClickCount() == 2 && event.getButton() == MouseButton.PRIMARY) {
            if (selectedItem.getParent() != null && selectedItem.getParent().getValue().equals("Databases")) {
                loadTablesForDatabase(selectedItem);
            }
        }

        if (event.getClickCount() == 1 && event.getButton() == MouseButton.PRIMARY) {
            if (selectedItem.getParent() != null &&
                selectedItem.getParent().getParent() != null &&
                queryPanel != null) {

                String tableName = selectedItem.getValue();

                if (currentDatabase != null) {
                    String query = "SELECT * FROM " + tableName + " LIMIT 100;";
                    queryPanel.setQueryAndExecute(query);
                }
            }
        }
    }

    private void loadTablesForDatabase(TreeItem<String> dbItem) {
        try {
            dbItem.getChildren().clear();
            String dbName = dbItem.getValue();

            Statement stmt = connection.createStatement();
            stmt.execute("USE " + dbName);
            currentDatabase = dbName;

            ResultSet rs = stmt.executeQuery("SHOW TABLES");

            while (rs.next()) {
                String tableName = rs.getString(1);
                TreeItem<String> tableItem = new TreeItem<>(tableName);
                dbItem.getChildren().add(tableItem);
            }

            dbItem.setExpanded(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void refresh() {
        try {
            // nyimpen nama database yang sebelumnya kebuka (klo ada)
            String expandedDb = null;
            if (treeView.getRoot() != null) {
                for (TreeItem<String> dbItem : treeView.getRoot().getChildren()) {
                    if (dbItem.isExpanded()) {
                        expandedDb = dbItem.getValue();
                        break;
                    }
                }
            }

            TreeItem<String> rootItem = new TreeItem<>("Databases");

            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SHOW DATABASES");

            while (rs.next()) {
                String dbName = rs.getString(1);
                TreeItem<String> dbItem = new TreeItem<>(dbName);

                // kalo database yang sebelumnya expanded, load tabel-nya langsung
                if (dbName.equals(expandedDb)) {
                    loadTablesForDatabase(dbItem); // include setExpanded(true)
                }

                rootItem.getChildren().add(dbItem);
            }

            rootItem.setExpanded(true);
            treeView.setRoot(rootItem);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public VBox getView() {
        return panel;
    }

    public void setQueryPanel(QueryPanel queryPanel) {
        this.queryPanel = queryPanel;
    }
}
