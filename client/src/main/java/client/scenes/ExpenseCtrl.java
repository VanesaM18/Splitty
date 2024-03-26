package client.scenes;

import java.net.URL;
import java.time.LocalDate;
import java.util.Currency;
import java.util.HashSet;
import java.util.Set;

import com.google.inject.Inject;

import client.utils.ServerUtils;
import commons.Event;
import commons.Expense;
import commons.Monetary;
import commons.Participant;
import commons.ExpenseType;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
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

    @FXML
    private Button deleteExpenseButton;

    private final ObservableList<Participant> participantsObs = FXCollections.observableArrayList();

    @FXML
    private ComboBox<ExpenseType> types;
    private final ObservableList<ExpenseType> typesObs = FXCollections.observableArrayList();

    @FXML
    private ListView<Participant> selectParticipant;
    @FXML
    private ListView<ExpenseType> selectedTags;
    private final ObservableList<ExpenseType> selectedTypesObs = FXCollections.observableArrayList();

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
        initTagsCombobox();
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
        typesObs.addAll(event.getTags());
    }

    /**
     * Set updateExpense
     *
     * @param e The expense to set
     */
    public void setUpdateExpense(Expense e) {
        this.updateExpense = e;
        if (e == null) {
            deleteExpenseButton.setVisible(false);
            return;
        }
        deleteExpenseButton.setVisible(true);
        this.date.setValue(e.getDate());
        this.description.setText(e.getName());
        this.amount.setText(e.getAmount().toString());
        this.participantsObs.clear();
        this.typesObs.clear();
        this.selectParticipantsObs.clear();
        this.selectParticipantsObs.addAll(e.getSplitBetween());
        this.participantsObs.addAll(this.event.getParticipants());
        this.typesObs.addAll(this.event.getTags());
        this.receiver.getSelectionModel().select(e.getCreator());
        this.selectedTypesObs.addAll(e.getTags());
        initTypes();
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

    private HashSet<Participant> validateSplitBetween() throws Exception {
        var newSplit = new HashSet<>(selectParticipantsObs);
        if (newSplit.size() < 1) {
            throw new Exception("At least one participant must be selected");
        }
        return newSplit;
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
            expense.setSplitBetween(validateSplitBetween());
            Set<ExpenseType> tags = new HashSet<>(selectedTypesObs);
            expense.setTags(tags);
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

    /**
     * Delete expense
     */
    public void deleteExpense() {
        try {
            server.deleteExpense(updateExpense);
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

    private void initTagsCombobox() {
        Callback<ListView<ExpenseType>, ListCell<ExpenseType>> cb = lv -> {
            return new ListCell<ExpenseType>() {
                @Override
                protected void updateItem(ExpenseType item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setGraphic(null);
                        return;
                    }
                    setGraphic(new Text(item.getName()));
                }
            };
        };

        types.setCellFactory(cb);
        types.setButtonCell(cb.call(null));
        types.setItems(typesObs);
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
        selectedTypesObs.clear();
        selectedTags.setItems(null);
        description.clear();
        amount.clear();
        date.setValue(null);
        selectParticipantsObs.clear();
        participantsObs.clear();
        typesObs.clear();
    }

    private LocalDate getDate() throws Exception {
        var newDate = date.getValue();
        if (newDate == null) {
            throw new Exception("Date must be specified");
        }
        return newDate;
    }

    private Monetary getAmount() throws Exception {
        String textValue = amount.getText().strip();
        if ("".equals(textValue)) {
            throw new Exception("Amount must be specified");
        }
        return Monetary.fromString(amount.getText(), Currency.getInstance("EUR"));
    }

    private String getName() throws Exception {
        String name = this.description.getText().strip();
        if ("".equals(name)) {
            throw new Exception("Name must be specified");
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

    /**
     * Adds a tag to the listView.
     */
    public void addTag() {
        ExpenseType tag = types.getSelectionModel().getSelectedItem();
        if (!selectedTypesObs.contains(tag))
            selectedTypesObs.add(tag);
        initTypes();
    }

    private void initTypes() {
        var cb = new Callback<ListView<ExpenseType>, ListCell<ExpenseType>>() {
            @Override
            public ListCell<ExpenseType> call(ListView<ExpenseType> listView) {
                return new ListCell<ExpenseType>() {
                    @Override
                    protected void updateItem(ExpenseType item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setGraphic(null);
                            setBackground(null);
                            return;
                        }

                        HBox hBox = new HBox(5);
                        hBox.setAlignment(Pos.CENTER_LEFT);
                        Text text = new Text(item.getName());
                        Button deleteButton = new Button();
                        deleteButton.setOnAction(event -> {
                            listView.getItems().remove(item);
                            selectedTypesObs.remove(item);
                        });
                        deleteButton.setAlignment(Pos.CENTER);

                        HBox.setHgrow(deleteButton, Priority.ALWAYS);
                        Region region = new Region();
                        HBox.setHgrow(region, Priority.ALWAYS);
                        attachImage(deleteButton, "/assets/circle-xmark-solid.png", 15, 15);
                        deleteButton.setStyle("-fx-background-color: transparent; " +
                                "-fx-padding: 0; -fx-border: none;");
                        deleteButton.setOnMouseEntered(event -> deleteButton.setCursor(Cursor.HAND));
                        deleteButton.setOnMouseExited(event -> deleteButton.setCursor(Cursor.DEFAULT));
                        hBox.getChildren().addAll(text, region, deleteButton);
                        setGraphic(hBox);
                        setBackground(new Background(new BackgroundFill(Color.web(item.getColor()),
                                new CornerRadii(20), Insets.EMPTY)));
                    }
                };
            }
        };
        selectedTags.setStyle("-fx-cell-size: 30px; -fx-spacing: 10px;");
        selectedTags.setCellFactory(cb);
        selectedTags.setItems(selectedTypesObs);
    }

    /**
     * Attaches an image to a button
     * 
     * @param but    the button to attach to
     * @param url    the url to the image
     * @param height the height of the image
     * @param width  the width of the image
     */
    public void attachImage(Button but, String url, float height, float width) {
        URL imageUrl = getClass().getResource(url);
        if (imageUrl != null) {
            Image image = new Image(imageUrl.toExternalForm());
            ImageView imageView = new ImageView(image);
            imageView.setFitHeight(height);
            imageView.setFitWidth(width);
            imageView.setPickOnBounds(true);
            imageView.setPreserveRatio(true);
            but.setGraphic(imageView);
        } else {
            System.out.println("Image URL is null. Check the path to the image file.");
        }
    }
}
