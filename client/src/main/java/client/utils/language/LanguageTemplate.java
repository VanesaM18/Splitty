package client.utils.language;

import client.Main;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;

import java.io.IOException;
import java.util.Locale;

public abstract class LanguageTemplate implements Language {

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

    public abstract String getText();

    private Locale getLocale() {
        return Locale.of(getLanguage(), getCountry());
    }

    protected abstract String getLanguage();

    protected abstract String getCountry();
}
