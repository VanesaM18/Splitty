package client.scenes;

import client.utils.EmailManager;
import client.utils.ResourceManager;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Event;
import commons.Debt;
import commons.Participant;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.image.Image;

import java.util.List;

import javafx.geometry.Insets;

import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;


public class OpenDebtsCtrl {
    private final MainCtrl mainCtrl;
    private final ServerUtils server;
    private final EmailManager emailManager;
    private Stage primaryStage;
    @FXML
    private HBox hboxContainer;

    @FXML
    private VBox debtContainer;
    private Event e;
    private Thread longPollingThread;
    private static ResourceManager resourceManager;

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
        this.resourceManager = new ResourceManager(mainCtrl);
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
        ResourceManager resourceManager = new ResourceManager(mainCtrl);
        TitledPane titledPane = new TitledPane();
        titledPane.setExpanded(false);
        titledPane.setMaxWidth(Double.MAX_VALUE);
        titledPane.setText(debt.getDebtor().getName()
                + " " + resourceManager.getStringForKey("content_gives")+ " "
                + debt.getAmount() + " " + resourceManager.getStringForKey("content_to")
                + " " + debt.getCreditor().getName());
        HBox graphicContainer = sideImagesDebt(debt);

        graphicContainer.setAlignment(Pos.CENTER_RIGHT);
        titledPane.setGraphic(graphicContainer);
        titledPane.setContentDisplay(ContentDisplay.RIGHT);

