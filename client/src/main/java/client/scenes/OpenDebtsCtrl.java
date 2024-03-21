package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Event;
import commons.Debt;
import javafx.application.Platform;
import javafx.concurrent.Task;
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
    private Event e;
    private Thread longPollingThread;

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
        this.e = e;
        if (e == null) {
            return;
        }
        this.e = server.getEventById(this.e.getInviteCode());
        System.out.println(e.getExpenses().size());
        if (this.e == null) {
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
                        + debt.getAmount().getInternalValue()
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
                    server.markDebtAsReceived(this.e.getInviteCode());
                    this.e = server.getEventById(this.e.getInviteCode());
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
        stopLongPolling();
        mainCtrl.refreshData();
        mainCtrl.showOverviewEvent(null);
    }

    /**
     * Starts long pooling for updates for open debts
     */
    public void startLongPolling() {
        Task<Void> longPollingTask = new Task<>() {
            @Override
            protected Void call() {
                while (true) {
                    try {
                        if (e != null) {
                            String result = server.longPoolDebts(e.getInviteCode());
                            Platform.runLater(() -> {
                                mainCtrl.showOpenDebts(e);
                            });
                        }
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
                return null;
            }
        };
        longPollingThread = new Thread(longPollingTask);
        longPollingThread.setDaemon(true);
        longPollingThread.start();
    }

    /**
     * Interrupts the request when user leaved the view
     */
    public void stopLongPolling() {
        if (longPollingThread != null && longPollingThread.isAlive()) {
            longPollingThread.interrupt();
        }
    }
}

