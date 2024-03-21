
package client.scenes;

import static com.google.inject.Guice.createInjector;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.testfx.api.FxRobot;
import org.testfx.assertions.api.Assertions;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;
import com.google.inject.Injector;
import client.MyModule;
import client.utils.SceneManager;
import client.utils.ServerUtils;
import commons.Admin;
import commons.Event;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

@ExtendWith(ApplicationExtension.class)
class LoginCtrlTest {

    Pane pane;
    LoginCtrl controller;
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
     * Will be called with {@code @Before} semantics, i. e. before each test method.
     *
     * @param stage - Will be injected by the test runner.
     * @throws IOException
     */
    @Start
    private void start(Stage stage) throws IOException {
        serverUtils = Mockito.mock(ServerUtils.class);
        mainCtrl = Mockito.mock(MainCtrl.class);

        // We need to load the fxml file in this complicated manner because we need to give it
        // access to an injector.
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/client/scenes/LoginView.fxml"));
        Locale locale = Locale.of("en", "EN");
        loader.setResources(ResourceBundle.getBundle("bundles.Splitty", locale));
        loader.setControllerFactory(parameter -> new LoginCtrl(serverUtils, mainCtrl));

        // Actually load the file, and also save the controller.
        pane = (Pane) loader.load();
        controller = loader.getController();

        stage.setScene(new Scene(pane));
        stage.show();
    }

    @BeforeEach
    void resetMock() {
        Mockito.reset(serverUtils, mainCtrl);
    }

    /**
     * @param robot - Will be injected by the test runner.
     */
    @Test
    void clearFields(FxRobot robot) {
        robot.clickOn("#username").type(KeyCode.A, KeyCode.B);
        robot.clickOn("#password").type(KeyCode.C, KeyCode.D);

        robot.interact(() -> {
            controller.clearFields();
        });

        Assertions.assertThat(robot.lookup("#username").queryAs(TextField.class)).hasText("");
        Assertions.assertThat(robot.lookup("#password").queryAs(TextField.class)).hasText("");
    }

    @Test
    void loginSuccessful(FxRobot robot) {
        Mockito.when(serverUtils.loginAdmin(new Admin("ab", "cd", "")))
                .thenReturn("Login successfully");


        robot.clickOn("#username").type(KeyCode.A, KeyCode.B);
        robot.clickOn("#password").type(KeyCode.C, KeyCode.D);

        robot.interact(() -> {
            controller.logIn();
        });

        Mockito.verify(mainCtrl, Mockito.times(1)).showManagementOverview();
        assertTrue(ServerUtils.isAuthenticated());
    }

    @Test
    void loginUnsuccessful(FxRobot robot) {
        Admin admin = new Admin("ab", "cd", "");

        Mockito.when(serverUtils.loginAdmin(admin)).thenReturn("Invalid credentials");


        robot.clickOn("#username").type(KeyCode.A, KeyCode.B);
        robot.clickOn("#password").type(KeyCode.C, KeyCode.D);

        // NOTE: We use Platform.runLater combed with the waitForFxEvents instead of robot.interact
        // to make sure the test doesn't hang. Otherwise, the test would hang forever because of the
        // call to alert.showAndWait()
        Platform.runLater(() -> {
            controller.logIn();
        });
        // Needed so the dialog can actually show up, since runLater returns immediately.
        WaitForAsyncUtils.waitForFxEvents();

        DialogPane dialogPane = robot.lookup(".dialog-pane").queryAs(DialogPane.class);
        Assertions.assertThat(dialogPane).isVisible();

        // Close the dialog.
        robot.clickOn("OK");
    }

    @Test
    void backButtonGoesBack(FxRobot robot) {
        SceneManager manager = Mockito.mock();

        Mockito.when(mainCtrl.getSceneManager()).thenReturn(manager);

        robot.clickOn("Back");

        Mockito.verify(manager, Mockito.times(1)).goBack();
    }
}
