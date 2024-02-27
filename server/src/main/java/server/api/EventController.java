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

    public EventController(EventRepository repo) {
        this.repo = repo;
    }

    @GetMapping(path = {"", "/"})
    public List<Event> getAll() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Event> getById(@PathVariable("id") long id) {
        if (id < 0 || !repo.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(repo.findById(id).get());
    }

    @GetMapping("/invites/{inviteCode}")
    public ResponseEntity<Event> getByInviteCode(@PathVariable("inviteCode") String code) {
        if (isNullOrEmpty(code) || !repo.existsByInviteCodeEqualsIgnoreCase(code)) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(repo.findFirstByInviteCodeEqualsIgnoreCase(code));
    }

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
