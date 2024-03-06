/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package client.scenes;

import client.InitializationData;
import commons.Event;
import commons.Participant;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainCtrl {

    private Stage primaryStage;
    private SettingsCtrl settingsCtrl;
    private Scene settings;

    private ManagementCtrl managementCtrl;
    private Scene management;

    private QuoteOverviewCtrl overviewCtrl;
    private Scene overview;

    private AddQuoteCtrl addCtrl;
    private Scene add;
    private ParticipantsCtrl participantsCtrl;
    private Scene participants;

    private LoginCtrl loginCtrl;
    private Scene login;
    private StartScreenCtrl startPageCtrl;
    private Scene startPage;
    private OverviewCtrl overviewEventCtrl;
    private Scene overviewEvent;

    /**
     * Initializes the app with the specified primary stage and scenes for various controllers.
     *
     * @param primaryStage The primary stage of the application.
     * @param data Contains all initialized pair of views
     */
    public void initialize(
        Stage primaryStage, InitializationData data) {
        this.primaryStage = primaryStage;

        this.settingsCtrl = data.getSettings().getKey();
        this.settings = new Scene(data.getSettings().getValue());

        this.managementCtrl = data.getManagement().getKey();
        this.management = new Scene(data.getManagement().getValue());

        this.overviewCtrl = data.getOverview().getKey();
        this.overview = new Scene(data.getOverview().getValue());

        this.addCtrl = data.getAdd().getKey();
        this.add = new Scene(data.getAdd().getValue());

        this.loginCtrl = data.getLogin().getKey();
        this.login = new Scene(data.getLogin().getValue());

        this.participantsCtrl = data.getParticipant().getKey();
        this.participants = new Scene(data.getParticipant().getValue());

        this.startPageCtrl = data.getStartPage().getKey();
        this.startPage = new Scene(data.getStartPage().getValue());

        this.overviewEventCtrl = data.getOverviewEvent().getKey();
        this.overviewEvent = new Scene(data.getOverviewEvent().getValue());

        // showLogin();
        showStartScreen();
        primaryStage.show();
    }

    /**
     * Prepares and displays the settings
     */
    public void showSettings() {
        primaryStage.setTitle("Settings");
        primaryStage.setScene(settings);
        settingsCtrl.make();
        settingsCtrl.refresh();
    }

    /**
     * displays the management overview
     */
    public void showManagementOverview() {
        primaryStage.setTitle("Management Overview");
        primaryStage.setScene(management);
        managementCtrl.refresh();
    }

    /**
     * Displays the overview of quotes.
     * This method sets the title of the primary stage to "Quotes: Overview",
     * sets the scene to the overview scene
     * and refreshes the content of the overview controller.
     */
    public void showOverview() {
        primaryStage.setTitle("Quotes: Overview");
        primaryStage.setScene(overview);
        overviewCtrl.refresh();
    }

    /**
     * Displays the login view.
     * This method sets the title of the primary stage to "Login: Admin"
     * and sets the scene to the login scene.
     */
    public void showLogin() {
        primaryStage.setTitle("Login: Admin");
        primaryStage.setScene(login);
        loginCtrl.clearFields();
    }

    /**
     * Displays the startScreen view.
     * This method sets the title of the primary stage to "Start page"
     * and sets the scene to the login scene.
     */
    public void showStartScreen() {
        primaryStage.setTitle("Start page");
        primaryStage.setScene(startPage);
        startPageCtrl.clearFields();
    }

    /**
     * Displays the window for adding a new quote.
     * This method sets the title of the primary stage to "Quotes: Adding Quote",
     * sets the scene to the add scene
     * and sets a key pressed event handler for the add controller.
     */
    public void showAdd() {
        primaryStage.setTitle("Quotes: Adding Quote");
        primaryStage.setScene(add);
        add.setOnKeyPressed(e -> addCtrl.keyPressed(e));
    }

    /**
     * Displays the window for adding a new participant.
     * This method sets the title of the primary stage to "Participants: Overview",
     * sets the scene to the participants scene
     * and sets a key pressed event handler for the participant's controller.
     * @param ev event where to add/edit participants
     * @param add true - add / false - edit.
     * @param change the name of the participant to be edited.
     */
    public void showParticipants(Event ev, boolean add, String change) {
        participantsCtrl.setAdd(add);
        if(!add) editParticipant(ev, change);
        primaryStage.setTitle("Participants: Overview");
        primaryStage.setScene(participants);
        participantsCtrl.setEvent(ev);
        participants.setOnKeyPressed(e -> participantsCtrl.keyPressed(e));
    }

    private void editParticipant(Event ev, String name) {
        for(Participant participant : ev.getParticipants()) {
            if(participant.getName().equals(name)) {
                participantsCtrl.setFields(participant);
                return;
            }
        }
    }

    /**
     * Displays the overview of the event.
     * This method sets the title of the primary stage to "Event",
     * sets the scene to the overview scene
     * and refreshes the content of the overview controller if it's a new event, or
     * keeps the data if it is the old one
     * @param e the event to update
     */
    public void showOverviewEvent(Event e) {
        if (e == null) {
            primaryStage.setTitle("Event: Overview");
            primaryStage.setScene(overviewEvent);
        } else {
            primaryStage.setTitle("Event: Overview");
            overviewEventCtrl.setEvent(e);
            primaryStage.setScene(overviewEvent);
            overviewEventCtrl.refresh();
        }
    }
}
