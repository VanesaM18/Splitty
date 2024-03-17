package client;

import client.scenes.*;
import javafx.scene.Parent;
import javafx.util.Pair;

public class InitializationData {
    private Pair<SettingsCtrl, Parent> settings;
    private Pair<ManagementCtrl, Parent> management;

    private Pair<ParticipantsCtrl, Parent> participant;
    private Pair<ExpenseCtrl, Parent> expense;

    private Pair<LoginCtrl, Parent> login;

    private Pair<StartScreenCtrl, Parent> startPage;

    private Pair<OverviewCtrl, Parent> overviewEvent;
    private Pair<InviteScreenCtrl, Parent> invite;

    /**
     * Creates an instance of initialize data
     */
    public InitializationData() {
    }

    public Pair<ExpenseCtrl, Parent> getExpense() {
        return expense;
    }

    public void setExpense(Pair<ExpenseCtrl, Parent> expense) {
        this.expense = expense;
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
}
