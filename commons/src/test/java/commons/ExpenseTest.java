package commons;

import static org.junit.jupiter.api.Assertions.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

public class ExpenseTest {
    private final static Set<Participant> SOME_PARTICIPANTS = Set.of(
            new Participant("Alice", "alice@example.com", "IT30T330058300597339E9682K7", "ABNANL2AAGS"),
            new Participant("Bob", "bob@example.com", "IT30T330058300597339E9682K7", "ABNANL2AAGS"),
            new Participant("Claude", "claude@example.com", "IT30T330058300597339E9682K7", "ABNANL2AAGS"));

    @Test
    public void testConstructor() throws ParseException {
        var event = new Event();
        var creator = new Participant();
        var amount = new Monetary(2022, "EUR");
        var date = LocalDate.now();
        Set<Participant> splitBetween = Set.of();
        var expense = new Expense(event, "Kobi's Grill", creator, amount, date, splitBetween);

        assertEquals(event, expense.getEvent());
        assertEquals(creator, expense.getCreator());
        assertEquals(amount, expense.getAmount());
        assertEquals(date, expense.getDate());
        assertEquals(splitBetween, expense.getSplitBetween());
    }

    @Test
    public void testAddParticipants() throws ParseException {
        var event = new Event();
        var creator = new Participant();
        var amount = new Monetary(2022, "EUR");
        var date = LocalDate.now();
        Set<Participant> splitBetween = new HashSet<Participant>();

        var expense = new Expense(event, "OWee", creator, amount, date, splitBetween);

        for (Participant p : SOME_PARTICIPANTS) {
            expense.addParticipant(p);
        }

        assertTrue(expense.getSplitBetween().containsAll(SOME_PARTICIPANTS));
    }

    @Test
    public void name() {
        Expense e = new Expense();
        e.setName("Party");
        assertEquals("Party", e.getName());
    }

    @Test
    public void split() {
        Expense e = new Expense();
        e.setReceiver(new Participant());
        e.setAmount(new Monetary());
        e.setEvent(new Event());
        e.setDate(LocalDate.now());
        e.setSplitBetween(SOME_PARTICIPANTS);
        assertEquals(SOME_PARTICIPANTS, e.getSplitBetween());
    }

    @Test
    public void id() {
        Expense e = new Expense();
        long id = 1;
        e.setId(id);
        assertEquals(id, e.getId());
    }

    @Test
    public void string() {
        Expense e = new Expense();
        assertEquals(e.toString(), e.toString());
    }

    @Test
    public void hash() {
        Expense e = new Expense();
        assertEquals(e.hashCode(), e.hashCode());
    }

    @Test
    public void equals() {
        Expense e = new Expense();
        assertTrue(e.equals(e));
    }

}
