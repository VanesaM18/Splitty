package client.scenes;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import client.utils.language.LanguageProcessor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import client.ConfigLoader;
import client.utils.ServerUtils;
import commons.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.testfx.util.WaitForAsyncUtils;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(ApplicationExtension.class)
class StartScreenCtrlTest {

    Pane pane;
    StartScreenCtrl controller;
    ServerUtils serverUtils;
    MainCtrl mainCtrl;
    ConfigLoader configLoader;
    LanguageProcessor languageProcessor;

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
        configLoader = Mockito.mock(ConfigLoader.class);

        Mockito.when(configLoader.getProperty("recentEvents")).thenReturn(List.of("testCode"));
        Event event = new Event("testCode", "abc", LocalDateTime.now(), new HashSet<>(),new HashSet<>());
        Mockito.when(serverUtils.getEventById("testCode")).thenReturn(event);

        // We need to load the fxml file in this complicated manner because we need to give it
        // access to an injector.
        FXMLLoader loader =
                new FXMLLoader(getClass().getResource("/client/scenes/StartScreen.fxml"));
        Locale locale = Locale.of("en", "EN");
        loader.setResources(ResourceBundle.getBundle("bundles.Splitty", locale));
        loader.setControllerFactory(
                parameter -> new StartScreenCtrl(serverUtils, mainCtrl, configLoader, languageProcessor));

        // Actually load the file, and also save the controller.
        pane = (Pane) loader.load();
        controller = loader.getController();

        stage.setScene(new Scene(pane));
        stage.show();
    }

    @BeforeEach
    void reset(FxRobot robot) {
        Mockito.reset(serverUtils, mainCtrl);
    }

    @Test
    public void joinFail(FxRobot robot) {
        robot.clickOn("Join");
        WaitForAsyncUtils.waitForFxEvents();

        assertTrue(robot.lookup(".alert").tryQuery().isPresent());
    }
    @Test
    public void createFail(FxRobot robot) {
        robot.clickOn("Create");
        WaitForAsyncUtils.waitForFxEvents();

        assertTrue(robot.lookup(".alert").tryQuery().isPresent());
    }
}
