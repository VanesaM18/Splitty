package client.scenes;

import client.utils.ServerUtils;
import commons.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
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
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

@ExtendWith(ApplicationExtension.class)
public class StatisticsCtrlTest {
    Event event;

    Pane pane;
    StatisticsCtrl controller;
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
        Participant p1 = new Participant("Alice", "alice@alice.com", "idk", "abcdef12");
        Participant p2 = new Participant("Bob", "bob@bob.com", "idk", "abcdef12");
        event = new Event("testCode", "name", LocalDateTime.now(), Set.of(p1, p2), new HashSet<>());
        Expense e1 = new Expense(event, "name", p1, new Monetary(100), LocalDate.now(), Set.of(p1, p2));
        Expense e2 = new Expense(event, "name 2", p2, new Monetary(50), LocalDate.now(), Set.of(p1, p2));
        ExpenseType tag1 = new ExpenseType("drink", "#fcba03", event);
        ExpenseType tag2 = new ExpenseType("activity", "##07f7ab", event);
        event.addType(tag1);
        event.addType(tag2);
        event.setExpenses(Set.of(e1, e2));
        serverUtils = Mockito.mock(ServerUtils.class);
        mainCtrl = Mockito.mock(MainCtrl.class);

        // We need to load the fxml file in this complicated manner because we need to
        // give it
        // access to an injector.
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/client/scenes/Statistics.fxml"));
        Locale locale = Locale.of("en", "EN");
        loader.setResources(ResourceBundle.getBundle("bundles.Splitty", locale));
        loader.setControllerFactory(parameter -> new StatisticsCtrl(serverUtils, mainCtrl));

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

    @Test
    void back(FxRobot robot) {
        robot.clickOn("Back");

        Mockito.verify(mainCtrl, Mockito.times(1)).showOverviewEvent(event);
    }

    @Test
    void backEsc(FxRobot robot) {
        robot.interact(() -> {
            KeyEvent event = new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.ESCAPE, false, false, false, false);
            controller.keyPressed(event);
        });
    }
}
