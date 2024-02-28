package server.api;

import commons.Event;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.database.EventRepository;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventRepository repo;

    /**
     * Create a new event controller.
     * This controller contains all api endpoints that have to do with events.
     *
     * @param repo The repository used for creating, reading, updating and deleting events.
     */
    public EventController(EventRepository repo) {
        this.repo = repo;
    }

    /**
     * API Endpoint for getting a list of all events.
     *
     * @return a list of all events.
     */
    @GetMapping(path = {"", "/"})
    public List<Event> getAll() {
        return repo.findAll();
    }

    /**
     * API Endpoint for getting a certain event based on its ID.
     *
     * @param id The ID of the event to get.
     * @return the event with matching ID, if it exists.
     * If it does not exist or an invalid ID is given, a 404 error is returned.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Event> getById(@PathVariable("id") long id) {
        if (id < 0 || !repo.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(repo.findById(id).get());
    }

    /**
     * API Endpoint for getting a certain event based on its invite code.
     *
     * @param code The invite code of the event to get.
     * @return the event with matching invite code, if it exists.
     * If it does not exist or an invalid invite code is given, a 400 error is returned.
     */
    @GetMapping("/invites/{inviteCode}")
    public ResponseEntity<Event> getByInviteCode(@PathVariable("inviteCode") String code) {
        if (isNullOrEmpty(code) || !repo.existsByInviteCodeEqualsIgnoreCase(code)) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(repo.findFirstByInviteCodeEqualsIgnoreCase(code));
    }

    /**
     * Create a new Event.
     *
     * @param event The event to create.
     * @return The saved version of the event, or a 400 error page.
     */
    @PostMapping(path = {"", "/"})
    public ResponseEntity<Event> add(@RequestBody Event event) {
        // NOTE: The participant list must be empty, people can only be added to an event by using
        // the invite code.
        if (isNullOrEmpty(event.getName())
                || event.getDateTime() == null
                || (event.getParticipants() != null && !event.getParticipants().isEmpty())) {
            return ResponseEntity.badRequest().build();
        }

        // Generate an invite code
        event.generateInviteCode();

        Event saved = repo.save(event);
        return ResponseEntity.ok(saved);
    }

    private static boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }
}
