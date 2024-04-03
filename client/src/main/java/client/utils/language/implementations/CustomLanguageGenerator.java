package client.utils.language.implementations;

import client.utils.language.Language;
import client.utils.language.LanguageTemplate;
import javafx.scene.control.Alert;

public class CustomLanguageGenerator extends LanguageTemplate {

    /**
     * returns the text for the custom language option
     * @return text
     */
    @Override
    public String getText() {
        return "add custom language";
    }

    /**
     * getting the placeholder for the language
     * @return the language string.
     */
    @Override
    protected String getLanguage() {
        return "?";
    }

    /**
     * getting the placeholder method for the country.
     * @return country string.
     */
    @Override
    protected String getCountry() {
        return "?";
    }

    /**
     * initiates the language switching process.
     */
    @Override
    public void switchLanguage() {
        showAlert();
    }

    /**
     * displays an error alert indicating that
     * the feature is under development.
     */
    public void showAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Currently under development");
        alert.setHeaderText("This feature has not been implemented yet");
        alert.showAndWait();
        alert.getOnCloseRequest();
    }

    /**
     * method for keeping the position of
     * the language object
     * @param other language
     * @return position
     */
    @Override
    public int compareTo(Language other) {
        return 1;
    }
}
