package server.api;

import commons.Participant;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.services.ParticipantService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/participants")
public class ParticipantController {
    private final ParticipantService participantService;

    /**
     * Create a new participant controller.
     * This controller contains all api endpoints that have to do with participants.
     *
     * @param participantService
     */
    public ParticipantController(ParticipantService participantService) {
        this.participantService = participantService;
    }

    /**
     * Gets all participant rows in our database
     * @return the list of admins
     */
    @GetMapping(path = { "", "/" })
    public List<Participant> getAll() {
        return participantService.getAllParticipants();
    }

    /**
     * Retrieves a participant by their ID.
     * @param id The ID of the participant to retrieve.
     * @return ResponseEntity containing the retrieved participant if found,
     * or a bad request response if the ID is invalid or the participant does not exist.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Participant> getById(@PathVariable("id") long id) {
        Optional<Participant> optional = participantService.getParticipantById(id);
        return optional.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    /**
     * Adds a participant.
     * @param participant the participant to be added
     * @return the added admin
     */
    @PostMapping(path = { "", "/" })
    public ResponseEntity<Participant> add(@RequestBody Participant participant) {
        Optional<Participant> optional = participantService.createParticipant(participant);
        return optional.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    /**
     * Deletes a participant if is exits by id.
     * @param id the id to be deleted
     * @return the status of the deletion
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable("id") Long id) {
        Optional<Participant> optional = participantService.deleteParticipantById(id);
        if (optional.isEmpty()) {
            return ResponseEntity.badRequest().body("Can't delete the participant.");
        }
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
        ResponseEntity<String> response =
                participantService.updateParticipant(id, updatedParticipant);
        if (response.getStatusCode() == HttpStatus.OK) {
            return ResponseEntity.ok().body(response.getBody());
        } else if (response.getStatusCode() == HttpStatus.BAD_REQUEST) {
            return ResponseEntity.badRequest().body(response.getBody());
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response.getBody());
        }
    }

}
