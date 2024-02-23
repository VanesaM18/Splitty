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
package client;

import static com.google.inject.Guice.createInjector;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.ResourceBundle;

import client.scenes.LoginCtrl;
import client.scenes.AddParticipantsCtrl;
import com.google.inject.Injector;

import client.scenes.AddQuoteCtrl;
import client.scenes.MainCtrl;
import client.scenes.QuoteOverviewCtrl;
import javafx.application.Application;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {

    private static final Injector INJECTOR = createInjector(new MyModule());
    private static final MyFXML FXML = new MyFXML(INJECTOR);
    private Stage stage;

    public static void main(String[] args) throws URISyntaxException, IOException {
        launch();
    }


    @Override
    public void start(Stage primaryStage) throws IOException {
        this.stage = primaryStage;
        var overview = FXML.load(QuoteOverviewCtrl.class, "client", "scenes", "QuoteOverview.fxml");
        var add = FXML.load(AddQuoteCtrl.class, "client", "scenes", "AddQuote.fxml");
        var loginAdmin = FXML.load(LoginCtrl.class, "client", "scenes", "LoginView.fxml");

        var participants = FXML.load(AddParticipantsCtrl.class, "client", "scenes", "AddParticipants.fxml");

        var mainCtrl = INJECTOR.getInstance(MainCtrl.class);
        mainCtrl.initialize(primaryStage, overview, add, participants, loginAdmin);
    }

    public void updateLocale(Locale locale) throws IOException {
        var overview = FXML.load(QuoteOverviewCtrl.class, locale, "client", "scenes", "QuoteOverview.fxml");
        var add = FXML.load(AddQuoteCtrl.class, locale, "client", "scenes", "AddQuote.fxml");
        var loginAdmin = FXML.load(LoginCtrl.class, locale, "client", "scenes", "LoginView.fxml");

        var participants = FXML.load(AddParticipantsCtrl.class, locale, "client", "scenes", "AddParticipants.fxml");

        var mainCtrl = INJECTOR.getInstance(MainCtrl.class);
        mainCtrl.initialize(this.stage, overview, add, participants, loginAdmin);
    }
}