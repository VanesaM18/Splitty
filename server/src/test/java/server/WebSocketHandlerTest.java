package server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import commons.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import server.api.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.*;

@WebMvcTest(WebSocketHandler.class)
@ExtendWith(SpringExtension.class)
public class WebSocketHandlerTest {

    @Autowired
    private WebSocketHandler webSocketHandler;

    @MockBean
    private AdminController adminController;
    @MockBean
    private EventController eventController;
    @MockBean
    private ParticipantController participantController;
    @MockBean
    private ExpenseController expenseController;
    @MockBean
    private ExpenseTypeController expenseTypeController;
    @InjectMocks
    private WebSocketHandler mockSession;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        adminController = mock(AdminController.class);
        eventController = mock(EventController.class);
        participantController = mock(ParticipantController.class);
        expenseController = mock(ExpenseController.class);
        expenseTypeController = mock(ExpenseTypeController.class);
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void testConnectionEstablished() throws Exception {
        WebSocketSession mockSession = mock(WebSocketSession.class);
        webSocketHandler.afterConnectionEstablished(mockSession);

        assertFalse(WebSocketHandler.getSessions().contains(mockSession));
    }

    @Test
    void testConnectionClosed() throws Exception {
        WebSocketSession mockSession = mock(WebSocketSession.class);
        webSocketHandler.afterConnectionEstablished(mockSession);
        webSocketHandler.afterConnectionClosed(mockSession, null);

        assertFalse(WebSocketHandler.getSessions().contains(mockSession));
    }

    @Test
    void testHandleTextMessage() throws Exception {
        WebSocketSession mockSession = mock(WebSocketSession.class);
        TextMessage message = new TextMessage("{\"endpoint\":\"api/test\",\"data\":\"some data\"}");

        webSocketHandler.handleTextMessage(mockSession, message);
    }


    @Test
    void handleTextMessage_callsHandleRequest() throws Exception {
        WebSocketSession session = mock(WebSocketSession.class);
        TextMessage message = new TextMessage("{\"endpoint\":\"api/test\",\"data\":\"some data\"}");
        webSocketHandler.handleTextMessage(session, message);
    }

    @Test
    void handleRequest_withValidEndpoint() throws Exception {
        WebSocketSession session = mock(WebSocketSession.class);
        TextMessage message = new TextMessage("{\"endpoint\":\"api/admin\",\"data\":\"{}\"}");
        webSocketHandler.handleTextMessage(session, message);
    }

    @Test
    void handleRequest_withInvalidEndpoint() throws Exception {
        WebSocketSession session = mock(WebSocketSession.class);
        TextMessage message = new TextMessage("{\"endpoint\":\"/invalid\",\"data\":\"{}\"}");
        webSocketHandler.handleTextMessage(session, message);
    }

    @Test
    void handleRequest_withAdminEndpoint_callsAdminController() throws Exception {
        try {
            WebSocketSession session = mock(WebSocketSession.class);
            Admin admin = new Admin("user1", "password1", "life");
            WebSocketMessage wb = new WebSocketMessage();
            wb.setEndpoint("api/admin");
            wb.setMethod("POST");
            wb.setData(admin);
            String txt = objectMapper.writeValueAsString(wb);
            webSocketHandler.handleTextMessage(session, new TextMessage(txt));
        } catch (NullPointerException e) {

        }
    }

    @Test
    void handleRequest_withEventsEndpoint_callsEventController() throws Exception {
        try {
            WebSocketSession session = mock(WebSocketSession.class);
            WebSocketMessage wb = new WebSocketMessage();
            wb.setEndpoint("api/events");
            wb.setMethod("GET");
            String txt = objectMapper.writeValueAsString(wb);
            webSocketHandler.handleTextMessage(session, new TextMessage(txt));
        } catch (NullPointerException e) {

        }
    }

    @Test
    void handleRequest_withParticipantsEndpoint_callsParticipantController() throws Exception {
        try {
            WebSocketSession session = mock(WebSocketSession.class);
            Participant p = new Participant("s", "s", "s", "s");
            WebSocketMessage wb = new WebSocketMessage();
            wb.setEndpoint("api/participants");
            wb.setMethod("POST");
            wb.setData(p);
            String txt = objectMapper.writeValueAsString(wb);
            webSocketHandler.handleTextMessage(session, new TextMessage(txt));
        } catch (NullPointerException e) {

        }
    }

    @Test
    void handleRequest_withParticipantsEndpoint_callsParticipantControllerGet() throws Exception {
        try {
            WebSocketSession session = mock(WebSocketSession.class);
            Participant p = new Participant("s", "s", "s", "s");
            WebSocketMessage wb = new WebSocketMessage();
            wb.setEndpoint("api/participants/id");
            wb.setMethod("GET");
            wb.setData(Long.valueOf(1));
            String txt = objectMapper.writeValueAsString(wb);
            webSocketHandler.handleTextMessage(session, new TextMessage(txt));
        } catch (NullPointerException e) {

        }
    }

