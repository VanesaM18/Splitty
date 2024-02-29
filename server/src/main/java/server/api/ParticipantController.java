package server.api;

import commons.Participant;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.database.ParticipantRepository;

import java.util.List;

@RestController
@RequestMapping("/api/participants")
public class ParticipantController {
    private final ParticipantRepository repo;

    /**
     * Create a new participant controller.
     * This controller contains all api endpoints that have to do with participants.
     *
     * @param repo The repository used for creating, reading, updating and deleting participants.
     */
    public ParticipantController(ParticipantRepository repo) {
        this.repo = repo;
    }

    /**
     * Gets all participant rows in our database
     * @return the list of admins
     */
    @GetMapping(path = { "", "/" })
    public List<Participant> getAll() {
        return repo.findAll();
    }

    /**
     * Adds a participant.
     * @param participant the participant to be added
     * @return the added admin
     */
    @PostMapping(path = { "", "/" })
    public ResponseEntity<Participant> add(@RequestBody Participant participant) {
        if (participant.getName() == null || participant.getName().equals("")) {
            return ResponseEntity.badRequest().build();
        }

        Participant saved = repo.save(participant);
        return ResponseEntity.ok(saved);
    }

    /**
     * Deletes a participant if is exits by id.
     * @param id the id to be deleted
     * @return the status of the deletion
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable("id") Long id) {
        if (isNull(id) || !repo.existsById(id)) {
            return ResponseEntity.badRequest().body("Can't delete the participant.");
        }
        repo.deleteById(id);
        return ResponseEntity.ok().body("Deleted successfully");
    }

    /**
     * Updates a participant by their ID.
     *
     * @param id The ID of the participant to update.
     * @param updatedParticipant The updated participant data.
     * @return ResponseEntity indicating the status of the update operation.
     */
    @PutMapping("/{id}")
    public ResponseEntity<String> update(@PathVariable("id") Long id, @RequestBody Participant updatedParticipant) {
        if (isNull(id) || !repo.existsById(id)) {
            return ResponseEntity.badRequest().body("Participant not found.");
        }

        // Validate the updated participant data
        if (updatedParticipant == null || isNullOrEmpty(updatedParticipant.getName()) || isNullOrEmpty(updatedParticipant.getEmail())) {
            return ResponseEntity.badRequest().body("Invalid participant data.");
        }

        // Update the id
        updatedParticipant.setId(id);

        // Update the participant
        try {
            Participant existingParticipant = repo.findById(id).orElse(null);
            if (existingParticipant != null) {
                // Update existing participant with new data
                existingParticipant.setName(updatedParticipant.getName());
                existingParticipant.setEmail(updatedParticipant.getEmail());

                // Save the updated participant
                repo.save(existingParticipant);
                return ResponseEntity.ok().body("Participant updated successfully.");
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating participant.");
        }
    }

    /**
     *  Checks if a Long is null.
     */
    private static boolean isNull(Long s) {
        return s == null;
    }

    /**
     *  Checks if a string is null or empty
     */
    private static boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }

}
