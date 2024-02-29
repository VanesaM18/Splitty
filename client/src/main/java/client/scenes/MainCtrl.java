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

    private AddParticipantsCtrl participantsCtrl;
    private Scene participants;

    private LoginCtrl loginCtrl;
    private Scene login;

    /**
     * Initializes the app with the specified primary stage and scenes for various controllers.
     *
     * @param primaryStage The primary stage of the application.
     *
     * @param settings A Pair containing the SettingsCtrl and its corresponding Parent scene.
     *
     *
     * @param participant A Pair containing the AddParticipantsCtrl and its corresponding scene.
     *
     * @param login A Pair containing the LoginCtrl and its corresponding Parent scene.
     */
    public void initialize(
            Stage primaryStage,
            Pair<SettingsCtrl, Parent> settings,
            Pair<AddParticipantsCtrl, Parent> participant,
            Pair<LoginCtrl, Parent> login) {
        this.primaryStage = primaryStage;

        this.settingsCtrl = settings.getKey();
        this.settings = new Scene(settings.getValue());

        this.loginCtrl = login.getKey();
        this.login = new Scene(login.getValue());
        showLogin();

        this.participantsCtrl = participant.getKey();
        this.participants = new Scene(participant.getValue());

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
