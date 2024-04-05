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

import client.ConfigLoader;
import client.MyWebSocketClient;
import client.scenes.MainCtrl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.inject.Inject;
import commons.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import javafx.util.Pair;
import org.glassfish.jersey.client.ClientConfig;

import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import java.util.concurrent.ExecutionException;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

public class ServerUtils {
    private String serverUrl;
    private final Client client = ClientBuilder.newClient(new ClientConfig());
    private final ObjectMapper objectMapper = new ObjectMapper();
    private MyWebSocketClient webSocketClient;
    private static Optional<String> auth = Optional.empty();
    private final ConfigLoader config;
    private UUID domainUuid = null;

    /**
     * Creates an instance of ServerUtils which is used for communicating with the
     * server
     * 
     * @param webSocketClient the websocket to communicate through
     * @param config the config loader to get the server address
     */
    @Inject
    public ServerUtils(MyWebSocketClient webSocketClient, ConfigLoader config) {
        this.serverUrl = (String) config.getProperty("address");
        this.webSocketClient = webSocketClient;
        this.config = config;
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
     * gets the server url
     * @return server url
     */
    public String getServerUrl() {
        return serverUrl;
    }

    /**
     * sets the server url
     * @param serverUrl server url
     */
    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
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
     * Deletes an expense.
     *
     * @param e the expense to be deleted.
     */
    public void deleteExpense(Expense e) {
        WebSocketMessage request = new WebSocketMessage();
        request.setEndpoint("api/expenses/id");
        request.setMethod("DELETE");
        request.setData(e.getId());
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
     * Deletes an event.
     * 
     * @param ev the event to be deleted.
     */
    public void deleteEvent(Event ev) {
        if (isAuthenticated()) {
            WebSocketMessage request = new WebSocketMessage();
            request.setEndpoint("api/events/id");
            request.setMethod("DELETE");
            request.setData(ev.getInviteCode());
            request.setAuthHeader(auth.get());
            sendMessageWithoutResponse(request);
        }
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
     * Adds expense type.
     * @param tag the expense type to be added.
     */
    public void addExpenseType(ExpenseType tag) {
        WebSocketMessage request = new WebSocketMessage();
        request.setEndpoint("api/expense_type/by_event");
        request.setMethod("POST");
        request.setData(tag);
        request.setParameters(List.of(tag.getEvent().getInviteCode()));
        try {
            sendMessageWithResponse(request);
        } catch (ExecutionException | InterruptedException e) {
            return;
        }
    }

    /**
     * Update an expense
     * 
     * @param expense The expense to update
     * 
     */
    public void updateExpense(Expense expense) {
        WebSocketMessage request = new WebSocketMessage();
        request.setEndpoint("api/expenses");
        request.setMethod("PUT");
        request.setData(expense);
        request.setParameters(List.of(expense.getId()));
        try {
            var resp = sendMessageWithResponse(request);
        } catch (ExecutionException | InterruptedException e) {
            return;
        }
    }

    /**
     * handles the request to retrieve a JSON dump of events from the server.
     * if the user is authenticated, this method sends a request to the server to fetch
     * a JSON dump of events.
     * it includes the Authorization header with the authentication
     * token obtained from the authentication service.
     *
     * @return An Optional containing a JSON dump of events if authentication is successful,
     * or an empty Optional if authentication fails or an error occurs during the request.
     */
    public Optional<String> handleJsonDump() {
        if(isAuthenticated()) {
            String jsonDump = client
                    .target(serverUrl)
                    .path("api/events/jsonDump")
                    .request(APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, auth.get())
                    .accept(APPLICATION_JSON)
                    .get(new GenericType<String>() {
                    });
            return Optional.of(jsonDump);
        }
        return Optional.empty();
    }

    /**
     * retrieves all events from the server.
     * this method requires authentication.
     * if the user is authenticated,
     * it fetches all events from the server using a JAX-RS Client.
     * if authentication fails, it returns an empty Optional.
     * @return an Optional containing the list of events if successful,
     * otherwise an empty Optional.
     */
//    public Optional<List<Event>> getAllEvents() {
//        if (isAuthenticated()) {
//            try {
//                WebSocketMessage requestMessage = new WebSocketMessage();
//                requestMessage.setEndpoint("api/events");
//                requestMessage.setMethod("GET");
//                requestMessage.setAuthHeader(auth.get());
//                WebSocketMessage response = sendMessageWithResponse(requestMessage);
//                if (response.getData() != null) {
//                    return Optional.of(getObjectMapper().convertValue(response.getData(),
//                            new TypeReference<ArrayList<Event>>() {
//                            }));
//                }
//            } catch (ExecutionException | InterruptedException e) {
//                return Optional.empty();
//            }
//        }
//        return Optional.empty();
//    }
    public Optional<List<Event>> getAllEvents() {
        if (isAuthenticated()) {
            List<Event> events = client
                    .target(serverUrl)
                    .path("api/events")
                    .request(APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, auth.get())
                    .accept(APPLICATION_JSON)
                    .get(new GenericType<List<Event>>() {
                    });
            return Optional.of(events);
        }
        return Optional.empty();
    }

    /**
     * imports an array of events to the server.
     * @param events array of Event objects to be imported.
     * @return optional containing a list of imported
     * Event objects if authentication is successful,
     * else returns an empty Optional.
     */
    public Optional<List<Event>> importEvents(Event[] events) {
        if (isAuthenticated()) {
            List<Event> eventList = client
                    .target(serverUrl)
                    .path("api/events/import")
                    .request(APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, auth.get())
                    .accept(APPLICATION_JSON)
                    .post(Entity.entity(events, APPLICATION_JSON), new GenericType<List<Event>>() {
                    });
            return Optional.of(eventList);
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

    public List<Debt> calculateDebts(Event event) {
        List<Debt> initialDebts = event.paymentsToDebt(event);
        int n = event.getParticipants().size();
        DebtMinimizationGraph solver = new DebtMinimizationGraph(n);
        HashMap<Participant, Integer> indexing = new HashMap<>();
        HashMap<Integer, Participant> reverseIndexing = new HashMap<>();
        int cnt = 0;
        for (Participant p: event.getParticipants()) {
            indexing.put(p, cnt);
            reverseIndexing.put(cnt, p);
            cnt += 1;
        }
        for (Debt debt: initialDebts) {
            solver.addEdge(indexing.get(debt.getDebtor()), indexing.get(debt.getCreditor()), (int)debt.getAmount().getInternalValue());
        }
        Set<Pair<Integer, Integer>> visited = new HashSet<>();
        while (true) {
            Pair<Integer, Integer> unvisited = getUnvisitedEdge(solver, visited, n);
            if (unvisited.getKey() == -1) {
                break;
            }
            int mxFlow = solver.maxFlow(unvisited.getKey(), unvisited.getValue());
            DebtMinimizationGraph residualGraph = new DebtMinimizationGraph(n);
            for (int from = 0; from < n; ++from) {
                List<DebtMinimizationGraph.Edge> adjacentEdges = solver.getEdgesForVertex(from);
                for (DebtMinimizationGraph.Edge edge: adjacentEdges) {
                    int flow = (edge.flow < 0 ? edge.capacity : (edge.capacity - edge.flow));
                    if (flow > 0 && (from != unvisited.getKey() || edge.to != unvisited.getValue())) {
                        residualGraph.addEdge(from, edge.to, flow);
                    }
                }
            }
            if (mxFlow > 0) {
                residualGraph.addEdge(unvisited.getKey(), unvisited.getValue(), mxFlow);
            }
            visited.add(unvisited);
            solver = residualGraph;
        }
        List<Debt> resultDebts = new ArrayList<>();
        for (int from = 0; from < n; ++from) {
            List<DebtMinimizationGraph.Edge> adjacentEdges = solver.getEdgesForVertex(from);
            for (DebtMinimizationGraph.Edge edge : adjacentEdges) {
                if (edge.capacity > 0) {
                    resultDebts.add(new Debt(reverseIndexing.get(from), new Monetary(edge.capacity), reverseIndexing.get(edge.to)));
                }
            }
        }
        Map<Pair<Participant, Participant>, Long> netBalances =
            getUnifiedDebts(resultDebts);
        List<Debt> result = new ArrayList<>();
        for (Map.Entry<Pair<Participant, Participant>, Long> entry : netBalances.entrySet()) {
            Pair<Participant, Participant> key = entry.getKey();
            Long balance = entry.getValue();
            if (balance > 0) {
                result.add(new Debt(key.getKey(), new Monetary(balance), key.getValue()));
            } else if (balance < 0) {
                result.add(new Debt(key.getValue(), new Monetary(-balance), key.getKey()));
            }
        }
        return result;
    }

    private static Map<Pair<Participant, Participant>, Long> getUnifiedDebts(
        List<Debt> resultDebts) {
        Map<Pair<Participant, Participant>, Long> netBalances = new HashMap<>();
        for (Debt debt : resultDebts) {
            Participant debtor = debt.getDebtor();
            Participant creditor = debt.getCreditor();
            Long amount = debt.getAmount().getInternalValue();
            Pair<Participant, Participant> key = new Pair<>(debtor, creditor);
            if (netBalances.containsKey(new Pair<>(creditor, debtor))) {
                key = new Pair<>(creditor, debtor);
                amount = -amount;
            }
            Long currentBalance = netBalances.getOrDefault(key, 0L);
            netBalances.put(key, currentBalance + amount);
        }
        return netBalances;
    }

    private Pair<Integer, Integer> getUnvisitedEdge(DebtMinimizationGraph solver, Set<Pair<Integer, Integer>> visited, int n) {
        for (int from = 0; from < n; ++from) {
            List<DebtMinimizationGraph.Edge> adjacentEdges = solver.getEdgesForVertex(from);
            for (DebtMinimizationGraph.Edge edge: adjacentEdges) {
                Pair<Integer, Integer> vis = new Pair<>(from, edge.to);
                if (!visited.contains(vis)) {
                    return vis;
                }
            }
        }
        return new Pair<>(-1, -1);
    }

    /**
     * adds the marked received debt as an expense
     * to cancel out the debt of the debtor
     *
     * @param e current event
     * @param debt marked debt
     */
    public void removeExpensesDebts(Event e, Debt debt){
        try{
            Set<Participant> splitBetween = new HashSet<>();
            splitBetween.add(debt.getCreditor());
            Expense expense = new Expense(e, "Debt", debt.getDebtor(),
                    debt.getAmount(), LocalDate.now(), splitBetween);
            addExpense(expense);

            refreshExpensesList(e);
        } catch (ExecutionException | InterruptedException er) {
            er.printStackTrace();
        }
    }

    /**
     * Refreshes the expenses list for an event
     * by removing participants that have everything settled
     * @param e the event
     */
    public void refreshExpensesList(Event e) {
        return;

    }

    /**
     * Retrieves all participants that can be removed from expenses
     * @param allExpenses all existing expenses
     * @return the list of participants that can be removed
     */
    private List<Participant> participantsNoInfluence(List<Expense> allExpenses) {
        if (allExpenses == null) {
            return new ArrayList<>();
        }
        HashMap<Participant, Long> sumPerParticipant = new HashMap<>();
        for (Expense ex : allExpenses) {
            Set<Participant> split = ex.getSplitBetween();
            long amountPerPerson = ex.getAmount().getInternalValue() / split.size();
            for (Participant p : split) {
                sumPerParticipant.put(p, sumPerParticipant.getOrDefault(p, 0L) + amountPerPerson);
            }
            sumPerParticipant.put(ex.getCreator(),
                sumPerParticipant.getOrDefault(ex.getCreator(), 0L)
                    - ex.getAmount().getInternalValue());
        }
        List<Participant> result = new ArrayList<>();
        for (Map.Entry<Participant, Long> entry : sumPerParticipant.entrySet()) {
            if (entry.getValue() == 0) {
                result.add(entry.getKey());
            }
        }
        return result;
    }

    /**
     * Announce all client that open debts view needs to be updated for an event
     * @param eventId the event id
     */
    public void markDebtAsReceived(String eventId) {
        String serverAddress = (String)config.getProperty("address");
        Response response = ClientBuilder.newClient(new ClientConfig())
            .target(serverAddress)
            .path("/api/debts/" + eventId + "/received")
            .request(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .post(Entity.text(""));

        if (response.getStatus() != 200) {
            throw new NotFoundException();
        }
    }

    /**
     * Long pools for updates in open debt view
     * @param eventId the event where the open debts are
     * @return the status of the update
     */
    public String longPollDebts(String eventId) {
        String serverAddress = (String)config.getProperty("address");
        Response response = ClientBuilder.newClient(new ClientConfig())
            .target(serverAddress)
            .path("/api/debts/" + eventId + "/updates")
            .request(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .post(Entity.text(""));

        if (response.getStatus() != 200) {
            throw new NotFoundException();
        }
        return "New update incoming";
    }

    /**
     * Updates the content of a tag.
     * @param tag tag with changed content.
     */
    public void updateTag(ExpenseType tag) {
        WebSocketMessage request = new WebSocketMessage();
        request.setEndpoint("api/expense_type");
        request.setMethod("PUT");
        request.setData(tag);
        sendMessageWithoutResponse(request);
    }

    /**
     * Updates the content of a tag.
     * @param tag tag with changed content.
     */
    public void deleteTag(ExpenseType tag) {
        WebSocketMessage request = new WebSocketMessage();
        request.setEndpoint("api/expense_type");
        request.setMethod("DELETE");
        request.setData(tag);
        sendMessageWithoutResponse(request);
    }

    /**
     * gets the UUID associated with this domain model.
     * @return the UUID associated with this domain model.
     */
    public UUID getDomainUuid() {
        return this.domainUuid;
    }

    /**
     * sets the UUID associated with this domain model.
     * @param domainUuid the UUID to be associated with this domain model.
     */
    public void setDomainUuid(UUID domainUuid) {
        this.domainUuid = domainUuid;
        System.out.println("\u001B[32mConnected to server with id "
                + domainUuid.toString() + "! ! !\u001B[0m");
    }

    /**
     * updates the WebSocket connection using the provided MainCtrl instance.
     * @param mainCtrl the MainCtrl instance used to update the WebSocket connection.
     * @throws URISyntaxException if the URI syntax is invalid.
     */
    public void updateWebSocketConnection(MainCtrl mainCtrl) throws URISyntaxException {
        this.webSocketClient = new MyWebSocketClient(config, mainCtrl);
    }

}
