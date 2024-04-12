package client.scenes;

import client.utils.ServerUtils;

import com.google.inject.Inject;

import commons.Admin;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;

import java.util.Objects;

public class LoginCtrl {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML
    private TextField username;

    @FXML
    private TextField password;

    /**
     * Controller responsible for handling the login view functionality.
     * 
     * @param server An instance of ServerUtils for server-related operations.
     * @param mainCtrl An instance of MainCtrl for coordinating with the main controller.
     */
    @Inject
    public LoginCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    /**
     * Tries to log in with the credentials provided by the user in the UI
     */
    public void logIn() {
        Admin admin = getAdmin();

        String result = server.loginAdmin(admin);
        if (!Objects.equals(result, "Login successfully")) {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(result);
            alert.showAndWait();
            return;
        }
        // login successful redirect to where needed
        ServerUtils.adminAuth(admin);
        clearFields();
        mainCtrl.showManagementOverview();
    }

    /**
     * Create an Admin instance with the credentials provided by the user
     */
    private Admin getAdmin() {
        return new Admin(username.getText(), password.getText(), "");
    }

    /**
     * Clears the fields
     */
    public void clearFields() {
        username.clear();
        password.clear();
    }

    /**
     * Goes back to the starting page.
     */
    public void back() {
        mainCtrl.getSceneManager().goBack();
    }

    /**
     * Event handler for pressing a key.
     *
     * @param e the key that is pressed
     */
    public void keyPressed(KeyEvent e) {
        switch (e.getCode()) {
            case ENTER:
                logIn();
                break;
            case ESCAPE:
                back();
                break;
            default:
                break;
        }
    }
}
