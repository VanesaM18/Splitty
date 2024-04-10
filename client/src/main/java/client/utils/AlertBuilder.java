package client.utils;

import client.scenes.MainCtrl;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;

import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

public class AlertBuilder {
    private final MainCtrl mainCtrl;
    private final ResourceBundle resourceBundle;
    private Alert.AlertType alertType;
    private String title;
    private String header;
    private String content;
    private Modality modality;

    /**
     * constructs an AlertBuilder object.
     * @param mainCtrl MainCtrl instance
     */
    public AlertBuilder(MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.resourceBundle = getResourceBundle();
        this.title = "";
        this.header = "";
        this.content = "";
        this.alertType = Alert.AlertType.NONE;
        this.modality = Modality.NONE;
    }

    private ResourceBundle getResourceBundle() {
        Optional<Locale> currentLocale = mainCtrl.getCurrentLocale();
        Locale locale = currentLocale.orElse(Locale.of("en","EN"));
        return ResourceBundle.getBundle("bundles.Splitty", locale);
    }

    /**
     * sets the key for the title of the alert.
     * @param titleKey key for the title in
     *                 the resource bundle.
     * @return this AlertBuilder instance
     * for method chaining.
     */
    public AlertBuilder setTitleKey(String titleKey) {
        try {
            this.title = resourceBundle.getString(titleKey);
        } catch (Exception exception) {
            this.title = "";
            //TODO log error
        }
        return this;
    }

    /**
     * sets the key for the header text of the alert.
     * @param headerKey key for the header text in
     *                  the resource bundle.
     * @return this AlertBuilder instance
     * for method chaining.
     */
    public AlertBuilder setHeaderKey(String headerKey) {
        try {
            this.header = resourceBundle.getString(headerKey);
        } catch (Exception exception) {
            this.header = "";
            //TODO log error;
        }
        return this;
    }

    /**
     * sets the key for the content text of the alert.
     * @param contentKey key for the content text in
     *                   the resource bundle.
     * @return this AlertBuilder instance
     * for method chaining.
     */
    public AlertBuilder setContentKey(String contentKey) {
        try {
            this.content = resourceBundle.getString(contentKey);
        } catch (Exception exception) {
            this.content = "";
            //TODO log error
        }
        return this;
    }

    /**
     * sets the type of the alert.
     * @param alertType type of the alert.
     * @return this AlertBuilder instance
     * for method chaining.
     */
    public AlertBuilder setAlertType(Alert.AlertType alertType) {
        this.alertType = alertType;
        return this;
    }

    /**
     * sets the modality of the alert.
     * @param modality modality of the alert.
     * @return this AlertBuilder instance
     * for method chaining.
     */
    public AlertBuilder setModality(Modality modality) {
        this.modality = modality;
        return this;
    }

    /**
     * shows the alert with the configured properties.
     * @return an Optional<ButtonType> representing
     * the user's response to the alert.
     */
    public Optional<ButtonType> show() {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.initModality(modality);
        return alert.showAndWait();
    }
}
