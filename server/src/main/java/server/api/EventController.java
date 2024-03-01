package server.api;

import commons.Admin;
import commons.Event;
import commons.PasswordHasher;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.BasicAuthParser;
import server.database.AdminRepository;
import server.database.EventRepository;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventRepository repo;
    private final AdminRepository adminRepository;

    /**
     * Create a new event controller.
     * This controller contains all api endpoints that have to do with events.
     *
     * @param repo The repository used for creating, reading, updating and deleting events.
     * @param adminRepository The repository used for checking authentication.
     */
    public EventController(EventRepository repo, AdminRepository adminRepository) {
        this.repo = repo;
        this.adminRepository = adminRepository;
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
     * The ID of an Event is equal to its invite code.
     *
     * @param id The ID of the event to get.
     * @return the event with matching ID, if it exists.
     * If it does not exist or an invalid ID is given, a 404 error is returned.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Event> getById(@PathVariable("id") String id) {
        if (isNullOrEmpty(id) || !repo.existsByInviteCodeEqualsIgnoreCase(id)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(repo.findById(id).get());
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
        // Check if the invite code is unique
        while (repo.existsByInviteCodeEqualsIgnoreCase(event.getInviteCode())) {
            event.generateInviteCode();
        }

        Event saved = repo.save(event);
        return ResponseEntity.ok(saved);
    }

    /**
     * Update a pre-existing event
     *
     * @param event The event to update.
     * @param id The ID of the event to update.
     * @return The updated version of the event, or a 400 error page.
     */
    @PutMapping(path = { "/{id}"})
    public ResponseEntity<Event> update(@PathVariable("id") String id, @RequestBody Event event) {
        if (isNullOrEmpty(event.getName())
                || event.getDateTime() == null
                || (event.getParticipants() != null && !event.getParticipants().isEmpty())) {
            return ResponseEntity.badRequest().build();
        }

        // Make sure the ID of the event we are editing is the ID from the URL.
        if (!id.equals(event.getInviteCode())) {
            return ResponseEntity.badRequest().build();
        }

        Event saved = repo.save(event);
        return ResponseEntity.ok(saved);
    }

    /**
     * Delete an event based on its ID.
     *
     * @param id The ID of the event to delete.
     * @param auth The authorization header.
     * @return a 200 OK on success, or a 400 error page on failure.
     */
    @DeleteMapping(path = {"/{id}"})
    public ResponseEntity<String> delete(
            @PathVariable("id") String id,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String auth
    ) {
        if (isNullOrEmpty(id) || !repo.existsByInviteCodeEqualsIgnoreCase(id)) {
            return ResponseEntity.badRequest().build();
        }

        // This is a protected API endpoint.
        // Check that authorization headers were provided.
        var login = BasicAuthParser.parse(auth);
        if (login == null) {
            return ResponseEntity.badRequest().build();
        }

        // Calculate the password hash
        PasswordHasher hasher = new PasswordHasher();
        String hash = hasher.compute(login.getPassword());

        // Check if the admin exists and the password matches.
        Admin admin = adminRepository.findById(login.getUsername()).orElse(null);
        if (admin == null || !hash.equals(admin.getPassword())) {
            return ResponseEntity.badRequest().build();
        }

        repo.deleteById(id);
        return ResponseEntity.ok("Deleted");
    }

    private static boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }
}
