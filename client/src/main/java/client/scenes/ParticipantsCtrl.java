package client.scenes;

import client.utils.ServerUtils;

import com.google.inject.Inject;

import commons.Event;
import commons.Participant;

import jakarta.ws.rs.WebApplicationException;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;

import java.util.Set;
import java.util.regex.Pattern;

public class ParticipantsCtrl {
    private boolean add;
    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    @FXML
    private Label warning;

    @FXML
    private TextField name;

    @FXML
    private TextField email;

    @FXML
    private TextField iban;

    @FXML
    private TextField bic;
    private Event ev;

    private Participant participantToChange;

    /**
     * Controller responsible for handling the addition of participants
     * functionality.
     * 
     * @param server   An instance of ServerUtils for server-related operations.
     * @param mainCtrl An instance of MainCtrl for coordinating with the main
     *                 controller.
     */
    @Inject
    public ParticipantsCtrl(ServerUtils server, MainCtrl mainCtrl) {
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
        Participant p = participantToChange;
        try {
            p = getParticipant();
            if (!uniqueName(ev, p)) {
                warning.setText("Not a unique name!");
                return;
            }
            if (p.getName().equals("")) {
                warning.setText("Name cannot be empty!");
                return;
            }
            if (p.getEmail().equals("") || !isEmailValid(p.getEmail())) {
                warning.setText("Invalid email!");
                return;
            }
            if (p.getIban().equals("") || !isIbanValid(p.getIban())) {
                warning.setText("Invalid IBAN!");
                return;
            }
            if (p.getBic().equals("") || !isIBicValid(p.getBic())) {
                warning.setText("Invalid BIC!");
                return;
            }
            p = server.addParticipant(p);
            ev.addParticipant(p);
            server.updateEvent(ev);
            warning.setText("");
        } catch (WebApplicationException e) {

            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            return;
        }

        clearFields();
        mainCtrl.showOverviewEvent(ev);
    }

    /**
     * Checks if the participants name is unique for the event.
     * 
     * @param ev event to be looked into.
     * @param p  participant whose name is to be checked.
     * @return whether the name is unique.
     */
    private boolean uniqueName(Event ev, Participant p) {
        if (!add) {
            return uniqueUpdate(ev, p);
        }
        Set<Participant> participants = ev.getParticipants();
        for (Participant participant : participants) {
            if (participant.getName().equals(p.getName()))
                return false;
        }
        return true;
    }

    /**
     * Checks if the participants name is only once in the event.
     * 
     * @param ev event to be looked into.
     * @param p  participant whose name is to be checked.
     * @return whether the name is unique.
     */
    private boolean uniqueUpdate(Event ev, Participant p) {
        int count = 0;
        Set<Participant> participants = ev.getParticipants();
        for (Participant participant : participants) {
            if (participant.getName().equals(p.getName()))
                count++;
            if (count > 1)
                return false;
        }
        return true;
    }

    /**
     * Email validator.
     * 
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
     * 
     * @param iban the IBAN to be checked.
     * @return is the IBAN valid or not.
     */
    public static boolean isIbanValid(String iban) {
        String regexPattern = "[a-zA-Z]{2}[0-9]{2}[a-zA-Z0-9]{4}[0-9]{7}([a-zA-Z0-9]?){0,16}";
        return Pattern.compile(regexPattern)
                .matcher(iban)
                .matches();
    }

    /**
     * BIC validator.
     * 
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
     * Creates or updates a participant from the text fields.
     * 
     * @return the created participant.
     */
    private Participant getParticipant() {
        Participant p = participantToChange;
        if (p == null) {
            p = new Participant(name.getText(), email.getText(), iban.getText(), bic.getText());
        } else {
            p.setName(name.getText());
            p.setIban(iban.getText());
            p.setEmail(email.getText());
            p.setBic(bic.getText());
        }
        return p;
    }

    /**
     * Event handler for pressing a key.
     * 
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

    /**
     * Sets the event where to add the participant
     * 
     * @param ev the event
     */
    public void setEvent(Event ev) {
        this.ev = ev;
    }

    /**
     * Checks weather the user is adding or editing participants.
     * 
     * @return true - add / false - edit.
     */
    public boolean isAdd() {
        return add;
    }

    /**
     * Set whether or not we are editing or adding a participant.
     * 
     * @param add true - add / false - edit.
     */
    public void setAdd(boolean add) {
        this.add = add;
    }

    /**
     * Sets the fields with the details of the participant
     * we want to update.
     * 
     * @param p
     */
    public void setFields(Participant p) {
        iban.setText(p.getIban());
        name.setText(p.getName());
        bic.setText(p.getBic());
        email.setText(p.getEmail());
        participantToChange = p;
    }

    /**
     * Sets the participant to be changed when editing.
     * 
     * @param participantToChange participant to be changed.
     */
    public void setParticipantToChange(Participant participantToChange) {
        this.participantToChange = participantToChange;
    }
}
