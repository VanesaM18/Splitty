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
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.image.Image;

import java.util.List;

import javafx.geometry.Insets;

import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;


public class OpenDebtsCtrl {
    private final MainCtrl mainCtrl;
    private final ServerUtils server;
    private final EmailManager emailManager;
    public VBox vboxContainer;
    public HBox hboxContainer;

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
        List<Debt> list = server.calculateDebts(e);

        for (Debt debt : list) {
            if (debt.getDebtor().getId() != debt.getCreditor().getId()) {
                HBox hbox = new HBox();
                hbox.setId("hbox_" + debt.getId());
                TitledPane titledPane = createDebtTitledPane(debt);
                HBox.setHgrow(titledPane, Priority.ALWAYS);

                Region region = new Region();
                HBox.setHgrow(region, Priority.ALWAYS);

                Button markReceivedButton = createMarkReceivedButton(debt);

                HBox.setMargin(markReceivedButton, new Insets(0, 0, 0, 30));
                hbox.getChildren().addAll(titledPane, region, markReceivedButton);
                hbox.prefWidthProperty().bind(hboxContainer.widthProperty());
                HBox.setHgrow(hbox, Priority.ALWAYS);
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
        HBox graphicContainer = sideImagesDebt(debt);

        graphicContainer.setAlignment(Pos.CENTER_RIGHT);
        titledPane.setGraphic(graphicContainer);
        titledPane.setContentDisplay(ContentDisplay.RIGHT);

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

    private static HBox sideImagesDebt(Debt debt) {
        HBox graphicContainer = new HBox();
        ImageView imageViewBank;
        ImageView imageViewEnvelope;
        String tooltipSetE;
        String tooltipSetB;
        Image imageBankG = new Image("assets/bank-icon-grey.png");
        Image imageBankB = new Image("assets/bank-icon.png");
        ImageView imageViewBankG = settingImage(imageBankG);
        ImageView imageViewBankB = settingImage(imageBankB);
        if(debt.getCreditor().getIban().isEmpty() || debt.getDebtor().getIban().isEmpty()){
            imageViewBank = imageViewBankG;
            tooltipSetB = "IBAN not set";
        } else{
            imageViewBank = imageViewBankB;
            tooltipSetB = "IBAN set";
        }
        Image imageEnvelopeG = new Image("assets/envelope-icon-grey.png");
        Image imageEnvelopeB = new Image("assets/envelope-icon.png");
        ImageView imageViewEnvelopeG = settingImage(imageEnvelopeG);
        ImageView imageViewEnvelopeB = settingImage(imageEnvelopeB);
        if(debt.getCreditor().getEmail().isEmpty() || debt.getDebtor().getEmail().isEmpty()){
            imageViewEnvelope = imageViewEnvelopeG;
            tooltipSetE = "e-mail not set";

        } else{
            imageViewEnvelope = imageViewEnvelopeB;
            tooltipSetE = "e-mail set";
        }
        HBox hboxE = getSubHBox(tooltipSetE, imageViewEnvelope);
        HBox hboxB = getSubHBox(tooltipSetB, imageViewBank);
        Region spacer = new Region();
        spacer.setPrefWidth(10);
        graphicContainer.getChildren().addAll(spacer, hboxE, hboxB);
        return graphicContainer;
    }

    private static ImageView settingImage(Image imageEnvelopeG) {
        double desiredWidth = 25;
        double desiredHeight = 25;
        ImageView imageViewEnvelopeG = new ImageView(imageEnvelopeG);
        imageViewEnvelopeG.setFitWidth(desiredWidth);
        imageViewEnvelopeG.setFitHeight(desiredHeight);
        return imageViewEnvelopeG;
    }

    private static HBox getSubHBox(String tooltipSetE, ImageView imageViewEnvelope) {
        Tooltip tooltipE = new Tooltip(tooltipSetE);
        HBox hboxE = new HBox();
        hboxE.getChildren().addAll(imageViewEnvelope);
        Tooltip.install(hboxE, tooltipE);
        return hboxE;
    }


    private Button createMarkReceivedButton(Debt debt) {
        Button button = new Button("Mark Received");
        button.setOnAction(event -> {
            server.removeExpensesDebts(e, debt);
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

    /**
        Get the long polling thread
        @return the long polling thread
     **/
    public Thread getLongPollingThread() {
        return longPollingThread;
    }
}

