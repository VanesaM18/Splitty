package client.scenes;

import commons.Event;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import client.utils.ServerUtils;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.stream.Collectors;


public class ManagementCtrl {
    private final MainCtrl mainCtrl;
    private final ServerUtils server;
    private List<Event> events;

    @FXML
    private Button jsonDumpButton;
    @FXML
    private TableView<Event> eventsTable;
    @FXML
    private TableColumn<Event, String> titleColumn;
    @FXML
    private TableColumn<Event, String> creationDateColumn;
    @FXML
    private TableColumn<Event, String> lastActivityColumn;


    /**
     * controller for handling the management overview functionality
     * @param server instance of ServerUtils for server-related operations
     * @param mainCtrl instance of MainCtrl for coordinating with the main controller
     */
    @Inject
    public ManagementCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    public void showEvents() {
        var optional = server.getAllEvents();
        if (optional.isPresent()) {
            this.events = optional.get();
            initializeTable();
        } else {
            showAlert(AlertType.ERROR, "Fetch Events Error", "Failed to fetch events");
        }
    }

    private final Function<LocalDateTime, String> formatDate = dateTime -> {
        var locale = extractLocale();
        var formatterBuilder = new DateTimeFormatterBuilder()
                .appendPattern("yyyy-MM-dd HH:mm");
        var formatter = locale != null
                ? formatterBuilder.toFormatter(locale)
                : formatterBuilder.toFormatter();
        String formatted = dateTime.format(formatter);
        return formatted;
    };

    private void initializeTable() {
        ObservableList<Event> events = this.events.stream().collect(Collectors
                .collectingAndThen(Collectors.toList(), FXCollections::observableArrayList));
        this.eventsTable.getItems().setAll(events);
        this.titleColumn.setCellValueFactory(w -> new SimpleStringProperty(w.getValue().getName()));
        this.creationDateColumn.setCellValueFactory(w ->
                new SimpleStringProperty(formatDate.apply(w.getValue().getCreationTime())));
        this.lastActivityColumn.setCellValueFactory(w ->
                new SimpleStringProperty(formatDate.apply(w.getValue().getLastUpdateTime())));
    }

    @FXML
    public void handleOrderBySelection() {
        showEvents();
    }

    /**
     * handles the action when the JSON dump button is clicked in the management overview
     * invokes the server's 'handleJsonDump' method and displays a corresponding alert
     */
    @FXML
    public void handleJsonDumpButton() {
        var optional = server.handleJsonDump();
        if (optional.isPresent()) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save JSON Dump");
            fileChooser.getExtensionFilters().
                    add(new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json"));
            File file = fileChooser.showSaveDialog(null);

            if (file != null) {
                saveJsonToFile(optional.get(), file);
            }
        } else {
            showAlert(AlertType.ERROR, "JSON Dump Error", "Failed to retrieve JSON dump");
        }
    }

    /**
     * saves a JSON string to a specified file
     * @param json JSON string to be saved
     * @param file file where the JSON string will be saved
     */
    private void saveJsonToFile(String json, File file) {
        try (PrintWriter writer = new PrintWriter(file)) {
            writer.write(json);
            showAlert(AlertType.INFORMATION, "JSON Dump Saved", "JSON dump saved successfully");
        } catch (IOException e) {
            showAlert(AlertType.ERROR, "Save Error", "Failed to save JSON dump: " + e.getMessage());
        }
    }

    /**
     * displays an alert with the specified type, title, and content
     * @param alertType type of alert
     * @param title     title of the alert
     * @param content   content or message of the alert
     */
    private void showAlert(AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * method for refreshing the events list view
     */
    public void refresh() {
        showEvents();
    }

    /**
     * Goes back to the starting page.
     */
    public void home() {
        mainCtrl.showStartScreen();
    }

    private Locale extractLocale() {
        var optionalLocale = this.mainCtrl.getCurrentLocale();
        return optionalLocale.orElse(null);
    }
}
