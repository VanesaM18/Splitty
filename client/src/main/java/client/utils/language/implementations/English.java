package client.utils.language.implementations;

import client.Main;
import client.utils.language.Language;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;

import java.io.IOException;
import java.util.Locale;

public class English implements Language {
    @Override
    public Button getButton() {
        Button button = new Button();
        button.setText("English");
        button.setOnAction(
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        var optionalMain = Main.getInstance();
                        if (optionalMain.isPresent()) {
                            try {
                                optionalMain.get().updateLocale(new Locale("en", "EN"));
                            } catch (IOException e) {
                                // TODO log error
                            }
                        }
                    }
                });
        return button;
    }
}
