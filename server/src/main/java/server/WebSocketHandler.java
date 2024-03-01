package server;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import commons.Admin;
import commons.Event;
import commons.Quote;
import commons.WebSocketMessage;
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
                if ("GET".equals(request.getMethod())) {
                    List<Event> all = eventController.getAll();
                    this.returnResult(session, request, all);
                } else if ("POST".equals(request.getMethod())) {
                    Event event = objectMapper.convertValue(request.getData(), Event.class);
                    ResponseEntity<Event> savedEvent = eventController.add(event);
                    this.returnResult(session, request, savedEvent.getBody());
                }
            }
            case "api/events/id" -> {
                if ("GET".equals(request.getMethod())) {
                    List<Object> parameters = request.getParameters();
                    long id = (long) parameters.get(0);
                    ResponseEntity<Event> event = eventController.getById(id);
                    this.returnResult(session, request, event.getBody());
                }
            }
            case "api/events/invites/inviteCode" -> {
                if ("GET".equals(request.getMethod())) {
                    List<Object> parameters = request.getParameters();
                    String code = (String) parameters.get(0);
                    ResponseEntity<Event> event = eventController.getByInviteCode(code);
                    this.returnResult(session, request, event.getBody());
                }
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
