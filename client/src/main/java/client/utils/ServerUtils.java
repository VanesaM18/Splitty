/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package client.utils;

import client.MyWebSocketClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.inject.Inject;
import commons.Admin;
import commons.Event;
import commons.Participant;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import commons.WebSocketMessage;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class ServerUtils {
    private final ConcurrentHashMap<String, CompletableFuture<WebSocketMessage>> pendingRequests
        = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final MyWebSocketClient webSocketClient;

    /**
     * Creates an instance of ServerUtils which is used for communicating with the server
     * @param webSocketClient the websocket to communicate through
     */
    @Inject
    public ServerUtils(MyWebSocketClient webSocketClient) {
        this.webSocketClient = webSocketClient;
        this.webSocketClient.setPendingRequests(pendingRequests);
    }

    /**
     * Adds a participant
     * @param p the participant to be added
     */
    public void addParticipant(Participant p) {
        WebSocketMessage request = new WebSocketMessage();
        String requestId = UUID.randomUUID().toString();
        request.setId(requestId);
        request.setEndpoint("api/participants");
        request.setMethod("POST");
        request.setData(p);

        try {
            String message = objectMapper.writeValueAsString(request);
            webSocketClient.send(message);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds an admin
     * @param admin to be added
     */
    public void addAdmin(Admin admin) {
        WebSocketMessage request = new WebSocketMessage();
        String requestId = UUID.randomUUID().toString();
        request.setId(requestId);
        request.setEndpoint("api/admin");
        request.setMethod("POST");
        request.setData(admin);

        try {
            String message = objectMapper.writeValueAsString(request);
            webSocketClient.send(message);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    /**
     * For login
     * @param admin credentials to be logged in with
     * @return if was successful or not
     */
    public String loginAdmin(Admin admin) {
        try {
            CompletableFuture<WebSocketMessage> future = new CompletableFuture<>();
            WebSocketMessage request = new WebSocketMessage();
            String requestId = UUID.randomUUID().toString();
            request.setId(requestId);
            request.setEndpoint("api/admin/login");
            request.setMethod("POST");
            request.setData(admin);
            pendingRequests.put(requestId, future);

            try {
                String message = objectMapper.writeValueAsString(request);
                webSocketClient.send(message);
            } catch (JsonProcessingException e) {
                future.completeExceptionally(e);
            }

            WebSocketMessage response = future.get();
            return objectMapper.convertValue(response.getData(), String.class);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Gets an event by id
     * @param id the id of the event
     * @return the requested event
     */
    public Event getEventById(long id) {
        try {
            CompletableFuture<WebSocketMessage> future = new CompletableFuture<>();
            WebSocketMessage request = new WebSocketMessage();
            String requestId = UUID.randomUUID().toString();
            request.setId(requestId);
            request.setEndpoint("api/events/id");
            request.setMethod("GET");
            List<Object> parameters = new ArrayList<>();
            parameters.add(id);
            request.setParameters(parameters);
            pendingRequests.put(requestId, future);

            try {
                String message = objectMapper.writeValueAsString(request);
                webSocketClient.send(message);
            } catch (JsonProcessingException e) {
                future.completeExceptionally(e);
            }

            WebSocketMessage response = future.get();
            return objectMapper.convertValue(response.getData(), Event.class);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
