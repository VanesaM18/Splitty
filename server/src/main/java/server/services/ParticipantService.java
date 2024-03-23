package server.services;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import server.database.ParticipantRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ParticipantService {
    private final ParticipantRepository participantRepository;

    /**
     * constructs a new ParticipantService with the provided ParticipantRepository
     * @param participantRepository repository used for participant-related operations
     */
    public ParticipantService(ParticipantRepository participantRepository) {
        this.participantRepository = participantRepository;
    }

    /**
     * gets a list of all participants
     * @return list of all participants
     */
    public List<commons.Participant> getAllParticipants() {
        return participantRepository.findAll();
    }

    /**
     * gets a participant by its ID
     * @param id ID of the participant to retrieve
     * @return an Optional containing the participant if found, or empty if not found
     */
    public Optional<commons.Participant> getParticipantById(Long id) {
        if (id < 0 || !participantRepository.existsById(id)) {
            return Optional.empty();
        }
        return participantRepository.findById(id);
    }

    /**
     * creates a new participant
     * @param participant participant to create
     * @return an Optional containing the created participant if successful, or empty if not
     */
    public Optional<commons.Participant> createParticipant(commons.Participant participant) {
        if (participant.getName() == null || participant.getName().equals("")) {
            return Optional.empty();
        }
        return Optional.of(participantRepository.save(participant));
    }

    /**
     * deletes a participant by its ID
     * @param id ID of the participant to delete
     * @return an Optional containing the deleted participant if successful, or empty if not found
     */
    public Optional<commons.Participant> deleteParticipantById(Long id) {
        if (id == null || !participantRepository.existsById(id)) {
            return Optional.empty();
        }
        participantRepository.deleteById(id);
        return Optional.of(new commons.Participant());
    }

    /**
     * updates a participant by its ID
     * @param id                 ID of the participant to update
     * @param updatedParticipant updated participant details
     * @return ResponseEntity with an appropriate status and
     * message indicating the result of the update operation
     */
    public ResponseEntity<String> updateParticipant(Long id,
                                                    commons.Participant updatedParticipant) {
        if (id == null || !participantRepository.existsById(id)) {
            return ResponseEntity.badRequest().body("Participant not found.");
        }

        // Validate the updated participant data
        if (updatedParticipant == null || isNullOrEmpty(updatedParticipant.getName())
                || isNullOrEmpty(updatedParticipant.getEmail())) {
            return ResponseEntity.badRequest().body("Invalid participant data.");
        }

        // Update the ID
        updatedParticipant.setId(id);

        // Update the participant
        try {
            Optional<commons.Participant> existingParticipantOptional =
                    participantRepository.findById(id);
            if (existingParticipantOptional.isPresent()) {
                commons.Participant existingParticipant = existingParticipantOptional.get();
                // Update existing participant with new data
                existingParticipant.setName(updatedParticipant.getName());
                existingParticipant.setEmail(updatedParticipant.getEmail());

                // Save the updated participant
                participantRepository.save(existingParticipant);
                return ResponseEntity.ok("Participant updated successfully.");
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating participant.");
        }
    }

    /**
     *  Checks if a string is null or empty
     */
    private boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }
}
