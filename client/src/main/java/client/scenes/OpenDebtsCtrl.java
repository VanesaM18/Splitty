package client.scenes;

import client.utils.EmailManager;
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
    private final EmailManager emailManager;
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
    public OpenDebtsCtrl(MainCtrl mainCtrl, ServerUtils server, EmailManager emailManager) {
        this.mainCtrl = mainCtrl;
        this.server = server;
        this.emailManager = emailManager;
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
                        + debt.getAmount().getCurrency().getSymbol()
                        + debt.getAmount().toString()
                        + " to " + debt.getCreditor().getName());

                VBox content = new VBox();
                Label debtLabel = null;
                if (debt.getCreditor().getIban().isEmpty()) {
                    debtLabel = new Label("Bank information not available");
                } else {
                    debtLabel = new Label("Bank information available, transfer the money to:\n"
                        + "Account holder: " + debt.getCreditor().getName()
                        + "\nIBAN: " + debt.getCreditor().getIban()
                        + "\nBIC: " + debt.getCreditor().getBic());
                }
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
                });

                HBox sendReminder = new HBox();
                if (this.emailManager.areCredentialsValid()) {
                    if (!debt.getDebtor().getEmail().isEmpty()) {
                        sendReminder.getChildren().add(new Label("Email configured: "));
                        Button sendEmail = new Button(("send reminder"));
                        sendEmail.setOnAction(event -> {
                            this.emailManager.sendEmail(debt.getDebtor().getEmail(),
                                "Payment reminder for " + e.getName(),
                                "You own " + debt.getDebtor().getName() + " "
                                    +  debt.getAmount().getCurrency().getSymbol()
                                    + debt.getAmount().toString() + " to " +
                                    debt.getDebtor().getName() + "\n You can pay him back using " +
                                    "the details from the Splitty event and mark as received!");
                        });
                        sendReminder.getChildren().add(sendEmail);
                    } else {
                        sendReminder.getChildren().add(new Label("Email not configured"));
                    }
                }
                content.getChildren().add(sendReminder);

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
        mainCtrl.setIsInOpenDebt(false);
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
                            String result = server.longPollDebts(e.getInviteCode());
                            Thread.sleep(100);
                            e = server.getEventById(e.getInviteCode());
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

    /**
     * Retuns the event
     * @return the event
     */
    public Event getEvent() {
        if (e != null) {
            e = server.getEventById(e.getInviteCode());
        }
        return e;
    }
}

