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
import commons.Quote;
import commons.Participant;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import commons.WebSocketMessage;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class ServerUtils {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final MyWebSocketClient webSocketClient;

    /**
     * Creates an instance of ServerUtils which is used for communicating with the server
     * @param webSocketClient the websocket to communicate through
     */
    @Inject
    public ServerUtils(MyWebSocketClient webSocketClient) {
        this.webSocketClient = webSocketClient;
    }

    /**
     * Adds a participant.
     * @param p the participant to be added.
     */
    public void addParticipant(Participant p) {
        WebSocketMessage request = new WebSocketMessage();
        request.setEndpoint("api/participants");
        request.setMethod("POST");
        request.setData(p);
        sendMessageWithoutResponse(request);
    }

    /**
     * Gets a participant by their id.
     * @param id the participant's id.
     */
    public void getParticipant(long id) {
        WebSocketMessage request = new WebSocketMessage();
        request.setEndpoint("api/participants/id");
        request.setMethod("GET");
        request.setData(id);
        sendMessageWithoutResponse(request);
    }

    /**
     * Deletes a participant.
     * @param p the participant to be deleted.
     */
    public void deleteParticipant(Participant p) {
        WebSocketMessage request = new WebSocketMessage();
        request.setEndpoint("api/participants/id");
        request.setMethod("DELETE");
        request.setData(p);
        sendMessageWithoutResponse(request);
    }

    /**
     * Edits a participant.
     * @param newParticipant the updated participant.
     * @param oldParticipant the participant to be edited.
     */
    public void editParticipant(Participant newParticipant, Participant oldParticipant) {
        WebSocketMessage request = new WebSocketMessage();
        request.setEndpoint("api/participants/id");
        request.setMethod("PUT");
        Participant[] p = new Participant[2];
        p[0] = newParticipant;
        p[1] = oldParticipant;
        request.setData(p);
        sendMessageWithoutResponse(request);
    }

    /**
     * Adds an admin
     * @param admin to be added
     */
    public void addAdmin(Admin admin) {
        WebSocketMessage request = new WebSocketMessage();
        request.setEndpoint("api/admin");
        request.setMethod("POST");
        request.setData(admin);
        sendMessageWithoutResponse(request);
    }

    /**
     * For login
     * @param admin credentials to be logged in with
     * @return if was successful or not
     */
    public String loginAdmin(Admin admin) {
        try {
            WebSocketMessage request = new WebSocketMessage();
            request.setEndpoint("api/admin/login");
            request.setMethod("POST");
            request.setData(admin);
            WebSocketMessage response = sendMessageWithResponse(request);
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
            WebSocketMessage request = new WebSocketMessage();
            request.setEndpoint("api/events/id");
            request.setMethod("GET");
            List<Object> parameters = new ArrayList<>();
            parameters.add(id);
            request.setParameters(parameters);
            WebSocketMessage response = sendMessageWithResponse(request);
            return objectMapper.convertValue(response.getData(), Event.class);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get all the quotes stored in the database
     * @return all the quotes
     */
    public List<Quote> getQuotes() {
        try {
            WebSocketMessage request = new WebSocketMessage();
            request.setEndpoint("api/quotes");
            request.setMethod("GET");

            WebSocketMessage response = sendMessageWithResponse( request);
            return objectMapper.convertValue(response.getData(),
                new TypeReference<List<Quote>>() {});
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Add a quote
     * @param quote to be added
     */
    public void addQuote(Quote quote) {
        WebSocketMessage request = new WebSocketMessage();
        request.setEndpoint("api/quotes");
        request.setMethod("POST");
        request.setData(quote);
        sendMessageWithoutResponse(request);
    }

    /**
     * Send a message to the server with awaiting response
     * @param request the message body
     * @return the response from the server
     * @throws ExecutionException if the object mapper fails
     * @throws InterruptedException if the connection is closed
     */
    private WebSocketMessage sendMessageWithResponse(WebSocketMessage request)
        throws ExecutionException, InterruptedException {
        String requestId = UUID.randomUUID().toString();
        request.setId(requestId);
        CompletableFuture<WebSocketMessage> future = webSocketClient.addPendingRequests(requestId);
        try {
            String message = objectMapper.writeValueAsString(request);
            webSocketClient.send(message);
        } catch (JsonProcessingException e) {
            future.completeExceptionally(e);
        }
        return future.get();
    }

    /**
     * Send a message to the server without awaiting response
     * @param request the message body
     */
    private void sendMessageWithoutResponse(WebSocketMessage request) {
        String requestId = UUID.randomUUID().toString();
        request.setId(requestId);
        try {
            String message = objectMapper.writeValueAsString(request);
            webSocketClient.send(message);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

}
