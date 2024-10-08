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
import client.utils.ResourceManager;
import client.utils.SceneEnum;
import client.utils.SceneManager;
import commons.Event;
import commons.Expense;
import commons.ExpenseType;
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
    private boolean isInManagement = false;
    private Scene management;
    private ParticipantsCtrl participantsCtrl;
    private Scene participants;
    private Scene expense;
    private ExpenseCtrl expenseCtrl;
    private LoginCtrl loginCtrl;
    private Scene login;
    private StartScreenCtrl startPageCtrl;
    private Scene startPage;
    private OverviewCtrl overviewEventCtrl;
    private Scene overviewEvent;
    private OpenDebtsCtrl openDebtsCtrl;
    private Scene openDebt;
    private InviteScreenCtrl inviteScreenCtrl;
    private Scene invite;
    private ExpenseTypeCtrl expenseTypeCtrl;
    private Scene expenseType;
    private AddEditTagsCtrl addEditTagsCtrl;
    private Scene addEditTags;
    private StatisticsCtrl statisticsCtrl;
    private Scene statistics;
    private Optional<Locale> currentLocale = Optional.empty();
    private boolean isInOpenDebt = false;
    private ResourceManager resourceManager = new ResourceManager(this);

    /**
     * Initializes the app with the specified primary stage and scenes for various
     * controllers.
     *
     * @param primaryStage The primary stage of the application.
     * @param data         Contains all initialized pair of views
     * @param sceneManager The SceneManager responsible for managing scenes.
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

        this.expenseCtrl = data.getExpense().getKey();
        this.expense = new Scene(data.getExpense().getValue());

        this.startPageCtrl = data.getStartPage().getKey();
        this.startPage = new Scene(data.getStartPage().getValue());

        this.overviewEventCtrl = data.getOverviewEvent().getKey();
        this.overviewEvent = new Scene(data.getOverviewEvent().getValue());

        this.inviteScreenCtrl = data.getInvite().getKey();
        this.invite = new Scene(data.getInvite().getValue());

        this.openDebtsCtrl = data.getOpenDebt().getKey();
        this.openDebt = new Scene(data.getOpenDebt().getValue());

        this.expenseTypeCtrl = data.getExpenseType().getKey();
        this.expenseType = new Scene(data.getExpenseType().getValue());

        this.addEditTagsCtrl = data.getAddEditTags().getKey();
        this.addEditTags = new Scene(data.getAddEditTags().getValue());

        this.statisticsCtrl = data.getStatistics().getKey();
        this.statistics = new Scene(data.getStatistics().getValue());

        this.resourceManager = new ResourceManager(this);
        primaryStage.setOnCloseRequest(event -> startPageCtrl.updateConfig());
        settingsCtrl.make();
        sceneManager.showCurrentScene();
        primaryStage.show();
    }

    /**
     * displays the application configuration view.
     */
    public void showAppConfiguration() {
        this.sceneManager.pushScene(SceneEnum.STARTUP, null);
        primaryStage.setTitle(this.resourceManager.getStringForKey("content_app_setup"));
        primaryStage.setScene(appConfiguration);

        appConfigurationCtrl.refresh();
        appConfigurationCtrl.make();
    }

    /**
     * Prepares and displays the settings
     */
    public void showSettings() {
        this.sceneManager.pushScene(SceneEnum.SETTINGS, null);
        primaryStage.setTitle(this.resourceManager.getStringForKey("content_settings"));
        settings.setOnKeyPressed(key -> settingsCtrl.keyPressed(key));
        primaryStage.setScene(settings);
        settingsCtrl.refresh();
    }

    /**
     * displays the management overview
     */
    public void showManagementOverview() {
        management.setOnKeyPressed(key -> managementCtrl.keyPressed(key));
        isInManagement = true;
        this.sceneManager.pushScene(SceneEnum.MANAGEMENT, null);
        primaryStage.setTitle(this.resourceManager.getStringForKey("content_management"));
        primaryStage.setScene(management);
        managementCtrl.refresh();

    }

    /**
     * Displays the login view. This method sets the title of the primary stage to
     * "Login: Admin"
     * and sets the scene to the login scene.
     */
    public void showLogin() {
        login.setOnKeyPressed(key -> loginCtrl.keyPressed(key));
        this.sceneManager.pushScene(SceneEnum.LOGIN, null);
        primaryStage.setTitle(this.resourceManager.getStringForKey("content_login"));
        primaryStage.setScene(login);
        loginCtrl.clearFields();
    }

    /**
     * Displays the startScreen view. This method sets the title of the primary
     * stage to "Start
     * page" and sets the scene to the login scene.
     */
    public void showStartScreen() {
        this.sceneManager.pushScene(SceneEnum.START, null);
        primaryStage.setTitle(this.resourceManager.getStringForKey("content_start"));
        primaryStage.setScene(startPage);
        startPageCtrl.clearFields();
        startPage.setOnKeyPressed(e -> startPageCtrl.keyPressed(e));
        startPageCtrl.refresh();
    }

    /**
     * Displays the window for adding a new participant.
     * This method sets the title of the primary stage to "Participants: Overview",
     * sets the scene to the participants scene
     * and sets a key pressed event handler for the participant's controller.
     * 
     * @param ev     event where to add/edit participants
     * @param add    true - add / false - edit.
     * @param change the name of the participant to be edited.
     */
    public void showParticipants(Event ev, boolean add, Participant change) {
        participantsCtrl.setAdd(add);
        participantsCtrl.setParticipantToChange(null);
        if (!add)
            editParticipant(ev, change);
        primaryStage.setTitle((add
                ? this.resourceManager.getStringForKey("content_add")
                : this.resourceManager.getStringForKey("content_edit"))
                .concat(" " + this.resourceManager.getStringForKey("content_participant")));
        participantsCtrl.setEvent(ev);
        primaryStage.setScene(participants);
        participants.setOnKeyPressed(e -> participantsCtrl.keyPressed(e));
    }

    private void editParticipant(Event ev, Participant participant) {
        if (participant == null)
            return;
        participantsCtrl.setFields(participant);
    }

    /**
     * Displays the overview of the event. This method sets the title of the
     * primary stage to "Event", sets the scene to the overview scene and
     * refreshes the content of the overview controller if it's a new event, or
     * keeps the data if it is the old one
     *
     * @param e the event to update
     */
    public void showOverviewEvent(Event e) {
        this.sceneManager.pushScene(SceneEnum.OVERVIEW, e);
        if (e == null) {
            primaryStage.setTitle(this.resourceManager.getStringForKey("content_overview"));
            primaryStage.setScene(overviewEvent);
        } else {
            primaryStage.setTitle(this.resourceManager.getStringForKey("content_overview"));
            overviewEventCtrl.setEvent(e);
            overviewEventCtrl.refresh();
            overviewEvent.setOnKeyPressed(key -> overviewEventCtrl.keyPressed(key));
            primaryStage.setScene(overviewEvent);
        }
    }

    /**
     * Displays the invite code of the vent.
     *
     * @param ev the event to use
     */
    public void showInviteScreen(Event ev) {
        if (ev == null)
            throw new IllegalArgumentException(this.resourceManager
                    .getStringForKey("content_event_null"));
        this.sceneManager.pushScene(SceneEnum.INVITE, ev);
        primaryStage.setTitle(this.resourceManager.getStringForKey("content_invite_participants"));
        inviteScreenCtrl.setEvent(ev);
        primaryStage.setScene(invite);
        inviteScreenCtrl.refresh();
    }

    /**
     * Refreshes data on client side
     */
    public void refreshData() {
        if (isInOpenDebt) {
            openDebtsCtrl.initialize(openDebtsCtrl.getEvent());
        } else if (isInManagement) {
            managementCtrl.refresh();
        } else {
            overviewEventCtrl.refresh();
            inviteScreenCtrl.refresh();
            statisticsCtrl.refresh();
            expenseTypeCtrl.refresh();
        }
    }

    /**
     * sets the current locale of the application
     * 
     * @param locale current locale of the application
     */
    public void setCurrentLocale(Locale locale) {
        this.currentLocale = Optional.of(locale);
    }

    /**
     * gets the current locale of the application
     * 
     * @return current locale of the application
     */
    public Optional<Locale> getCurrentLocale() {
        return this.currentLocale;
    }

    /**
     * Displays the open debts event
     *
     * @param ev the current event
     */
    public void showOpenDebts(Event ev) {
        openDebt.setOnKeyPressed(e -> openDebtsCtrl.keyPressed(e));
        openDebtsCtrl.stopLongPolling();
        if (ev == null) {
            primaryStage.setTitle(this.resourceManager.getStringForKey("content_open_debts"));
            primaryStage.setScene(openDebt);
        } else {
            primaryStage.setTitle(this.resourceManager.getStringForKey("content_open_debt"));
            primaryStage.setScene(openDebt);
            openDebtsCtrl.initialize(ev);
        }
        openDebtsCtrl.startLongPolling();
        isInOpenDebt = true;
    }

    /**
     * gets the scene manager responsible for managing scenes in the application.
     * 
     * @return SceneManager object.
     */
    public SceneManager getSceneManager() {
        return this.sceneManager;
    }

    /**
     * gets the primary stage of the application.
     * 
     * @return the primary stage of the application.
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Show expense view.
     *
     * @param ev           The event to show the expense view for
     * @param edit         The expense to edit. Pass null to create a new one
     */
    public void showExpense(Event ev, Expense edit) {
        expense.setOnKeyPressed(e -> expenseCtrl.keyPressed(e));
        expenseCtrl.setEvent(ev);
        expenseCtrl.setUpdateExpense(edit);
        primaryStage.setTitle((edit == null
                ? this.resourceManager.getStringForKey("content_add")
                : this.resourceManager.getStringForKey("content_edit"))
                .concat(" " + this.resourceManager.getStringForKey("content_expense")));
        primaryStage.setScene(expense);
    }

    /**
     * Sets status of open debt view
     *
     * @param b the status
     */
    public void setIsInOpenDebt(boolean b) {
        isInOpenDebt = b;
    }

    /**
     * Shows the expense type screen.
     * 
     * @param ev event to be considered.
     */
    public void showExpenseTypes(Event ev) {
        primaryStage.setTitle(this.resourceManager.getStringForKey("content_expense_types"));
        expenseTypeCtrl.setEvent(ev);
        primaryStage.setScene(expenseType);
        expenseTypeCtrl.refresh();
        expenseType.setOnKeyPressed(e -> expenseTypeCtrl.keyPressed(e));
    }

    /**
     * Shows the add tag screen.
     * 
     * @param event event to which we want to add tags.
     */
    public void showAddTags(Event event) {
        addEditTags.setOnKeyPressed(e -> addEditTagsCtrl.keyPressed(e));
        addEditTagsCtrl.setEvent(event);
        primaryStage.setTitle(this.resourceManager.getStringForKey("content_add_expense_types"));
        primaryStage.setScene(addEditTags);
    }

    /**
     * Shows the update tag screen.
     * 
     * @param event event to which we want to add tags.
     * @param type  tag to be edited.
     */
    public void showUpdateTags(Event event, ExpenseType type) {
        addEditTags.setOnKeyPressed(e -> addEditTagsCtrl.keyPressed(e));
        addEditTagsCtrl.setEvent(event);
        addEditTagsCtrl.setExpenseType(type);
        primaryStage.setTitle(this.resourceManager.getStringForKey("content_update_expense_types"));
        primaryStage.setScene(addEditTags);
    }

    /**
     * Sets status of management view
     *
     * @param b the status
     */
    public void setIsInManagement(boolean b) {
        isInOpenDebt = b;
    }

    /**
     * Shows the statistics scene.
     * @param ev event to be considered.
     */
    public void showStatistics(Event ev) {
        statistics.setOnKeyPressed(e -> statisticsCtrl.keyPressed(e));
        statisticsCtrl.setEvent(ev);
        primaryStage.setTitle(this.resourceManager.getStringForKey("content_statistics"));
        primaryStage.setScene(statistics);
    }
}
