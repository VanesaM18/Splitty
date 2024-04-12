package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Event;
import commons.ExpenseType;
import javafx.fxml.FXML;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.stage.Modality;


public class AddEditTagsCtrl {
    private final MainCtrl mainCtrl;
    private final ServerUtils server;
    private Event event;
    private ExpenseType expenseType;
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
        if(!validInput()) return;
        expenseType.setName(name.getText());
        String colorString = color.getValue().toString();
        if(!colorString.startsWith("#")) {
            colorString = colorString.substring(2);
            colorString = "#" + colorString;
        }
        expenseType.setColor(colorString);
        expenseType.setEvent(event);
        server.updateTag(expenseType);
        clearFields();
        mainCtrl.showExpenseTypes(event);
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
            if(tag.getName().equals(tagName) && !tag.equals(expenseType)) {
                alert("Expense with the same name already exists.");
                return false;
            }
            if(tag.getColor().equals(color.getValue().toString()) && !tag.equals(expenseType)) {
                alert("Expense with the same background color already exists.");
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
        expenseType = null;
        name.setText("");
        color.setValue(javafx.scene.paint.Color.WHITE);
    }

    private void alert(String content) {
        var alert = new Alert(Alert.AlertType.ERROR);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Sets the expense type of event to be edited.
     * @param expenseType tag to be updated.
     */
    public void setExpenseType(ExpenseType expenseType) {
        this.expenseType = expenseType;
        name.setText(expenseType.getName());
        color.setValue(Color.web(expenseType.getColor()));
    }
    /**
     * Event handler for pressing a key.
     *
     * @param e the key that is pressed
     */
    public void keyPressed(KeyEvent e) {
        switch (e.getCode()) {
            case ENTER:
                ok();
                break;
            case ESCAPE:
                abort();
                break;
            default:
                break;
        }
    }
}
