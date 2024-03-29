package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Event;
import commons.Expense;
import commons.ExpenseType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Callback;

import java.net.URL;
import java.util.Optional;

public class ExpenseTypeCtrl {
    private Event event;
    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    @FXML
    private Button addTag;
    @FXML
    private ListView<ExpenseType> tags;
    private final ObservableList<ExpenseType> tagsObs = FXCollections.observableArrayList();

    /**
     * Controller responsible for handling the editing tags
     * functionality.
     *
     * @param server   An instance of ServerUtils for server-related operations.
     * @param mainCtrl An instance of MainCtrl for coordinating with the main
     *                 controller.
     */
    @Inject
    public ExpenseTypeCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    /**
     * Goes back to the expense overview.
     */
    public void back() {
        mainCtrl.showOverviewEvent(event);
    }

    /**
     * Sets the event where to view tags.
     *
     * @param ev the event
     */
    public void setEvent(Event ev) {
        this.event = ev;
    }

    /**
     * Refreshes the expense type screen.
     */
    public void refresh() {
        if (event == null) {
            return;
        }
        this.attachImage(addTag, "/assets/plus-sign.png", 20, 20);
        if (this.event != null) {
            this.event = server.getEventById(event.getInviteCode());
            tagsObs.clear();
            refreshTags();
            tags.setItems(tagsObs);
        }
    }

    private void refreshTags() {
        tagsObs.clear();
        for (ExpenseType t : this.event.getTags()) {
            tagsObs.add(t);
        }
    }

    /**
     * Initialize callback from FXML
     */
    @FXML
    public void initialize() {
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
                        Color c = Color.web(item.getColor());
                        double brightness = (c.getRed() + c.getGreen() + c.getBlue()) / 3.0;
                        String closestColor = (brightness <= 0.5) ? "#ffffff" : "#000000";
                        text.setFill(Color.web(closestColor));
                        Button deleteButton = new Button();
                        deleteButton.setOnAction(e -> {
                            deleteTag(listView, item);
                        });
                        deleteButton.setAlignment(Pos.CENTER);
                        HBox.setHgrow(deleteButton, Priority.ALWAYS);
                        Region region = new Region();
                        HBox.setHgrow(region, Priority.ALWAYS);
                        attachImage(deleteButton, "/assets/circle-xmark-solid.png", 15, 15);
                        buttonStyle(deleteButton);
                        Button editButton = new Button();
                        editButton.setOnAction(event -> {
                            updateTag(item);
                        });
                        editButton.setAlignment(Pos.CENTER);
                        HBox.setHgrow(editButton, Priority.ALWAYS);
                        attachImage(editButton, "/assets/pen-solid.png", 15, 15);
                        buttonStyle(editButton);
                        hBox.getChildren().addAll(text, editButton, region, deleteButton);
                        setGraphic(hBox);
                        setBackground(new Background(new BackgroundFill(Color.web(item.getColor()),
                                new CornerRadii(20), Insets.EMPTY)));
                    }
                };
            }
        };
        tags.setStyle("-fx-cell-size: 30px; -fx-spacing: 10px;");
        tags.setCellFactory(cb);
        tags.setItems(tagsObs);
    }

    /**
     * Styles a button.
     * @param button button to be styled.
     */
    public void buttonStyle(Button button) {
        button.setStyle("-fx-background-color: transparent; " +
                "-fx-padding: 0; -fx-border: none;");
        button.setOnMouseEntered(event ->
                button.setCursor(Cursor.HAND));
        button.setOnMouseExited(event ->
                button.setCursor(Cursor.DEFAULT));
    }
    private void deleteTag(ListView<ExpenseType> listView, ExpenseType item) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation required");
        alert.setHeaderText("Deleting an expense type");
        alert.setContentText(item.getName()
                + " will de deleted.");

        ButtonType confirm = new ButtonType("Confirm");
        ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(confirm, cancel);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == confirm){
            listView.getItems().remove(item);
            event.removeTag(item);
            server.updateEvent(event);
            for (Expense expense : event.getExpenses()) {
                server.updateExpense(expense);
            }
            item.setEvent(null);
            server.deleteTag(item);
            alert.close();
        } else {
            alert.close();
        }
    }

    /**
     * Attaches an image to a button
     * @param but the button to attach to
     * @param url the url to the image
     * @param height the height of the image
     * @param width the width of the image
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

    /**
     * Shows the add tags screen.
     */
    public void addTags() {
        mainCtrl.showAddTags(event);
    }

    /**
     * Shows the update tags screen.
     */
    private void updateTag(ExpenseType item) {
        mainCtrl.showUpdateTags(event, item);
    }
}
