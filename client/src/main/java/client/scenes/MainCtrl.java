/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package client.scenes;

import client.InitializationData;
import client.utils.SceneEnum;
import client.utils.SceneManager;
import commons.Event;
import commons.Participant;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Locale;
import java.util.Optional;

public class MainCtrl {

    private Stage primaryStage;
    private SceneManager sceneManager;
    private AppConfigurationCtrl appConfigurationCtrl;
    private Scene appConfiguration;
    private SettingsCtrl settingsCtrl;
    private Scene settings;
    private ManagementCtrl managementCtrl;
    private Scene management;
    private Scene overview;
    private Scene add;
    private ParticipantsCtrl participantsCtrl;
    private Scene participants;
    private LoginCtrl loginCtrl;
    private Scene login;
    private StartScreenCtrl startPageCtrl;
    private Scene startPage;
    private OverviewCtrl overviewEventCtrl;
    private Scene overviewEvent;
    private InviteScreenCtrl inviteScreenCtrl;
    private Scene invite;
    private Optional<Locale> currentLocale = Optional.empty();


    /**
     * Initializes the app with the specified primary stage and scenes for various controllers.
     *
     * @param primaryStage The primary stage of the application.
     * @param data Contains all initialized pair of views
     */
    public void initialize(Stage primaryStage, InitializationData data, SceneManager sceneManager) {
        this.primaryStage = primaryStage;
        this.sceneManager = sceneManager;

        this.appConfigurationCtrl = data.getAppConfiguration().getKey();
        this.appConfiguration = new Scene(data.getAppConfiguration().getValue());

        this.settingsCtrl = data.getSettings().getKey();
        this.settings = new Scene(data.getSettings().getValue());

        this.managementCtrl = data.getManagement().getKey();
        this.management = new Scene(data.getManagement().getValue());

        this.loginCtrl = data.getLogin().getKey();
        this.login = new Scene(data.getLogin().getValue());

        this.participantsCtrl = data.getParticipant().getKey();
        this.participants = new Scene(data.getParticipant().getValue());

        this.startPageCtrl = data.getStartPage().getKey();
        this.startPage = new Scene(data.getStartPage().getValue());

        this.overviewEventCtrl = data.getOverviewEvent().getKey();
        this.overviewEvent = new Scene(data.getOverviewEvent().getValue());

        this.inviteScreenCtrl = data.getInvite().getKey();
        this.invite = new Scene(data.getInvite().getValue());

        primaryStage.setOnCloseRequest(event -> {
            startPageCtrl.updateConfig();
        });

        // showLogin();
        settingsCtrl.make();
        sceneManager.showCurrentScene();
        primaryStage.show();
    }

    public void showAppConfiguration() {
        this.sceneManager.pushScene(SceneEnum.STARTUP, null);
        primaryStage.setTitle("Application Setup");
        primaryStage.setScene(appConfiguration);
        appConfigurationCtrl.make();
        appConfigurationCtrl.refresh();
    }
    /**
     * Prepares and displays the settings
     */
    public void showSettings() {
        this.sceneManager.pushScene(SceneEnum.SETTINGS, null);
        primaryStage.setTitle("Settings");
        primaryStage.setScene(settings);
        settingsCtrl.refresh();
    }

    /**
     * displays the management overview
     */
    public void showManagementOverview() {
        this.sceneManager.pushScene(SceneEnum.MANAGEMENT, null);
        primaryStage.setTitle("Management Overview");
        primaryStage.setScene(management);
        managementCtrl.refresh();
    }

    /**
     * Displays the login view. This method sets the title of the primary stage to "Login: Admin"
     * and sets the scene to the login scene.
     */
    public void showLogin() {
        this.sceneManager.pushScene(SceneEnum.LOGIN, null);
        primaryStage.setTitle("Login: Admin");
        primaryStage.setScene(login);
        loginCtrl.clearFields();
    }

    /**
     * Displays the startScreen view. This method sets the title of the primary stage to "Start
     * page" and sets the scene to the login scene.
     */
    public void showStartScreen() {
        this.sceneManager.pushScene(SceneEnum.START, null);
        primaryStage.setTitle("Start page");
        primaryStage.setScene(startPage);
        startPageCtrl.clearFields();
        startPageCtrl.refresh();
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
        participantsCtrl.setParticipantToChange(null);
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
     * Displays the overview of the event. This method sets the title of the primary stage to
     * "Event", sets the scene to the overview scene and refreshes the content of the overview
     * controller if it's a new event, or keeps the data if it is the old one
     * 
     * @param e the event to update
     */
    public void showOverviewEvent(Event e) {
        this.sceneManager.pushScene(SceneEnum.OVERVIEW, e);
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


    /**
     * Displays the invite code of the vent.
     *
     * @param ev the event to use
     */
    public void showInviteScreen(Event ev) {
        if (ev == null)
            throw new IllegalArgumentException("Event may not be null");
        this.sceneManager.pushScene(SceneEnum.INVITE, ev);
        primaryStage.setTitle("Invite participants");
        inviteScreenCtrl.setEvent(ev);
        primaryStage.setScene(invite);
        inviteScreenCtrl.refresh();
    }

    /**
     * Refreshes data on client side
     */
    public void refreshData() {
        overviewEventCtrl.refresh();
    }

    /**
     * sets the current locale of the application
     * @param locale current locale of the application
     */
    public void setCurrentLocale(Locale locale) {
        this.currentLocale = Optional.of(locale);
    }

    /**
     * gets the current locale of the application
     * @return current locale of the application
     */
    public Optional<Locale> getCurrentLocale() {
        return this.currentLocale;
    }

    public SceneManager getSceneManager() {
        return this.sceneManager;
    }
}
