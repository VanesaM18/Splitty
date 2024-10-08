package client.scenes;

import client.ConfigLoader;
import client.utils.AlertBuilder;
import client.utils.ResourceManager;
import client.utils.ServerUtils;

import client.utils.language.LanguageProcessor;
import com.google.inject.Inject;

import commons.Event;
import commons.ExpenseType;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.util.Callback;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;

public class StartScreenCtrl implements Initializable {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private final ConfigLoader config;
    private final LanguageProcessor languageProcessor;

    @FXML
    private TextField createEventField;
    @FXML
    private TextField joinEventField;
    @FXML
    private ListView<String> recentEvents;
    @FXML
    private HBox mainHBox;
    @FXML
    private TitledPane languageNavigator;
    @FXML
    private ImageView imageView;
    private String lastEvent;
    /**
     * Controller responsible for handling event creation and joining.
     *
     * @param server   An instance of ServerUtils for server-related operations.
     * @param mainCtrl An instance of MainCtrl for coordinating with the main controller.
     * @param config An instance of ConfigLoader used to update/read the config file
     * @param languageProcessor instance of LanguageProcessor.
     */
    @Inject
    public StartScreenCtrl(ServerUtils server, MainCtrl mainCtrl,
                           ConfigLoader config, LanguageProcessor languageProcessor) {
        this.server = server;
        this.mainCtrl = mainCtrl;
        this.config = config;
        this.languageProcessor = languageProcessor;
    }

    /**
     * Initializes the controller after its root element has been completely processed.
     * @param location
     * The location used to resolve relative paths for the root object, or
     * {@code null} if the location is not known.
     *
     * @param resources
     * The resources used to localize the root object, or {@code null} if
     * the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        List<String> inviteCodes = (List<String>) config.getProperty("recentEvents");
        recentEvents.getItems().addAll(inviteCodes);
        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(
                "/assets/splitty-splitty.png")));
        imageView.setImage(image);
        recentEvents.setCellFactory(new Callback<>() {
            @Override
            public ListCell<String> call(ListView<String> listView) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null || server.getEventById(item) == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            setGraphic(makeGraphics(item, listView));
                        }
                    }
                };
            }
        });
        languageNavigator.setExpanded(false);
    }

    private HBox makeGraphics(String item, ListView<?> listView) {
        Event e = server.getEventById(item);
        HBox hBox = new HBox(10);
        hBox.setAlignment(Pos.CENTER_LEFT);
        Text text = new Text(e.getName());
        Button deleteButton = new Button();
        deleteButton.setOnAction(event -> listView.getItems().remove(item));
        deleteButton.setAlignment(Pos.CENTER);
        Button joinButton = new Button();
        joinButton.setOnAction(event -> joinEventField.setText(item));
        HBox.setHgrow(joinButton, Priority.ALWAYS);
        Region region = new Region();
        HBox.setHgrow(region, Priority.ALWAYS);
        attachImage(joinButton, "/assets/up-right-arrow.png", 15, 15);
        attachImage(deleteButton, "/assets/circle-xmark-solid.png", 15, 15);
        joinButton.setStyle("-fx-background-color: transparent; " +
            "-fx-padding: 0; -fx-border: none;");
        deleteButton.setStyle("-fx-background-color: transparent; " +
            "-fx-padding: 0; -fx-border: none;");
        joinButton.setOnMouseEntered(event ->
            joinButton.setCursor(Cursor.HAND));
        joinButton.setOnMouseExited(event ->
            joinButton.setCursor(Cursor.DEFAULT));
        deleteButton.setOnMouseEntered(event ->
            deleteButton.setCursor(Cursor.HAND));
        deleteButton.setOnMouseExited(event ->
            deleteButton.setCursor(Cursor.DEFAULT));
        ResourceManager resourceManager = new ResourceManager(mainCtrl);
        deleteButton.setTooltip(new Tooltip(resourceManager
                .getStringForKey("tooltip_remove_event")));
        joinButton.setTooltip(new Tooltip(resourceManager
                .getStringForKey("tooltip_show_invite_code")));
        hBox.getChildren().addAll(text, joinButton, region, deleteButton);
        return hBox;
    }

    /**
     * Handles creating a new event based on the input from the createEventField.
     */
    public void createEvent() {
        String eventName = createEventField.getText();
        if(eventName.equals("")) {
            new AlertBuilder(mainCtrl)
                    .setAlertType(Alert.AlertType.ERROR)
                    .setModality(Modality.APPLICATION_MODAL)
                    .setContentKey("content_event_name")
                    .show();
            return;
        }

        Event event = new Event( "ABCDEF", eventName, LocalDateTime.now(),
                new HashSet<>(), new HashSet<>());
        event.generateInviteCode();
        event = server.addEvent(event);

        ExpenseType food = new ExpenseType("food", "#5bf562", event);
        server.addExpenseType(food);
        event.addType(food);

        ExpenseType fees = new ExpenseType("entrance fees", "#5ba0f5", event);
        server.addExpenseType(fees);
        event.addType(fees);

        ExpenseType travel = new ExpenseType("travel", "#f7596c", event);
        server.addExpenseType(travel);
        event.addType(travel);

        lastEvent = event.getInviteCode();
        server.sendUpdateStatus(lastEvent);
        updateConfig();
        clearFields();
        mainCtrl.showOverviewEvent(event);
    }

