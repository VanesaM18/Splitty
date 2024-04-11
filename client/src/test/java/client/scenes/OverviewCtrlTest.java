
package client.scenes;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

import client.utils.language.LanguageProcessor;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.testfx.api.FxRobot;
import org.testfx.assertions.api.Assertions;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;
import client.utils.SceneManager;
import client.utils.ServerUtils;
import commons.Event;
import commons.Expense;
import commons.Monetary;
import commons.Participant;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import static org.junit.jupiter.api.Assertions.assertTrue;


@ExtendWith(ApplicationExtension.class)
public class OverviewCtrlTest {
    // private final Event event =
    // new Event("testCode", "name", LocalDateTime.now(), Set.of(), new HashSet<>());
    // private final List<Participant> participants = List.of(new Participant("Alice", "", "", ""),
    // new Participant("Bob", "", "", ""), new Participant("Charlie", "", "", ""),
    // new Participant("David", "", "", ""), new Participant("Eve", "", "", ""));
    // private final Set<Expense> expenses =
    // Set.of(new Expense(event, "McDonald's", participants.get(0), new Monetary(23),
    // LocalDate.now(), Set.of(participants.get(0), participants.get(1))));

    private Event event;

    Pane pane;
    OverviewCtrl controller;
    ServerUtils serverUtils;
    MainCtrl mainCtrl;
    LanguageProcessor languageProcessor;
    SceneManager sceneManager;

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
        setupEvent();

        serverUtils = Mockito.mock(ServerUtils.class);
        mainCtrl = Mockito.mock(MainCtrl.class);
        sceneManager = Mockito.mock();
        languageProcessor = Mockito.mock(LanguageProcessor.class);
        Mockito.when(mainCtrl.getSceneManager()).thenReturn(sceneManager);
        Mockito.when(serverUtils.getEventById("test")).thenReturn(event);

        // We need to load the fxml file in this complicated manner because we need to give it
        // access to an injector.
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/client/scenes/Overview.fxml"));
        Locale locale = Locale.of("en", "EN");
        loader.setResources(ResourceBundle.getBundle("bundles.Splitty", locale));
        loader.setControllerFactory(parameter -> {
            OverviewCtrl ctrl = new OverviewCtrl(serverUtils, mainCtrl, languageProcessor);
            // Make sure the event is set here, before returning the controller!
            // Otherwise, we set the event too late and a lot of code is executed with a null event
            ctrl.setEvent(event);
            return ctrl;
        });

        // Actually load the file, and also save the controller.
        pane = (Pane) loader.load();
        controller = loader.getController();

