package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Debt;
import commons.Event;
import commons.Expense;
import commons.Participant;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Callback;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.net.URL;
import java.util.*;

public class OverviewCtrl {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    @FXML
    private ListView<Expense> expensesAll;
    @FXML
    private ListView<Expense> expensesFrom;
    @FXML
    private Tab tabFrom;
    @FXML
    private ListView<Expense> expensesIncluding;
    @FXML
    private Tab tabIncluding;

    private final ObservableList<Expense> expensesAllObs = FXCollections.observableArrayList();
    private final ObservableList<Expense> expensesFromObs = FXCollections.observableArrayList();
    private final ObservableList<Expense> expensesIncludingObs =
            FXCollections.observableArrayList();

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
        warning.setText("");
        if (ev == null) {
            return;
        }
        title.setText(ev.getName());
        this.attachImage(editTitleButton, "/assets/pen-solid.png");
        this.attachImage(addParticipantButton, "/assets/user-plus-solid.png");
        this.attachImage(editParticipantButton, "/assets/pen-solid.png");
        this.attachImage(deleteParticipantButton, "/assets/bin.png");
        this.ev = server.getEventById(ev.getInviteCode());
        if (this.ev != null) {
            title.setText(ev.getName());
            participantsObs.clear();
            refreshParticipants();
            participantNames.setItems(participantsObs);
            this.refreshExpenses();
        }
    }

    /**
     * Initialize callback from FXML
     */
    @FXML
    public void initialize() {
        initExpenses();
        initParticipants();
    }

    private void initExpenses() {
        Callback<ListView<Expense>, ListCell<Expense>> cb = lv -> new ListCell<Expense>() {
            @Override
            protected void updateItem(Expense item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setGraphic(null);
                    return;
                }
                Text textBoldP1 = new Text(item.getCreator().getName());
                Text textBoldP2 = new Text("" + item.getAmount().toString()
                        + item.getAmount().getCurrency().getSymbol());
                Text textBoldP3 = new Text(item.getName());
                textBoldP1.setFont(Font.font("System", FontWeight.BOLD, 12));
                textBoldP2.setFont(Font.font("System", FontWeight.BOLD, 12));
                textBoldP3.setFont(Font.font("System", FontWeight.BOLD, 12));
                TextFlow mainTextFlow = new TextFlow(textBoldP1, new Text(" paid "),
                        textBoldP2, new Text(" for "), textBoldP3);
                Text smallText = OverviewCtrl.getText(item);
                smallText.setFont(Font.font("System", FontWeight.NORMAL, 10));
                smallText.setFill(Color.GRAY.darker().darker());

                VBox vbox = new VBox(mainTextFlow, smallText);
                vbox.setSpacing(5);

                Text dateText = new Text(item.getDate().toString());
                dateText.setFont(Font.font("System", FontWeight.NORMAL, 12));
                Region region = new Region();
                HBox.setHgrow(region, Priority.ALWAYS);
                Button editButton = new Button();
                editButton.setOnAction(e -> mainCtrl.showExpense(item.getEvent(),
                        participantComboBox.getSelectionModel().getSelectedItem(), item));
                attachImage(editButton, "/assets/pen-solid.png");
                HBox element = new HBox(dateText, vbox, region, editButton);
                element.setSpacing(15);
                setGraphic(element);
            }
        };
        expensesAll.setCellFactory(cb);
        expensesFrom.setCellFactory(cb);
        expensesIncluding.setCellFactory(cb);
        expensesAll.setItems(expensesAllObs);
        expensesFrom.setItems(expensesFromObs);
        expensesIncluding.setItems(expensesIncludingObs);
    }

    /**
     * Returns a nice formatted text of the participants in the expense
     * 
     * @param item the expense
     * @return the nice formatted text
     */
    private static Text getText(Expense item) {
        Set<Participant> participantSet = item.getSplitBetween();
        StringBuilder s = new StringBuilder();
        Iterator<Participant> it = participantSet.iterator();
        for (int i = 0; i < participantSet.size(); ++i) {
            s.append(it.next().getName());
            if (i != participantSet.size() - 1) {
                s.append(", ");
            }
        }
        return new Text("(" + s + ")");
    }

    private void initParticipants() {
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
        var selectedParticipant = participantComboBox.getSelectionModel().selectedItemProperty();
        selectedParticipant.subscribe(participant -> refreshExpenses());

        participantComboBox.setCellFactory(cb);
        participantComboBox.setButtonCell(cb.call(null));
        participantNames.setCellFactory(cb);
        participantComboBox.setItems(participantsObs);
        participantNames.setItems(participantsObs);
    }

    private void refreshParticipants() {

        participantsObs.clear();
        for (Participant p : this.ev.getParticipants()) {
            participantsObs.add(p);
        }
    }

    private void refreshExpenses() {
        if (this.ev == null) {
            return;
        }
        var selectedItem = participantComboBox.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            var name = selectedItem.getName();
            tabFrom.setText("From ".concat(name));
            tabIncluding.setText("Including ".concat(name));
        } else {
            tabFrom.setText("From ...");
            tabIncluding.setText("Including ...");
        }
        expensesAllObs.clear();
        expensesFromObs.clear();
        expensesIncludingObs.clear();

        Participant selectedParticipant = participantComboBox.getSelectionModel().getSelectedItem();
        for (Expense e : this.ev.getExpenses()) {
            expensesAllObs.add(e);
            if (Objects.equals(e.getCreator(),
                    participantComboBox.getSelectionModel().getSelectedItem())) {
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
     * Method to add a new participant. This method triggers the display of the add participant
     * window.
     */
    public void addParticipant() {
        mainCtrl.showParticipants(this.ev, true, null);
        refresh();
    }

    /**
     * Trigger the new expense dialog
     */
    public void addExpense() {
        if(ev.getParticipants().size() < 2) {
            warning.setText("Not enough people!");
            return;
        }
        mainCtrl.showExpense(this.ev, participantComboBox.getSelectionModel().getSelectedItem(),
                null);
        refresh();
        warning.setText("");
    }

    /**
     * Method to edit a new participant. This method triggers the display of the edit participant
     * window.
     */
    public void editParticipant() {
        if (participantNames.getSelectionModel().getSelectedItem() == null) {
            warning.setText("First chose a participant.");
            return;
        }
        warning.setText("");
        mainCtrl.showParticipants(this.ev, false,
                participantNames.getSelectionModel().getSelectedItem());
    }

    /**
     * Show the send invites screen.
     */
    public void sendInvites() {
        mainCtrl.showInviteScreen(this.ev);
    }

    /**
     * Open a dialog with an input box to allow the user to enter a new event name. The name that is
     * entered is made to be new event name. If the user cancels the dialog, no action is performed.
     */
    public void editTitle() {
        TextInputDialog dialog = new TextInputDialog(ev.getName());
        dialog.setTitle("Change event name");

        Optional<String> newNameOpt = dialog.showAndWait();
        if (!newNameOpt.isPresent()) {
            warning.setText("");
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
        mainCtrl.getSceneManager().goBack();
        warning.setText("");
    }

    /**
     * Deletes a participant from the event.
     */
    public void deleteParticipant() {
        if (participantNames.getSelectionModel().getSelectedItem() == null) {
            warning.setText("First chose a participant.");
            return;
        }
        if(partOfExpense(participantNames.getSelectionModel().getSelectedItem())) {
            warning.setText("Settle debt first!");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation required");
        alert.setHeaderText("Deleting a participant");
        alert.setContentText(participantNames.getSelectionModel().getSelectedItem().getName()
                + " will de deleted.");

        ButtonType confirm = new ButtonType("Confirm");
        ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(confirm, cancel);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == confirm){
            warning.setText("");
            ev.removeParticipant(participantNames.getSelectionModel().getSelectedItem());
            server.updateEvent(ev);
            mainCtrl.showOverviewEvent(ev);
            refresh();
        } else {
            alert.close();
        }
        // server.deleteParticipant(participant);
    }

    /**
     * Checks weather a participant is part of an expense.
     * @param participant participant to be checked.
     * @return weather the participant is part of any expense.
     */
    private boolean partOfExpense(Participant participant) {
        Set<Expense> expenses = ev.getExpenses();
        for(Expense expense : expenses) {
            if(expense.getCreator().equals(participant)) return true;
            if(expense.getSplitBetween().contains(participant)) return true;
        }
        return false;
    }

    /**
     * Goes to open debts page
     */
    @FXML
    public void settleDebt() {
        List<Debt> list = Event.finalCalculation(ev);
        if (list.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("No debts to settle.");

            ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
            alert.getButtonTypes().setAll(okButton);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == okButton) {
                mainCtrl.showOverviewEvent(ev);
            }
        } else {
            mainCtrl.showOpenDebts(this.ev);
        }
    }

    /**
     * Event handler for pressing a key.
     *
     * @param e the key that is pressed
     */
    public void keyPressed(KeyEvent e) {
        switch (e.getCode()) {
            case ESCAPE:
                back();
                break;
            default:
                break;
        }
    }

    /**
     * Shows the tags screen.
     */
    public void showTags() {
        mainCtrl.showExpenseTypes(ev);
    }
}
