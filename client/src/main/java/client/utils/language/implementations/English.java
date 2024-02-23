package client.utils.language.implementations;

import client.utils.language.Language;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;

import java.util.Locale;

public class English implements Language {
    @Override
    public Button getButton() {
        Button button = new Button();
        button.setText("English");
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //TODO update locale with (new Locale("en", "EN"));
            }
        });
        return button;
    }

}
