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

import client.scenes.*;

import com.google.inject.Injector;

import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.Optional;

public class Main extends Application {

    private static final Injector INJECTOR = createInjector(new MyModule());
    private static final MyFXML FXML = new MyFXML(INJECTOR);
    private static final Locale DEFAULT_LOCALE = Locale.of("en","EN");
    private static Main instance;
    private Stage stage;

    /**
     * The main of our client
     * @param args to be passed to the client
     * @throws URISyntaxException if anything happens related to URI
     * @throws IOException if anything happens related to IO
     */
    public static void main(String[] args) throws URISyntaxException, IOException {
        launch();
    }

    /**
     * It starts the client
     * @param primaryStage the primary stage for this application, onto which
     * the application scene can be set.
     * Applications may create other stages, if needed, but they will not be
     * primary stages.
     * @throws IOException any IO related error
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        instance = this;
        this.stage = primaryStage;
        this.start(DEFAULT_LOCALE);
    }

    /**
     * Returns an instance of our main
     * @return the main instance
     */
    public static Optional<Main> getInstance() {
        return Optional.of(instance);
    }

    /**
     * Updates the language of our application
     * @param locale our language package
     * @throws IOException any IO error related
     */
    public void start(Locale locale) throws IOException {
        var settings = FXML.load(SettingsCtrl.class, locale, "client", "scenes", "Settings.fxml");
        var overview =
            FXML.load(
                QuoteOverviewCtrl.class, locale, "client", "scenes", "QuoteOverview.fxml");
        var add = FXML.load(AddQuoteCtrl.class, locale, "client", "scenes", "AddQuote.fxml");
        var loginAdmin = FXML.load(LoginCtrl.class, locale, "client", "scenes", "LoginView.fxml");
        var participants =
            FXML.load(
                AddParticipantsCtrl.class,
                locale,
                "client",
                "scenes",
                "AddParticipants.fxml");
        var startPage = FXML.load(StartScreenCtrl.class, locale, "client", "scenes", "StartScreen.fxml");

        var mainCtrl = INJECTOR.getInstance(MainCtrl.class);
        mainCtrl.initialize(this.stage, settings, overview, add, participants, loginAdmin, startPage);

    }
}
