package server.services;

import commons.Participant;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import server.database.ParticipantRepository;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ParticipantServiceTest {

    @Mock
    private ParticipantRepository participantRepository;

    @InjectMocks
    private ParticipantService participantService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllParticipants() {
        Participant participant = new Participant("name", "email", "iban", "bic");
        when(participantRepository.findAll()).thenReturn(List.of(participant));

        List<Participant> result = participantService.getAllParticipants();
        Assertions.assertEquals(List.of(participant), result);
    }

    @Test
    void testGetParticipantById() {
        Participant participant = new Participant("name", "email", "iban", "bic");
        when(participantRepository.findById(any())).thenReturn(Optional.of(participant));
        when(participantRepository.existsById(any())).thenReturn(true);

        Optional<Participant> result = participantService.getParticipantById(Long.valueOf(1));
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(participant, result.get());
    }

    @Test
    void testCreateParticipant() {
        Participant participant = new Participant("name", null, null, null);
        when(participantRepository.save(any())).thenReturn(participant);

        Optional<Participant> result = participantService.createParticipant(participant);
        Assertions.assertEquals(Optional.of(participant), result);
    }

    @Test
    void testDeleteParticipantById() {
        Participant participant = new Participant("name", "email", "iban", "bic");
        when(participantRepository.existsById(any())).thenReturn(true);
        when(participantRepository.findById(any())).thenReturn(Optional.of(participant));

        Optional<Participant> result = participantService.deleteParticipantById(Long.valueOf(1));
        verify(participantRepository).deleteById(any());
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(participant, result.get());
    }

    @Test
    void testUpdateParticipant() {
        Participant participant = new Participant("name", "email", "iban", "bic");
        when(participantRepository.save(any())).thenReturn(participant);

        boolean result = participantService.updateParticipant(participant, participant);
        Assertions.assertTrue(result);
    }

    @Test
    void testCheckUpdatedParticipant() {
        boolean result = participantService.checkUpdatedParticipant(new Participant("name", "email", null, null));
        Assertions.assertTrue(result);
    }

    @Test
    void testCheckParticipantId() {
        when(participantRepository.existsById(any())).thenReturn(true);

        boolean result = participantService.checkParticipantId(Long.valueOf(1));
        Assertions.assertTrue(result);
    }
}
