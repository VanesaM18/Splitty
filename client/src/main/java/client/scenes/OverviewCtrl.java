package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Event;
import commons.Expense;
import commons.Participant;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.util.Callback;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.net.URL;
import java.util.Objects;
import java.util.Optional;

public class OverviewCtrl {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    @FXML
    private ListView<Expense> expensesAll;
    @FXML
    private ListView<Expense> expensesFrom;
    @FXML
    private ListView<Expense> expensesIncluding;

    private final ObservableList<Expense> expensesAllObs = FXCollections.observableArrayList();
    private final ObservableList<Expense> expensesFromObs = FXCollections.observableArrayList();
    private final ObservableList<Expense> expensesIncludingObs = FXCollections.observableArrayList();

    private Event ev;

    @FXML
    private Label title;
    @FXML
    private Button editTitleButton;
    @FXML
    private Button addParticipantButton;
    @FXML
    private Button deleteParticipantButton;
    @FXML
    private Button editParticipantButton;
    @FXML
    private ListView<Participant> participantNames;
    private final ObservableList<Participant> participantsObs = FXCollections.observableArrayList();
    @FXML
    private ComboBox<Participant> participantComboBox;
    @FXML
    private Label warning;

    /**
     * Controller responsible for handling the quote overview functionality.
     * 
     * @param server   An instance of ServerUtils for server-related operations.
     * @param mainCtrl An instance of MainCtrl for coordinating with the main
     *                 controller.
     */
    @Inject
    public OverviewCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }

    /**
     * Initialize the event data
     * 
     * @param ev the event
     */
    public void setEvent(Event ev) {
        this.ev = ev;
    }

    /**
     * Method to refresh the current view.
     */
    public void refresh() {
        if (ev == null) {
            return;
        }
        title.setText(ev.getName());
        this.attachImage(editTitleButton, "/assets/pen-solid.png");
        this.attachImage(addParticipantButton, "/assets/user-plus-solid.png");
        this.attachImage(editParticipantButton, "/assets/pen-solid.png");
        this.attachImage(deleteParticipantButton, "/assets/bin.png");
        if (this.ev != null) {
            this.ev = server.getEventById(ev.getInviteCode());
            participantsObs.clear();
            refreshParticipants();
            participantNames.setItems(participantsObs);
            this.refreshExpenses();
        }
    }

    private void refreshParticipants() {
        var cb = new Callback<ListView<Participant>, ListCell<Participant>>() {
            @Override
            public ListCell<Participant> call(ListView<Participant> listView) {
                return new ListCell<Participant>() {
                    @Override
                    protected void updateItem(Participant item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setGraphic(null);
                            return;
                        }
                        setGraphic(new Text(item.getName()));
                    }
                };
            }
        };

        for (Participant p : this.ev.getParticipants()) {
            participantsObs.add(p);
        }
        participantComboBox.setCellFactory(cb);
        participantComboBox.setButtonCell(cb.call(null));
        participantNames.setCellFactory(cb);
        participantComboBox.setItems(participantsObs);
        participantNames.setItems(participantsObs);
    }

    private void refreshExpenses() {
        var cb = new Callback<ListView<Expense>, ListCell<Expense>>() {
            @Override
            public ListCell<Expense> call(ListView<Expense> listView) {
                return new ListCell<Expense>() {
                    @Override
                    protected void updateItem(Expense item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setGraphic(null);
                            return;
                        }
                        // TODO: Make this more detailed
                        setGraphic(new Text(item.getName()));
                    }
                };
            }
        };
        if (expensesAll != null)
            expensesAll.setCellFactory(cb);
        if (expensesFrom != null)
            expensesFrom.setCellFactory(cb);
        if (expensesIncluding != null)
            expensesIncluding.setCellFactory(cb);
        expensesAllObs.clear();
        expensesFromObs.clear();
        expensesIncludingObs.clear();

        Participant selectedParticipant = participantComboBox.getSelectionModel().getSelectedItem();
        for (Expense e : this.ev.getExpenses()) {
            expensesAllObs.add(e);
            if (Objects.equals(e.getCreator(), participantComboBox.getSelectionModel().getSelectedItem())) {
                expensesFromObs.add(e);
            }
            if (e.getSplitBetween().contains(selectedParticipant)) {
                expensesIncludingObs.add(e);
            }
        }
    }

    /**
     * Attaches an image to a button
     * 
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
     * Method to add a new participant. This method triggers the display of the add
     * participant window.
     */
    public void addParticipant() {
        mainCtrl.showParticipants(this.ev, true, null);
        refresh();
    }

    public void addExpense() {
        // if (participantComboBox.getSelectionModel().getSelectedItem() == null) {
        // // TODO: Add separate warning label
        // warning.setText("First choose a participant");
        // }
        mainCtrl.showExpense(this.ev, participantComboBox.getSelectionModel().getSelectedItem(), null);
        refresh();
    }

    /**
     * Method to edit a new participant.
     * This method triggers the display of the edit participant window.
     */
    public void editParticipant() {
        if (participantNames.getSelectionModel().getSelectedItem() == null) {
            warning.setText("First chose a participant.");
            return;
        }
        warning.setText("");
        mainCtrl.showParticipants(this.ev, false,
                participantNames
                        .getSelectionModel()
                        .getSelectedItem());
    }

    /**
     * Show the send invites screen.
     */
    public void sendInvites() {
        mainCtrl.showInviteScreen(this.ev);
    }

    /**
     * Open a dialog with an input box to allow the user to enter a new event name.
     * The name that is
     * entered is made to be new event name. If the user cancels the dialog, no
     * action is performed.
     */
    public void editTitle() {
        TextInputDialog dialog = new TextInputDialog(ev.getName());
        dialog.setTitle("Change event name");

        Optional<String> newNameOpt = dialog.showAndWait();
        if (!newNameOpt.isPresent()) {
            return;
        }

        ev.setName(newNameOpt.get());
        server.updateEvent(ev);
        refresh();
    }

    /**
     * Goes back to the starting page.
     */
    public void back() {
        mainCtrl.showStartScreen();
    }

    /**
     * Deletes a participant from the event.
     */
    public void deleteParticipant() {
        if (participantNames.getSelectionModel().getSelectedItem() == null) {
            warning.setText("First chose a participant.");
            return;
        }
        warning.setText("");
        ev.removeParticipant(participantNames.getSelectionModel().getSelectedItem());
        server.updateEvent(ev);
        mainCtrl.showOverviewEvent(ev);
        refresh();
        // server.deleteParticipant(participant);
    }
}
