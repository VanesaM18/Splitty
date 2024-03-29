package client.scenes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import commons.Event;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import client.utils.ServerUtils;
import javafx.stage.FileChooser;

import com.google.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatterBuilder;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
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
    @FXML
    private TableColumn<Event, Button> downloadColumn;


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

    /**
     * fetches and displays all events from the server.
     * if all events are successfully retrieved,
     * updates the local events list and initializes the display table.
     * if retrieval fails, shows an error alert.
     */
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
        this.downloadColumn.setCellFactory(w -> this.createJsonDownloadButton());
    }

    private void downloadJsonDumpForEvent(Event event) {
        ObjectMapper objectMapper = this.server.getObjectMapper();
        String jsonDump;
        try {
            jsonDump = objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            showAlert(AlertType.ERROR, "JSON Serialization Error",
                    "Failed to serialize event to JSON: " + e.getMessage());
            return;
        }
        var defaultTitle = "event_" + event.getInviteCode() + "_dump.json";
        showFileChooser(jsonDump, defaultTitle);
    }

    private TableCell<Event, Button> createJsonDownloadButton() {
        return new TableCell<>() {
            private final Button downloadButton = new Button("download json");

            {
                downloadButton.setOnAction(event -> {
                    downloadJsonDumpForEvent(this.getTableView().getItems().get(getIndex()));
                });
            }
            @Override
            @com.google.inject.Inject
            protected void updateItem(Button item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(downloadButton);
                }
            }
        };
    }

    /**
     * handles the action when the JSON dump button is clicked in the management overview
     * invokes the server's 'handleJsonDump' method and displays a corresponding alert
     */
    @FXML
    public void handleJsonDumpButton() {
        var optional = server.handleJsonDump();
        optional.ifPresentOrElse(dump -> this.showFileChooser(dump, null), () ->
                showAlert(AlertType.ERROR, "JSON Dump Error", "Failed to retrieve JSON dump"));
    }

    private void showFileChooser(String fileContent, String defaultTitle) {
        var fileContentOptional = Optional.ofNullable(fileContent);
        var defaultTitleOptional = Optional.ofNullable(defaultTitle);
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save JSON Dump");
        fileChooser.getExtensionFilters().
                add(new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json"));
        defaultTitleOptional.ifPresent(fileChooser::setInitialFileName);
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            fileContentOptional.ifPresent(content -> saveJsonToFile(content, file));
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
        var sceneManager = mainCtrl.getSceneManager();
        sceneManager.popScene();
        mainCtrl.showStartScreen();
    }

    private Locale extractLocale() {
        var optionalLocale = this.mainCtrl.getCurrentLocale();
        return optionalLocale.orElse(null);
    }
}
