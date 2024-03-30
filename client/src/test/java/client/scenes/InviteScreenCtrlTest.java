package client.scenes;

import static com.google.inject.Guice.createInjector;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

import client.ConfigLoader;
import client.Main;
import client.utils.EmailManager;
import client.utils.ServerUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.testfx.api.FxRobot;
import org.testfx.assertions.api.Assertions;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import com.google.inject.Injector;
import client.MyModule;
import commons.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

@ExtendWith(ApplicationExtension.class)
class InviteScreenCtrlTest {

    private final Event event = new Event("testCode", "name", LocalDateTime.now(), Set.of(), new HashSet<>());

    Pane pane;
    InviteScreenCtrl controller;

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
        ServerUtils serverUtils = Mockito.mock(ServerUtils.class);
        MainCtrl mainCtrl = Mockito.mock(MainCtrl.class);
        EmailManager emailManager = Mockito.mock(EmailManager.class);
        ConfigLoader configLoader = Mockito.mock(ConfigLoader.class);
        FXMLLoader loader =
                new FXMLLoader(getClass().getResource("/client/scenes/InviteScreen.fxml"));
        Locale locale = Locale.of("en", "EN");
        loader.setResources(ResourceBundle.getBundle("bundles.Splitty", locale));
        loader.setControllerFactory(parameter -> new InviteScreenCtrl(mainCtrl, emailManager, serverUtils, configLoader));

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
    void inviteCodeIsDisplayed(FxRobot robot) {
        // We cannot directly intereact with the controller from here, as we must be in a JavaFX
        // theda to do so. Thus, we use the FxRobot.interact method.
        // See more: https://github.com/TestFX/TestFX/issues/222
        robot.interact(() -> {
            controller.setEvent(event);
            controller.refresh();
        });

        String expectedString = "Give people the following invite code: " + event.getInviteCode();

        Label inviteCodeLabel = robot.lookup("#inviteCodeLabel").queryAs(Label.class);
        Assertions.assertThat(inviteCodeLabel).hasText(expectedString);
    }
}
