
package client.scenes;

import client.MyModule;
import com.google.inject.Injector;
import commons.Event;
import commons.Participant;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.assertions.api.Assertions;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.robot.Motion;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

import static com.google.inject.Guice.createInjector;

@ExtendWith(ApplicationExtension.class)
public class OverviewCtrlTest {
    private final Event event = new Event("testCode", "name", LocalDateTime.now(), Set.of(
            new Participant("Nathan", "nathan@example.org", "IL434700657700148540079", "XLTXTJSX"),
            new Participant("Sem", "sem@example.org", "QA02RSADO4966W8630650U7KG1171", "XAVTTOAI"),
            new Participant("Femke", "femke@example.org", "DK7787948201327760", "RTFWNFFXRDJ")),
            Set.of());

    Pane pane;
    OverviewCtrl controller;

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
     * Will be called with {@code @Before} semantics, i. e. before each test method.
     *
     * @param stage - Will be injected by the test runner.
     * @throws IOException
     */
    @Start
    private void start(Stage stage) throws IOException {
        // We need to load the fxml file in this complicated manner because we need to
        // give it
        // access to an injector.
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/client/scenes/Overview.fxml"));
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

    /**
     * @param robot - Will be injected by the test runner.
     */
    @Test
    void eventDetailsAreShown(FxRobot robot) {
        // We cannot directly intereact with the controller from here, as we must be in
        // a JavaFX
        // theda to do so. Thus, we use the FxRobot.interact method.
        // See more: https://github.com/TestFX/TestFX/issues/222
        robot.interact(() -> {
            controller.setEvent(event);
            controller.refresh();
        });

        Label title = robot.lookup("#title").queryAs(Label.class);
        Assertions.assertThat(title).hasText(event.getName());
    }

    @Test
    void back(FxRobot robot) {
        robot.interact(() -> {
            controller.back();
        });
    }
}
