package client;

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

    /**
     * Creates an instance of initialize data
     */
    public InitializationData() {
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
     * Set expense pair
     *
     * @param expense The expense pair
     */
    public void setExpense(Pair<ExpenseCtrl, Parent> expense) {
        this.expense = expense;
    }

    /**
     * sets the appConfiguration pair
     * @param appConfiguration pair
     */
    public void setAppConfiguration(Pair<AppConfigurationCtrl, Parent> appConfiguration) {
        this.appConfiguration = appConfiguration;
    }
    /**
     * Sets the settings pair
     *
     * @param settings pair
     */
    public void setSettings(Pair<SettingsCtrl, Parent> settings) {
        this.settings = settings;
    }

    /**
     * sets the management pair
     *
     * @param management pair
     */
    public void setManagement(Pair<ManagementCtrl, Parent> management) {
        this.management = management;
    }

    /**
     * Sets the participant pair
     *
     * @param participant pair
     */
    public void setParticipant(Pair<ParticipantsCtrl, Parent> participant) {
        this.participant = participant;
    }

    /**
     * Sets the login pair
     *
     * @param login pair
     */
    public void setLogin(Pair<LoginCtrl, Parent> login) {
        this.login = login;
    }

    /**
     * Sets the startPage pair
     *
     * @param startPage pair
     */
    public void setStartPage(Pair<StartScreenCtrl, Parent> startPage) {
        this.startPage = startPage;
    }

    /**
     * Sets the overviewEvent pair
     *
     * @param overviewEvent pair
     */
    public void setOverviewEvent(Pair<OverviewCtrl, Parent> overviewEvent) {
        this.overviewEvent = overviewEvent;
    }

    /**
     * Sets the invite pair
     *
     * @param invite pair
     */
    public void setInvite(Pair<InviteScreenCtrl, Parent> invite) {
        this.invite = invite;
    }

    /**
     * Sets the open debt pair
     *
     * @param openDebt pair
     */
    public void setOpenDebt(Pair<OpenDebtsCtrl, Parent> openDebt) {
        this.openDebt = openDebt;
    }

    /**
     * gets the appConfiguration pair
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
    public Pair<OpenDebtsCtrl, Parent> getOpenDebt() { return openDebt;}

    /**
     * Gets the expense type pair
     *
     * @return expense type pair
     */
    public Pair<ExpenseTypeCtrl, Parent> getExpenseType() { return expenseType; }

    /**
     * Sets the expense type pair
     *
     * @param expenseType pair
     */
    public void setExpenseType(Pair<ExpenseTypeCtrl, Parent> expenseType) {
        this.expenseType = expenseType;
    }
}
