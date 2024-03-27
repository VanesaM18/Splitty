package client.scenes;

import client.ConfigLoader;
import client.utils.EmailManager;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Event;
import commons.Participant;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.StackPane;

public class InviteScreenCtrl {

    private final MainCtrl mainCtrl;
    private final EmailManager emailManager;
    private final ServerUtils serverUtils;
    private final ConfigLoader configLoader;
    @FXML
    private Label eventNameLabel;
    @FXML
    private Label inviteCodeLabel;
    @FXML
    private Button backButton;
    @FXML
    private StackPane sendInvitesButtonWrapper;
    @FXML
    private TextArea emailTextArea;
    @FXML
    private Button sendInvitesButton;
    private Event event;

    private final Tooltip sendInvitesTooltip = new Tooltip();
    private boolean isSendingEmail = false;
    private boolean existingName = false;

    /**
     * Controller responsible for showing the invite code.
     * 
     * @param mainCtrl An instance of MainCtrl for coordinating with the main controller.
     * @param emailManager An instance of EmailManager for sending emails.
     * @param configLoader An instance of ConfigLoader for getting the server address.
     * @param serverUtils An instance of ServerUtils for coordinating with the server
     */
    @Inject
    public InviteScreenCtrl(MainCtrl mainCtrl, EmailManager emailManager,
                            ServerUtils serverUtils, ConfigLoader configLoader) {
        this.mainCtrl = mainCtrl;
        this.emailManager = emailManager;
        this.serverUtils = serverUtils;
        this.configLoader = configLoader;
        sendInvitesTooltip.setShowDelay(javafx.util.Duration.ZERO);
        sendInvitesTooltip.setHideDelay(javafx.util.Duration.ZERO);
    }

    /**
     * Initializes the invite view with the required elements
     */
    public void initialize() {
        emailTextArea.textProperty().addListener((observable, oldValue, newValue) -> {
            String[] emails = newValue.split("\n");
            existingName = false;
            for (String email: emails) {
                if (!(email.isEmpty() || (email.equals("@")))) {
                    String participantName = email.split("@")[0];
                    for (Participant p : event.getParticipants()) {
                        if (p.getName().equals(participantName)) {
                            existingName = true;
                            break;
                        }
                    }
                }
            }
            updateSendInvitesButtonState();
        });
        sendInvitesButton.styleProperty().bind(Bindings.when(sendInvitesButton.disabledProperty())
            .then("-fx-background-color: lightgrey; -fx-text-fill: darkgrey;")
            .otherwise("-fx-background-color: lightblue; -fx-text-fill: black;"));
        Tooltip.install(sendInvitesButtonWrapper, sendInvitesTooltip);
        Tooltip.install(sendInvitesButton, sendInvitesTooltip);
        updateSendInvitesButtonState();
    }

    /**
     * Updates the state of the invite button
     */
    private void updateSendInvitesButtonState() {
        if (!emailManager.areCredentialsValid()) {
            sendInvitesTooltip.setText("Email credentials invalid");
            sendInvitesButton.setDisable(true);
        } else if (emailTextArea.getText().trim().isEmpty()) {
            sendInvitesTooltip.setText("Email field is empty");
            sendInvitesButton.setDisable(true);
        } else if(existingName) {
            sendInvitesTooltip.setText("Participant already invited");
            sendInvitesButton.setDisable(true);
        } else {
            sendInvitesTooltip.setText("Invite to join the event");
            sendInvitesButton.setDisable(isSendingEmail);
        }
    }

    /**
     * Sends invites to all email address in the text field
     */
    public void sendInvites() {
        if (emailTextArea.getText().trim().isEmpty() || !emailManager.areCredentialsValid()) {
            return;
        }

        isSendingEmail = true;
        updateSendInvitesButtonState();
        sendInvitesButton.setText("Sending...");

        new Thread(() -> {
            String address = (String) configLoader.getProperty("address");
            String[] emails = emailTextArea.getText().split("\n");
            for (String email: emails) {
                emailManager.sendEmail(email, "Your are invited to join event "
                    + event.getName() + "!", "The invite code is " + event.getInviteCode() +
                    "\nThe server address is: " + address +
                    "\nYou can join using the Splitty app, you will find in the people list a " +
                    "person with the name of your email, press on it and edit with your details.\n"+
                    "Happy splitting!");
                if (!email.equals("@") && !email.isEmpty()) {
                    event.addParticipant(new Participant(email.split("@")[0],
                        email, "", ""));
                    serverUtils.updateEvent(event);
                }
            }

            Platform.runLater(() -> {
                sendInvitesButton.setText("Send invites");
                isSendingEmail = false;
                updateSendInvitesButtonState();
            });
            refresh();
        }).start();
    }

    /**
     * Return to the event overview screen
     */
    public void goBack() {
        mainCtrl.getSceneManager().goBack();
    }

    /**
     * Refresh the current view
     */
    public void refresh() {
        eventNameLabel.setText(event.getName());
        inviteCodeLabel.setText("Give people the following invite code: " + event.getInviteCode());
        emailTextArea.setText("");
        event = serverUtils.getEventById(event.getInviteCode());
    }

    /**
     * Replace the current event
     * 
     * @param ev The new event.
     */
    public void setEvent(Event ev) {
        this.event = ev;
    }

}
