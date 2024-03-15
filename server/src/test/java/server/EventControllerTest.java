package server;

import com.fasterxml.jackson.databind.ObjectMapper;
import commons.Admin;
import commons.Event;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import server.api.EventController;
import server.database.AdminRepository;
import server.database.EventRepository;

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
    private EventRepository eventRepository;

    @MockBean
    private AdminRepository adminRepository;

    @Test
    public void getAllEvents_Authenticated_ReturnsEventsList() throws Exception {
        Event event = new Event("ABC123", "Event Name", LocalDateTime.now(), Collections.emptySet());
        given(eventRepository.findAll()).willReturn(List.of(event));
        given(adminRepository.findById("Aladdin")).willReturn(Optional.of(new Admin("Aladdin", "opensesame", "")));

        mockMvc.perform(get("/api/events")
                .header("Authorization", "Basic QWxhZGRpbjpkOWZiOTJlM2JiZTY1YmUxZjFhYWQ0YTgyZWVmNDU2N2Y3YTFlYmUyY2QxMTBjODA0OWI5Njk4YmU3YTcwYzg4"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].inviteCode").value(event.getInviteCode()));
    }

    @Test
    public void getEventById_NotFound_Returns404() throws Exception {
        given(eventRepository.existsByInviteCodeEqualsIgnoreCase("NOT_EXIST")).willReturn(false);

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
        given(eventRepository.save(any(Event.class))).willReturn(event);

        mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(event)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.inviteCode").value("NEWCODE"));
    }
}
