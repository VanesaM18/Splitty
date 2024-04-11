package client.scenes;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.testfx.api.FxRobot;
import org.testfx.assertions.api.Assertions;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import client.utils.EmailManager;
import client.utils.ServerUtils;
import commons.Event;
import commons.ExpenseType;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

@ExtendWith(ApplicationExtension.class)
class ExpenseTypeCtrlTest {

    Event event;
    ExpenseType expenseType;

    Pane pane;
    ExpenseTypeCtrl controller;
    ServerUtils serverUtils;
    MainCtrl mainCtrl;
    EmailManager emailManager;

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
        event = new Event("code", "name", LocalDateTime.now(), new HashSet<>(), new HashSet<>());
        expenseType = new ExpenseType("name", "red", event);
        Set<ExpenseType> expenseTypes = new HashSet<>();
        expenseTypes.add(expenseType);
        event.setTags(expenseTypes);
        event.setExpenses(new HashSet<>());

        serverUtils = Mockito.mock(ServerUtils.class);
        mainCtrl = Mockito.mock(MainCtrl.class);

        Mockito.when(serverUtils.getEventById("code")).thenReturn(event);

        // We need to load the fxml file in this complicated manner because we need to give it
        // access to an injector.
        FXMLLoader loader =
                new FXMLLoader(getClass().getResource("/client/scenes/ExpenseTypes.fxml"));
        Locale locale = Locale.of("en", "EN");
        loader.setResources(ResourceBundle.getBundle("bundles.Splitty", locale));
        loader.setControllerFactory(parameter -> {
            ExpenseTypeCtrl expenseTypeCtrl = new ExpenseTypeCtrl(serverUtils, mainCtrl);
            expenseTypeCtrl.setEvent(event);
            return expenseTypeCtrl;
        });

        // Actually load the file, and also save the controller.
        pane = (Pane) loader.load();
        controller = loader.getController();

        stage.setScene(new Scene(pane));
        stage.show();
    }

    @Test
    void backButtonGoesBack(FxRobot robot) {
        robot.clickOn("Save changes");

        Mockito.verify(mainCtrl, Mockito.times(1)).showOverviewEvent(Mockito.any());
    }

    @Test
    void backButtonGoesBackEscape(FxRobot robot) {
        robot.interact(() -> {
            KeyEvent event = new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.ESCAPE, false, false, false, false);
            controller.keyPressed(event);
        });

        Mockito.verify(mainCtrl, Mockito.times(1)).showOverviewEvent(Mockito.any());
    }

    @Test
    void deleteShowsConfirmation(FxRobot robot) {
        robot.interact(() -> {
            controller.setEvent(event);
            controller.refresh();
        });

        // Take the second button. The first one is for editing
        Button deleteButton = robot.lookup("#tags").lookup(".button").nth(1).queryButton();
        robot.clickOn(deleteButton);

        DialogPane dialogPane = robot.lookup(".dialog-pane").queryAs(DialogPane.class);
        Assertions.assertThat(dialogPane).isVisible();

        // Close the dialog.
        robot.clickOn("Confirm");
    }
}
