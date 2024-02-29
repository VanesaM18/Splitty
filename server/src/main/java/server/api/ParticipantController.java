package server.api;

import commons.Participant;
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
     *  Checks if a Long is null.
     */
    private static boolean isNull(Long s) {
        return s == null;
    }

}
