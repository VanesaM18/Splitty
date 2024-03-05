package client.scenes;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import client.utils.ServerUtils;
import javafx.stage.FileChooser;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;


public class ManagementCtrl {
    private final MainCtrl mainCtrl;
    private final ServerUtils server;

    @FXML
    private Button jsonDumpButton;


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
     * method for refreshing the settings page
     */
    public void refresh() {
    }

    /**
     * Goes back to the starting page.
     */
    public void home() {
        mainCtrl.showStartScreen();
    }
}
