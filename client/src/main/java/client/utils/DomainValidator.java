package client.utils;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

import client.scenes.MainCtrl;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.GenericType;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.glassfish.jersey.client.ClientConfig;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

public class DomainValidator {

    private final Stage loadingStage;
    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private UUID domainUuid;

    /**
     * constructs a DomainValidator object.
     * @param mainCtrl MainCtrl instance.
     * @param server   ServerUtils instance.
     */
    public DomainValidator(MainCtrl mainCtrl, ServerUtils server) {
        this.mainCtrl = mainCtrl;
        this.server = server;
        this.loadingStage = createLoadingStage();
    }

    /**
     * validates the provided URL asynchronously.
     * @param urlString         URL to validate.
     * @param onSuccessSupplier supplier to be executed if validation succeeds.
     */
    public void validateUrl(String urlString, Supplier<Boolean> onSuccessSupplier) {
        long minDisplayTime = 2;
        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                Thread.sleep(Duration.ofSeconds(minDisplayTime));
                return verify(urlString);
            }
        };
        validateServerWithDisplay(task, onSuccessSupplier);
    }

    private Boolean verify(String urlString) {
        URL url;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            return false;
        }
        UUID domainUuid;
        try {
            String uuidString = ClientBuilder.newClient(new ClientConfig())
                    .target(url.toString())
                    .path("splitty-domain")
                    .request(APPLICATION_JSON)
                    .accept(APPLICATION_JSON)
                    .get(new GenericType<String>() {
                    });
            domainUuid = UUID.fromString(uuidString);
        } catch (Exception e) {
            this.setDomainUuid(null);
            return false;
        }
        this.setDomainUuid(domainUuid);
        return true;
    }

    private void setDomainUuid(UUID domainUuid) {
        this.domainUuid = domainUuid;
    }

    private void validateServerWithDisplay(Task<Boolean> task,
                                           Supplier<Boolean> onSuccessSupplier) {
        task.setOnRunning(e -> Platform.runLater(this.loadingStage::show));
        AtomicBoolean returningValue = new AtomicBoolean(false);

        task.setOnSucceeded(event -> {
            boolean isHostAvailable = task.getValue();
            if (isHostAvailable) {
                returningValue.set(true);
                hideLoadingStage();
                var isSuccess = onSuccessSupplier.get();
                if(isSuccess) {
                    this.server.setDomainUuid(this.domainUuid);
                    try {
                        this.server.updateWebSocketConnection(this.mainCtrl);
                    } catch (URISyntaxException e) {
                        //TODO log error
                    }
                }
            } else {
                finish();
            }
        });

        task.setOnFailed(e -> finish());

        var thread = new Thread(task);
        thread.start();
    }

    private void finish() {
        hideLoadingStage();
        showAlert();
    }

    private void hideLoadingStage() {
        this.loadingStage.hide();
    }


    private Stage createLoadingStage() {
        ProgressIndicator loadingWheel = new ProgressIndicator();
        StackPane stackPane = new StackPane(loadingWheel);
        stackPane.setStyle("-fx-background-color: rgba(0, 0, 0, 1.0);");
        loadingWheel.setStyle(" -fx-progress-color: orange;");
        Stage primaryStage = mainCtrl.getPrimaryStage();
        Stage loadingStage = new Stage();
        loadingStage.initOwner(primaryStage);
        loadingStage.setScene(new javafx.scene.Scene(stackPane, 300, 300));
        return loadingStage;
    }

    /**
     * displays an alert indicating an invalid URL.
     */
    public void showAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Invalid URL");
        alert.setHeaderText("Invalid URL");
        alert.setContentText("Please enter a valid URL.");
        alert.showAndWait();
        alert.getOnCloseRequest();
    }

}