    /**
     * Handles joining an existing event based on the input from the joinEventField.
     */
    public void joinEvent() {
        String eventCode = joinEventField.getText();
        if(eventCode.equals("")) {
            new AlertBuilder(mainCtrl)
                    .setAlertType(Alert.AlertType.ERROR)
                    .setModality(Modality.APPLICATION_MODAL)
                    .setContentKey("content_event_code")
                    .show();
            return;
        }
        Event ev = server.getEventById(eventCode);
        if (ev == null) {
            new AlertBuilder(mainCtrl)
                    .setAlertType(Alert.AlertType.ERROR)
                    .setModality(Modality.APPLICATION_MODAL)
                    .setContentKey("content_event")
                    .show();
            clearFields();
            return;
        }
        server.sendUpdateStatus(eventCode);
        lastEvent = eventCode;
        updateConfig();
        clearFields();
        mainCtrl.showOverviewEvent(ev);
    }
    /**
     * Redirects the user to the admin view
     */
    public void goToAdmin() {
        updateConfig();

        // If already authenticated, skip the login screen
        if (ServerUtils.isAuthenticated()) {
            mainCtrl.showManagementOverview();
        } else {
            mainCtrl.showLogin();
        }
    }
    /**
     * Clears the fields
     */
    public void clearFields() {
        joinEventField.clear();
        createEventField.clear();
    }

    /**
     * opens the settings view
     */
    public void openSettings() {
        mainCtrl.showSettings();
    }

    /**
     * Updates the config after the client entered an event or created a new one
     */
    public void updateConfig() {
        ObservableList<String> items = recentEvents.getItems();
        List<String> lst = new ArrayList<>();
        int cnt = 0;
        if (lastEvent != null) {
            lst.add(lastEvent);
            cnt += 1;
        }
        for (String item: items) {
            if (item != null && !item.equals(lastEvent)) {
                lst.add(item);
                cnt += 1;
            }
            if (cnt == 5) {
                break;
            }
        }
        lastEvent = null;
        config.updateProperty("recentEvents", lst);
        config.saveConfig();
    }

    /**
     * Refreshes the event list
     */
    public void refresh() {
        List<String> inviteCodes = (List<String>) config.getProperty("recentEvents");
        // Make sure to filter out any events that might no longer exist!
        inviteCodes = inviteCodes.stream()
            .filter(code -> server.getEventById(code) != null)
            .toList();
        recentEvents.getItems().setAll(inviteCodes);
        languageProcessor.populateTitledPane(languageNavigator, mainCtrl
                .getCurrentLocale().orElse(Locale.of("en","EN")));
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
     * Event handler for pressing a key.
     *
     * @param e the key that is pressed
     */
    public void keyPressed(KeyEvent e) {
        switch (e.getCode()) {
            case ENTER:
                if (joinEventField.isFocused()) {
                    joinEvent();
                } else if (createEventField.isFocused()) {
                    createEvent();
                }
                break;
            default:
                break;
        }
    }
    private void alert(String content) {
        var alert = new Alert(Alert.AlertType.ERROR);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
