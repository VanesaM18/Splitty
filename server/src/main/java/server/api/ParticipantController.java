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
     * Retrieves a participant by their ID.
     * @param id The ID of the participant to retrieve.
     * @return ResponseEntity containing the retrieved participant if found,
     * or a bad request response if the ID is invalid or the participant does not exist.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Participant> getById(@PathVariable("id") long id) {
        if (id < 0 || !repo.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(repo.findById(id).get());
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
    public ResponseEntity<String> update(@PathVariable("id") Long id,
                                         @RequestBody Participant updatedParticipant) {
        if (isNull(id) || !repo.existsById(id)) {
            return ResponseEntity.badRequest().body("Participant not found.");
        }
        if (updatedParticipant == null || isNullOrEmpty(updatedParticipant.getName())
                || isNullOrEmpty(updatedParticipant.getEmail())) {
            return ResponseEntity.badRequest().body("Invalid participant data.");
        }
        updatedParticipant.setId(id);
        try {
            Participant existingParticipant = repo.findById(id).orElse(null);
            if (existingParticipant != null) {
                existingParticipant.setName(updatedParticipant.getName());
                existingParticipant.setEmail(updatedParticipant.getEmail());

                repo.save(existingParticipant);
                return ResponseEntity.ok().body("Participant updated successfully.");
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating participant.");
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
