package client.scenes;

import client.utils.ServerUtils;

import com.google.inject.Inject;

import commons.Participant;

import jakarta.ws.rs.WebApplicationException;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;

import java.util.regex.Pattern;

public class AddParticipantsCtrl {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    @FXML private Label warning;

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
        warning.setText("");
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
            Participant p = getParticipant();
            if(p.getName().equals("")) {
                warning.setText("Name cannot be empty!");
                return;
            }
            if(!p.getEmail().equals("") && !isEmailValid(p.getEmail())) {
                warning.setText("Invalid email!");
                return;
            }
            if(!p.getIban().equals("") && !isIbanValid(p.getIban())) {
                warning.setText("Invalid IBAN!");
                return;
            }
            if(!p.getBic().equals("") && !isIBicValid(p.getBic())) {
                warning.setText("Invalid BIC!");
                return;
            }
            server.addParticipant(p);
            warning.setText("");
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
     * Email validator.
     * @param emailAddress the email address to be checked.
     * @return is the email valid or not.
     */
    public static boolean isEmailValid(String emailAddress) {
        String regexPattern = "^(.+)@(\\S+)$";
        return Pattern.compile(regexPattern)
                .matcher(emailAddress)
                .matches();
    }

    /**
     * IBAN validator.
     * @param iban the IBAN to be checked.
     * @return is the IBAN valid or not.
     */
    public static boolean isIbanValid(String iban) {
        String regexPattern =
                "\\b[A-Z]{2}[0-9]{2}(?:[ ]?[0-9]{4}){4}(?!(?:[ ]?[0-9]){3})(?:[ ]?[0-9]{1,2})?\\b";
        return Pattern.compile(regexPattern)
                .matcher(iban)
                .matches();
    }

    /**
     * BIC validator.
     * @param bic the BIC to be checked.
     * @return is the BIC valid or not.
     */
    public static boolean isIBicValid(String bic) {
        String regexPattern = "^[a-zA-Z]{6}[0-9a-zA-Z]{2}([0-9a-zA-Z]{3})?$";
        return Pattern.compile(regexPattern)
                .matcher(bic)
                .matches();
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