        stage.setScene(new Scene(pane));
        stage.show();
    }

    private void setupEvent() {
        List<Participant> participants = List.of(new Participant("Alice", "", "", ""),
                new Participant("Bob", "", "", ""), new Participant("Charlie", "", "", ""),
                new Participant("David", "", "", ""), new Participant("Eve", "", "", ""));
        event = new Event("test", "name", LocalDateTime.now(), new HashSet<>(participants),
                new HashSet<>());
        Set<Expense> expenses = new HashSet<>();
        Expense e1 = new Expense(event, "McDonald's", participants.get(0), new Monetary(23),
                LocalDate.now(), new HashSet<>(Set.of(participants.get(0), participants.get(1))));
        e1.setTags(new HashSet<>());
        expenses.add(e1);
        event.setExpenses(new HashSet<>(expenses));
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

    @Test
    void backEsc(FxRobot robot) {
        robot.interact(() -> {
            KeyEvent event = new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.ESCAPE, false, false, false, false);
            controller.keyPressed(event);
        });
    }

    @Test
    void ctrlS(FxRobot robot) {
        robot.interact(() -> {
            KeyEvent event = new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.S, false, true, false, false);
            controller.keyPressed(event);
        });
        Mockito.verify(mainCtrl, Mockito.times(1)).showStatistics(event);
    }

    @Test
    void ctrlE(FxRobot robot) {
        robot.interact(() -> {
            KeyEvent event = new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.E, false, true, false, false);
            controller.keyPressed(event);
        });
        Mockito.verify(mainCtrl, Mockito.times(1)).showExpense(event, null);
    }

    @Test
    @Timeout(value = 10)
    void deleteParticipantSuccesfull(FxRobot robot) {
        robot.interact(() -> {
            controller.setEvent(event);
            controller.refresh();
        });

        ListView<Participant> participantsListView =
                robot.lookup("#participantNames").queryListView();
        Participant participant = new ArrayList<>(event.getParticipants()).stream()
                .filter(p -> p.getName().equals("David")).findFirst().get();
        participantsListView.getSelectionModel().select(participant);

        // NOTE: We use Platform.runLater combed with the waitForFxEvents instead of robot.interact
        // to make sure the test doesn't hang. Otherwise, the test would hang forever because of the
        // call to alert.showAndWait()
        Platform.runLater(() -> {
            controller.deleteParticipant();
        });
        // Needed so the dialog can actually show up, since runLater returns immediately.
        WaitForAsyncUtils.waitForFxEvents();

        DialogPane dialogPane = robot.lookup(".dialog-pane").queryAs(DialogPane.class);
        Assertions.assertThat(dialogPane).isVisible();

        // Close the dialog.
        robot.clickOn("Confirm");
    }

    @Test
    void deleteExpenseCancel(FxRobot robot) {
        robot.interact(() -> {
            controller.setEvent(event);
            controller.refresh();
        });

        Platform.runLater(() -> {
            controller.deleteExpense((Expense) event.getExpenses().toArray()[0]);
        });
        WaitForAsyncUtils.waitForFxEvents();

        DialogPane dialogPane = robot.lookup(".dialog-pane").queryAs(DialogPane.class);
        Assertions.assertThat(dialogPane).isVisible();

        // Close the dialog.
        robot.clickOn("Cancel");

        Mockito.verify(serverUtils, Mockito.never()).deleteExpense(Mockito.any());
    }

    @Test
    void deleteExpenseSuccessfull(FxRobot robot) {
        robot.interact(() -> {
            controller.setEvent(event);
            controller.refresh();
        });

        Expense e = (Expense) event.getExpenses().toArray()[0];
        Platform.runLater(() -> {
            controller.deleteExpense(e);
        });
        WaitForAsyncUtils.waitForFxEvents();

        DialogPane dialogPane = robot.lookup(".dialog-pane").queryAs(DialogPane.class);
        Assertions.assertThat(dialogPane).isVisible();

        // Close the dialog.
        robot.clickOn("OK");

        Mockito.verify(serverUtils, Mockito.times(1)).deleteExpense(e);
    }

    @Test
    void deleteExpenseErrorShowsDialog(FxRobot robot) {
        Mockito.doThrow(new RuntimeException("Mock error")).when(serverUtils)
                .deleteExpense(Mockito.any());

        robot.interact(() -> {
            controller.setEvent(event);
            controller.refresh();
        });

        Expense e = (Expense) event.getExpenses().toArray()[0];
        Platform.runLater(() -> {
            controller.deleteExpense(e);
        });
        WaitForAsyncUtils.waitForFxEvents();

        DialogPane dialogPane = robot.lookup(".dialog-pane").queryAs(DialogPane.class);
        Assertions.assertThat(dialogPane).isVisible();

        // Close the dialog.
        robot.clickOn("OK");

        Mockito.verify(serverUtils, Mockito.times(1)).deleteExpense(e);

        // Assert that a new error message is now shown, with the right error
        dialogPane = robot.lookup(".dialog-pane").queryAs(DialogPane.class);
        Assertions.assertThat(dialogPane).hasChild("Mock error");
    }

    @Test
    void selectParticipantShowsCorrectExpenses(FxRobot robot) {
        robot.interact(() -> {
            controller.setEvent(event);
            controller.refresh();
        });

        ComboBox<Participant> combobox = robot.lookup("#participantComboBox").queryComboBox();
        Participant participant = new ArrayList<>(event.getParticipants()).stream()
                .filter(p -> p.getName().equals("Alice")).findFirst().get();
        robot.interact(() -> {
            combobox.getSelectionModel().select(participant);
        });

        Assertions.assertThat(pane).hasChild("From " + participant.getName());
    }

    @Test
    void settleDebtShowsDialogWhenNoDebts(FxRobot robot) {
        Event tempEvent =
                new Event("code", "name", LocalDateTime.now(), new HashSet<>(), new HashSet<>());
        tempEvent.setExpenses(new HashSet<>());

        robot.interact(() -> {
            controller.setEvent(tempEvent);
            controller.refresh();
        });

        robot.clickOn("Settle Debts");
        WaitForAsyncUtils.waitForFxEvents();

        DialogPane dialogPane = robot.lookup(".dialog-pane").queryAs(DialogPane.class);
        Assertions.assertThat(dialogPane).isVisible();
        Mockito.verify(mainCtrl, Mockito.never()).showOpenDebts(Mockito.any());

        robot.clickOn("OK");
    }
}
