package client.scenes;

import client.utils.AlertBuilder;
import client.utils.ResourceManager;
import client.utils.ServerUtils;
import client.utils.language.LanguageProcessor;
import com.google.inject.Inject;
import commons.*;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
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
import javafx.stage.Modality;
import javafx.util.Callback;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.net.URL;
import java.util.*;

public class OverviewCtrl {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private final LanguageProcessor languageProcessor;
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
    private final ObservableList<Expense> expensesIncludingObs = FXCollections
            .observableArrayList();

    private Event ev;

    @FXML
    private Label title;
    @FXML
    private Button editTitleButton;
    @FXML
    private TitledPane languageNavigator;
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

    /**
     * Controller responsible for handling the quote overview functionality.
     * 
     * @param server An instance of ServerUtils for server-related operations.
     * @param mainCtrl An instance of MainCtrl for coordinating with the main controller.
     * @param languageProcessor instance of LanguageProcessor.
     */
    @Inject
    public OverviewCtrl(ServerUtils server, MainCtrl mainCtrl,
                        LanguageProcessor languageProcessor) {
        this.server = server;
        this.mainCtrl = mainCtrl;
        this.languageProcessor = languageProcessor;
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
        this.ev = server.getEventById(ev.getInviteCode());
        if (this.ev != null) {
            title.setText(ev.getName());
            participantsObs.clear();
            refreshParticipants();
            participantNames.setItems(participantsObs);
            server.refreshExpensesList(ev);
            this.refreshExpenses();
        }

        languageProcessor.populateTitledPane(languageNavigator, mainCtrl.getCurrentLocale()
                .orElse(Locale.of("en","EN")));
    }

