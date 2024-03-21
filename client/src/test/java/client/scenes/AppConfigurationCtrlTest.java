package client.scenes;

import com.google.inject.Injector;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

import static com.google.inject.Guice.createInjector;

@ExtendWith(ApplicationExtension.class)
public class AppConfigurationCtrlTest {

    Pane pane;
    AppConfigurationCtrl controller;

    @BeforeAll
    static void setup() {
        // Make sure the tests are run headless, since the CI server does not have a WM.
        System.setProperty("testfx.robot", "glass");
        System.setProperty("testfx.headless", "true");
        System.setProperty("prism.order", "sw");
        System.setProperty("prism.text", "t2k");
        System.setProperty("java.awt.headless", "true");
    }

    /**
     * Will be called with {@code @Before} semantics, i.e. before each test method.
     *
     * @param stage - Will be injected by the test runner.
     * @throws IOException
     */
    @Start
    private void start(Stage stage) throws IOException {
        // We need to load the fxml file in this complicated manner because we need to give it
        // access to an injector.
        FXMLLoader loader =
                new FXMLLoader(getClass().getResource("/client/scenes/AppConfiguration.fxml"));
        Locale locale = Locale.of("en", "EN");
        loader.setResources(ResourceBundle.getBundle("bundles.Splitty", locale));
        Injector injector = createInjector(new TestModule());
        loader.setControllerFactory(injector::getInstance);

        // Actually load the file, and also save the controller.
        pane = (Pane) loader.load();
        controller = loader.getController();

        stage.setScene(new Scene(pane));
        stage.show();
    }

    @Test
    void refresh(FxRobot robot) {
        robot.interact(() -> {
            controller.refresh();
        });
    }

    @Test
    void make(FxRobot robot) {
        robot.interact(() -> {
            controller.make();
        });
    }

}
