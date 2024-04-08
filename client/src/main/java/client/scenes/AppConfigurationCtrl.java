package client.scenes;

import client.ConfigLoader;
import client.utils.DomainValidator;
import client.utils.SceneEnum;
import client.utils.ServerUtils;
import client.utils.language.LanguageProcessor;
import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class AppConfigurationCtrl {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private final LanguageProcessor languageProcessor;
    private final ConfigLoader configLoader;
//    @FXML
//    private ChoiceBox<String> choiceBox;
    @FXML
    private TextField urlTextField;
    @FXML
    private ComboBox<String> comboBox;

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
        //choiceBox.getItems().addAll(actions.keySet());
        //choiceBox.setValue("English");
        comboBox.getItems().addAll(actions.keySet());
        var local = mainCtrl.getCurrentLocale().orElse(Locale.of("en","EN"));

        var currentLanguage = languageProcessor
                .getLanguages().stream()
                .filter(language -> language.getLocale().equals(local))
                .toList().get(0);

        comboBox.setValue(currentLanguage.getText());
        comboBox.getItems().add(currentLanguage.getText());

    }

    /**
     * saves the selected language, updates configuration properties,
     * and navigates back to the previous scene
     * while executing language-specific actions
     */
    public void onSave() {
        String url = urlTextField.getText();

        Supplier<Boolean> onSuccessSupplier = () -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText("Are you sure you want to proceed?");
            alert.setContentText("Do you want to save these settings and proceed?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                String selectedLanguage = comboBox.getValue();//choiceBox.getValue();
                server.setServerUrl(url);
                configLoader.updateProperty("address", url);
                var sceneManager = mainCtrl.getSceneManager();
                sceneManager.popScene();
                sceneManager.pushScene(SceneEnum.START);
                configLoader.updateProperty("startUpShown", "true");
                languageProcessor.getActions().get(selectedLanguage).run();
                return true;
            }
            return false;
        };

        DomainValidator domain = new DomainValidator(this.mainCtrl, this.server);
        domain.validateUrl(url, onSuccessSupplier);
    }

    /**
     * refreshes the configuration settings
     */
    public void refresh() {
    }
}
