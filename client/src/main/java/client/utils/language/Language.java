package client.utils.language;

import javafx.scene.control.Button;

public interface Language extends Runnable {

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
    void run();

    /**
     * gets the text content for the specific language.
     * @return text content.
     */
    String getText();
}
