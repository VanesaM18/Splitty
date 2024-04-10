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
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import client.utils.ServerUtils;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;

import com.google.inject.Inject;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatterBuilder;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


public class ManagementCtrl {
    private final MainCtrl mainCtrl;
    private final ServerUtils server;
    private List<Event> events;

    @FXML
    private Button jsonDumpButton;
    @FXML
    private Button importJsonDumpButton;
    @FXML
    private TableView<Event> eventsTable;
    @FXML
    private TableColumn<Event, String> titleColumn;
    @FXML
    private TableColumn<Event, String> creationDateColumn;
    @FXML
    private TableColumn<Event, String> lastActivityColumn;
    @FXML
    private TableColumn<Event, String> inviteCodeColumn;


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
        var formatterBuilder = new DateTimeFormatterBuilder();
        formatterBuilder.appendPattern("yyyy-MM-dd HH:mm");
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
        this.inviteCodeColumn.setCellValueFactory(w ->
                new SimpleStringProperty(w.getValue().getInviteCode()));
        this.creationDateColumn.setCellValueFactory(w ->
                new SimpleStringProperty(formatDate.apply(w.getValue().getCreationTime())));
        this.lastActivityColumn.setCellValueFactory(w ->
                new SimpleStringProperty(formatDate.apply(w.getValue().getLastUpdateTime())));
        this.eventsTable.setContextMenu(createContextMenu());
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


    private ContextMenu createContextMenu() {
        ContextMenu cm = new ContextMenu();

        MenuItem copyInviteCodeMenuItem = new MenuItem("Copy invite code");
        copyInviteCodeMenuItem.setOnAction(ignored -> {
            Event event = (Event) eventsTable.getSelectionModel().getSelectedItem();
            if (event == null) return;

            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putString(event.getInviteCode());
            clipboard.setContent(content);
        });

        MenuItem downloadJsonMenuItem = new MenuItem("Download JSON");
        downloadJsonMenuItem.setOnAction(ignored -> {
            Event event = (Event) eventsTable.getSelectionModel().getSelectedItem();
            if (event == null) return;

            downloadJsonDumpForEvent(event);
        });

        MenuItem deleteMenuItem = new MenuItem("Delete");
        deleteMenuItem.setOnAction(ignored -> {
            Event event = (Event) eventsTable.getSelectionModel().getSelectedItem();
            if (event == null) return;

            // Ask for confirmation.
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation required");
            alert.setHeaderText("Deleting an event");
            alert.setContentText(event.getName() + " will de deleted.");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                // Tell server we are viewing this event, so that we are sent a message on its
                // deletion.
                server.sendUpdateStatus(event.getInviteCode());
                server.deleteEvent(event);
            }
        });

        cm.getItems().addAll(copyInviteCodeMenuItem, downloadJsonMenuItem, deleteMenuItem);

        return cm;
    }

    /**
     * handles the action when the JSON dump button
     * is clicked in the management overview.
     * invokes the server's 'handleJsonDump' method
     * and displays a file picker (on success) or alert (on error).
     */
    @FXML
    public void handleJsonDumpButton() {
        var optional = server.handleJsonDump();
        optional.ifPresentOrElse(dump ->
                this.showFileChooser(dump, "splitty_jsonDump_"+ UUID.randomUUID() +".json"), () ->
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
     * dandles the action when the "Import JSON Dump" button is clicked.
     * opens a file chooser dialog to select a JSON file.
     * reads the selected JSON file and imports its contents as events.
     * displays an error message if the file reading fails.
     */
    @FXML
    public void handleImportJsonDumpButton() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Import JSON Dump");
        fileChooser.getExtensionFilters()
                .add(new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json"));
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            String jsonContent = readJsonFile(file);
            if (jsonContent != null) {
                importEventsFromJsonString(jsonContent);
            } else {
                showAlert(AlertType.ERROR, "Import Error", "Failed to read JSON file");
            }
        }
    }

    private String readJsonFile(File file) {
        StringBuilder contentBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                contentBuilder.append(line);
            }
            return contentBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void importEventsFromJsonString(String jsonString) {
        ObjectMapper objectMapper = this.server.getObjectMapper();
        try {
            Event[] importedEvents = objectMapper.readValue(jsonString, Event[].class);
            importEventArray(importedEvents);
        } catch (IOException e) {
            try {
                Event importedEvent = objectMapper
                        .readValue(jsonString, Event.class);
                Event[] importedEvents = new Event[1];
                importedEvents[0] = importedEvent;
                importEventArray(importedEvents);
            } catch (IOException error) {
                showAlert(AlertType.ERROR, "Import Error",
                        "Failed to import events from JSON string: " + e.getMessage());
            }
        }
    }

    private void importEventArray(Event[] importedEvents) {
        List<Event> importedEventsList = addEventsToDatabase(importedEvents);
        this.events.addAll(importedEventsList);
        displayEventsInTableView(importedEventsList);
        String importMessage = formatImportMessage(importedEventsList);
        showAlert(AlertType.INFORMATION, "Import Success", importMessage);
    }

    private String formatImportMessage(List<Event> importedEvents) {
        int n = importedEvents.size();
        return switch (n) {
            case 0:
                yield "No events have been imported !!!";
            case 1:
                yield "One event imported successfully !!!";
            default:
                yield n + " events imported successfully !!!";
        };
    }

    private void displayEventsInTableView(List<Event> events) {
        ObservableList<Event> eventList = FXCollections.observableArrayList(events);
        eventsTable.getItems().addAll(eventList);
    }

    private List<Event> addEventsToDatabase(Event[] events) {
        return server.importEvents(events).orElse(new ArrayList<>());
    }

    /**
     * displays an alert with the specified type, title, and content
     * @param alertType type of alert
     * @param title title of the alert
     * @param content content or message of the alert
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
        mainCtrl.setIsInManagement(false);

        var sceneManager = mainCtrl.getSceneManager();
        sceneManager.popScene();
        mainCtrl.showStartScreen();
    }

    private Locale extractLocale() {
        var optionalLocale = this.mainCtrl.getCurrentLocale();
        return optionalLocale.orElse(null);
    }

    /**
     * Event handler for pressing a key.
     *
     * @param e the key that is pressed
     */
    public void keyPressed(KeyEvent e) {
        switch (e.getCode()) {
            case ESCAPE:
                home();
                break;
            default:
                break;
        }
    }
}
