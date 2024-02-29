package client.utils.language;

import client.Main;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;

import java.io.IOException;
import java.util.Locale;

public abstract class LanguageTemplate implements Language {

    /**
     * Activates the change of language
     * @return button of the language
     */
    public Button getButton() {
        Button button = new Button();
        button.setText(this.getText());
        Locale locale = this.getLocale();
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                var optionalMain = Main.getInstance();
                if (optionalMain.isPresent()) {
                    try {
                        optionalMain.get().start(locale);
                    } catch (IOException e) {
                        //TODO log error
                    }
                }
            }
        });
        return button;
    }

    protected abstract String getText();

    private Locale getLocale() {
        return Locale.of(getLanguage(), getCountry());
    }

    protected abstract String getLanguage();

    protected abstract String getCountry();
}
