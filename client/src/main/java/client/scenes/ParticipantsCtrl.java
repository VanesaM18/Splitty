package client.scenes;

import client.utils.AlertBuilder;
import client.utils.ServerUtils;

import com.google.inject.Inject;

import commons.Event;
import commons.Participant;

import jakarta.ws.rs.WebApplicationException;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
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
     * @param server       An instance of ServerUtils for server-related operations.
     * @param mainCtrl     An instance of MainCtrl for coordinating with the main
     *                     controller.
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
        mainCtrl.showOverviewEvent(null);
    }

    /**
     * Clears the text fields.
     */
    public void clearFields() {
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
            if (checkParticipantDetails(p)) return;
        } catch (WebApplicationException e) {
            new AlertBuilder(mainCtrl)
                    .setAlertType(Alert.AlertType.ERROR)
                    .setModality(Modality.APPLICATION_MODAL)
                    .alterContentText(e.getMessage() + "%s")
                    .show();
            return;
        }

        clearFields();
        mainCtrl.showOverviewEvent(ev);
    }

    private boolean checkParticipantDetails(Participant p) {
        if (!uniqueName(ev, p)) {
            new AlertBuilder(mainCtrl)
                    .setAlertType(Alert.AlertType.ERROR)
                    .setModality(Modality.APPLICATION_MODAL)
                    .setContentKey("content_participant_unique_name")
                    .show();
            return true;
        }
        if (p.getName().equals("")) {
            new AlertBuilder(mainCtrl)
                    .setAlertType(Alert.AlertType.ERROR)
                    .setModality(Modality.APPLICATION_MODAL)
                    .setContentKey("content_participant_name")
                    .show();
            return true;
        }
        if (!p.getEmail().equals("") && !isEmailValid(p.getEmail())) {
            new AlertBuilder(mainCtrl)
                    .setAlertType(Alert.AlertType.ERROR)
                    .setModality(Modality.APPLICATION_MODAL)
                    .setContentKey("content_participant_email")
                    .show();
            return true;
        }
        if (!p.getIban().equals("") && !isIbanValid(p.getIban())) {
            new AlertBuilder(mainCtrl)
                    .setAlertType(Alert.AlertType.ERROR)
                    .setModality(Modality.APPLICATION_MODAL)
                    .setContentKey("content_participant_iban")
                    .show();
            return true;
        }
        if (!p.getBic().equals("") && !isIBicValid(p.getBic())) {
            new AlertBuilder(mainCtrl)
                    .setAlertType(Alert.AlertType.ERROR)
                    .setModality(Modality.APPLICATION_MODAL)
                    .setContentKey("content_participant_bic")
                    .show();
            return true;
        }
        p = server.addParticipant(p);
        ev.addParticipant(p);
        server.updateEvent(ev);
        return false;
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
    public boolean uniqueUpdate(Event ev, Participant p) {
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
        String regexPattern = "^[^@]+@[^@.]+\\.[^@.]+$";
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
     * Set weather or not we are editing or adding a participant.
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
    private void alert(String content) {
        var alert = new Alert(Alert.AlertType.ERROR);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
