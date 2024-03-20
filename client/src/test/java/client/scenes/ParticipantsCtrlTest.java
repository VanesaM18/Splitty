package client.scenes;

import client.MyModule;
import com.google.inject.Injector;
import commons.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.assertions.api.Assertions;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Set;

import static com.google.inject.Guice.createInjector;

@ExtendWith(ApplicationExtension.class)
public class ParticipantsCtrlTest {
    private final Event event = new Event("testCode", "name", LocalDateTime.now(), Set.of());

    Pane pane;
    ParticipantsCtrl controller;

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
        // We need to load the fxml file in this complicated manner because we need to give it
        // access to an injector.
        FXMLLoader loader =
                new FXMLLoader(getClass().getResource("/client/scenes/Participants.fxml"));
        Injector injector = createInjector(new MyModule());
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
    void clearedFields(FxRobot robot) {
        // We cannot directly intereact with the controller from here, as we must be in a JavaFX
        // theda to do so. Thus, we use the FxRobot.interact method.
        // See more: https://github.com/TestFX/TestFX/issues/222
        robot.interact(() -> {
            controller.setEvent(event);
            controller.clearFields();
        });

        TextField name = robot.lookup("#name").queryAs(TextField.class);
        Assertions.assertThat(name).hasText("");

        TextField email = robot.lookup("#email").queryAs(TextField.class);
        Assertions.assertThat(email).hasText("");

        TextField iban = robot.lookup("#iban").queryAs(TextField.class);
        Assertions.assertThat(iban).hasText("");

        TextField bic = robot.lookup("#bic").queryAs(TextField.class);
        Assertions.assertThat(bic).hasText("");
    }

    @Test
    void validEmail(FxRobot robot) {
        Assertions.assertThat(ParticipantsCtrl.isEmailValid("mike@gmail.com")).isTrue();
        Assertions.assertThat(ParticipantsCtrl.isEmailValid("mike-gmail.com")).isFalse();
    }

    @Test
    void validIban(FxRobot robot) {
        Assertions.assertThat(ParticipantsCtrl.isIbanValid("CY97946484768297433832859518")).isTrue();
        Assertions.assertThat(ParticipantsCtrl.isIbanValid("CY9794648")).isFalse();
    }
}
