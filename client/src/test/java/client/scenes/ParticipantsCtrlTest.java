package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Injector;
import commons.Event;
import commons.Participant;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.testfx.api.FxRobot;
import org.testfx.assertions.api.Assertions;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

import static com.google.inject.Guice.createInjector;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(ApplicationExtension.class)
public class ParticipantsCtrlTest {
    Set<Participant> participants = new HashSet<>();
    private final Event event = new Event("testCode", "name", LocalDateTime.now(), participants, new HashSet<>());

    Pane pane;
    ParticipantsCtrl controller;
    ServerUtils serverUtils;
    MainCtrl mainCtrl;

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
        serverUtils = Mockito.mock(ServerUtils.class);
        mainCtrl = Mockito.mock(MainCtrl.class);

        FXMLLoader loader =
                new FXMLLoader(getClass().getResource("/client/scenes/Participants.fxml"));
        Locale locale = Locale.of("en", "EN");
        loader.setResources(ResourceBundle.getBundle("bundles.Splitty", locale));
        Injector injector = createInjector(new TestModule());
        loader.setControllerFactory(parameter -> new ParticipantsCtrl(serverUtils, mainCtrl));

        // Actually load the file, and also save the controller.
        pane = (Pane) loader.load();
        controller = loader.getController();

        stage.setScene(new Scene(pane));
        stage.show();
    }

    @BeforeEach
    void reset(FxRobot robot) {
        Mockito.reset(serverUtils, mainCtrl);

        robot.interact(() -> {
            controller.setEvent(event);
        });
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
    void validEmail() {
        Assertions.assertThat(ParticipantsCtrl.isEmailValid("mike@gmail.com")).isTrue();
        Assertions.assertThat(ParticipantsCtrl.isEmailValid("mike-gmail.com")).isFalse();
    }

    @Test
    void validIban() {
        Assertions.assertThat(ParticipantsCtrl.isIbanValid("CY97946484768297433832859518")).isTrue();
        Assertions.assertThat(ParticipantsCtrl.isIbanValid("CY9794648")).isFalse();
    }

    @Test
    void validBic() {
        Assertions.assertThat(ParticipantsCtrl.isIBicValid("DLXUNR6QAEN")).isTrue();
        Assertions.assertThat(ParticipantsCtrl.isIBicValid("GT72454147X6JF38M7V6085887BG")).isFalse();
    }

    @Test
    void abortEsc(FxRobot robot) {
        // We cannot directly intereact with the controller from here, as we must be in a JavaFX
        // theda to do so. Thus, we use the FxRobot.interact method.
        // See more: https://github.com/TestFX/TestFX/issues/222
        robot.interact(() -> {
            KeyEvent event = new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.ESCAPE, false, false, false, false);
            controller.keyPressed(event);
        });

        Mockito.verify(mainCtrl, Mockito.times(1)).showOverviewEvent(null);
    }

    @Test
    void abort(FxRobot robot) {
        // We cannot directly intereact with the controller from here, as we must be in a JavaFX
        // theda to do so. Thus, we use the FxRobot.interact method.
        // See more: https://github.com/TestFX/TestFX/issues/222
        robot.clickOn("Abort");

        Mockito.verify(mainCtrl, Mockito.times(1)).showOverviewEvent(null);
    }

    @Test
    void add(FxRobot robot) {
        // We cannot directly intereact with the controller from here, as we must be in a JavaFX
        // theda to do so. Thus, we use the FxRobot.interact method.
        // See more: https://github.com/TestFX/TestFX/issues/222
        robot.interact(() -> {
            controller.setEvent(event);
            controller.setAdd(true);
            Assertions.assertThat(controller.isAdd()).isTrue();
        });
    }

    @Test
    void setFields(FxRobot robot) {
        // We cannot directly intereact with the controller from here, as we must be in a JavaFX
        // theda to do so. Thus, we use the FxRobot.interact method.
        // See more: https://github.com/TestFX/TestFX/issues/222
        robot.interact(() -> {
            controller.setEvent(event);
            Participant p = new Participant("Leo", "leo@mail.nl", "CY97946484768297433832859518", "DLXUNR6QAEN");
            controller.setFields(p);
        });

        TextField name = robot.lookup("#name").queryAs(TextField.class);
        Assertions.assertThat(name).hasText("Leo");

        TextField email = robot.lookup("#email").queryAs(TextField.class);
        Assertions.assertThat(email).hasText("leo@mail.nl");

        TextField iban = robot.lookup("#iban").queryAs(TextField.class);
        Assertions.assertThat(iban).hasText("CY97946484768297433832859518");

        TextField bic = robot.lookup("#bic").queryAs(TextField.class);
        Assertions.assertThat(bic).hasText("DLXUNR6QAEN");
    }

    @Test
    void ok(FxRobot robot) {
        TextField name = robot.lookup("#name").queryAs(TextField.class);

        robot.clickOn(name);
        robot.type(KeyCode.N, KeyCode.A, KeyCode.M, KeyCode.E);

        robot.type(KeyCode.ENTER);
        Participant expectedParticipant = new Participant("name", "", "", "");

        Mockito.verify(serverUtils, Mockito.times(1)).addParticipant(expectedParticipant);
    }

    @Test
    void ok_name(FxRobot robot) {
        // We cannot directly intereact with the controller from here, as we must be in a JavaFX
        // theda to do so. Thus, we use the FxRobot.interact method.
        // See more: https://github.com/TestFX/TestFX/issues/222
        robot.clickOn("Ok");

        WaitForAsyncUtils.waitForFxEvents();

        // Check if any instance of Alert dialog is present
        assertTrue(robot.lookup(".alert").tryQuery().isPresent());

    }
    @Test
    void ok_uniqueName(FxRobot robot) {
        // We cannot directly intereact with the controller from here, as we must be in a JavaFX
        // theda to do so. Thus, we use the FxRobot.interact method.
        // See more: https://github.com/TestFX/TestFX/issues/222
        robot.interact(() -> {
            Participant p = new Participant("name", "", "", "");
            event.addParticipant(p);
            controller.setEvent(event);
            controller.setAdd(true);
            controller.setParticipantToChange(p);
        });

        TextField name = robot.lookup("#name").queryAs(TextField.class);

        robot.clickOn(name);
        robot.type(KeyCode.N, KeyCode.A, KeyCode.M, KeyCode.E);

        robot.clickOn("Ok");
        assertTrue(robot.lookup(".alert").tryQuery().isPresent());

    }

    @Test
    void ok_uniqueNameUpdate(FxRobot robot) {
        // We cannot directly intereact with the controller from here, as we must be in a JavaFX
        // theda to do so. Thus, we use the FxRobot.interact method.
        // See more: https://github.com/TestFX/TestFX/issues/222
        robot.interact(() -> {
            Participant p1 = new Participant("name", "", "", "");
            Participant p2 = new Participant("name2", "", "", "");
            event.addParticipant(p2);
            controller.setEvent(event);
            controller.setAdd(true);
        });

        TextField name = robot.lookup("#name").queryAs(TextField.class);

        robot.clickOn(name);
        robot.type(KeyCode.BACK_SPACE);

        robot.clickOn("Ok");
        assertTrue(robot.lookup(".alert").tryQuery().isPresent());
    }

    @Test
    void ok_email(FxRobot robot) {
        // We cannot directly intereact with the controller from here, as we must be in a JavaFX
        // theda to do so. Thus, we use the FxRobot.interact method.
        // See more: https://github.com/TestFX/TestFX/issues/222
        TextField name = robot.lookup("#name").queryAs(TextField.class);
        TextField email = robot.lookup("#email").queryAs(TextField.class);

        robot.clickOn(name);
        robot.type(KeyCode.N, KeyCode.A, KeyCode.M, KeyCode.E);

        robot.clickOn(email);
        robot.type(KeyCode.N, KeyCode.A, KeyCode.M, KeyCode.E);

        robot.clickOn("Ok");

        WaitForAsyncUtils.waitForFxEvents();

        // Check if any instance of Alert dialog is present
        assertTrue(robot.lookup(".alert").tryQuery().isPresent());
    }

    @Test
    void ok_iban(FxRobot robot) {
        TextField name = robot.lookup("#name").queryAs(TextField.class);
        TextField iban = robot.lookup("#iban").queryAs(TextField.class);

        robot.clickOn(name);
        robot.type(KeyCode.N, KeyCode.A, KeyCode.M, KeyCode.E);

        robot.clickOn(iban);
        robot.type(KeyCode.N, KeyCode.A, KeyCode.M, KeyCode.E);

        robot.clickOn("Ok");

        WaitForAsyncUtils.waitForFxEvents();

        // Check if any instance of Alert dialog is present
        assertTrue(robot.lookup(".alert").tryQuery().isPresent());

    }

    @Test
    void ok_bic(FxRobot robot) {
        TextField name = robot.lookup("#name").queryAs(TextField.class);
        TextField bic = robot.lookup("#bic").queryAs(TextField.class);

        robot.clickOn(name);
        robot.type(KeyCode.N, KeyCode.A, KeyCode.M, KeyCode.E);

        robot.clickOn(bic);
        robot.type(KeyCode.N, KeyCode.A, KeyCode.M, KeyCode.E);

        robot.clickOn("Ok");

        WaitForAsyncUtils.waitForFxEvents();

        // Check if any instance of Alert dialog is present
        assertTrue(robot.lookup(".alert").tryQuery().isPresent());
    }
}
