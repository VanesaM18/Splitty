package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Event;
import commons.Debt;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.util.List;
import javafx.geometry.Insets;


public class OpenDebtsCtrl {
    private final MainCtrl mainCtrl;
    private final ServerUtils server;
    @FXML
    private VBox debtContainer;

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

    /**
     * Initializes the open debt page by using the calculatePayments
     * and paymentsToDebt methods in event.
     * The information from the debt is then put
     * in an accordion with a mark received button
     * generated next to it, that deletes the amount.
     *
     * @param e the current event
     */
    public void initialize(Event e){
        debtContainer.getChildren().clear();
        if (e == null) {
            return;
        }

        List<Debt> list = Event.paymentsToDebt(e);

        for (Debt debt : list) {
            if(debt.getDebtor().getId() != debt.getCreditor().getId()) {
                HBox hbox = new HBox();
                String hboxId = "hbox_" + debt.getId();
                hbox.setId(hboxId);
                TitledPane titledPane = new TitledPane();
                titledPane.setExpanded(false);
                titledPane.setMaxWidth(Double.MAX_VALUE);
                titledPane.setText(debt.getDebtor().getName() + " gives "
                        + debt.getAmount().getCurrency().getSymbol()
                        + debt.getAmount().toString()
                        + " to " + debt.getCreditor().getName());

                VBox content = new VBox();
                Label debtLabel = new Label("Bank information available, transfer the money to:\n"
                        + "Account holder: " + debt.getCreditor().getName()
                        + "\nIBAN: " + debt.getCreditor().getIban()
                        + "\nBIC: " + debt.getCreditor().getBic());
                content.getChildren().add(debtLabel);
                titledPane.setContent(content);

                Button markReceivedButton = new Button("Mark Received");
                markReceivedButton.setOnAction(event -> {
                    Node parentHBox = markReceivedButton.getParent();
                    if (parentHBox instanceof HBox) {
                        debtContainer.getChildren().remove(parentHBox);
                    }
                    server.deleteDebts(debt, e);
                });

                hbox.getChildren().addAll(titledPane, markReceivedButton);
                HBox.setMargin(titledPane, new Insets(10, 0, 0, 0));
                HBox.setMargin(markReceivedButton, new Insets(10, 0, 0, 10));
                debtContainer.getChildren().add(hbox);
            }
        }
    }

    /**
     * goes back to the overview event
     */
    public void back() {
        mainCtrl.refreshData();
        mainCtrl.showOverviewEvent(null);
    }

}

