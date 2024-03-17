package client.scenes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Currency;

import client.utils.ServerUtils;
import commons.Event;
import commons.Expense;
import commons.Monetary;
import commons.Participant;
import jakarta.ws.rs.WebApplicationException;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.util.Callback;

public class ExpenseCtrl {
    @FXML
    private Label title;

    @FXML
    private TextField description;

    @FXML
    private TextField amount;

    @FXML
    private TextField date;

    @FXML
    private Label warning;

    @FXML
    private ComboBox<Participant> receiver;

    private final ObservableList<Participant> participantsObs = FXCollections.observableArrayList();

    @FXML
    private ListView<Participant> selectParticipant;

    private final ObservableSet<Participant> selectParticipantsObs = FXCollections
            .observableSet();

    private final MainCtrl mainCtrl;
    private final ServerUtils server;
    private Event event;

    private Expense updateExpense;

    public ExpenseCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
        selectParticipant.setItems(participantsObs);
        initReceiverCombobox();
        initSelectParticipants();

    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
        clearFields();
        for (Participant p : event.getParticipants()) {
            participantsObs.add(p);
        }
    }

    public void setUpdateExpense(Expense e) {
        this.updateExpense = e;
    }

    /**
     * Abort adding/editing expense
     */
    public void abort() {
        clearFields();
        warning.setText("");
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
            expense.setAmount(validateAmount());
            expense.setDate(validateDate());
            expense.setName(validateName());
            expense.setReceiver(validateReceiver());
        } catch (Exception ex) {
            warning.setText(ex.getMessage());
            return;
        }

        warning.setText("");
        try {
        } catch (WebApplicationException err) {

            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(err.getMessage());
            alert.showAndWait();
            return;
        }

        clearFields();
        mainCtrl.showOverviewEvent(event);
    }

    private void initReceiverCombobox() {
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

        receiver.setCellFactory(cb);
        receiver.setButtonCell(cb.call(null));

    }

    private void initSelectParticipants() {
        var getSelectedProperty = new Callback<Participant, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(Participant participant) {
                return Bindings.createBooleanBinding(
                        () -> selectParticipantsObs.contains(participant), selectParticipantsObs);
            }
        };
        selectParticipant.setCellFactory(CheckBoxListCell.forListView(getSelectedProperty));
        selectParticipant.setItems(participantsObs);
    }

    private void clearFields() {
        description.clear();
        amount.clear();
        date.clear();
        selectParticipantsObs.clear();
        participantsObs.clear();
    }

    private LocalDate validateDate() throws Exception {
        try {
            return LocalDate.parse(date.getText());
        } catch (DateTimeParseException ex) {
            // TODO: Add language support
            throw new Exception("Invalid Date");
        }
    }

    private long pow(long input) {
        long out = 1;
        for (int i = 0; i < input; i++) {
            out *= 10;
        }
        return out;
    }

    private Monetary validateAmount() throws Exception {
        BigDecimal amount;
        try {
            amount = new BigDecimal(this.amount.getText());
        } catch (NumberFormatException ex) {
            // TODO: Add language support
            throw new Exception("Invalid Amount");
        }

        // TODO: support multiple currencies
        Currency currency = Currency.getInstance("EUR");
        amount.setScale(currency.getDefaultFractionDigits(), RoundingMode.HALF_UP);
        amount.multiply(new BigDecimal(pow(currency.getDefaultFractionDigits())));
        return new Monetary(amount.longValueExact(), currency);

    }

    private String validateName() throws Exception {
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
