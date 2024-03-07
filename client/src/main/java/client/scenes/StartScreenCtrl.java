package client.scenes;

import client.ConfigLoader;
import client.utils.ServerUtils;

import com.google.inject.Inject;

import commons.Event;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.ListCell;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.util.Callback;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;

public class StartScreenCtrl implements Initializable {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private final ConfigLoader config;

    @FXML
    private TextField createEventField;
    @FXML
    private TextField joinEventField;
    @FXML
    private ListView<String> recentEvents;
    private String lastEvent;
    /**
     * Controller responsible for handling event creation and joining.
     *
     * @param server   An instance of ServerUtils for server-related operations.
     * @param mainCtrl An instance of MainCtrl for coordinating with the main controller.
     */
    @Inject
    public StartScreenCtrl(ServerUtils server, MainCtrl mainCtrl, ConfigLoader config) {
        this.server = server;
        this.mainCtrl = mainCtrl;
        this.config = config;
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
        recentEvents.setCellFactory(new Callback<>() {
            @Override
            public ListCell<String> call(ListView<String> listView) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            Event e = server.getEventById(item);
                            setText(e.getName());
                            Button deleteButton = new Button("X");
                            deleteButton.setOnAction(event -> listView.getItems().remove(item));
                            setGraphic(deleteButton);
                        }
                    }
                };
            }
        });
    }
    /**
     * Handles creating a new event based on the input from the createEventField.
     */
    public void createEvent() {
        String eventName = createEventField.getText();
        Event event = new Event( "ABCDEF", eventName, LocalDateTime.now(), new HashSet<>());
        event.generateInviteCode();
        event = server.addEvent(event);
        lastEvent = event.getInviteCode();
        updateConfig();
        clearFields();
        mainCtrl.showOverviewEvent(event);
    }

    /**
     * Handles joining an existing event based on the input from the joinEventField.
     */
    public void joinEvent() {
        String eventCode = joinEventField.getText();
        Event ev = server.getEventById(eventCode);
        if (ev == null) {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText("The event does not exist");
            alert.showAndWait();
            clearFields();
            return;
        }
        lastEvent = eventCode;
        updateConfig();
        clearFields();
        mainCtrl.showOverviewEvent(ev);
    }
    /**
     * Redirects the user to the admin view
     */
    public void goToAdmin() {
        mainCtrl.showLogin();
    }
    /**
     * Clears the fields
     */
    public void clearFields() {
        joinEventField.clear();
        createEventField.clear();
    }

    /**
     * Updates the config after the client entered an event or created a new one
     */
    private void updateConfig() {
        ObservableList<String> items = recentEvents.getItems();
        List<String> lst = new ArrayList<>();
        if (lastEvent != null) {
            lst.add(lastEvent);
        }
        for (String item: items) {
            if (item != null) {
                lst.add(item);
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
        updateConfig();
        recentEvents.getItems().setAll((List<String>) config.getProperty("recentEvents"));
    }
}
