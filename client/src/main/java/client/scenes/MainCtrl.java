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

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Pair;

public class MainCtrl {

    private Stage primaryStage;
    private SettingsCtrl settingsCtrl;
    private Scene settings;

    private QuoteOverviewCtrl overviewCtrl;
    private Scene overview;

    private AddQuoteCtrl addCtrl;
    private Scene add;
    private AddParticipantsCtrl participantsCtrl;
    private Scene participants;

    private LoginCtrl loginCtrl;
    private Scene login;
    private StartScreenCtrl startPageCtrl;
    private Scene startPage;

    /**
     * Initializes the app with the specified primary stage and scenes for various controllers.
     *
     * @param primaryStage The primary stage of the application.
     *
     * @param settings A Pair containing the SettingsCtrl and its corresponding Parent scene.
     *
     * @param overview A Pair containing the QuoteOverviewCtrl and its corresponding Parent scene.
     *
     * @param add A Pair containing the AddQuoteCtrl and its corresponding Parent scene.
     *
     * @param participant A Pair containing the AddParticipantsCtrl and its corresponding scene.
     *
     * @param login A Pair containing the LoginCtrl and its corresponding Parent scene.
     *
     * @param startPage A Pair containing the StartPageCtrl and its corresponding Parent scene.
     */
    public void initialize(
        Stage primaryStage,
        Pair<SettingsCtrl, Parent> settings,
        Pair<QuoteOverviewCtrl, Parent> overview,
        Pair<AddQuoteCtrl, Parent> add,
        Pair<AddParticipantsCtrl, Parent> participant,
        Pair<LoginCtrl, Parent> login,
        Pair<StartScreenCtrl, Parent> startPage
        ) {
        this.primaryStage = primaryStage;

        this.settingsCtrl = settings.getKey();
        this.settings = new Scene(settings.getValue());

        this.overviewCtrl = overview.getKey();
        this.overview = new Scene(overview.getValue());

        this.addCtrl = add.getKey();
        this.add = new Scene(add.getValue());

        this.loginCtrl = login.getKey();
        this.login = new Scene(login.getValue());
        showLogin();

        this.participantsCtrl = participant.getKey();
        this.participants = new Scene(participant.getValue());

        this.startPageCtrl = startPage.getKey();
        this.startPage = new Scene(startPage.getValue());

        // showOverview();
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
     * This method sets the title of the primary stage to "Add/Edit Participants",
     * sets the scene to the participants scene
     * and sets a key pressed event handler for the participant's controller.
     */
    public void showParticipants() {
        primaryStage.setTitle("Add/Edit Participants");
        primaryStage.setScene(participants);
        participants.setOnKeyPressed(e -> participantsCtrl.keyPressed(e));
    }
}