        VBox content = new VBox();
        String bankInfo = resourceManager.getStringForKey("content_bank_info");
        String bankInfoOk = resourceManager.getStringForKey("content_bank_info_ok");
        String accountHolder = resourceManager.getStringForKey("content_account_holder");
        Label debtLabel = new Label(debt.getCreditor().getIban().isEmpty()
            ? bankInfo :
            bankInfoOk + "\n" + accountHolder
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

    private HBox sideImagesDebt(Debt debt) {
        HBox graphicContainer = new HBox();

        Image imageBankG = new Image("assets/bank-icon-grey.png");
        Image imageBankB = new Image("assets/bank-icon.png");
        ImageView imageViewBankG = settingImage(imageBankG);
        ImageView imageViewBankB = settingImage(imageBankB);
        Image imageEnvelopeG = new Image("assets/envelope-icon-grey.png");
        Image imageEnvelopeB = new Image("assets/envelope-icon.png");

        ImageView imageViewEnvelopeG = settingImage(imageEnvelopeG);
        ImageView imageViewEnvelopeB = settingImage(imageEnvelopeB);
        hoverOverImage(imageViewBankG);
        hoverOverImage(imageViewEnvelopeG);

        SetImages setImages = getSetImages(debt, imageViewBankG, imageViewBankB,
                imageViewEnvelopeG, imageViewEnvelopeB);
        HBox hboxE = getSubHBox(setImages.tooltipSetE(), setImages.imageViewEnvelope());
        HBox hboxB = getSubHBox(setImages.tooltipSetB(), setImages.imageViewBank());
        hboxB.setPickOnBounds(true);
        hboxE.setPickOnBounds(true);
        if(debt.getCreditor().getIban().isEmpty()){
            hboxB.setOnMouseClicked(event -> {
                openPopUp(debt.getCreditor(), hboxB, imageViewBankG);
            });
        } else {
            hboxB.setOnMouseClicked(event -> {});
        }


        if(debt.getDebtor().getEmail().isEmpty()){
            hboxE.setOnMouseClicked(event -> {
                openPopUp(debt.getDebtor(), hboxE, imageViewBankG);
            });
        } else {
            hboxE.setOnMouseClicked(event -> {});
        }

        Region spacer = new Region();
        spacer.setPrefWidth(12);
        Region spacer2 = new Region();
        spacer2.setPrefWidth(7);
        graphicContainer.getChildren().addAll(spacer, hboxE, spacer2, hboxB);
        return graphicContainer;
    }

    private static SetImages getSetImages(Debt debt, ImageView imageViewBankG,
                                          ImageView imageViewBankB, ImageView imageViewEnvelopeG,
                                          ImageView imageViewEnvelopeB) {
        String tooltipSetE;
        ImageView imageViewBank;
        ImageView imageViewEnvelope;
        String tooltipSetB;
        if(debt.getCreditor().getIban().isEmpty()){
            imageViewBank = imageViewBankG;
            tooltipSetB = resourceManager.getStringForKey("content_set_iban_of") + " "
                    + debt.getCreditor().getName()
                    + ", " + resourceManager.getStringForKey("content_to_receive");
        } else{
            imageViewBank = imageViewBankB;
            tooltipSetB = resourceManager.getStringForKey("content_iban_of") + " "
                    + debt.getCreditor().getName()
                    + " " + resourceManager.getStringForKey("content_is_set");
        }

        if(debt.getDebtor().getEmail().isEmpty()){
            imageViewEnvelope = imageViewEnvelopeG;
            tooltipSetE = resourceManager.getStringForKey("content_set_email_of") + " "
                    + debt.getDebtor().getName()
                    + ", " + resourceManager.getStringForKey("content_to_send_reminder");

        } else{
            imageViewEnvelope = imageViewEnvelopeB;
            tooltipSetE = resourceManager.getStringForKey("content_email_of") + " "
                    + debt.getDebtor().getName()
                    + " " + resourceManager.getStringForKey("content_is_set");
        }

        SetImages setImages = new SetImages(imageViewBank, imageViewEnvelope,
                tooltipSetE, tooltipSetB);
        return setImages;
    }

    private record SetImages(ImageView imageViewBank, ImageView imageViewEnvelope,
                             String tooltipSetE, String tooltipSetB) {
    }

    private static void hoverOverImage(ImageView imageView) {
        ColorAdjust colorAdjust = new ColorAdjust();
        imageView.setEffect(colorAdjust);
        imageView.setOnMouseEntered(e -> {
            colorAdjust.setBrightness(0.4);
        });
        imageView.setOnMouseExited(e -> {
            colorAdjust.setBrightness(0);
        });
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
        tooltipE.setMaxWidth(200);
        tooltipE.setWrapText(true);
        HBox hbox = new HBox();
        hbox.getChildren().addAll(imageViewEnvelope);
        Tooltip.install(hbox, tooltipE);
        return hbox;
    }

    private void openPopUp(Participant participant, HBox hbox, ImageView imageViewBankG) {
        ResourceManager resourceManager = new ResourceManager(mainCtrl);
        Stage popUpStage = new Stage();
        popUpStage.initModality(Modality.APPLICATION_MODAL);
        popUpStage.initOwner(primaryStage);
        String title = resourceManager.getStringForKey("content_email_setup");
        TextField textField = new TextField();

        String enterEmail = resourceManager.getStringForKey("content_enter_email") + " ";
        String enterIban = resourceManager.getStringForKey("content_enter_iban");
        Label label = new Label(enterEmail + participant.getName() + " :");
        label.setAlignment(Pos.CENTER_LEFT);

        Button okButton = buttonE(participant, textField, popUpStage);
        if(hbox.getChildren().contains(imageViewBankG)){
            okButton = buttonB(participant, textField, popUpStage);
            title = resourceManager.getStringForKey("content_iban_setup");
            label.setText(enterIban + participant.getName() + " :");
        }

        Button cancelButton = new Button(resourceManager.getStringForKey("cancel_button_text"));
        cancelButton.setOnAction(event -> popUpStage.close());

        VBox popUpLayout = new VBox(10);
        popUpLayout.setPadding(new Insets(10));
        HBox buttonContainer = new HBox(okButton, cancelButton);
        buttonContainer.setAlignment(Pos.CENTER_RIGHT);
        buttonContainer.setSpacing(10);
        popUpLayout.getChildren().addAll(label, textField, buttonContainer);

        Scene popUpScene = new Scene(popUpLayout, 250, 125);
        popUpStage.setScene(popUpScene);
        popUpStage.setTitle(title);
        popUpStage.show();
    }

    private Button buttonB(Participant participant, TextField textField, Stage popUpStage) {
        Button okButton = new Button("OK");
        okButton.setOnAction(e -> {
            String userInput = textField.getText();
            participant.setIban(userInput);
            if (textField.getText().isEmpty() ||
                    !ParticipantsCtrl.isIbanValid(participant.getIban())) {
                ResourceManager resourceManager = new ResourceManager(mainCtrl);
                String invalidIban = resourceManager.getStringForKey("content_participant_iban");
                alert(invalidIban);
                return;
            }
            server.addParticipant(participant);
            mainCtrl.showOpenDebts(this.e);
            popUpStage.close();
        });
        return okButton;
    }

    private Button buttonE(Participant participant, TextField textField, Stage popUpStage) {
        Button okButton = new Button("OK");
        okButton.setOnAction(e -> {
            String userInput = textField.getText();
            participant.setEmail(userInput);
            if (textField.getText().isEmpty() ||
                    !ParticipantsCtrl.isEmailValid(participant.getEmail())) {
                ResourceManager resourceManager = new ResourceManager(mainCtrl);
                String invalidEmail = resourceManager.getStringForKey("content_participant_email");
                alert(invalidEmail);
                return;
            }
            server.addParticipant(participant);
            mainCtrl.showOpenDebts(this.e);
            popUpStage.close();
        });
        return okButton;
    }


    private Button createMarkReceivedButton(Debt debt) {
        String markrecieved = resourceManager.getStringForKey("content_mark_recieved");
        Button button = new Button(markrecieved);
        button.setOnAction(event -> {
            server.removeExpensesDebts(e, debt);
            server.markDebtAsReceived(e.getInviteCode());
            debtContainer.getChildren().removeIf(node ->
                node instanceof HBox && node.getId().equals("hbox_" + debt.getId()));
        });
        return button;
    }

    private HBox createSendReminderContent(Debt debt) {
        var resourceManager = new  ResourceManager(mainCtrl);
        String emailAction = resourceManager.getStringForKey("content_email_action");
        String sendReminder = resourceManager.getStringForKey("content_send_reminder");
        HBox hbox = new HBox(10);
        String enterIban = " " + resourceManager.getStringForKey("content_enter_iban")  + " ";
        Label actionLabel = new Label(emailAction + enterIban);
        Button sendEmailButton = new Button(sendReminder);
        StackPane buttonWrapper = new StackPane(sendEmailButton);

        setupSendEmailButton(sendEmailButton, buttonWrapper, debt);

        hbox.getChildren().addAll(actionLabel, buttonWrapper);
        return hbox;
    }

    private void setupSendEmailButton(Button button, StackPane buttonWrapper, Debt debt) {
        var resourceManager = new  ResourceManager(mainCtrl);
        String emailCred = resourceManager.getStringForKey("content_email_cred");
        String debtorEmail = resourceManager.getStringForKey("content_email_debtor");
        String sendReminder = resourceManager.getStringForKey("content_send_reminder");
        String paymentReminder = resourceManager.getStringForKey("content_payment_reminder");
        String youOwe = resourceManager.getStringForKey("content_you_owe");
        String sending = resourceManager.getStringForKey("content_sending");


        Tooltip tooltip = new Tooltip();
        tooltip.setShowDelay(javafx.util.Duration.ZERO);
        tooltip.setHideDelay(javafx.util.Duration.ZERO);
        tooltip.setHideDelay(javafx.util.Duration.ZERO);

        if (!emailManager.areCredentialsValid()) {
            tooltip.setText(emailCred);
            button.setDisable(true);
        } else if (debt.getDebtor().getEmail().isEmpty()) {
            tooltip.setText(debtorEmail);
            button.setDisable(true);
        } else {
            tooltip.setText(paymentReminder + " " + debt.getDebtor().getName());
            button.setDisable(false);
            button.setOnAction(event -> {
                button.setDisable(true);
                button.setText(sending);
                String payReminderFor = resourceManager.getStringForKey("content_pay_reminder_for");
                String to = resourceManager.getStringForKey("content_to");
                new Thread(() -> {
                    emailManager.sendEmail(debt.getDebtor().getEmail(),
                        payReminderFor + " " + e.getName(),
                        youOwe + " " + debt.getAmount() + " "
                                + to + " " + debt.getCreditor().getName());

                    Platform.runLater(() -> {
                        button.setText(sendReminder);
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
    private void alert(String content) {
        var alert = new Alert(Alert.AlertType.ERROR);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setContentText(content);
        alert.showAndWait();
    }
    /**
     * Event handler for pressing a key.
     *
     * @param e the key that is pressed
     */
    public void keyPressed(KeyEvent e) {
        switch (e.getCode()) {
            case ESCAPE:
                back();
                break;
            default:
                break;
        }
    }
}