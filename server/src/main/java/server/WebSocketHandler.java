package server;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import commons.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.websocket.server.ServerEndpoint;
import server.api.AdminController;
import server.api.EventController;
import server.api.ParticipantController;
import server.api.QuoteController;
import java.util.List;

@Component
@ServerEndpoint(value = "/ws", configurator = ContextConfigurator.class)
public class WebSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private AdminController adminController;
    @Autowired
    private EventController eventController;
    @Autowired
    private QuoteController quoteController;
    @Autowired
    private ParticipantController participantController;

    {
        objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * Creates a class for handling the websocket connection
     */
    public WebSocketHandler() {
        this.objectMapper.registerModule(new JavaTimeModule());
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
        } else if (endPoint.contains("/quotes")) {
            handleQuotesApi(session, request);
        } else if (endPoint.contains("/participants")) {
            handleParticipantsApi (session, request);
        }
    }
    /**
     * Handles the quotes specific to /api/quote
     * @param session the channel used to communicate
     * @param request the parsed request
     * @throws Exception if the message can't be parsed
     */
    private void handleQuotesApi(WebSocketSession session,
                                 WebSocketMessage request) throws Exception {
        switch (request.getEndpoint()) {
            case "api/quotes" -> {
                if ("GET".equals(request.getMethod())) {
                    List<Quote> all = quoteController.getAll();
                    this.returnResult(session, request, all);
                } else if ("POST".equals(request.getMethod())) {
                    Quote quote = objectMapper.convertValue(request.getData(), Quote.class);
                    ResponseEntity<Quote> savedEvent = quoteController.add(quote);
                    this.returnResult(session, request, savedEvent.getBody());
                }
            }
            case "api/quotes/id" -> {
                if ("GET".equals(request.getMethod())) {
                    List<Object> parameters = request.getParameters();
                    long id = (long) parameters.get(0);
                    ResponseEntity<Quote> quote = quoteController.getById(id);
                    this.returnResult(session, request, quote.getBody());
                }
            }
        }
    }
    /**
     * Handles the event specific to /api/event
     * @param session the channel used to communicate
     * @param request the parsed request
     * @throws Exception if the message can't be parsed
     */
    private void handleEventsApi(WebSocketSession session,
                                 WebSocketMessage request) throws Exception {
        switch (request.getEndpoint()) {
            case "api/events" -> {
                if ("POST".equals(request.getMethod())) {
                    Event event = objectMapper.convertValue(request.getData(), Event.class);
                    ResponseEntity<Event> savedEvent = eventController.add(event);
                    this.returnResult(session, request, savedEvent.getBody());
                } else if ("PUT".equals(request.getMethod())) {
                    Event event = objectMapper.convertValue(request.getData(), Event.class);
                    ResponseEntity<Event> savedEvent =
                        eventController.update(event.getInviteCode(), event);
                    this.returnResult(session, request, savedEvent.getBody());
                }
            }
            case "api/events/id" -> {
                if ("GET".equals(request.getMethod())) {
                    List<Object> parameters = request.getParameters();
                    String id = (String) parameters.get(0);
                    ResponseEntity<Event> event = eventController.getById(id);
                    this.returnResult(session, request, event.getBody());
                }
            }
            case "api/events/jsonDump" -> {
                handleJsonDumpApi(session, request);
            }
        }
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
