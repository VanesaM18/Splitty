package client.scenes;

import client.utils.EmailManager;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Event;
import commons.Debt;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.*;

import java.time.LocalDate;
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
     * @param server an instance of ServerUtils
     * @param emailManager an instance of EmailManager
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
        List<Debt> list = Event.finalCalculation(e);

        for (Debt debt : list) {
            if (debt.getDebtor().getId() != debt.getCreditor().getId()) {
                HBox hbox = new HBox();
                hbox.setId("hbox_" + debt.getId());
                TitledPane titledPane = createDebtTitledPane(debt);
                hbox.getChildren().add(titledPane);

                Region region = new Region();
                HBox.setHgrow(region, Priority.ALWAYS);

                Button markReceivedButton = createMarkReceivedButton(debt);
                HBox.setMargin(markReceivedButton, new Insets(0, 0, 0, 15));
                hbox.getChildren().addAll(region, markReceivedButton);

                debtContainer.getChildren().add(hbox);
            }
        }
    }

    private TitledPane createDebtTitledPane(Debt debt) {
        TitledPane titledPane = new TitledPane();
        titledPane.setExpanded(false);
        titledPane.setMaxWidth(Double.MAX_VALUE);
        titledPane.setText(debt.getDebtor().getName() + " gives "
            + debt.getAmount() + " to " + debt.getCreditor().getName());

        VBox content = new VBox();
        Label debtLabel = new Label(debt.getCreditor().getIban().isEmpty()
            ? "Bank information not available" :
            "Bank information available, transfer to:\nAccount holder: "
                + debt.getCreditor().getName() +
                "\nIBAN: " + debt.getCreditor().getIban() +
                "\nBIC: " + debt.getCreditor().getBic());
        content.getChildren().add(debtLabel);

        Pane spacer = new Pane();
        spacer.setMinHeight(10);

        content.getChildren().add(spacer);

        HBox sendReminderContent = createSendReminderContent(debt);
        content.getChildren().add(sendReminderContent);

        titledPane.setContent(content);

        return titledPane;
    }

    private Button createMarkReceivedButton(Debt debt) {
        Button button = new Button("Mark Received");
        button.setOnAction(event -> {

//            server.deleteDebt2(e, debt);
            server.removeExpensesDebts(e, debt);
//            server.deleteDebts(debt, e);
            server.markDebtAsReceived(e.getInviteCode());
            debtContainer.getChildren().removeIf(node ->
                node instanceof HBox && node.getId().equals("hbox_" + debt.getId()));
        });
        return button;
    }

    private HBox createSendReminderContent(Debt debt) {
        HBox hbox = new HBox(10);
        Label actionLabel = new Label("Email action: ");
        Button sendEmailButton = new Button("Send Reminder");
        StackPane buttonWrapper = new StackPane(sendEmailButton);

        setupSendEmailButton(sendEmailButton, buttonWrapper, debt);

        hbox.getChildren().addAll(actionLabel, buttonWrapper);
        return hbox;
    }

    private void setupSendEmailButton(Button button, StackPane buttonWrapper, Debt debt) {
        Tooltip tooltip = new Tooltip();
        tooltip.setShowDelay(javafx.util.Duration.ZERO);
        tooltip.setHideDelay(javafx.util.Duration.ZERO);

        if (!emailManager.areCredentialsValid()) {
            tooltip.setText("Email credentials invalid");
            button.setDisable(true);
        } else if (debt.getDebtor().getEmail().isEmpty()) {
            tooltip.setText("Debtor email missing");
            button.setDisable(true);
        } else {
            tooltip.setText("Send payment reminder through email to " + debt.getDebtor().getName());
            button.setDisable(false);
            button.setOnAction(event -> {
                button.setDisable(true);
                button.setText("Sending...");

                new Thread(() -> {
                    emailManager.sendEmail(debt.getDebtor().getEmail(),
                        "Payment reminder for " + e.getName(),
                        "You owe " + debt.getAmount() + " to " + debt.getCreditor().getName());

                    Platform.runLater(() -> {
                        button.setText("Send Reminder");
                        button.setDisable(false);
                    });
                }).start();
            });
        }

        Tooltip.install(buttonWrapper, tooltip);
        Tooltip.install(button, tooltip);

        button.styleProperty().bind(Bindings.when(button.disabledProperty())
            .then("-fx-background-color: lightgrey; -fx-text-fill: darkgrey;")
            .otherwise("-fx-background-color: lightblue; -fx-text-fill: black;"));
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

