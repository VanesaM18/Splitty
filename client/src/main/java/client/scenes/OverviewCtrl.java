package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Event;
import commons.Participant;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.net.URL;

public class OverviewCtrl {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private Event ev;

    @FXML
    private Label title;
    @FXML
    private Button addParticipantButton;
    @FXML
    private Button deleteParticipantButton;
    @FXML
    private Button editParticipantButton;
    @FXML
    private ListView<String> participantNames;
    private final ObservableList<String> participantNamesObs = FXCollections.observableArrayList();
    @FXML
    private ComboBox<String> participantComboBox;
    @FXML
    private Label warning;

    /**
     * Controller responsible for handling the quote overview functionality.
     * @param server An instance of ServerUtils for server-related operations.
     * @param mainCtrl An instance of MainCtrl for coordinating with the main controller.
     */
    @Inject
    public OverviewCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }

    /**
     * Initialize the event data
     * @param ev the event
     */
    public void setEvent(Event ev) {
        this.ev = ev;
    }
    /**
     * Method to refresh the current view.
     */
    public void refresh() {
        title.setText(ev.getName());
        this.attachImage(addParticipantButton, "/assets/user-plus-solid.png");
        this.attachImage(editParticipantButton, "/assets/pen-solid.png");
        this.attachImage(deleteParticipantButton, "/assets/bin.png");
        if (this.ev != null) {
            this.ev = server.getEventById(ev.getInviteCode());
            participantNamesObs.clear();
            for (Participant p : this.ev.getParticipants()) {
                participantNamesObs.add(p.getName());
            }
            participantNames.setItems(participantNamesObs);
            participantComboBox.setItems(participantNamesObs);
        }
    }

    /**
     * Attaches an image to a button
     * @param but the button to attach to
     * @param url the url to the image
     */
    public void attachImage(Button but, String url) {
        URL imageUrl = getClass().getResource(url);
        if (imageUrl != null) {
            Image image = new Image(imageUrl.toExternalForm());
            ImageView imageView = new ImageView(image);
            imageView.setFitHeight(25.0);
            imageView.setFitWidth(18.0);
            imageView.setPickOnBounds(true);
            imageView.setPreserveRatio(true);
            but.setGraphic(imageView);
        } else {
            System.out.println("Image URL is null. Check the path to the image file.");
        }
    }

    /**
     * Method to add a new participant.
     * This method triggers the display of the add participant window.
     */
    public void addParticipant() {
        mainCtrl.showParticipants(this.ev, true, "");
    }

    /**
     * Method to edit a new participant.
     * This method triggers the display of the edit participant window.
     */
    public void editParticipant() {
        if(participantNames.getSelectionModel().getSelectedItem() == null) {
            warning.setText("First chose a participant.");
            return;
        }
        warning.setText("");
        mainCtrl.showParticipants(this.ev, false,
                participantNames.getSelectionModel().getSelectedItem());
    }

    /**
     * Goes back to the starting page.
     */
    public void back() {
        mainCtrl.showStartScreen();
    }

    /**
     *  Deletes a participant from the event.
     */
    public void deleteParticipant() {
        if(participantNames.getSelectionModel().getSelectedItem() == null) {
            warning.setText("First chose a participant.");
            return;
        }
        warning.setText("");
        String name = participantNames.getSelectionModel().getSelectedItem();
        ev.removeParticipant(name);
        server.updateEvent(ev);
        refresh();
        mainCtrl.showOverviewEvent(ev);
    }
}
