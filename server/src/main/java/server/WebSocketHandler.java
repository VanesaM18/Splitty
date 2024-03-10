package server;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import commons.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.websocket.server.ServerEndpoint;
import server.api.AdminController;
import server.api.EventController;
import server.api.ParticipantController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Component
@ServerEndpoint(value = "/ws", configurator = ContextConfigurator.class)
public class WebSocketHandler extends TextWebSocketHandler {
    private static final HashMap<WebSocketSession, String> connectionToEvent = new HashMap<>();
    private static final List<WebSocketSession> sessions = new ArrayList<>();
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private AdminController adminController;
    @Autowired
    private EventController eventController;
    @Autowired
    private ParticipantController participantController;

    /**
     * Creates a class for handling the websocket connection
     */
    public WebSocketHandler() {
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * Adds a new client to the lists of clients
     * @param session the client session
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
    }

    /**
     * Removes a client from the list of clients if he disconnected or errored
     * @param session the client session
     * @param status the closed status
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session,
                                      CloseStatus status) throws Exception {
        connectionToEvent.remove(session);
        sessions.remove(session);
    }

    /**
     * Handles requests from clients
     * @param session the channel used to communicate
     * @param message the message received through the channel
     * @throws Exception if the message can't be parsed
     */
    @Override
    protected void handleTextMessage(WebSocketSession session,
                                     TextMessage message) throws Exception {
        WebSocketMessage request = objectMapper.readValue(message.getPayload(),
            WebSocketMessage.class);
        handleRequest(session, request);
    }

    /**
     * Handles the request
     * @param session the channel used to communicate
     * @param request the parsed request
     * @throws Exception if the message can't be parsed
     */
    private void handleRequest(WebSocketSession session,
                               WebSocketMessage request) throws Exception {
        String endPoint = request.getEndpoint();
        if (endPoint.contains("/admin")) {
            handleAdminApi(session, request);
        } else if (endPoint.contains("/events")) {
            handleEventsApi(session, request);
        } else if (endPoint.contains("/participants")) {
            handleParticipantsApi (session, request);
        } else if (endPoint.contains("/client")) {
            handleClientUpdate(session, request);
        }
        updateClients(session, request);
    }

    /**
     * Send requests to refresh views to all clients that are in the same event as the current one
     * Only if the request was POST/PUT/DELETE which means an update
     * @param session the session of the current user
     * @param request the request from the current user
     * @throws IOException if the object mapper fails
     */
    private void updateClients(WebSocketSession session,
                               WebSocketMessage request) throws IOException {
        if (connectionToEvent.containsKey(session) && !Objects.equals(request.getMethod(), "GET")) {
            WebSocketMessage toUpdate = new WebSocketMessage();
            toUpdate.setEndpoint("events/refresh");
            TextMessage convMessage = new TextMessage(objectMapper.writeValueAsString(toUpdate));
            String inviteCode = connectionToEvent.get(session);
            for (WebSocketSession ses: sessions) {
                if (connectionToEvent.containsKey(ses)
                    && connectionToEvent.get(ses).equals(inviteCode)
                    && ses != session) {
                    ses.sendMessage(convMessage);
                }
            }
        }
    }

    /**
     * Process the special requests from the client
     * @param session the client session used to identify the client
     * @param request the request message
     */
    private void handleClientUpdate(WebSocketSession session, WebSocketMessage request) {
        if (Objects.equals(request.getEndpoint(), "api/client")) {
            if ("POST".equals(request.getMethod())) {
                connectionToEvent.put(session, (String)request.getData());
            }
        }
    }

    /**
     * Handles the event specific to /api/event
     * @param session the channel used to communicate
     * @param request the parsed request
     * @throws Exception if the message can't be parsed
     */
//    private void handleEventsApi(WebSocketSession session,
//                                 WebSocketMessage request) throws Exception {
//        switch (request.getEndpoint()) {
//            case "api/events" -> {
//                if ("POST".equals(request.getMethod())) {
//                    Event event = objectMapper.convertValue(request.getData(), Event.class);
//                    ResponseEntity<Event> savedEvent = eventController.add(event);
//                    this.returnResult(session, request, savedEvent.getBody());
//                } else if ("PUT".equals(request.getMethod())) {
//                    Event event = objectMapper.convertValue(request.getData(), Event.class);
//                    ResponseEntity<Event> savedEvent =
//                        eventController.update(event.getInviteCode(), event);
//                    this.returnResult(session, request, savedEvent.getBody());
//                }
//            }
//            case "api/events/id" -> {
//                if ("GET".equals(request.getMethod())) {
//                    List<Object> parameters = request.getParameters();
//                    String id = (String) parameters.get(0);
//                    ResponseEntity<Event> event = eventController.getById(id);
//                    this.returnResult(session, request, event.getBody());
//                }
//            }
//            case "api/events/jsonDump" -> {
//                handleJsonDumpApi(session, request);
//            }
//        }
//    }

    private void handleEventsApi(WebSocketSession session, WebSocketMessage request) throws Exception {
        String endpoint = request.getEndpoint();
        String method = request.getMethod();

        switch (endpoint) {
            case "api/events" -> handleEventsEndpoint(session, request, method);
            case "api/events/id" -> handleEventsByIdEndpoint(session, request, method);
            case "api/events/jsonDump" -> handleJsonDumpApi(session, request);
        }
    }

