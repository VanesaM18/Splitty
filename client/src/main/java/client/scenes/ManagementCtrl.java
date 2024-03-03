package client.scenes;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import client.utils.ServerUtils;

import javax.inject.Inject;


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
            showAlert(AlertType.INFORMATION, "JSON Dump", optional.get());
        } else {
            showAlert(AlertType.ERROR, "JSON Dump Error", "Failed to retrieve JSON dump");
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
     * Method to add a new participant.
     * This method triggers the display of the add participant window.
     */
    public void addParticipant() {
        mainCtrl.showParticipants();
    }
}