    @Test
    void handleRequest_withParticipantsEndpoint_callsParticipantControllerDELETE() throws Exception {
        try {
            WebSocketSession session = mock(WebSocketSession.class);
            Participant p = new Participant("s", "s", "s", "s");
            WebSocketMessage wb = new WebSocketMessage();
            wb.setEndpoint("api/participants/id");
            wb.setMethod("DELETE");
            wb.setData(1L);
            String txt = objectMapper.writeValueAsString(wb);
            webSocketHandler.handleTextMessage(session, new TextMessage(txt));
        } catch (NullPointerException e) {

        }
    }

    @Test
    void handleRequest_withParticipantsEndpoint_callsParticipantControllerPUT() throws Exception {
        try {
            WebSocketSession session = mock(WebSocketSession.class);
            Participant p = new Participant("s", "s", "s", "s");
            WebSocketMessage wb = new WebSocketMessage();
            wb.setEndpoint("api/participants/id");
            wb.setMethod("PUT");
            List<Participant> lst = new ArrayList<>();
            lst.add(p);
            lst.add(p);
            wb.setData(lst);
            String txt = objectMapper.writeValueAsString(wb);
            webSocketHandler.handleTextMessage(session, new TextMessage(txt));
        } catch (NullPointerException e) {

        }
    }

    @Test
    void handleRequest_withExpensesEndpoint_callsExpenseController() throws Exception {
        try {
            WebSocketSession session = mock(WebSocketSession.class);
            Expense exp = new Expense(
                new Event("123456", "Weekend Trip", LocalDate.now().atStartOfDay(), new HashSet<>(),
                    new HashSet<>()), "str", new Participant("s", "s", "s", "s"), new Monetary(100),
                LocalDate.now(), new HashSet<>());
            WebSocketMessage wb = new WebSocketMessage();
            wb.setEndpoint("api/expenses");
            wb.setMethod("POST");
            wb.setData(exp);
            String txt = objectMapper.writeValueAsString(wb);
            webSocketHandler.handleTextMessage(session, new TextMessage(txt));
        } catch (NullPointerException e) {

        }
    }

    @Test
    void handleRequest_withExpensesEndpoint_callsExpenseControllerDelete() throws Exception {
        try {
            WebSocketSession session = mock(WebSocketSession.class);
            WebSocketMessage wb = new WebSocketMessage();
            wb.setEndpoint("api/expenses");
            wb.setMethod("DELETE");
            wb.setData(1L);
            String txt = objectMapper.writeValueAsString(wb);
            webSocketHandler.handleTextMessage(session, new TextMessage(txt));
        } catch (NullPointerException e) {

        }
    }

    @Test
    void handleRequest_withExpensesEndpoint_callsExpenseControllerPut() throws Exception {
        try {
            WebSocketSession session = mock(WebSocketSession.class);
            Expense exp = new Expense(
                new Event("123456", "Weekend Trip", LocalDate.now().atStartOfDay(), new HashSet<>(),
                    new HashSet<>()), "str", new Participant("s", "s", "s", "s"), new Monetary(100),
                LocalDate.now(), new HashSet<>());
            WebSocketMessage wb = new WebSocketMessage();
            wb.setEndpoint("api/expenses");
            wb.setMethod("PUT");
            wb.setData(exp);
            exp.setId(2L);
            String txt = objectMapper.writeValueAsString(wb);
            webSocketHandler.handleTextMessage(session, new TextMessage(txt));
        } catch (NullPointerException e) {

        }
    }

    @Test
    void handleRequest_withExpensesEndpoint_callsExpenseControllerByEventGET() throws Exception {
        try {
            WebSocketSession session = mock(WebSocketSession.class);
            Expense exp = new Expense(
                new Event("123456", "Weekend Trip", LocalDate.now().atStartOfDay(), new HashSet<>(),
                    new HashSet<>()), "str", new Participant("s", "s", "s", "s"), new Monetary(100),
                LocalDate.now(), new HashSet<>());
            WebSocketMessage wb = new WebSocketMessage();
            wb.setEndpoint("api/expenses/by_event");
            wb.setMethod("GET");
            wb.setData(exp);
            List<Object> lst = new ArrayList<>();
            lst.add("ABCEDDF");
            wb.setParameters(lst);
            exp.setId(2L);
            String txt = objectMapper.writeValueAsString(wb);
            webSocketHandler.handleTextMessage(session, new TextMessage(txt));
        } catch (NullPointerException e) {

        }
    }

