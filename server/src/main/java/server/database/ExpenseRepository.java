package server.database;

import commons.Expense;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    /**
     * Get all the expenses by an event's id
     * @param eventId The event id
     * @return The expenses
     */
    List<Expense> getExpensesByEventId(String eventId);
}
