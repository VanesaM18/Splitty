package server.database;

import commons.ExpenseType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExpenseTypeRepository extends JpaRepository<ExpenseType, Long> {
    /**
     * Get all the expenses by an event's id
     * @param inviteCode The event id (invite code)
     * @return The expenses
     */
    List<ExpenseType> getExpenseTypesByEventInviteCode(String inviteCode);
}
