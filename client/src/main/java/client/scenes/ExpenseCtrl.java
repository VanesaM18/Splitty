package client.scenes;

import java.time.LocalDate;
import java.util.Currency;
import java.util.HashSet;

import com.google.inject.Inject;

import client.utils.ServerUtils;
import commons.Event;
import commons.Expense;
import commons.Monetary;
import commons.Participant;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.util.Callback;
import javafx.util.StringConverter;

public class ExpenseCtrl {
    @FXML
    private TextField description;

    @FXML
    private TextField amount;

    @FXML
    private DatePicker date;

    @FXML
    private Label warning;

    @FXML
    private ComboBox<Participant> receiver;

    private final ObservableList<Participant> participantsObs = FXCollections.observableArrayList();

    @FXML
    private ListView<Participant> selectParticipant;

    private final ObservableSet<Participant> selectParticipantsObs = FXCollections.observableSet();

    private final MainCtrl mainCtrl;
    private final ServerUtils server;
    private Event event;

    private Expense updateExpense;

    /**
     * Construct an ExpenseCtrl
     *
     * @param server   The server utilities (gets injected)
     * @param mainCtrl The main controller (gets injected)
     */
    @Inject
    public ExpenseCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;

    }

    /**
     * Initialize the controller
     */
    @FXML
    public void initialize() {
        selectParticipant.setItems(participantsObs);
        initReceiverCombobox();
        initSelectParticipants();
    }

    /**
     * Get the event currently linked to the controller
     *
     * @return The event
     */
    public Event getEvent() {
        return event;
    }

    /**
     * Link a new event to the controller
     *
     * @param event The event to link
     */
    public void setEvent(Event event) {
        this.event = event;
        clearFields();
        participantsObs.addAll(event.getParticipants());
    }

    /**
     * Set updateExpense
     *
     * @param e The expense to set
     */
    public void setUpdateExpense(Expense e) {
        this.updateExpense = e;
        if (e == null) {
            return;
        }
        this.date.setValue(e.getDate());
        this.description.setText(e.getName());
        this.amount.setText(e.getAmount().toString());
        this.participantsObs.clear();
        this.selectParticipantsObs.clear();
        this.selectParticipantsObs.addAll(e.getSplitBetween());
        this.participantsObs.addAll(this.event.getParticipants());
        this.receiver.getSelectionModel().select(e.getCreator());
    }

    /**
     * Abort adding/editing expense
     */
    public void abort() {
        clearFields();
        warning.setText("");
        mainCtrl.showOverviewEvent(null);
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
     * Validates the fields and updates value on the server if successful.
     */
    public void ok() {
        Expense expense = updateExpense;
        boolean newExpense = false;
        if (expense == null) {
            expense = new Expense();
            newExpense = true;
        }

        try {
            expense.setAmount(getAmount());
            expense.setDate(getDate());
            expense.setName(getName());
            expense.setReceiver(validateReceiver());
            expense.setEvent(this.event);
            System.out.println("TEST: " + selectParticipantsObs.toString());
            var newSplit = new HashSet<>(selectParticipantsObs);
            System.out.println("NEW SPLIT: " + newSplit.toString());
            expense.setSplitBetween(newSplit);
        } catch (Exception ex) {
            warning.setText(ex.getMessage());
            return;
        }

        warning.setText("");
        try {
            if (newExpense) {
                server.addExpense(expense);
            } else {
                server.updateExpense(expense);
                // do nothing for now
            }
        } catch (Exception err) {

            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(err.getMessage());
            alert.showAndWait();
            return;
        }

        clearFields();
        mainCtrl.refreshData();
        mainCtrl.showOverviewEvent(event);
    }

    private void initReceiverCombobox() {
        Callback<ListView<Participant>, ListCell<Participant>> cb = lv -> {
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
        };

        receiver.setCellFactory(cb);
        receiver.setButtonCell(cb.call(null));
        receiver.setItems(participantsObs);

    }

    private void initSelectParticipants() {
        var getSelectedProperty = new Callback<Participant, ObservableValue<Boolean>>() {

            @Override
            public ObservableValue<Boolean> call(Participant participant) {
                final SimpleBooleanProperty prop = new SimpleBooleanProperty(
                        selectParticipantsObs.contains(participant));
                prop.subscribe((Boolean value) -> {
                    if (value) {
                        selectParticipantsObs.add(participant);
                    } else {
                        selectParticipantsObs.remove(participant);
                    }
                });
                selectParticipantsObs.subscribe(() -> {
                    boolean contains = selectParticipantsObs.contains(participant);
                    if (contains != prop.getValue())
                        prop.setValue(contains);
                });
                return prop;
            }

        };
        selectParticipant.setCellFactory(CheckBoxListCell.forListView(getSelectedProperty,
                new StringConverter<Participant>() {
                    @Override
                    public String toString(Participant participant) {
                        return participant.getName();
                    }

                    @Override
                    public Participant fromString(String string) {
                        throw new UnsupportedOperationException("Not needed");
                    }

                }));
        selectParticipant.setItems(participantsObs);
    }

    private void clearFields() {
        description.clear();
        amount.clear();
        date.setValue(null);
        selectParticipantsObs.clear();
        participantsObs.clear();
    }

    private LocalDate getDate() throws Exception {
        return date.getValue();
    }

    private Monetary getAmount() throws Exception {
        return Monetary.fromString(amount.getText(), Currency.getInstance("EUR"));
    }

    private String getName() throws Exception {
        String name = this.description.getText();
        if ("".equals(name.strip())) {
            throw new Exception("Name must not be empty");
        }
        return name;
    }

    private Participant validateReceiver() throws Exception {
        Participant selected = receiver.getSelectionModel().getSelectedItem();
        if (selected == null) {
            throw new Exception("Please select a receiver");
        }
        return selected;
    }

    // private Set<Participant> validateSplitBetween() throws Exception {
    // }

}