    private void handleEventsEndpoint(WebSocketSession session, WebSocketMessage request, String method) throws Exception {
        switch (method) {
            case "POST" -> handleAddEvent(session, request);
            case "PUT" -> handleUpdateEvent(session, request);
            case "GET" -> handleGetEvents(session, request);
        }
    }


    private void handleEventsByIdEndpoint(WebSocketSession session,
                                          WebSocketMessage request, String method) throws Exception {
        if ("GET".equals(method)) {
            handleGetEventById(session, request);
        }
    }

    private void handleAddEvent(WebSocketSession session, WebSocketMessage request) throws Exception {
        Event event = objectMapper.convertValue(request.getData(), Event.class);
        ResponseEntity<Event> savedEvent = eventController.add(event);
        returnResult(session, request, savedEvent.getBody());
    }

    private void handleUpdateEvent(WebSocketSession session, WebSocketMessage request) throws Exception {
        Event event = objectMapper.convertValue(request.getData(), Event.class);
        ResponseEntity<Event> savedEvent = eventController.update(event.getInviteCode(), event);
        returnResult(session, request, savedEvent.getBody());
    }

    private void handleGetEvents(WebSocketSession session, WebSocketMessage request) throws Exception {
        String authHeader = request.getAuthHeader();
        ResponseEntity<List<Event>> events = eventController.getAll(authHeader);
        if (events != null) {
            returnResult(session, request, events.getBody());
        }
    }

    private void handleGetEventById(WebSocketSession session, WebSocketMessage request) throws Exception {
        List<Object> parameters = request.getParameters();
        String id = (String) parameters.get(0);

        ResponseEntity<Event> event = eventController.getById(id);
        returnResult(session, request, event.getBody());
    }

    private void handleJsonDumpApi(WebSocketSession session,
                                   WebSocketMessage request) throws Exception {
        if ("GET".equals(request.getMethod())) {
            ResponseEntity<String> jsonDumpResponse = eventController.getJsonDump();
            if (jsonDumpResponse.getStatusCode().is2xxSuccessful()) {
                this.returnResult(session, request, jsonDumpResponse.getBody());
            }
        }
    }

    /**
     * Handles the event specific to /api/admin
     * @param session the channel used to communicate
     * @param request the parsed request
     * @throws Exception if the message can't be parsed
     */
    private void handleAdminApi(WebSocketSession session,
                                WebSocketMessage request) throws Exception {
        switch (request.getEndpoint()) {
            case "api/admin" -> {
                if ("POST".equals(request.getMethod())) {
                    Admin admin = objectMapper.convertValue(request.getData(), Admin.class);
                    ResponseEntity<Admin> savedAdmin = adminController.add(admin);
                    this.returnResult(session, request, savedAdmin.getBody());
                }
            }
            case "api/admin/login" -> {
                if ("POST".equals(request.getMethod())) {
                    Admin admin = objectMapper.convertValue(request.getData(), Admin.class);
                    ResponseEntity<String> status = adminController.login(admin);
                    this.returnResult(session, request, status.getBody());
                }
            }
        }
    }

    /**
     * Handles the participants specific to /api/participants
     * @param session the channel used to communicate
     * @param request the parsed request
     * @throws Exception if the message can't be parsed
     */
    private void handleParticipantsApi(WebSocketSession session,
                                 WebSocketMessage request) throws Exception {
        switch (request.getEndpoint()) {
            case "api/participants" -> {
                if ("GET".equals(request.getMethod())) {
                    List<Participant> all = participantController.getAll();
                    this.returnResult(session, request, all);
                } else if ("POST".equals(request.getMethod())) {
                    Participant p = objectMapper.convertValue(request.getData(), Participant.class);
                    ResponseEntity<Participant> response = participantController.add(p);
                    this.returnResult(session, request, response.getBody());
                }
            }
            case "api/participants/id" -> {
                handleParticipantsApiByID(session, request);
            }
        }
    }

    /**
     * Handles the participants specific to /api/participants/id
     * @param session the channel used to communicate
     * @param request the parsed request
     * @throws Exception if the message can't be parsed
     */
    private void handleParticipantsApiByID(WebSocketSession session,
                                           WebSocketMessage request) throws Exception {
        if ("DELETE".equals(request.getMethod())) {
            long participantId = ((Participant) request.getData()).getId();
            ResponseEntity<String> response = participantController.delete(participantId);
            this.returnResult(session, request, response.getBody());
        }
        else if ("PUT".equals(request.getMethod())) {
            Participant[] participants = objectMapper.convertValue(
                    request.getData(), Participant[].class);
            Participant oldParticipant = participants[1];
            Participant newParticipant = participants[0];
            ResponseEntity<String> response = participantController.update(
                    oldParticipant.getId(), newParticipant);
            this.returnResult(session, request, response.getBody());
        }
        else if ("GET".equals(request.getMethod())) {
            long id = (long) request.getData();
            ResponseEntity<Participant> response = participantController.getById(id);
            this.returnResult(session, request, response.getBody());
        }
    }

    /**
     * Returns through the same channel the requested resource (to be used only
     * if it's expected to get a result, if not this call can be ignored)
     * @param session the channel used to communicate
     * @param request the parsed request
     * @param obj the data to be sent back
     * @throws Exception if the message can't be parsed
     */
    private void returnResult(WebSocketSession session,
                              WebSocketMessage request, Object obj) throws Exception {
        WebSocketMessage messageBack = new WebSocketMessage();
        messageBack.setId(request.getId());
        messageBack.setData(obj);
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(messageBack)));
    }
}
