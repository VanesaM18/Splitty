package server.services;

import commons.Participant;
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
        var participant = participantRepository.findById(id);
        participantRepository.deleteById(id);
        return participant;
    }

    /**
     * updates a participant by its ID
     * @param existingParticipant existing participant details
     * @param updatedParticipant updated participant details
     * @return ResponseEntity with an appropriate status and
     * message indicating the result of the update operation
     */
    public boolean updateParticipant(commons.Participant existingParticipant,
                                                    commons.Participant updatedParticipant) {


        try {
            // Update the ID
            updatedParticipant.setId(existingParticipant.getId());

            // Update existing participant with new data
            existingParticipant.setName(updatedParticipant.getName());
            existingParticipant.setEmail(updatedParticipant.getEmail());

            // Save the updated participant
            participantRepository.save(existingParticipant);
            return true;
        } catch (Exception exception) {
            return false;
        }


    }

    /**
     *
     * @param updatedParticipant participant to verify.
     * @return true when ok.
     */
    public boolean checkUpdatedParticipant(Participant updatedParticipant) {
        return !(updatedParticipant == null || isNullOrEmpty(updatedParticipant.getName())
                || isNullOrEmpty(updatedParticipant.getEmail()));
    }

    /**
     *
     * @param id the id that shall be put under the verifycation operation.
     * @return true when ok, false when bad!
     */
    public boolean checkParticipantId(Long id) {
        return id != null && participantRepository.existsById(id);
    }

    /**
     *  Checks if a string is null or empty
     */
    private boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }
}
