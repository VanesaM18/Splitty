package client.utils.language.implementations;

import client.Main;
import client.utils.language.Language;
import client.utils.language.LanguageTemplate;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import java.io.IOException;
import java.util.Locale;

public class English extends LanguageTemplate {


    @Override
    protected String getText() {
        return "English";
    }

    @Override
    protected String getLanguage() {
        return "en";
    }

    @Override
    protected String getCountry() {
        return "EN";
    }
}
