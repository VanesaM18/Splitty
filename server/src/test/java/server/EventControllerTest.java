package server;

import com.fasterxml.jackson.databind.ObjectMapper;
import commons.Event;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import server.api.EventController;
import server.services.EventService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EventController.class)
public class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EventService eventService;

    @Test
    public void getAllEvents_Authenticated_ReturnsEventsList() throws Exception {
        var auth = "Basic QWxhZGRpbjpkOWZiOTJlM2JiZTY1YmUxZjFhYWQ0YTgyZWVmNDU2N2Y3YTFlYmUyY2QxMTBjODA0OWI5Njk4YmU3YTcwYzg4";
        Event event = new Event("ABC123", "Event Name", LocalDateTime.now(), Collections.emptySet());
        given(eventService.getAllEvents()).willReturn(List.of(event));
        given(eventService.isAuthenticated(auth)).willReturn(true);

        mockMvc.perform(get("/api/events")
                .header("Authorization", auth))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].inviteCode").value(event.getInviteCode()));
    }

    @Test
    public void getEventById_NotFound_Returns400() throws Exception {
        given(eventService.getEventByInviteCode("NOT_EXIST")).willReturn(Optional.empty());

        mockMvc.perform(get("/api/events/NOT_EXIST"))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void deleteEvent_Unauthorized_ReturnsBadRequest() throws Exception {
        mockMvc.perform(delete("/api/events/ABC123")
                .header("Authorization", "wrong_header"))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void addEvent_Success_ReturnsEvent() throws Exception {
        Event event = new Event("NEWCODE", "New Event", LocalDateTime.now(), Collections.emptySet());
        given(eventService.createEvent(any(Event.class))).willReturn(Optional.of(event));

        mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(event)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.inviteCode").value("NEWCODE"));
    }
}
