package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Event;
import commons.ExpenseType;
import javafx.fxml.FXML;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.*;
import javafx.stage.Modality;


public class AddEditTagsCtrl {
    private final MainCtrl mainCtrl;
    private final ServerUtils server;
    private Event event;
    private ExpenseType expenseType;
    @FXML
    private Label title;
    @FXML
    private TextField name;
    @FXML
    private ColorPicker color;

    /**
     * Construct an Add / Edit tags ctrl.
     *
     * @param server   The server utilities (gets injected)
     * @param mainCtrl The main controller (gets injected)
     */
    @Inject
    public AddEditTagsCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    /**
     * Sets the event where to add / edit tags.
     *
     * @param ev the event
     */
    public void setEvent(Event ev) {
        this.event = ev;
    }

    /**
     * Functionality of the OK button.
     */
    public void ok() {
        if(expenseType == null) add();
        else update();
    }

    private void add() {
        if(!validInput()) return;
        ExpenseType newTag = new ExpenseType(name.getText(), color.getValue().toString(), event);
        event.addType(newTag);
        server.addExpenseType(newTag);
        clearFields();
        mainCtrl.showExpenseTypes(event);
    }
    private void update() {

    }

    private boolean validInput() {
        if(name.getText().equals("")) {
            alert("Name cannot be empty.");
            return false;
        }
        if (color.getValue().equals(javafx.scene.paint.Color.WHITE)) {
            alert("Background color cannot be white.");
            return false;
        }
        String tagName = name.getText();
        for (ExpenseType tag : event.getTags()) {
            if(tag.getName().equals(tagName)) {
                alert("Expense with the same name already exists.");
                return false;
            }
        }
        return true;
    }

    /**
     * Functionality of the abort button.
     */
    public void abort() {
        clearFields();
        mainCtrl.showExpenseTypes(event);
    }

    /**
     * Clears the name and the color field.
     */
    private void clearFields() {
        name.setText("");
        color.setValue(javafx.scene.paint.Color.WHITE);
    }

    /**
     * Sets the title label of the screen.
     * @param title new title to the screen.
     */
    public void setTitle(String title) {
        this.title.setText(title);
    }
    private void alert(String content) {
        var alert = new Alert(Alert.AlertType.ERROR);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
