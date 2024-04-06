package client.utils.language;

import client.Main;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.util.Locale;

public abstract class LanguageTemplate implements Language {

    /**
     * executes language-specific actions, changing the application's locale
     * and updating the user interface.
     * retrieves the current locale, starts the main application with the specified locale,
     * and updates the scene by popping the current one from the scene manager.
     */
    @Override
    public void switchLanguage() {
        Locale locale = this.getLocale();
        var optionalMain = Main.getInstance();
        if (optionalMain.isPresent()) {
            try {
                var main = optionalMain.get();
                main.start(locale, main.getSceneManager().popScene());

            } catch (IOException e) {
                //TODO log error
            }
        }
    }

    /**
     * Activates the change of language
     * @return button of the language
     */
    public Button getButton() {
        //return button;
        return this.getButtonWithFlag();
    }

    /**
     * gets the text content in the specific language.
     * @return text content.
     */
    public abstract String getText();

    /**
     * gets the locale based on the language and country.
     * @return the locale.
     */
    public Locale getLocale() {
        return Locale.of(getLanguage(), getCountry());
    }

    protected abstract String getLanguage();

    protected abstract String getCountry();

    /**
     * Returns a button with an image as the flag icon of the language.
     * The image is loaded from the resources directory based on the language name.
     * @return Button with the flag icon.
     */
    public Button getButtonWithFlag() {
        LanguageButton button = new LanguageButton();

        //Image image = new Image("flags/" + languageName + ".svg");
        try {
            Image img = getFlag();
            ImageView imageView = new ImageView(img);
            imageView.setFitHeight(20);
            imageView.setPreserveRatio(true);
            button.setGraphic(imageView);
        } catch (IllegalArgumentException e) {
            System.out.println("Image not found for language: " + this.getText());
        }
        button.setText(this.getText());
        Runnable action = this;
        button.setOnAction(event -> action.run());
        return button;
    }

    protected static class LanguageButton extends Button {
        @Override
        public ObservableList<Node> getChildren() {
            return super.getChildren();
        }
    }

    /**
     * gets the flag image of the language.
     * @return flag image.
     */
    @Override
    public Image getFlag(){
        String languageName = "flag_" + this.getLanguage() + "_" + this.getCountry();
        String imagePath = "flags/" + languageName + ".png";
        //Image image = new Image("flags/" + languageName + ".svg");
        return new Image(imagePath);
    }
}
