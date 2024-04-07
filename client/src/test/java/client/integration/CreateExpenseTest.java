package client.integration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import client.InitializationData;
import client.scenes.MainCtrl;
import client.utils.SceneEnum;
import client.utils.SceneManager;
import client.utils.TestClient;
import commons.Event;
import commons.Expense;
import commons.ExpenseType;
import commons.Monetary;
import commons.Participant;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

@ExtendWith(ApplicationExtension.class)
public class CreateExpenseTest extends TestClient {
    /**
     * Test application startup
     * TestFx is needed to run it on the JavaFX thread
     */
    private MainCtrl mainCtrl;
    private SceneManager sm;

    @Override
    @Start
    public void start(Stage stage) {
        super.start(stage);
        mainCtrl = injector.getInstance(MainCtrl.class);
        sm = injector.getInstance(SceneManager.class);

    }

    @Test
    void testCreateEvent(FxRobot robot) throws TimeoutException, ExecutionException, InterruptedException {
        sm.pushScene(SceneEnum.START);
        // Run in JavaFx thread
        robot.interact(() -> mainCtrl.initialize(stage,
                injector.getInstance(InitializationData.class),
                injector.getInstance(SceneManager.class)));

        Event event = new Event(
                "ABC123",
                "hello",
                LocalDateTime.of(2024, 4, 1, 0, 0),
                new HashSet<Participant>(Set.of(
                        new Participant(
                                "John Doe", "john.doe@example.com", "IBAN123456", "BIC456"),
                        new Participant(
                                "Jane Doe", "jane.doe@example.com", "IBAN123456", "BIC456"))),
                new HashSet<ExpenseType>(Set.of()));

        ExpenseType food = new ExpenseType("food", "#5bf562", event);
        ExpenseType fees = new ExpenseType("entrance fees", "#5ba0f5", event);
        ExpenseType travel = new ExpenseType("travel", "#f7596c", event);

        event.setExpenses(Set.of());
        event.addType(food);
        event.addType(fees);
        event.addType(travel);

        Mockito.when(serverUtils.getEventById(event.getInviteCode())).thenReturn(event);

        robot.interact(() -> mainCtrl.showOverviewEvent(event));
        robot.interact(() -> robot.clickOn("#addExpenseButton"));
        robot.interact(() -> {
            // Select description
            robot.clickOn("#description");
            robot.type(KeyCode.N, KeyCode.I, KeyCode.C, KeyCode.E);
            // Select date
            robot.clickOn("#date");
            // "1/1/2024"
            robot.type(KeyCode.DIGIT1,
                    KeyCode.SLASH,
                    KeyCode.DIGIT1,
                    KeyCode.SLASH,
                    KeyCode.DIGIT2, KeyCode.DIGIT0, KeyCode.DIGIT2, KeyCode.DIGIT4);
            // Select amount
            robot.clickOn("#amount");
            // "1.23"
            robot.type(KeyCode.DIGIT1, KeyCode.PERIOD, KeyCode.DIGIT2, KeyCode.DIGIT3);

            // Select receiver
            var comboBox = robot.lookup("#receiver").queryComboBox();
            comboBox.getSelectionModel().selectFirst();

            // Add an event type
            var expenseTypes = robot.lookup("#types").queryComboBox();
            expenseTypes.getSelectionModel().selectFirst();

            // Select participants (just select them all)
            Set<CheckBox> checkBoxes = robot.lookup("#selectParticipant")
                    .lookup((Node x) -> x instanceof CheckBox).queryAllAs(CheckBox.class);
            for (var cb : checkBoxes) {
                robot.clickOn(cb);
            }

            robot.clickOn("Ok");
        });
        ArgumentCaptor<Expense> args = ArgumentCaptor.forClass(Expense.class);
        Mockito.verify(serverUtils, Mockito.times(1)).addExpense(args.capture());
        Expense e = args.getValue();
        Assertions.assertEquals("nice", e.getName());
        Assertions.assertEquals(LocalDate.of(2024, 1, 1), e.getDate());
        Assertions.assertEquals(new Monetary(123, "EUR"), e.getAmount());
        Assertions.assertTrue(event.getTags().containsAll(e.getTags()));
        Assertions.assertTrue(event.getParticipants().contains(e.getCreator()));
        Assertions.assertEquals(event.getParticipants(), e.getSplitBetween());
    }
}
