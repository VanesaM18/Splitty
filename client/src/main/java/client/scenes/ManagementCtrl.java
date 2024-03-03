package client.scenes;

import javafx.event.ActionEvent;
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


    @Inject
    public ManagementCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    @FXML
    public void handleJsonDumpButton() {
        var optional = server.handleJsonDump();
        if (optional.isPresent()) {
            showAlert(AlertType.INFORMATION, "JSON Dump", optional.get());
        } else {
            showAlert(AlertType.ERROR, "JSON Dump Error", "Failed to retrieve JSON dump");
        }
    }

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
