package client.utils.language;

import client.Main;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;

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
    public void run() {
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
        Button button = new Button();
        button.setText(this.getText());
        Runnable action = this;
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                action.run();
            }
        });
        return button;
    }

    /**
     * gets the text content in the specific language.
     * @return text content.
     */
    public abstract String getText();

    private Locale getLocale() {
        return Locale.of(getLanguage(), getCountry());
    }

    protected abstract String getLanguage();

    protected abstract String getCountry();
}
