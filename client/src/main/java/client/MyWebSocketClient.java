package client;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.inject.Inject;
import commons.WebSocketMessage;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MyWebSocketClient extends WebSocketClient {
    private ConcurrentHashMap<String, CompletableFuture<WebSocketMessage>> pendingRequests
        = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper;

    /**
     * Creates a websocket class instance
     * @param config the config that will be used to get the address of the server
     */
    @Inject
    public MyWebSocketClient(ConfigLoader config) throws URISyntaxException {
        super(new URI((String) config.getProperty("address")));
        this.objectMapper = new ObjectMapper();
        try {
            this.connectSync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Connects to the websocket
     * @throws InterruptedException if the connections is intrerupted
     */
    public void connectSync() throws InterruptedException {
        this.connectBlocking();
    }

    /**
     * Sets the pending requests of our client
     * @param pendingRequests
     */
    public void setPendingRequests(ConcurrentHashMap<String, CompletableFuture<WebSocketMessage>>
                                       pendingRequests) {
        this.pendingRequests = pendingRequests;
    }

    /**
     * Adds a request
     * @param requestId the request id
     * @return the future for the response
     */
    public CompletableFuture<WebSocketMessage> addPendingRequests(String requestId) {
        CompletableFuture<WebSocketMessage> future = new CompletableFuture<>();
        pendingRequests.put(requestId, future);
        return future;
    }

    /**
     * Called when the connection is opened
     * @param handshake The handshake of the websocket instance
     */
    @Override
    public void onOpen(ServerHandshake handshake) {
        System.out.println("Opened WebSocket connection");
    }

    /**
     * Handles what happens when the client receives a message
     * @param message The UTF-8 decoded message that was received.
     */
    @Override
    public void onMessage(String message) {
        try {
            WebSocketMessage response = objectMapper.readValue(message, WebSocketMessage.class);
            CompletableFuture<WebSocketMessage> future = pendingRequests.remove(response.getId());
            if (future != null) {
                future.complete(response);
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles when the websocket connection is closed
     * @param code   The exit code
     * @param reason Additional information string
     * @param remote Returns whether the closing of the connection was initiated by the remote
     *               host.
     */
    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Closed with exit code " + code + ", reason: " + reason);
    }

    /**
     * Handles when the connection errors
     * @param e The exception causing this error
     */
    @Override
    public void onError(Exception e) {
        e.printStackTrace();
    }
}
