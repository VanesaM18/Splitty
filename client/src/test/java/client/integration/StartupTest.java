package client.integration;

import client.InitializationData;
import client.scenes.MainCtrl;
import client.utils.SceneEnum;
import client.utils.SceneManager;
import client.utils.TestClient;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.JavaFXInterceptorUtils;
import org.testfx.framework.junit5.Start;

@ExtendWith(ApplicationExtension.class)
@ExtendWith(JavaFXInterceptorUtils.JavaFxInterceptor.class)
public class StartupTest extends TestClient {
    /**
     * Test application startup
     * TestFx is needed to run it on the JavaFX thread
     */
    private MainCtrl mainCtrl;
    private SceneManager sm;

    @Override
    @Start
    public void start(Stage stage) {
        super.start(stage);
        mainCtrl = injector.getInstance(MainCtrl.class);
        sm = injector.getInstance(SceneManager.class);

    }

    @Test
    void testStartup(FxRobot robot) {
        sm.pushScene(SceneEnum.STARTUP);
        robot.interact(() -> mainCtrl.initialize(stage,
                injector.getInstance(InitializationData.class),
                injector.getInstance(SceneManager.class)));

        @SuppressWarnings("unchecked")
        ChoiceBox<String> selection = robot.lookup("#choiceBox").queryAs(ChoiceBox.class);
        TextField urlTextField = robot.lookup("#urlTextField").queryAs(TextField.class);

        Assertions.assertEquals("English", selection.getSelectionModel().getSelectedItem());
        Assertions.assertEquals("http://localhost:8080", urlTextField.getText());
    }
}
