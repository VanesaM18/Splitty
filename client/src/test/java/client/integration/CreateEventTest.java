package client.integration;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.JavaFXInterceptorUtils;
import org.testfx.framework.junit5.Start;
import org.testfx.framework.junit5.TestFx;
import org.testfx.util.WaitForAsyncUtils;

import com.google.common.base.Objects;

import client.InitializationData;
import client.scenes.MainCtrl;
import client.utils.SceneEnum;
import client.utils.SceneManager;
import client.utils.TestClient;
import commons.Event;
import commons.ExpenseType;
import commons.Participant;
import javafx.application.Platform;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

@ExtendWith(ApplicationExtension.class)
public class CreateEventTest extends TestClient {
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
    void testCreateEvent(FxRobot robot) throws TimeoutException {
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

        Mockito.when(serverUtils.addEvent(Mockito.any())).thenReturn(event);

        TextField eventField = robot.lookup("#createEventField").queryAs(TextField.class);
        robot.interact(() -> {
            robot.clickOn(eventField);
            robot.type(KeyCode.H, KeyCode.E, KeyCode.L, KeyCode.L, KeyCode.O);
            robot.clickOn("Create");
        });
        System.out.println(javafx.application.Platform.isFxApplicationThread() ? "App" : "Test");
        Mockito.verify(serverUtils, Mockito.times(1))
                .addEvent(Mockito.argThat(e -> Objects.equal(e.getName(), event.getName()) &&
                        Objects.equal(e.getParticipants(), Set.of()) &&
                        Objects.equal(e.getTags(), Set.of())));
        ArgumentCaptor<ExpenseType> args = ArgumentCaptor.forClass(ExpenseType.class);
        Mockito.verify(serverUtils, Mockito.times(3))
                .addExpenseType(args.capture());
        var values = args.getAllValues();
        Assertions.assertTrue(values.contains(food));
        Assertions.assertTrue(values.contains(fees));
        Assertions.assertTrue(values.contains(travel));
    }
}
