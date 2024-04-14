package client.scenes;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import javafx.application.Platform;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import client.utils.EmailManager;
import client.utils.ServerUtils;
import commons.Debt;
import commons.Event;
import commons.Expense;
import commons.Monetary;
import commons.Participant;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(ApplicationExtension.class)
class OpenDebtsCtrlTest {

    List<Participant> participants;
    Event event;

    Pane pane;
    OpenDebtsCtrl controller;
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
        serverUtils = Mockito.mock(ServerUtils.class);
        mainCtrl = Mockito.mock(MainCtrl.class);
        emailManager = Mockito.mock(EmailManager.class);
        Mockito.when(emailManager.areCredentialsValid()).thenReturn(true);

        setupEvent();

        // Set up some fake debts to be returned
        List<Debt> debts = new ArrayList<>(
                List.of(new Debt(participants.get(0), new Monetary(20), participants.get(1)),
                        new Debt(participants.get(1), new Monetary(20), participants.get(2)),
                        new Debt(participants.get(2), new Monetary(20), participants.get(0))));

        Mockito.when(serverUtils.calculateDebts(event)).thenReturn(debts);


        // We need to load the fxml file in this complicated manner because we need to give it
        // access to an injector.
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/client/scenes/OpenDebts.fxml"));
        Locale locale = Locale.of("en", "EN");
        loader.setResources(ResourceBundle.getBundle("bundles.Splitty", locale));
        loader.setControllerFactory(parameter -> {
            OpenDebtsCtrl openDebtsCtrl = new OpenDebtsCtrl(mainCtrl, serverUtils, emailManager);
            return openDebtsCtrl;
        });

        // Actually load the file, and also save the controller.
        pane = (Pane) loader.load();
        controller = loader.getController();
        controller.initialize(event);

        stage.setScene(new Scene(pane));
        stage.show();
    }

    @BeforeEach
    void resetMock() {
        Mockito.reset(serverUtils, mainCtrl);
    }

    private void setupEvent() {
        participants = List.of(new Participant("Alice", "alice@alice.com", "iban", "bic"),
                new Participant("Bob", "", "", ""), new Participant("Charlie", "", "", ""),
                new Participant("David", "", "", ""), new Participant("Eve", "", "", ""));
        for (int i = 0; i < participants.size(); i++) {
            participants.get(i).setId(i);
        }
        event = new Event("test", "name", LocalDateTime.now(), new HashSet<>(participants),
                new HashSet<>());
        Set<Expense> expenses = new HashSet<>();
        Expense e1 = new Expense(event, "McDonald's", participants.get(0), new Monetary(23),
                LocalDate.now(), new HashSet<>(Set.of(participants.get(0), participants.get(1))));
        e1.setTags(new HashSet<>());
        expenses.add(e1);
        event.setExpenses(new HashSet<>(expenses));
        when(serverUtils.getEventById("test")).thenReturn(event);
    }

    @Test
    void backButtonGoesBack(FxRobot robot) {
        robot.clickOn("Back");

        verify(mainCtrl, times(1)).setIsInOpenDebt(false);
        verify(mainCtrl, times(1)).refreshData();
        verify(mainCtrl, times(1)).showOverviewEvent(null);
    }

    @Test
    void initializeShouldClearDebtsIfEventIsNull(FxRobot robot) {
        Platform.runLater(() -> {
            controller.initialize(null);
        });
        robot.sleep(3000);
        verify(serverUtils, times(0)).calculateDebts(event);
    }
    @Test
    void startLongPollingUpdatesUIWhenDataChanges(FxRobot robot) {
        String updatedInviteCode = "UPDATED";
        Set<Participant> participantSet = new HashSet<>(participants);
        Event updatedEvent = new Event(
            event.getInviteCode(),
            event.getName(),
            event.getDateTime(),
            participantSet,
            new HashSet<>()
        );

        Mockito.when(serverUtils.longPollDebts(event.getInviteCode())).thenReturn(updatedInviteCode);
        Mockito.when(serverUtils.getEventById(event.getInviteCode())).thenReturn(updatedEvent);

        controller.startLongPolling();
        robot.sleep(2000);

        verify(mainCtrl, times(2)).showOpenDebts(any(Event.class));
    }

    @Test
    void backEsc(FxRobot robot) {
        robot.interact(() -> {
            KeyEvent event = new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.ESCAPE, false, false, false, false);
            controller.keyPressed(event);
        });
    }
}