    /**
     * Initialize callback from FXML
     */
    @FXML
    public void initialize() {
        initExpenses();
        initParticipants();
        languageNavigator.setExpanded(false);
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
                Text tags = OverviewCtrl.getTags(item);
                smallText.setFont(Font.font("System", FontWeight.NORMAL, 10));
                smallText.setFill(Color.GRAY.darker().darker());
                tags.setFont(Font.font("System", FontWeight.NORMAL, 10));
                tags.setFill(Color.GRAY.darker().darker());
                VBox vbox = new VBox(mainTextFlow, smallText, tags);
                vbox.setSpacing(5);
                Text dateText = new Text(item.getDate().toString());
                dateText.setFont(Font.font("System", FontWeight.NORMAL, 12));
                Region region = new Region();
                HBox.setHgrow(region, Priority.ALWAYS);
                Button editButton = new Button();
                Button deleteButton = new Button();
                editButton.setOnAction(e -> mainCtrl.showExpense(item.getEvent(), item));
                deleteButton.setOnAction(e -> deleteExpense(item));
                styleButton(deleteButton, "Delete expense", "/assets/bin.png");
                styleButton(editButton, "Edit expense", "/assets/pen-solid.png");
                HBox element = new HBox(dateText, vbox, region, deleteButton, editButton);
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

    private void styleButton(Button button, String tooltip, String image) {
        attachImage(button, image);
        button.setCursor(Cursor.cursor("HAND"));
        button.setTooltip(new Tooltip(tooltip));
    }

    private static Text getTags(Expense item) {
        Set<ExpenseType> expenseTypesSet = item.getTags();
        StringBuilder s = new StringBuilder();
        Iterator<ExpenseType> it = expenseTypesSet.iterator();
        for (int i = 0; i < expenseTypesSet.size(); ++i) {
            s.append(it.next().getName());
            if (i != expenseTypesSet.size() - 1) {
                s.append(", ");
            }
        }
        return new Text(s.toString());
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
        // Make sure the participants appear in a consistent, sorted order.
        participantsObs.sort((p1, p2) -> p1.getName().compareToIgnoreCase(p2.getName()));
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
        List<Expense> sortedExpenses = new ArrayList<>(this.ev.getExpenses());
        java.util.Collections.sort(sortedExpenses, (a, b) -> b.getDate().compareTo(a.getDate()));
        for (Expense e : sortedExpenses) {
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
     * Method to add a new participant. This method triggers the display of the add
     * participant
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
        if (ev.getParticipants().size() < 2) {
            new AlertBuilder(mainCtrl)
                    .setAlertType(Alert.AlertType.ERROR)
                    .setModality(Modality.APPLICATION_MODAL)
                    .setContentKey("content_expense_participants")
                    .show();
            return;
        }
        mainCtrl.showExpense(this.ev, null);
        refresh();
    }

    /**
     * Method to edit a new participant. This method triggers the display of the
     * edit participant
     * window.
     */
    public void editParticipant() {
        if (participantNames.getSelectionModel().getSelectedItem() == null) {
            new AlertBuilder(mainCtrl)
                    .setAlertType(Alert.AlertType.ERROR)
                    .setModality(Modality.APPLICATION_MODAL)
                    .setContentKey("content_edit_participant")
                    .show();
            return;
        }
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
     * Open a dialog with an input box to allow the user to enter a new event name.
     * The name that is
     * entered is made to be new event name. If the user cancels the dialog, no
     * action is performed.
     */
    public void editTitle() {
        ResourceManager resourceManager = new ResourceManager(mainCtrl);
        String changeText = resourceManager.getStringForKey("content_change_event_name");
        TextInputDialog dialog = new TextInputDialog(ev.getName());
        dialog.setTitle(changeText);

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
        mainCtrl.getSceneManager().goBack();
    }

    /**
     * Deletes a participant from the event.
     */
    public void deleteParticipant() {
        if (participantNames.getSelectionModel().getSelectedItem() == null) {
            new AlertBuilder(mainCtrl)
                    .setAlertType(Alert.AlertType.ERROR)
                    .setModality(Modality.APPLICATION_MODAL)
                    .setContentKey("content_delete_participant")
                    .show();
            return;
        }
        if (partOfExpense(participantNames.getSelectionModel().getSelectedItem())) {
            new AlertBuilder(mainCtrl)
                    .setAlertType(Alert.AlertType.ERROR)
                    .setModality(Modality.APPLICATION_MODAL)
                    .setContentKey("content_delete_participant_settle_debts")
                    .show();
            return;
        }

        ResourceManager resourceManager = new ResourceManager(this.mainCtrl);
        String confirmText = resourceManager.getStringForKey("confirm_button_text");
        String cancelText = resourceManager.getStringForKey("cancel_button_text");
        ButtonType confirm = new ButtonType(confirmText);
        ButtonType cancel = new ButtonType(cancelText, ButtonBar.ButtonData.CANCEL_CLOSE);

        AlertBuilder alertBuilder = new AlertBuilder(mainCtrl);
        Optional<ButtonType> result = alertBuilder
                .setAlertType(Alert.AlertType.CONFIRMATION)
                .setTitleKey("confirmation_title")
                .setHeaderKey("content_deleting_participant")
                .setContentKey("content_delete_tag_item")
                .alterContentText(participantNames.getSelectionModel()
                        .getSelectedItem().getName() + " %s")
                .setCustomButtons(confirm, cancel)
                .show();

        if (result.get() == confirm) {
            ev.removeParticipant(participantNames.getSelectionModel().getSelectedItem());
            server.updateEvent(ev);
            mainCtrl.showOverviewEvent(ev);
            refresh();
        } else {
            alertBuilder.closeAlert();
        }
        // server.deleteParticipant(participant);
    }

    /**
     * Delete an expense
     *
     * @param e The expense to delete
     */
    public void deleteExpense(Expense e) {
        Optional<ButtonType> result = new AlertBuilder(mainCtrl)
                .setAlertType(Alert.AlertType.CONFIRMATION)
                .setTitleKey("confirmation_title")
                .setHeaderKey("confirmation_header")
                .setContentKey("content_delete_expense")
                .show();

        if (!result.map(x -> x == ButtonType.OK).orElse(false)) {
            return;
        }
        try {
            server.deleteExpense(e);
        } catch (Exception err) {
            new AlertBuilder(mainCtrl)
                    .setAlertType(Alert.AlertType.ERROR)
                    .setContentKey(err.getMessage())
                    .show();
            return;
        }
        refresh();
    }

    /**
     * Checks weather a participant is part of an expense.
     *
     * @param participant participant to be checked.
     * @return weather the participant is part of any expense.
     */
    private boolean partOfExpense(Participant participant) {
        Set<Expense> expenses = ev.getExpenses();
        for (Expense expense : expenses) {
            if (expense.getCreator().equals(participant))
                return true;
            if (expense.getSplitBetween().contains(participant))
                return true;
        }
        return false;
    }

    /**
     * Goes to open debts page
     */
    @FXML
    public void settleDebt() {
        List<Debt> list = server.calculateDebts(ev);
        if (list.isEmpty()) {
            ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);

            AlertBuilder alertBuilder = new AlertBuilder(mainCtrl);
            Optional<ButtonType> result = alertBuilder
                    .setAlertType(Alert.AlertType.ERROR)
                    .setTitleKey("error_title")
                    .setContentKey("content_settle_debt")
                    .setCustomButtons(okButton)
                    .show();

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
            case S:
                if(e.isControlDown()) showStatistics();
                break;
            case E:
                if(e.isControlDown()) addExpense();
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

    /**
     * Shows the statistics screen.
     */
    public void showStatistics() {
        if (ev.getExpenses() == null || ev.getExpenses().size() == 0) {
            new AlertBuilder(mainCtrl)
                    .setAlertType(Alert.AlertType.ERROR)
                    .setModality(Modality.APPLICATION_MODAL)
                    .setContentKey("content_show_statistics")
                    .show();
            return;
        }
        mainCtrl.showStatistics(ev);
    }
    private void alert(String content) {
        var alert = new Alert(Alert.AlertType.ERROR);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
