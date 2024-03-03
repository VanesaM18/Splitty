package client.scenes;

import client.utils.ServerUtils;

import com.google.inject.Inject;

import commons.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Modality;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.ResourceBundle;

public class StartScreenCtrl {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML
    private TextField createEventField;
    @FXML
    private TextField joinEventField;
    /**
     * Controller responsible for handling event creation and joining.
     *
     * @param server   An instance of ServerUtils for server-related operations.
     * @param mainCtrl An instance of MainCtrl for coordinating with the main controller.
     */
    @Inject
    public StartScreenCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }

    /**
     * Handles creating a new event based on the input from the createEventField.
     */
    public void createEvent() {
        String eventName = createEventField.getText();
        Event event = new Event( "ABCDEF", eventName, LocalDateTime.now(), new HashSet<>());
        event.generateInviteCode();
        event = server.addEvent(event);
        clearFields();
        mainCtrl.showOverviewEvent(event);
    }

    /**
     * Handles joining an existing event based on the input from the joinEventField.
     */
    public void joinEvent() {
        String eventCode = joinEventField.getText();
        Event ev = server.getEventById(eventCode);
        if (ev == null) {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText("The event does not exist");
            alert.showAndWait();
            clearFields();
            return;
        }
        clearFields();
        mainCtrl.showOverviewEvent(ev);
    }
    /**
     * Redirects the user to the admin view
     */
    public void goToAdmin() {
        mainCtrl.showLogin();
    }
    /**
     * Clears the fields
     */
    public void clearFields() {
        joinEventField.clear();
        createEventField.clear();
    }
}
