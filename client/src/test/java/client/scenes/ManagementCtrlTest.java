package client.scenes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.testfx.assertions.api.Assertions.assertThat;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.testfx.api.FxRobot;
import org.testfx.assertions.api.Assertions;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;
import com.google.common.collect.Table;
import client.utils.EmailManager;
import client.utils.SceneManager;
import client.utils.ServerUtils;
import commons.Event;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.DialogPane;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn.SortType;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

@ExtendWith(ApplicationExtension.class)
class ManagementCtrlTest {

    Event event = new Event("code", "name", LocalDateTime.now(), new HashSet<>(), new HashSet<>());

    Pane pane;
    ManagementCtrl controller;
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

        Mockito.when(serverUtils.getAllEvents()).thenReturn(Optional.of(List.of(event)));

        // We need to load the fxml file in this complicated manner because we need to give it
        // access to an injector.
        FXMLLoader loader =
                new FXMLLoader(getClass().getResource("/client/scenes/Management.fxml"));
        Locale locale = Locale.of("en", "EN");
        loader.setResources(ResourceBundle.getBundle("bundles.Splitty", locale));
        loader.setControllerFactory(parameter -> new ManagementCtrl(serverUtils, mainCtrl));

        // Actually load the file, and also save the controller.
        pane = (Pane) loader.load();
        controller = loader.getController();

        controller.refresh();

        stage.setScene(new Scene(pane));
        stage.show();
    }

    @BeforeEach
    void resetMock() {
        Mockito.reset(serverUtils, mainCtrl);
    }

    @Test
    void backButtonGoesBack(FxRobot robot) {
        SceneManager manager = Mockito.mock();
        Mockito.when(mainCtrl.getSceneManager()).thenReturn(manager);

        robot.clickOn("Home");

        Mockito.verify(manager, Mockito.times(1)).popScene();
        Mockito.verify(mainCtrl, Mockito.times(1)).setIsInManagement(false);
    }

    @Test
    void deleteShowsConfirmation(FxRobot robot) {
        robot.clickOn(event.getName(), MouseButton.SECONDARY);
        TableView<Event> table = robot.lookup("#eventsTable").queryTableView();
        MenuItem item = table.getContextMenu().getItems().get(2);

        Platform.runLater(() -> {
            item.fire();
        });

        WaitForAsyncUtils.waitForFxEvents();

        DialogPane dialogPane = robot.lookup(".dialog-pane").queryAs(DialogPane.class);
        Assertions.assertThat(dialogPane).isVisible();

        // Close the dialog.
        robot.clickOn("OK");
    }

    @Test
    void backEsc(FxRobot robot) {
        SceneManager manager = Mockito.mock();
        Mockito.when(mainCtrl.getSceneManager()).thenReturn(manager);

        robot.interact(() -> {
            KeyEvent event = new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.ESCAPE, false, false, false, false);
            controller.keyPressed(event);
        });

        Mockito.verify(manager, Mockito.times(1)).popScene();
        Mockito.verify(mainCtrl, Mockito.times(1)).setIsInManagement(false);
    }

    @Test
    void clipboardDoesntThrow(FxRobot robot) {
        robot.clickOn(event.getName(), MouseButton.SECONDARY);
        TableView<Event> table = robot.lookup("#eventsTable").queryTableView();
        MenuItem item = table.getContextMenu().getItems().get(0);

        robot.interact(() -> {
            item.fire();
        });
    }

    @Test
    void defaultSorting(FxRobot robot) {
        TableView<Event> table = robot.lookup("#eventsTable").queryTableView();
        // The creation date column is the third column
        TableColumn<Event, String> creationDateColumn = (TableColumn<Event, String>) table.getColumns().get(2);

        assertEquals(SortType.DESCENDING, creationDateColumn.getSortType());
        assertThat(table.getSortOrder()).contains(creationDateColumn);
    }
}
