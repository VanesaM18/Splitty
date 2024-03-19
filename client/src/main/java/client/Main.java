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
package client;

import static com.google.inject.Guice.createInjector;

import client.scenes.*;

import client.utils.SceneEnum;
import client.utils.SceneManager;
import com.google.inject.Injector;

import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.Optional;
import atlantafx.base.theme.CupertinoLight;

public class Main extends Application {

    private static final Injector INJECTOR = createInjector(new MyModule());
    private static final MyFXML FXML = new MyFXML(INJECTOR);
    public static final Locale DEFAULT_LOCALE = Locale.of("en", "EN");
    private static Main instance;
    private Stage stage;
    private final ConfigLoader configLoader;

    private final SceneManager sceneManager;

    {
        this.configLoader = INJECTOR.getInstance(ConfigLoader.class);
        this.sceneManager = INJECTOR.getInstance(SceneManager.class);
    }

    /**
     * The main of our client
     *
     * @param args to be passed to the client
     * @throws URISyntaxException if anything happens related to URI
     * @throws IOException if anything happens related to IO
     */
    public static void main(String[] args) throws URISyntaxException, IOException {
        launch();
    }

    /**
     * It starts the client
     * 
     * @param primaryStage the primary stage for this application, onto which the application scene
     *        can be set. Applications may create other stages, if needed, but they will not be
     *        primary stages.
     * @throws IOException any IO related error
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        instance = this;
        this.stage = primaryStage;
        Application.setUserAgentStylesheet(new CupertinoLight().getUserAgentStylesheet());
        this.start(configLoader.getLanguage(), configLoader.getStartScene());
    }

    /**
     * Returns an instance of our main
     *
     * @return the main instance
     */
    public static Optional<Main> getInstance() {
        return Optional.of(instance);
    }

    /**
     * gets the scene manager
     * @return SceneManager object
     */
    public SceneManager getSceneManager() {
        return sceneManager;
    }

    /**
     * updates the language of the application and starts the specified scene.
     *
     * @param locale    the locale representing the language package to be applied.
     * @param sceneEnum the enum value representing the scene to start.
     * @throws IOException any IO error related
     */
    public void start(Locale locale, SceneEnum sceneEnum) throws IOException {
        this.configLoader.updateProperty("language", locale);
        this.configLoader.saveConfig();
        var appConfiguration = FXML.load(AppConfigurationCtrl.class, locale,
                "client", "scenes", "AppConfiguration.fxml");
        var settings = FXML.load(SettingsCtrl.class, locale, "client", "scenes", "Settings.fxml");
        var management = FXML.load(ManagementCtrl.class, locale,
                "client", "scenes", "Management.fxml");
        var loginAdmin = FXML.load(LoginCtrl.class, locale, "client", "scenes", "LoginView.fxml");

        var participants =
                FXML.load(ParticipantsCtrl.class, locale, "client", "scenes", "Participants.fxml");

        var expense =
            FXML.load(ExpenseCtrl.class, locale, "client", "scenes", "Expense.fxml");
        var startPage =
                FXML.load(StartScreenCtrl.class, locale, "client", "scenes", "StartScreen.fxml");
        var overviewEvent =
                FXML.load(OverviewCtrl.class, locale, "client", "scenes", "Overview.fxml");
        var invite =
            FXML.load(InviteScreenCtrl.class, locale, "client", "scenes", "InviteScreen.fxml");
        var openDebt =
            FXML.load(OpenDebtsCtrl.class, locale, "client", "scenes", "OpenDebts.fxml");

        InitializationData data = new InitializationData();
        data.setAppConfiguration(appConfiguration);
        data.setSettings(settings);
        data.setManagement(management);
        data.setLogin(loginAdmin);
        data.setParticipant(participants);
        data.setExpense(expense);
        data.setStartPage(startPage);
        data.setOverviewEvent(overviewEvent);
        data.setInvite(invite);
        data.setOpenDebt(openDebt);

        var mainCtrl = INJECTOR.getInstance(MainCtrl.class);
        sceneManager.setMainCtrl(mainCtrl);
        sceneManager.pushScene(sceneEnum);
        mainCtrl.initialize(this.stage, data, sceneManager);

    }
}
