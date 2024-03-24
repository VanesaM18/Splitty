package client.scenes;

import client.ConfigLoader;
import client.utils.SceneEnum;
import client.utils.ServerUtils;
import client.utils.language.LanguageProcessor;
import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Optional;

public class AppConfigurationCtrl {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private final LanguageProcessor languageProcessor;
    private final ConfigLoader configLoader;
    @FXML
    private ChoiceBox<String> choiceBox;
    @FXML
    private TextField urlTextField;

    /**
     * controller for handling the application configuration functionality.
     * @param server instance of ServerUtils for server-related operations.
     * @param mainCtrl instance of MainCtrl for coordinating with the main controller.
     * @param languageProcessor instance of LanguageProcessor.
     * @param configLoader instance of ConfigLoader.
     */
    @Inject
    public AppConfigurationCtrl(ServerUtils server, MainCtrl mainCtrl,
                                LanguageProcessor languageProcessor, ConfigLoader configLoader) {
        this.server = server;
        this.mainCtrl = mainCtrl;
        this.languageProcessor = languageProcessor;
        this.configLoader = configLoader;
    }

    /**
     * populates the choice box with available language options and
     * sets the default language to English
     */
    public void make() {
        Map<String, Runnable> actions = languageProcessor.getActions();
        choiceBox.getItems().addAll(actions.keySet());
        choiceBox.setValue("English");
    }

    /**
     * saves the selected language, updates configuration properties,
     * and navigates back to the previous scene
     * while executing language-specific actions
     */
    public void onSave() {
        String url = urlTextField.getText();

        try {
            new URL(url); // Attempt to create a URL object
        } catch (MalformedURLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid URL");
            alert.setHeaderText("Invalid URL");
            alert.setContentText("Please enter a valid URL.");
            alert.showAndWait();
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Are you sure you want to proceed?");
        alert.setContentText("Do you want to save these settings and proceed?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            String selectedLanguage = choiceBox.getValue();
            server.setServerUrl(url);
            configLoader.updateProperty("address", url);
            var sceneManager = mainCtrl.getSceneManager();
            sceneManager.popScene();
            sceneManager.pushScene(SceneEnum.START);
            configLoader.updateProperty("startUpShown", "true");
            languageProcessor.getActions().get(selectedLanguage).run();
        }
    }

    /**
     * refreshes the configuration settings
     */
    public void refresh() {
    }
}
