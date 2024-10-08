package client;

import com.google.inject.Inject;

import client.scenes.*;
import javafx.scene.Parent;
import javafx.util.Pair;

public class InitializationData {
    private Pair<AppConfigurationCtrl, Parent> appConfiguration;
    private Pair<SettingsCtrl, Parent> settings;
    private Pair<ManagementCtrl, Parent> management;

    private Pair<ParticipantsCtrl, Parent> participant;
    private Pair<ExpenseCtrl, Parent> expense;

    private Pair<LoginCtrl, Parent> login;

    private Pair<StartScreenCtrl, Parent> startPage;

    private Pair<OverviewCtrl, Parent> overviewEvent;
    private Pair<InviteScreenCtrl, Parent> invite;

    private Pair<OpenDebtsCtrl, Parent> openDebt;

    private Pair<ExpenseTypeCtrl, Parent> expenseType;
    private Pair<AddEditTagsCtrl, Parent> addEditTags;
    private Pair<StatisticsCtrl, Parent> statistics;

    /**
     * Creates an instance of initialize data
     *
     * @param fxml         FXML Loader, injected.
     * @param configLoader The config loader, injected.
     */
    @Inject
    public InitializationData(MyFXML fxml, ConfigLoader configLoader) {
        var locale = configLoader.getLanguage();

        settings = fxml.load(SettingsCtrl.class, locale, "client", "scenes", "Settings.fxml");
        login = fxml.load(LoginCtrl.class, locale, "client", "scenes", "LoginView.fxml");
        expense = fxml.load(ExpenseCtrl.class, locale, "client", "scenes", "Expense.fxml");
        overviewEvent = fxml.load(OverviewCtrl.class, locale, "client", "scenes", "Overview.fxml");
        invite = fxml.load(InviteScreenCtrl.class, locale, "client", "scenes", "InviteScreen.fxml");
        openDebt = fxml.load(OpenDebtsCtrl.class, locale, "client", "scenes", "OpenDebts.fxml");
        management = fxml.load(ManagementCtrl.class, locale,
                "client", "scenes", "Management.fxml");
        startPage = fxml.load(
                StartScreenCtrl.class, locale, "client", "scenes", "StartScreen.fxml");
        expenseType = fxml.load(
                ExpenseTypeCtrl.class, locale, "client", "scenes", "ExpenseTypes.fxml");
        addEditTags = fxml.load(
                AddEditTagsCtrl.class, locale, "client", "scenes", "AddEditTags.fxml");
        participant = fxml.load(
                ParticipantsCtrl.class, locale, "client", "scenes", "Participants.fxml");
        appConfiguration = fxml.load(AppConfigurationCtrl.class, locale,
                "client", "scenes", "AppConfiguration.fxml");
        statistics = fxml.load(
                StatisticsCtrl.class, locale, "client", "scenes", "Statistics.fxml");
    }

    /**
     * Get expense pair
     *
     * @return the expense pair
     */
    public Pair<ExpenseCtrl, Parent> getExpense() {
        return expense;
    }

    /**
     * gets the appConfiguration pair
     * 
     * @return appConfiguration pair
     */
    public Pair<AppConfigurationCtrl, Parent> getAppConfiguration() {
        return appConfiguration;
    }

    /**
     * Gets the settings pair
     *
     * @return settings pair
     */
    public Pair<SettingsCtrl, Parent> getSettings() {
        return settings;
    }

    /**
     * gets the management pair
     *
     * @return management pair
     */
    public Pair<ManagementCtrl, Parent> getManagement() {
        return management;
    }

    /**
     * Gets the participant pair
     *
     * @return participant pair
     */
    public Pair<ParticipantsCtrl, Parent> getParticipant() {
        return participant;
    }

    /**
     * Gets the login pair
     *
     * @return login pair
     */
    public Pair<LoginCtrl, Parent> getLogin() {
        return login;
    }

    /**
     * Gets the startPage pair
     *
     * @return startPage pair
     */
    public Pair<StartScreenCtrl, Parent> getStartPage() {
        return startPage;
    }

    /**
     * Gets the overviewEvent pair
     *
     * @return overviewEvent pair
     */
    public Pair<OverviewCtrl, Parent> getOverviewEvent() {
        return overviewEvent;
    }

    /**
     * Gets the invite pair
     *
     * @return invite pair
     */
    public Pair<InviteScreenCtrl, Parent> getInvite() {
        return invite;
    }

    /**
     * Gets the open debt pair
     *
     * @return open debt pair
     */
    public Pair<OpenDebtsCtrl, Parent> getOpenDebt() {
        return openDebt;
    }

    /**
     * Gets the expense type pair
     *
     * @return expense type pair
     */
    public Pair<ExpenseTypeCtrl, Parent> getExpenseType() {
        return expenseType;
    }

    /**
     * Gets the add / edit expense type pair
     *
     * @return expense type pair
     */
    public Pair<AddEditTagsCtrl, Parent> getAddEditTags() {
        return addEditTags;
    }

    /**
     * Gets the statistics pair
     *
     * @return expense type pair
     */
    public Pair<StatisticsCtrl, Parent> getStatistics() {
        return statistics;
    }

    /**
     * Sets the statistics pair
     *
     * @param statistics pair
     */
    public void setStatistics(Pair<StatisticsCtrl, Parent> statistics) {
        this.statistics = statistics;
    }
}
