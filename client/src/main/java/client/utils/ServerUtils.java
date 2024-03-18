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
import commons.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.*;
import java.util.concurrent.CompletableFuture;

import java.util.concurrent.ExecutionException;

public class ServerUtils {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final MyWebSocketClient webSocketClient;
    private static Optional<String> auth = Optional.empty();

    /**
     * Creates an instance of ServerUtils which is used for communicating with the
     * server
     * 
     * @param webSocketClient the websocket to communicate through
     */
    @Inject
    public ServerUtils(MyWebSocketClient webSocketClient) {
        this.webSocketClient = webSocketClient;
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * gets the ObjectMapper instance for handling JSON
     * serialization/deserialization
     * 
     * @return ObjectMapper instance
     */
    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    /**
     * gets the WebSocket client instance for communication with the server
     * 
     * @return WebSocket client instance
     */
    public MyWebSocketClient getWebSocketClient() {
        return webSocketClient;
    }

    /**
     * Adds a participant.
     * 
     * @param p the participant to be added.
     * @return the created event with the associated id
     */
    public Participant addParticipant(Participant p) {
        try {
            WebSocketMessage request = new WebSocketMessage();
            request.setEndpoint("api/participants");
            request.setMethod("POST");
            request.setData(p);
            WebSocketMessage response = sendMessageWithResponse(request);
            return objectMapper.convertValue(response.getData(), Participant.class);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Gets all expenses from an event
     *
     * @param ev The event to get the expenses for
     *
     * @return The list of expenses from the event
     */
    public List<Expense> getAllExpensesFromEvent(Event ev) {
        try {
            WebSocketMessage request = new WebSocketMessage();
            request.setEndpoint("api/expenses/by_event");
            request.setMethod("GET");
            request.setParameters(List.of(ev.getInviteCode()));
            WebSocketMessage response = sendMessageWithResponse(request);
            List<Expense> expenses = objectMapper
                    .convertValue(response.getData(), new TypeReference<List<Expense>>() {
                    });
            return expenses;
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return List.of();
    }

    /**
     * Gets a participant by their id.
     * 
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
     * 
     * @param p the participant to be deleted.
     */
    public void deleteParticipant(Participant p) {
        WebSocketMessage request = new WebSocketMessage();
        request.setEndpoint("api/participants/id");
        request.setMethod("DELETE");
        request.setData(p.getId());
        sendMessageWithoutResponse(request);
    }

    /**
     * Edits a participant.
     * 
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
     * 
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
     * 
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
     * 
     * @param code the code of the event
     * @return the requested event or null if it does not exist
     */
    public Event getEventById(String code) {
        try {
            WebSocketMessage request = new WebSocketMessage();
            request.setEndpoint("api/events/id");
            request.setMethod("GET");
            List<Object> parameters = new ArrayList<>();
            parameters.add(code);
            request.setParameters(parameters);
            WebSocketMessage response = sendMessageWithResponse(request);
            return objectMapper.convertValue(response.getData(), Event.class);
        } catch (ExecutionException | InterruptedException ignored) {

        }
        return null;
    }

    /**
     * Adds an event
     * 
     * @param ev to be added
     * @return the event with the new inviteCode
     */
    public Event addEvent(Event ev) {
        try {
            WebSocketMessage request = new WebSocketMessage();
            request.setEndpoint("api/events");
            request.setMethod("POST");
            request.setData(ev);
            WebSocketMessage response = sendMessageWithResponse(request);
            return objectMapper.convertValue(response.getData(), Event.class);
        } catch (ExecutionException | InterruptedException ignored) {

        }
        return null;
    }

    /**
     * Updates an event
     * 
     * @param ev to be updated
     */
    public void updateEvent(Event ev) {
        WebSocketMessage request = new WebSocketMessage();
        request.setEndpoint("api/events");
        request.setMethod("PUT");
        request.setData(ev);
        sendMessageWithoutResponse(request);
    }

    /**
     * Get all the quotes stored in the database
     * 
     * @return all the quotes
     */
    public List<Quote> getQuotes() {
        try {
            WebSocketMessage request = new WebSocketMessage();
            request.setEndpoint("api/quotes");
            request.setMethod("GET");

            WebSocketMessage response = sendMessageWithResponse(request);
            return objectMapper.convertValue(response.getData(),
                    new TypeReference<List<Quote>>() {
                    });
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Add a quote
     * 
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
     * Add expense
     * 
     * @param expense The expense to add
     * @throws InterruptedException
     * @throws ExecutionException
     */
    public void addExpense(Expense expense) throws ExecutionException, InterruptedException {
        WebSocketMessage request = new WebSocketMessage();
        request.setEndpoint("api/expenses/by_event");
        request.setMethod("POST");
        request.setData(expense);
        request.setParameters(List.of(expense.getEvent().getInviteCode()));
        try {
            sendMessageWithResponse(request);
        } catch (ExecutionException | InterruptedException e) {
            return;
        }
    }

    /**
     * sends a JSON dump request to the server via WebSocket and waits for the
     * response
     * 
     * @return an Optional containing the JSON dump as a String if successful,
     *         or empty if an error occurs
     */
    public Optional<String> handleJsonDump() {

        try {
            WebSocketMessage requestMessage = new WebSocketMessage();
            requestMessage.setEndpoint("api/events/jsonDump");
            requestMessage.setMethod("GET");
            WebSocketMessage response = sendMessageWithResponse(requestMessage);
            if (response.getData() != null) {
                return Optional.of(
                    getObjectMapper().convertValue(response.getData(), String.class));
            } else {
                return Optional.empty();
            }
        } catch (ExecutionException | InterruptedException e) {
            return Optional.empty();
        }
    }

    /**
     * retrieves a list of events from the server via WebSocket.
     * if authentication is successful,
     * sends a GET request to the "api/events" endpoint with the authentication
     * header.
     * parses and returns the list of events received in the response.
     * 
     * @return an Optional containing the list of events if successful,
     *         otherwise an empty Optional.
     */
    public Optional<List<Event>> getAllEvents() {
        if (isAuthenticated()) {
            try {
                WebSocketMessage requestMessage = new WebSocketMessage();
                requestMessage.setEndpoint("api/events");
                requestMessage.setMethod("GET");
                requestMessage.setAuthHeader(auth.get());
                WebSocketMessage response = sendMessageWithResponse(requestMessage);
                if (response.getData() != null) {
                    return Optional.of(getObjectMapper().convertValue(response.getData(),
                            new TypeReference<ArrayList<Event>>() {
                            }));
                }
            } catch (ExecutionException | InterruptedException e) {
                return Optional.empty();
            }
        }
        return Optional.empty();
    }

    /**
     * Send to the websocket the eventId to which the current client it's connected
     * too
     * 
     * @param eventId the inviteCode of the event
     */
    public void sendUpdateStatus(String eventId) {
        WebSocketMessage requestMessage = new WebSocketMessage();
        requestMessage.setEndpoint("api/client");
        requestMessage.setMethod("POST");
        requestMessage.setData(eventId);
        sendMessageWithoutResponse(requestMessage);
    }

    /**
     * Send a message to the server with awaiting response
     * 
     * @param request the message body
     * @return the response from the server
     * @throws ExecutionException   if the object mapper fails
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
     * 
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

    /**
     * checks if the user is a currently authenticated admin.
     * 
     * @return true if authenticated, false otherwise.
     */
    public static boolean isAuthenticated() {
        return auth.isPresent();
    }

    /**
     * sets the authentication credentials using the provided username and password.
     * encodes the credentials using Base64 and constructs the authentication
     * header.
     * 
     * @param username username for authentication.
     * @param password password for authentication.
     */
    private static void setAuth(String username, String password) {
        String credentials = username + ":" + password;
        String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());
        String authHeaderString = "Basic " + encodedCredentials;
        auth = Optional.of(authHeaderString);
    }

    /**
     * authenticates the application using the provided admin credentials.
     * sets the authentication credentials based on the admin's username and
     * password.
     * 
     * @param admin Admin object containing authentication details.
     */
    public static void adminAuth(Admin admin) {
        setAuth(admin.getUsername(), admin.getPassword());
    }

}
