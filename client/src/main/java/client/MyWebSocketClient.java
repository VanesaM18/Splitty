package client;

import java.net.URI;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.core.JsonProcessingException;
import commons.WebSocketMessage;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MyWebSocketClient extends WebSocketClient {
    private ConcurrentHashMap<String, CompletableFuture<WebSocketMessage>> pendingRequests = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper;
    public MyWebSocketClient(URI serverUri) {
        super(serverUri);
        this.objectMapper = new ObjectMapper();
        try {
            this.connectSync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void connectSync() throws InterruptedException {
        this.connectBlocking();
    }
    public void setPendingRequests(ConcurrentHashMap<String, CompletableFuture<WebSocketMessage>> pendingRequests) {
        this.pendingRequests = pendingRequests;
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        System.out.println("Opened WebSocket connection");
    }

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

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Closed with exit code " + code + ", reason: " + reason);
    }

    @Override
    public void onError(Exception e) {
        e.printStackTrace();
    }
}
