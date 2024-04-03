package client.utils.language;

import javafx.scene.control.Button;
import javafx.scene.image.Image;

import java.util.Locale;

public interface Language extends Runnable, Comparable<Language> {

    /**
     * When called, the language of the application is changes
     * to the corresponding language of the button
     * @return button
     */
    Button getButton();

    /**
     * executes language-specific actions, changing the application's locale
     * and updating the user interface.
     */
    void switchLanguage();

    /**
     * executes language-specific actions, changing the application's locale
     * and updating the user interface.
     * Needed in order to transform the action of switching a language
     * to a runnable in an efficient manner.
     */
    @Override
    default void run() {
        switchLanguage();
    }

    /**
     * gets the text content for the specific language.
     * @return text content.
     */
    String getText();

    /**
     * method for keeping the position of
     * the language object
     * @param other language
     * @return position
     */
    @Override
    default int compareTo(Language other) {
        return 0;
    }

    /**
     * gets the locale object associated with the language.
     * @return the language's locale.
     */
    Locale getLocale();

    /**
     * gets the flag image representing the language.
     * @return flag image associated with the language.
     */
    Image getFlag();
}
