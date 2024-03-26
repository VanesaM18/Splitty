package server.api;

import commons.Event;
import commons.ExpenseType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.database.EventRepository;
import server.database.ExpenseTypeRepository;

import java.util.List;

@RestController
@RequestMapping("/api/expense_type")
public class ExpenseTypeController {
    private final ExpenseTypeRepository repo;
    private final EventRepository eventRepo;

    /**
     * Create a new expense type controller. This controller manages expense types for events
     *
     * @param repo      The expense typerepository
     * @param eventRepo The event repository
     */
    public ExpenseTypeController(ExpenseTypeRepository repo, EventRepository eventRepo) {
        this.repo = repo;
        this.eventRepo = eventRepo;
    }

    /**
     * Create a new expense type, supplying the event id in the URL
     *
     * @param eventId the ID of the event
     * @param newExpenseType the expense type to add
     * @return 204 No content if successful, else 400 bad request
     */
    @PostMapping("/by_event/{id}")
    public ResponseEntity<String> add(@PathVariable("id") String eventId,
                                      @RequestBody ExpenseType newExpenseType) {
        if (newExpenseType == null) {
            return ResponseEntity.badRequest().body("POSTed expense is incomplete");
        }
        Event event = eventRepo.getReferenceById(eventId);
        newExpenseType.setEvent(event);
        repo.save(newExpenseType);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get all the expenses belonging to an event
     *
     * @param eventId The id of the event containing the expenses
     * @return The expenses belonging to the provided event or 404 if empty
     */
    @GetMapping("/by_event/{id}")
    public ResponseEntity<List<ExpenseType>> getByEvent(@PathVariable("id") String eventId) {
        List<ExpenseType> tags = repo.getExpenseTypesByEventInviteCode(eventId);
        if (tags == null || tags.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(tags);
    }
}
