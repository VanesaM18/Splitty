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

    public void initialize(Stage primaryStage, Pair<SettingsCtrl, Parent> settings,
                           Pair<QuoteOverviewCtrl, Parent> overview, Pair<AddQuoteCtrl, Parent> add,
                           Pair<AddParticipantsCtrl, Parent> participant, Pair<LoginCtrl, Parent> login) {
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

        // showOverview();
        primaryStage.show();
    }

    public void showSettings() {
        primaryStage.setTitle("Settings");
        primaryStage.setScene(settings);
        settingsCtrl.make();
        settingsCtrl.refresh();
    }

    public void showOverview() {
        primaryStage.setTitle("Quotes: Overview");
        primaryStage.setScene(overview);
        overviewCtrl.refresh();
    }

    public void showLogin() {
        primaryStage.setTitle("Login: Admin");
        primaryStage.setScene(login);
        loginCtrl.clearFields();
    }

    public void showAdd() {
        primaryStage.setTitle("Quotes: Adding Quote");
        primaryStage.setScene(add);
        add.setOnKeyPressed(e -> addCtrl.keyPressed(e));
    }

    public void showParticipants() {
        primaryStage.setTitle("Add/Edit Participants");
        primaryStage.setScene(participants);
        participants.setOnKeyPressed(e -> participantsCtrl.keyPressed(e));
    }
}