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
import org.testfx.assertions.api.Assertions;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import client.ConfigLoader;
import client.utils.ServerUtils;
import commons.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

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

//    @Test
//    void testCreateEvent(FxRobot robot) {
//        TextField eventName = robot.lookup("#createEventField").queryAs(TextField.class);
//
//        robot.clickOn(eventName);
//        robot.type(KeyCode.A, KeyCode.B, KeyCode.C);
//
//        Event event = new Event("testCode", "abc", LocalDateTime.now(), new HashSet<>(), new HashSet<>());
//        Mockito.when(serverUtils.addEvent(Mockito.any())).thenReturn(event);
//
//        robot.clickOn("Create");
//
//        // We only want to verify the name, so use argThat.
//        Mockito.verify(serverUtils, Mockito.times(1)).addEvent(Mockito.argThat((ev) -> {
//            return ev.getName().equals(event.getName());
//        }));
//    }
//
//    @Test
//    void testJoinEventExists(FxRobot robot) {
//        TextField eventName = robot.lookup("#joinEventField").queryAs(TextField.class);
//
//        robot.clickOn(eventName);
//        robot.type(KeyCode.T, KeyCode.E, KeyCode.S, KeyCode.T);
//
//        Event event = new Event("test", "abc", LocalDateTime.now(), new HashSet<>(), new HashSet<>());
//        Mockito.when(serverUtils.getEventById("test")).thenReturn(event);
//
//        robot.clickOn("Join");
//
//        Mockito.verify(serverUtils, Mockito.times(1)).getEventById("test");
//        Mockito.verify(serverUtils, Mockito.times(1)).sendUpdateStatus("test");
//    }
//
    @Test
    void testJoinEventDoesNotExist(FxRobot robot) {
        TextField eventName = robot.lookup("#joinEventField").queryAs(TextField.class);

        robot.clickOn(eventName);
        robot.type(KeyCode.N, KeyCode.O);

        robot.clickOn("Join");

        Mockito.verify(serverUtils, Mockito.times(1)).getEventById("no");

        // Assert that the error dialog shows up
        DialogPane dialogPane = robot.lookup(".dialog-pane").queryAs(DialogPane.class);
        Assertions.assertThat(dialogPane).isVisible();
    }
}
