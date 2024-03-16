package client.scenes;

import com.google.inject.Inject;
import commons.Monetary;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

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
        try {
            URL url = new URL("http://localhost:8080/debts/total"); // Adjust the URL as per your server configuration
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            String totalDebt = response.toString();
            totalDebtLabel.setText(totalDebt);
            createClearDebtButton();

            connection.disconnect();
        } catch (Exception e) {
            showErrorDialog("Failed to retrieve total debt. Please try again later.");
            e.printStackTrace();
        }

    }
    private void createClearDebtButton() {
        clearDebtButton = new Button("Clear Total Debt");
        clearDebtButton.setLayoutX(200); // Adjust the layout coordinates as needed
        clearDebtButton.setLayoutY(200); // Adjust the layout coordinates as needed
        clearDebtButton.setOnAction(event -> clearTotalDebt());
        anchorPane.getChildren().add(clearDebtButton);
    }

    @FXML
    private void clearTotalDebt() {
        anchorPane.getChildren().remove(totalDebtLabel);
        anchorPane.getChildren().remove(clearDebtButton);
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

