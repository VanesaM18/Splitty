package client.scenes;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import client.utils.ServerUtils;
import commons.Event;
import commons.Expense;
import commons.Monetary;
import commons.Participant;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

@ExtendWith(ApplicationExtension.class)
class ExpenseCtrlTest {

    Event event;

    Pane pane;
    ExpenseCtrl controller;
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
        event.setExpenses(Set.of(e1, e2));
        serverUtils = Mockito.mock(ServerUtils.class);
        mainCtrl = Mockito.mock(MainCtrl.class);

        // We need to load the fxml file in this complicated manner because we need to
        // give it
        // access to an injector.
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/client/scenes/Expense.fxml"));
        Locale locale = Locale.of("en", "EN");
        loader.setResources(ResourceBundle.getBundle("bundles.Splitty", locale));
        loader.setControllerFactory(parameter -> new ExpenseCtrl(serverUtils, mainCtrl));

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
    void testOk(FxRobot robot) throws InterruptedException, ExecutionException {
        TextField description = robot.lookup("#description").queryAs(TextField.class);
        TextField amount = robot.lookup("#amount").queryAs(TextField.class);
        DatePicker date = robot.lookup("#date").queryAs(DatePicker.class);
        ComboBox<Participant> receiver = robot.lookup("#receiver").queryComboBox();
        ListView<Participant> selectParticipant = robot.lookup("#selectParticipant").queryListView();
        Set<CheckBox> checkBoxes = robot.from(selectParticipant).lookup((Node node) -> (node instanceof CheckBox))
                .queryAllAs(CheckBox.class);

        robot.clickOn(description);
        robot.type(KeyCode.N, KeyCode.A, KeyCode.M, KeyCode.E);

        robot.clickOn(amount);
        robot.type(KeyCode.DIGIT1, KeyCode.DIGIT0);

        Participant creator = receiver.getItems().getFirst();
        LocalDate now = LocalDate.now();
        robot.interact(() -> {
            date.setValue(now);

            for (CheckBox cb : checkBoxes) {
                cb.setSelected(true);
            }

            receiver.setValue(creator);
        });

        robot.clickOn("Ok");

        Expense expectedExpense = new Expense(event, "name", creator, new Monetary(1000), now, event.getParticipants());
        expectedExpense.setTags(new HashSet<>());
        // NOTE: Use Mockito.eq since we cannot get the same instance of monetary, so we
        // must check for equality, not them being the same instance.
        // Mockito.verify(serverUtils, Mockito.times(1)).addExpense(Mockito.eq(expectedExpense));
    }

    @Test
    void abortButtonGoesBack(FxRobot robot) {
        robot.clickOn("Abort");

        Mockito.verify(mainCtrl, Mockito.times(1)).showOverviewEvent(null);
    }

    @Test
    void pressingEscapeGoesBack(FxRobot robot) {
        // This is normally set in the mainCtrl.
        // Thus, we need to do it manually here.
        pane.setOnKeyPressed(controller::keyPressed);

        robot.type(KeyCode.ESCAPE);

        Mockito.verify(mainCtrl, Mockito.times(1)).showOverviewEvent(null);
    }
}
