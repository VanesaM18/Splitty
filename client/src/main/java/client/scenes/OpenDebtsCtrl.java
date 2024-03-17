package client.scenes;

import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

//import java.io.BufferedReader;
//import java.io.InputStreamReader;
//import java.net.HttpURLConnection;
//import java.net.URL;

public class OpenDebtsCtrl {
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private Accordion debtAccordion;
    private final MainCtrl mainCtrl;
    @FXML
    private Button markReceived;

    /**
     * constructs open debts
     * @param mainCtrl an instance of MainCtrl
     */
    @Inject
    public OpenDebtsCtrl(MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
    }


    @FXML
    private void clearTotalDebt() {
        anchorPane.getChildren().remove(debtAccordion);
        anchorPane.getChildren().remove(markReceived);
    }

    private void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * goes back to the overview event
     */
    public void back() {
        mainCtrl.showOverviewEvent(null);
    }
}

