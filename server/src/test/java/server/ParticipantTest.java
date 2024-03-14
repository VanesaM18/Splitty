package server;

import com.fasterxml.jackson.databind.ObjectMapper;
import commons.Participant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import server.api.ParticipantController;
import server.database.ParticipantRepository;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ParticipantController.class)
public class ParticipantTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ParticipantRepository participantRepository;

    private Participant participant;

    @BeforeEach
    void setUp() {
        participant = new Participant("John Doe", "johndoe@example.com", "IBAN12345", "BIC67890");
    }

    @Test
    void getAllParticipantsShouldReturnParticipantList() throws Exception {
        Mockito.when(participantRepository.findAll()).thenReturn(Arrays.asList(participant));

        mockMvc.perform(get("/api/participants"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].name").value(participant.getName()))
            .andExpect(jsonPath("$[0].email").value(participant.getEmail()))
            .andExpect(jsonPath("$[0].iban").value(participant.getIban()))
            .andExpect(jsonPath("$[0].bic").value(participant.getBic()));
    }

    @Test
    void getParticipantByIdShouldReturnParticipant() throws Exception {
        Mockito.when(participantRepository.existsById(anyLong())).thenReturn(true);
        Mockito.when(participantRepository.findById(anyLong())).thenReturn(Optional.of(participant));

        mockMvc.perform(get("/api/participants/{id}", participant.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value(participant.getName()))
            .andExpect(jsonPath("$.email").value(participant.getEmail()))
            .andExpect(jsonPath("$.iban").value(participant.getIban()))
            .andExpect(jsonPath("$.bic").value(participant.getBic()));
    }

    @Test
    void addParticipantShouldReturnSavedParticipant() throws Exception {
        Mockito.when(participantRepository.save(any(Participant.class))).thenReturn(participant);

        mockMvc.perform(post("/api/participants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(participant)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value(participant.getName()))
            .andExpect(jsonPath("$.email").value(participant.getEmail()))
            .andExpect(jsonPath("$.iban").value(participant.getIban()))
            .andExpect(jsonPath("$.bic").value(participant.getBic()));
    }

    @Test
    void updateParticipantShouldReturnSuccessStatus() throws Exception {
        Mockito.when(participantRepository.existsById(anyLong())).thenReturn(true);
        Mockito.when(participantRepository.findById(anyLong())).thenReturn(Optional.of(participant));
        Mockito.when(participantRepository.save(any(Participant.class))).thenReturn(participant);

        Participant updatedParticipant = new Participant("Jane Doe", "janedoe@example.com", "IBAN54321", "BIC09876");

        mockMvc.perform(put("/api/participants/{id}", participant.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(updatedParticipant)))
            .andExpect(status().isOk())
            .andExpect(content().string("Participant updated successfully."));
    }

    @Test
    void deleteParticipantShouldReturnSuccessStatus() throws Exception {
        Mockito.when(participantRepository.existsById(anyLong())).thenReturn(true);

        mockMvc.perform(delete("/api/participants/{id}", participant.getId()))
            .andExpect(status().isOk())
            .andExpect(content().string("Deleted successfully"));
    }

    @Test
    void getParticipantByInvalidIdShouldReturnBadRequest() throws Exception {
        Mockito.when(participantRepository.existsById(anyLong())).thenReturn(false);

        mockMvc.perform(get("/api/participants/{id}", -1))
            .andExpect(status().isBadRequest());
    }

    @Test
    void addParticipantWithMissingFieldsShouldReturnBadRequest() throws Exception {
        Participant incompleteParticipant = new Participant("", "", "", "");

        mockMvc.perform(post("/api/participants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(incompleteParticipant)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void updateNonExistentParticipantShouldReturnNotFound() throws Exception {
        Mockito.when(participantRepository.existsById(anyLong())).thenReturn(false);

        mockMvc.perform(put("/api/participants/{id}", anyLong())
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(participant)))
            .andExpect(status().isBadRequest())
            .andExpect(content().string("Participant not found."));
    }
}
