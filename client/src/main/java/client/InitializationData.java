package client;

import client.scenes.*;
import javafx.scene.Parent;
import javafx.util.Pair;

public class InitializationData {
    private Pair<SettingsCtrl, Parent> settings;
    private Pair<QuoteOverviewCtrl, Parent> overview;
    private Pair<AddQuoteCtrl, Parent> add;
    private Pair<AddParticipantsCtrl, Parent> participant;
    private Pair<LoginCtrl, Parent> login;
    private Pair<StartScreenCtrl, Parent> startPage;

    /**
     * Creates an instance of initialize data
     */
    public InitializationData() {
    }

    /**
     * Sets the settings pair
     * @param settings pair
     */
    public void setSettings(Pair<SettingsCtrl, Parent> settings) {
        this.settings = settings;
    }
    /**
     * Sets the overview pair
     * @param overview pair
     */
    public void setOverview(Pair<QuoteOverviewCtrl, Parent> overview) {
        this.overview = overview;
    }
    /**
     * Sets the add pair
     * @param add pair
     */
    public void setAdd(Pair<AddQuoteCtrl, Parent> add) {
        this.add = add;
    }
    /**
     * Sets the participant pair
     * @param participant pair
     */
    public void setParticipant(Pair<AddParticipantsCtrl, Parent> participant) {
        this.participant = participant;
    }
    /**
     * Sets the login pair
     * @param login pair
     */
    public void setLogin(Pair<LoginCtrl, Parent> login) {
        this.login = login;
    }
    /**
     * Sets the startPage pair
     * @param startPage pair
     */
    public void setStartPage(Pair<StartScreenCtrl, Parent> startPage) {
        this.startPage = startPage;
    }

    /**
     * Gets the settings pair
     * @return settings pair
     */
    public Pair<SettingsCtrl, Parent> getSettings() {
        return settings;
    }
    /**
     * Gets the overview pair
     * @return overview pair
     */
    public Pair<QuoteOverviewCtrl, Parent> getOverview() {
        return overview;
    }
    /**
     * Gets the add pair
     * @return add pair
     */
    public Pair<AddQuoteCtrl, Parent> getAdd() {
        return add;
    }
    /**
     * Gets the participant pair
     * @return participant pair
     */
    public Pair<AddParticipantsCtrl, Parent> getParticipant() {
        return participant;
    }
    /**
     * Gets the login pair
     * @return login pair
     */
    public Pair<LoginCtrl, Parent> getLogin() {
        return login;
    }
    /**
     * Gets the startPage pair
     * @return startPage pair
     */
    public Pair<StartScreenCtrl, Parent> getStartPage() {
        return startPage;
    }
}
