package client.scenes;

import client.utils.ServerUtils;
import client.utils.language.LanguageProcessor;
import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class SettingsCtrl {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    @FXML
    private VBox languages;

    @Inject
    public SettingsCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    public void refresh() {
    }

    public void make() {
        languages.getChildren().add(LanguageProcessor.getButtons());
    }
}
