package client.scenes;

import com.google.inject.Inject;
import commons.Monetary;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import server.api.DebtController;

public class OpenDebtsCtrl {
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private Label totalDebtLabel;
    private final MainCtrl mainCtrl;
    @FXML
    private Button clearDebtButton;
    @Inject
    public OpenDebtsCtrl(MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
    }

    public void initialize() {
        long id = 0;
        Monetary totalDebt = DebtController.calculateTotalDebt(id);
        if (totalDebt != null) {
            totalDebtLabel.setText(totalDebt.toString());
            clearDebtButton = new Button("Clear Total Debt");
            clearDebtButton.setLayoutX(200); // Adjust the layout coordinates as needed
            clearDebtButton.setLayoutY(200); // Adjust the layout coordinates as needed
            clearDebtButton.setOnAction(event -> clearTotalDebt());
            anchorPane.getChildren().add(clearDebtButton);
        } else {
            showErrorDialog("Failed to retrieve total debt. Please try again later.");
        }
    }

    @FXML
    private void clearTotalDebt() {
        anchorPane.getChildren().remove(totalDebtLabel);
        anchorPane.getChildren().remove(clearDebtButton);
    }

    // Method to display an error dialog
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

