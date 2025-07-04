package org.dbclient.view;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

public class ResultPanel {

    private final VBox panel;
    private TableView<List<String>> tableView;
    private Connection connection;
    private boolean isTableFullySelected = false;

    // inisialisasi panel hasil
    public ResultPanel() {
        panel = new VBox();
        tableView = new TableView<>();

        // Konfigurasi selection
        tableView.getSelectionModel().setCellSelectionEnabled(true);
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        ScrollPane scrollPane = new ScrollPane(tableView);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        panel.setStyle("""
            -fx-background-color: #f5f5f5;
            -fx-padding: 12;
        """);

        tableView.setStyle("""
            -fx-font-family: 'Consolas';
            -fx-font-size: 13px;
            -fx-background-color: white;
        """);

        scrollPane.setStyle("""
            -fx-background-color: transparent;
        """);

        panel.getChildren().add(scrollPane);

        // klik → select seluruh isi tabel
        tableView.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            isTableFullySelected = true;
            tableView.getSelectionModel().clearSelection();
            for (int row = 0; row < tableView.getItems().size(); row++) {
                for (int col = 0; col < tableView.getColumns().size(); col++) {
                    tableView.getSelectionModel().select(row, tableView.getColumns().get(col));
                }
            }
            tableView.refresh();
        });

        // Ctrl + C untuk copy data seluruh tabel
        tableView.setOnKeyPressed(event -> {
            if (event.isControlDown() && event.getCode() == KeyCode.C) {
                StringBuilder clipboardString = new StringBuilder();

                var columns = tableView.getColumns();
                for (int i = 0; i < columns.size(); i++) {
                    clipboardString.append(columns.get(i).getText());
                    if (i < columns.size() - 1) clipboardString.append("\t");
                }
                clipboardString.append("\n");

                for (List<String> row : tableView.getItems()) {
                    clipboardString.append(String.join("\t", row)).append("\n");
                }

                ClipboardContent content = new ClipboardContent();
                content.putString(clipboardString.toString());
                Clipboard.getSystemClipboard().setContent(content);

                isTableFullySelected = false;
                tableView.getSelectionModel().clearSelection();
                tableView.refresh();
            }
        });
    }

    // set koneksi dari luar
    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    // method utama untuk menampilkan hasil query
    public void showResult(String query) {
        tableView.getItems().clear();
        tableView.getColumns().clear();
        panel.getChildren().setAll(tableView);
        isTableFullySelected = false;

        try (Statement stmt = connection.createStatement()) {
            boolean isResultSet = stmt.execute(query);

            if (isResultSet) {
                ResultSet rs = stmt.getResultSet();
                ResultSetMetaData metaData = rs.getMetaData(); //ambil metadata buat tau info kolom
                int columnCount = metaData.getColumnCount();

                // generate kolom berdasarkan metadata
                for (int i = 1; i <= columnCount; i++) {
                    final int colIndex = i - 1;
                    TableColumn<List<String>, String> col = new TableColumn<>(metaData.getColumnLabel(i)); //ambil kolom nama dari metadata query database secara dinamis
                    col.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(colIndex)));
                    col.setCellFactory(getHighlightingCellFactory());
                    tableView.getColumns().add(col);
                }

                // isi data ke tabel
                while (rs.next()) {
                    List<String> row = new ArrayList<>();
                    for (int i = 1; i <= columnCount; i++) {
                        row.add(rs.getString(i));
                    }
                    tableView.getItems().add(row);
                }

                tableView.setRowFactory(tv -> new TableRow<>() {
                    @Override
                    protected void updateItem(List<String> item, boolean empty) {
                        super.updateItem(item, empty);
                        if (isTableFullySelected && !empty) {
                            setStyle("-fx-background-color: -fx-accent; -fx-text-fill: white;");
                        } else {
                            setStyle("");
                        }
                    }
                });

            } else {
                int updateCount = stmt.getUpdateCount();
                showMessage("✅ Query OK, " + updateCount + " row(s) affected.");
            }

        } catch (Exception e) {
            showError("❌ ERROR: " + e.getMessage());
        }
    }

    private Callback<TableColumn<List<String>, String>, TableCell<List<String>, String>> getHighlightingCellFactory() {
        return col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item);
                if (isTableFullySelected && !empty) {
                    setStyle("-fx-background-color: -fx-accent; -fx-text-fill: white;");
                } else {
                    setStyle("");
                }
            }
        };
    }

    public void showError(String message) {
        tableView.getItems().clear();
        tableView.getColumns().clear();

        Label errorLabel = new Label("❌ ERROR: " + message);
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold; -fx-padding: 8;");
        panel.getChildren().setAll(errorLabel);
    }

    public void showMessage(String message) {
        tableView.getItems().clear();
        tableView.getColumns().clear();

        Label msgLabel = new Label(message);
        msgLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold; -fx-padding: 8;");
        panel.getChildren().setAll(msgLabel);
    }

    // getter tampilan
    public VBox getView() {
        return panel;
    }
}
