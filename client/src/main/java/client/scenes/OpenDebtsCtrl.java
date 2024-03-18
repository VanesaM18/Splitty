package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Event;
import commons.Debt;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.EventHandler;
import java.io.IOException;
import java.util.List;
import javafx.geometry.Insets;


public class OpenDebtsCtrl {
    private final MainCtrl mainCtrl;
    private final ServerUtils server;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private VBox debtContainer;
    @FXML
    private Button markReceived;
    /**
     * constructs open debts
     *
     * @param mainCtrl an instance of MainCtrl
     * @param server
     */
    @Inject
    public OpenDebtsCtrl(MainCtrl mainCtrl, ServerUtils server) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    public void initialize(Event e){
        debtContainer.getChildren().clear();
        if (e == null) {
            return;
        }

        List<Debt> list = Event.paymentsToDebt(e);

        for (Debt debt : list) {
            // Create an HBox to hold the label and button horizontally
            HBox hbox = new HBox();
            String hboxId = "hbox_" + debt.getId(); // Assuming you have a method to get a unique ID for each debt
            hbox.setId(hboxId);
            // Create a label to display debt information
            Label debtLabel = new Label("Debtor: " + debt.getDebtor().getName() + ", Amount: $" + debt.getAmount().getInternalValue() + " to " + debt.getCreditor().getName());

            // Create a settle button for each debt
            Button settleButton = new Button("Settle");
            settleButton.setOnAction(event -> {
                // Retrieve the parent HBox
//                server.removeDebt(debt.getId());
                Node parentHBox = settleButton.getParent();

                // Check if the parent is an HBox
                if (parentHBox instanceof HBox) {
                    // Remove the HBox from the debtContainer
                    debtContainer.getChildren().remove(parentHBox);
                }
            });

            // Add the label and settle button to the HBox
            hbox.getChildren().addAll(debtLabel, settleButton);

            // Add margin to the button
            HBox.setMargin(settleButton, new Insets(0, 0, 0, 10)); // Adjust the insets as needed

            // Add the HBox to the debtContainer
            debtContainer.getChildren().add(hbox);
        }
    }


//    @FXML
//    private void clearTotalDebt() {
//        anchorPane.getChildren().remove(debtAccordion);
//        anchorPane.getChildren().remove(markReceived);
//    }

    /**
     * goes back to the overview event
     */
    public void back() {
        mainCtrl.showOverviewEvent(null);
    }

}

