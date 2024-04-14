package client.scenes;

import client.ConfigLoader;
import client.utils.AlertBuilder;
import client.utils.DomainValidator;
import client.utils.ServerUtils;
import client.utils.language.LanguageProcessor;
import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;

import java.util.Optional;
import java.util.function.Supplier;

public class SettingsCtrl {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private final LanguageProcessor languageProcessor;
    private final ConfigLoader configLoader;
    @FXML
    private VBox languages;
    @FXML
    private TextField urlTextField;

    /**
     * Controller for handling the settings overview functionality.
     * @param server instance of ServerUtils for server-related operations.
     * @param mainCtrl instance of MainCtrl for coordinating with the main controller.
     * @param languageProcessor instance of LanguageProcessor.
     * @param configLoader instance of ConfigLoader.
     */
    @Inject
    public SettingsCtrl(ServerUtils server, MainCtrl mainCtrl,
                        LanguageProcessor languageProcessor, ConfigLoader configLoader) {
        this.mainCtrl = mainCtrl;
        this.server = server;
        this.languageProcessor = languageProcessor;
        this.configLoader = configLoader;
    }

    /**
     * navigates back to the previous scene
     */
    public void goBack() {
        mainCtrl.getSceneManager().goBack();
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
        languages.getChildren().add(this.languageProcessor.getButtons());
        Object url = configLoader.getProperty("address");
        if(url != null) {
            urlTextField.setPromptText(url.toString());
        }
    }

    /**
     * saves the entered URL, updates server settings, and configuration file
     */
    public void saveURL() {
        String url = urlTextField.getText();
        Supplier<Boolean> onSuccessSupplier = () -> {
            Optional<ButtonType> result = new AlertBuilder(mainCtrl)
                    .setAlertType(Alert.AlertType.CONFIRMATION)
                    .setTitleKey("confirmation_title")
                    .setHeaderKey("confirmation_header")
                    .setContentKey("content_save_settings")
                    .show();

            if (result.isPresent() && result.get() == ButtonType.OK) {

                new AlertBuilder(mainCtrl)
                        .setAlertType(Alert.AlertType.INFORMATION)
                        .setTitleKey("content_url")
                        .setContentKey("content_url_saved")
                        .show();

                server.setServerUrl(url);
                configLoader.updateProperty("address", url);

                new AlertBuilder(mainCtrl)
                        .setAlertType(Alert.AlertType.WARNING)
                        .setTitleKey("content_restart")
                        .setContentKey("content_restart_client")
                        .show();

                goBack();
                return true;
            }
            return false;
        };
        DomainValidator domain = new DomainValidator(this.mainCtrl, this.server);
        domain.validateUrl(url, onSuccessSupplier);

    }

    /**
     * Event handler for pressing a key.
     *
     * @param e the key that is pressed
     */
    public void keyPressed(KeyEvent e) {
        switch (e.getCode()) {
            case ENTER:
                saveURL();
                break;
            case ESCAPE:
                goBack();
                break;
            default:
                break;
        }
    }
}
