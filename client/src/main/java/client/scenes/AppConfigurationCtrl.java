package client.scenes;

import client.ConfigLoader;
import client.Main;
import client.utils.SceneEnum;
import client.utils.ServerUtils;
import client.utils.language.LanguageProcessor;
import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;

import java.util.Map;

public class AppConfigurationCtrl {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private final LanguageProcessor languageProcessor;
    private final ConfigLoader configLoader;
    @FXML
    private ChoiceBox<String> choiceBox;

    /**
     * controller for handling the application configuration functionality.
     * @param server instance of ServerUtils for server-related operations.
     * @param mainCtrl instance of MainCtrl for coordinating with the main controller.
     * @param languageProcessor instance of LanguageProcessor.
     */
    @Inject
    public AppConfigurationCtrl(ServerUtils server, MainCtrl mainCtrl,
                                LanguageProcessor languageProcessor, ConfigLoader configLoader) {
        this.server = server;
        this.mainCtrl = mainCtrl;
        this.languageProcessor = languageProcessor;
        this.configLoader = configLoader;
    }

    public void make() {
        Map<String, Runnable> actions = languageProcessor.getActions();
        choiceBox.getItems().addAll(actions.keySet());
        choiceBox.setValue("English");
    }

    public void onSave() {
        String selectedLanguage = choiceBox.getValue();
        configLoader.updateProperty("startUpShown", "true");
        var sceneManager = mainCtrl.getSceneManager();
        sceneManager.popScene();
        sceneManager.pushScene(SceneEnum.START);
        languageProcessor.getActions().get(selectedLanguage).run();
    }
    public void refresh() {
    }
}
