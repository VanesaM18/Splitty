package client.scenes;

import client.utils.ServerUtils;

import com.google.inject.Inject;

import commons.Participant;

import jakarta.ws.rs.WebApplicationException;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;

public class AddParticipantsCtrl {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML private TextField name;

    @FXML private TextField email;

    @FXML private TextField iban;

    @FXML private TextField bic;

    /**
     * Controller responsible for handling the addition of participants functionality.
     * @param server An instance of ServerUtils for server-related operations.
     * @param mainCtrl An instance of MainCtrl for coordinating with the main controller.
     */
    @Inject
    public AddParticipantsCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    /**
     * Aborts the process of adding a new participant.
     */
    public void abort() {
        clearFields();
        mainCtrl.showOverviewEvent(null);
    }

    /**
     * Clears the text fields.
     */
    private void clearFields() {
        name.clear();
        email.clear();
        iban.clear();
        bic.clear();
    }

    /**
     * Adds the participant to the server if the input is valid.
     * Throws an error if the request is bad.
     */
    public void ok() {
        try {
            server.addParticipant(getParticipant());
        } catch (WebApplicationException e) {

            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            return;
        }

        clearFields();
        mainCtrl.showOverviewEvent(null);
    }

    /**
     * Creates a participant from the text fields.
     * @return the created participant.
     */
    private Participant getParticipant() {
        Participant p =
                new Participant(name.getText(), email.getText(), iban.getText(), bic.getText());
        return p;
    }

    /**
     * Event handler for pressing a key.
     * @param e the key that is pressed
     */
    public void keyPressed(KeyEvent e) {
        switch (e.getCode()) {
            case ENTER:
                ok();
                break;
            case ESCAPE:
                abort();
                break;
            default:
                break;
        }
    }
}
