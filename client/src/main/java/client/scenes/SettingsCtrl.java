package client.scenes;

import client.utils.ServerUtils;
import client.utils.language.LanguageProcessor;
import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

public class SettingsCtrl {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    @FXML
    private VBox languages;

    /**
     * Controller for handling the settings overview functionality.
     * @param server instance of ServerUtils for server-related operations.
     * @param mainCtrl instance of MainCtrl for coordinating with the main controller.
     */
    @Inject
    public SettingsCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    /**
     * method for refreshing the settings page
     */
    public void refresh() {
    }

    /**
     * method for preprocessing settings page (load language buttons)
     */
    public void make() {
        languages.getChildren().add(LanguageProcessor.getButtons());
    }
}
