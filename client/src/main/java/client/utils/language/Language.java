package client.utils.language;

import javafx.scene.control.Button;

public interface Language extends Runnable {

    /**
     * When called, the language of the application is changes
     * to the corresponding language of the button
     * @return button
     */
    Button getButton();
    void run();
    String getText();
}
