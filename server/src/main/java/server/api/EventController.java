package server.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import commons.Event;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final server.services.EventService eventService;

    /**
     * Create a new event controller. This controller contains all api endpoints that have to do
     * with events.
     *
     * @param eventService
     */
    public EventController(server.services.EventService eventService) {
        this.eventService = eventService;
    }

    /**
     * API Endpoint for getting a list of all events.
     *
     * @param auth The authorization header.
     * @return a list of all events.
     */
    @GetMapping(path = {"", "/"})
    public ResponseEntity<List<Event>> getAll(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String auth) {
        // This is a protected API endpoint.
        List<Event> events = eventService.getAllEvents();
        if (!eventService.isAuthenticated(auth)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(events);
    }

    /**
     * API Endpoint for getting a JSON dump of all events.
     *
     * @return ResponseEntity with the JSON dump in the response body.
     */
    @GetMapping("/jsonDump")
    public ResponseEntity<String> getJsonDump() {
        List<Event> events = eventService.getAllEvents();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        try {
            String jsonDump = objectMapper.writeValueAsString(events);
            // HttpHeaders headers = new HttpHeaders();
            // headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
            // headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment;
            // filename=events_dump.json");
            // headers.add("Access-Control-Expose-Headers", "Content-Disposition");
            return ResponseEntity.ok().body(jsonDump); // .headers(headers)
        } catch (JsonProcessingException e) {
            // TODO log error
            return ResponseEntity.status(500).body("Error generating JSON dump");
        }
    }

    /**
     * API Endpoint for getting a certain event based on its ID. The ID of an Event is equal to its
     * invite code.
     *
     * @param id The ID of the event to get.
     * @return the event with matching ID, if it exists. If it does not exist or an invalid ID is
     *         given, a 404 error is returned.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Event> getById(@PathVariable("id") String id) {
        Optional<Event> optional = eventService.getEventByInviteCode(id);
        return optional.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().build());
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
        // the invite code. (checked in service)
        Optional<Event> saved = eventService.createEvent(event);
        return saved.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    /**
     * Update a pre-existing event
     *
     * @param updatedEvent The event to update.
     * @param id The ID of the event to update.
     * @return The updated version of the event, or a 400 error page.
     */
    @PutMapping(path = {"/{id}"})
    public ResponseEntity<Event> update(@PathVariable("id") String id,
            @RequestBody Event updatedEvent) {
        Optional<Event> optionalUpdatedEvent = eventService.updateEvent(id, updatedEvent);
        return optionalUpdatedEvent.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Delete an event based on its ID.
     *
     * @param id The ID of the event to delete.
     * @param auth The authorization header.
     * @return a 200 OK on success, or a 400 error page on failure.
     */
    @DeleteMapping(path = {"/{id}"})
    public ResponseEntity<String> delete(@PathVariable("id") String id,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String auth) {
        // This is a protected API endpoint.
        if (!eventService.isAuthenticated(auth)) {
            return ResponseEntity.badRequest().build();
        }
        Optional<Event> optional = eventService.deleteEvent(id);
        if (optional.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok("Deleted");
    }

}
