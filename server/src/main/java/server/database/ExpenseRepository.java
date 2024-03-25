package server.database;

import commons.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    /**
     * Get all the expenses by an event's id
     * @param inviteCode The event id (invite code)
     * @return The expenses
     */
    List<Expense> getExpensesByEventInviteCode(String inviteCode);
}
