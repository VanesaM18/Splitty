package server.api;

import commons.Expense;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.database.ExpenseRepository;

import java.util.List;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {
    private final ExpenseRepository repo;

    /**
     * Create a new expenses controller. This controller manages expenses for events
     *
     * @param repo The expense repository
     */
    public ExpenseController(ExpenseRepository repo) {
        this.repo = repo;
    }

    /**
     * Get all the expenses belonging to an event
     * 
     * @param eventId The id of the event containing the expenses
     * @return The expenses belonging to the provided event or 404 if empty
     */
    @GetMapping("/by_event/{id}")
    public ResponseEntity<List<Expense>> getByEvent(@PathVariable("id") String eventId) {
        List<Expense> expenses = repo.getExpensesByEventInviteCode(eventId);
        if (expenses == null || expenses.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(expenses);
    }

    /**
     * Create a new expense
     * 
     * @param newExpense the expense to add
     * @return 204 No content if successful, else 400 bad request
     */
    @PostMapping("/")
    public ResponseEntity<String> addExpense(@RequestBody Expense newExpense) {
        if (newExpense == null
                || newExpense.getCreator() == null
                || newExpense.getEvent() == null || newExpense.getAmount() == null) {
            return ResponseEntity.badRequest().body("POSTed expense is incomplete");
        }
        repo.save(newExpense);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get an expense by id
     * 
     * @param id The id of the expense to fetch
     * @return The expense belonging to the id, 400 when id is negative, or 404 when
     *         not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Expense> getById(@PathVariable("id") long id) {
        if (id < 0) {
            return ResponseEntity.badRequest().build();
        }
        try {
            Expense expense = repo.getReferenceById(id);
            return ResponseEntity.ok(expense);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Update by id
     * 
     * @param id      The expense to update
     * @param expense The partial expense
     * @return the updated expense if successful,
     *         else 404 for not found or 400 for badly formatted request
     */
    @PutMapping("/{id}")
    public ResponseEntity<Object> updateById(@PathVariable("id") long id,
            @RequestBody Expense expense) {
        if (id < 0) {
            return ResponseEntity.badRequest().build();
        }
        Expense oldExpense;
        try {
            oldExpense = repo.getReferenceById(id);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
        if (expense.getAmount() != null) {
            oldExpense.setAmount(expense.getAmount());
        }
        if (expense.getEvent() != null) {
            return ResponseEntity
                    .badRequest()
                    .body("Cannot change event of expense, delete this and create a new one instead");
        }
        if (expense.getCreator() != null) {
            oldExpense.setReceiver(expense.getCreator());
        }
        return ResponseEntity.ok(repo.save(oldExpense));
    }

    /**
     * Delete an expense by id
     * 
     * @param id The id to delete
     * @return 204 if deleted successfully, 404 if not found
     *         and 400 if id incorrect
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteById(@PathVariable("id") long id) {
        if (id < 0) {
            return ResponseEntity.badRequest().build();
        }
        if (!repo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
