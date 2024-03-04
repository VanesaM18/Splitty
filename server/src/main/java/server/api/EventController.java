package server.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
     * API Endpoint for getting a JSON dump of all events.
     *
     * @return ResponseEntity with the JSON dump in the response body.
     */
    @GetMapping("/jsonDump")
    public ResponseEntity<String> getJsonDump() {
        List<Event> events = repo.findAll();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        try {
            String jsonDump = objectMapper.writeValueAsString(events);
//            HttpHeaders headers = new HttpHeaders();
//            headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
//            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=events_dump.json");
//            headers.add("Access-Control-Expose-Headers", "Content-Disposition");
            return ResponseEntity.ok().body(jsonDump); //.headers(headers)
        } catch (JsonProcessingException e) {
            //TODO log error
            return ResponseEntity.status(500).body("Error generating JSON dump");
        }
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
     * @param updatedEvent The event to update.
     * @param id The ID of the event to update.
     * @return The updated version of the event, or a 400 error page.
     */
    @PutMapping(path = { "/{id}"})
    public ResponseEntity<Event> update(@PathVariable("id") String id,
                                        @RequestBody Event updatedEvent) {
        if (isNullOrEmpty(updatedEvent.getName())
            || updatedEvent.getDateTime() == null
            || !id.equals(updatedEvent.getInviteCode())) {
            return ResponseEntity.badRequest().build();
        }

        return repo.findById(id).map(existingEvent -> {
            existingEvent.setName(updatedEvent.getName());
            existingEvent.setDateTime(updatedEvent.getDateTime());

            if (updatedEvent.getParticipants() != null) {
                existingEvent.getParticipants().clear();
                existingEvent.getParticipants().addAll(updatedEvent.getParticipants());
            }

            Event savedEvent = repo.save(existingEvent);
            return ResponseEntity.ok(savedEvent);
        }).orElse(ResponseEntity.notFound().build());
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