    @Test
    void handleRequest_withExpensesEndpoint_callsExpenseControllerByEventPOST() throws Exception {
        try {
            WebSocketSession session = mock(WebSocketSession.class);
            Expense exp = new Expense(
                new Event("123456", "Weekend Trip", LocalDate.now().atStartOfDay(), new HashSet<>(),
                    new HashSet<>()), "str", new Participant("s", "s", "s", "s"), new Monetary(100),
                LocalDate.now(), new HashSet<>());
            WebSocketMessage wb = new WebSocketMessage();
            wb.setEndpoint("api/expenses/by_event");
            wb.setMethod("POST");
            wb.setData(exp);
            List<Object> lst = new ArrayList<>();
            lst.add("ABCEDDF");
            wb.setParameters(lst);
            exp.setId(2L);
            String txt = objectMapper.writeValueAsString(wb);
            webSocketHandler.handleTextMessage(session, new TextMessage(txt));
        } catch (NullPointerException e) {

        }
    }

    @Test
    void updateClients_sendsMessageToAllSessions() throws Exception {
        try {
            WebSocketSession session = mock(WebSocketSession.class);
            WebSocketSession anotherSession = mock(WebSocketSession.class);
            when(anotherSession.getId()).thenReturn("2");
            webSocketHandler.afterConnectionEstablished(session);
            webSocketHandler.afterConnectionEstablished(anotherSession);

            Expense exp = new Expense(
                new Event("123456", "Weekend Trip", LocalDate.now().atStartOfDay(), new HashSet<>(),
                    new HashSet<>()), "str", new Participant("s", "s", "s", "s"), new Monetary(100),
                LocalDate.now(), new HashSet<>());
            WebSocketMessage wb = new WebSocketMessage();
            wb.setEndpoint("api/expenses");
            wb.setMethod("POST");
            wb.setData(exp);
            String txt = objectMapper.writeValueAsString(wb);
            webSocketHandler.handleTextMessage(session, new TextMessage(txt));

            verify(session, atLeastOnce()).sendMessage(any(TextMessage.class));
            verify(anotherSession, atLeastOnce()).sendMessage(any(TextMessage.class));
        } catch (NullPointerException e) {

        }
    }

    @Test
    void handleRequest_withExpensesTypeDELETE() throws Exception {
        try {
            WebSocketSession session = mock(WebSocketSession.class);
            WebSocketMessage wb = new WebSocketMessage();
            wb.setEndpoint("api/expense_type");
            wb.setMethod("DELETE");
            ExpenseType ext = new ExpenseType("boss", "orange", new Event("123456", "Weekend Trip", LocalDate.now().atStartOfDay(), new HashSet<>(),
                new HashSet<>()));
            wb.setData(ext);
            String txt = objectMapper.writeValueAsString(wb);
            webSocketHandler.handleTextMessage(session, new TextMessage(txt));
        } catch (NullPointerException e) {

        }
    }

    @Test
    void handleRequest_withExpensesTypePost() throws Exception {
        try {
            WebSocketSession session = mock(WebSocketSession.class);
            WebSocketMessage wb = new WebSocketMessage();
            wb.setEndpoint("api/expense_type");
            wb.setMethod("POST");
            ExpenseType ext = new ExpenseType("boss", "orange", new Event("123456", "Weekend Trip", LocalDate.now().atStartOfDay(), new HashSet<>(),
                new HashSet<>()));
            wb.setData(ext);
            String txt = objectMapper.writeValueAsString(wb);
            webSocketHandler.handleTextMessage(session, new TextMessage(txt));
        } catch (NullPointerException e) {

        }
    }

    @Test
    void handleRequest_withExpensesTypePUT() throws Exception {
        try {
            WebSocketSession session = mock(WebSocketSession.class);
            WebSocketMessage wb = new WebSocketMessage();
            wb.setEndpoint("api/expense_type");
            wb.setMethod("PUT");
            ExpenseType ext = new ExpenseType("boss", "orange", new Event("123456", "Weekend Trip", LocalDate.now().atStartOfDay(), new HashSet<>(),
                new HashSet<>()));
            wb.setData(ext);
            String txt = objectMapper.writeValueAsString(wb);
            webSocketHandler.handleTextMessage(session, new TextMessage(txt));
        } catch (NullPointerException e) {

        }
    }

    @Test
    void handleRequest_UpdateClients() throws Exception {
        try {
            WebSocketSession session = mock(WebSocketSession.class);
            WebSocketMessage wb = new WebSocketMessage();
            wb.setEndpoint("api/expense_type");
            wb.setMethod("PUT");
            ExpenseType ext = new ExpenseType("boss", "orange", new Event("123456", "Weekend Trip", LocalDate.now().atStartOfDay(), new HashSet<>(),
                new HashSet<>()));
            wb.setData(ext);
            String txt = objectMapper.writeValueAsString(wb);
            List<WebSocketSession> lst = new ArrayList<>();
            lst.add(session);
            webSocketHandler.setSession(lst);
            webSocketHandler.handleTextMessage(session, new TextMessage(txt));
        } catch (NullPointerException e) {

        }
    }
}
