package client.scenes;

import com.google.inject.Inject;
import commons.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

public class InviteScreenCtrl {

    private final MainCtrl mainCtrl;
    @FXML
    private Label eventNameLabel;
    @FXML
    private Label inviteCodeLabel;
    @FXML
    private Button backButton;
    @FXML
    private Button sendInvitesButton;
    @FXML
    private TextArea emailTextArea;

    private Event event;

    /**
     * Controller responsible for showing the invite code.
     * 
     * @param mainCtrl An instance of MainCtrl for coordinating with the main controller.
     */
    @Inject
    public InviteScreenCtrl(MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
    }

    /**
     * Does nothing. In the future this will call an API endpoint that sends the emails.
     */
    public void sendInvites() {
        // NOTE: NOOP
    }

    /**
     * Return to the event overview screen
     */
    public void goBack() {
        mainCtrl.getSceneManager().goBack();
        //mainCtrl.showOverviewEvent(null);
    }

    /**
     * Refresh the current view
     */
    public void refresh() {
        eventNameLabel.setText(event.getName());
        inviteCodeLabel.setText("Give people the following invite code: " + event.getInviteCode());
        emailTextArea.setText("");
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
