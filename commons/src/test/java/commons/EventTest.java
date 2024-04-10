package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class EventTest {

    Event event;
    LocalDateTime dateTime = LocalDateTime.of(1, 1, 1, 1, 1, 1);

    @BeforeEach
    void setup() {
        event = new Event("ABCDEF","Test Event", dateTime, new HashSet<>(), new HashSet<>());
        event.setExpenses(new HashSet<>());
    }

    @Test
    void getName() {
        assertEquals("Test Event", event.getName());
    }

    @Test
    void setName() {
        event.setName("Changed name");
        assertEquals("Changed name", event.getName());
    }

    @Test
    void getDateTime() {
        assertEquals(dateTime, event.getDateTime());
    }

    @Test
    void setDateTime() {
        LocalDateTime newDateTime = LocalDateTime.of(2, 2, 2, 2, 2, 2);
        event.setDateTime(newDateTime);
        assertEquals(newDateTime, event.getDateTime());
    }

    @Test
    void getParticipants() {
        assertEquals(new HashSet<>(), event.getParticipants());
    }

    @Test
    void setParticipants() {
        TreeSet<Participant> newParticipants = new TreeSet<>();
        event.setParticipants(newParticipants);
        assertEquals(newParticipants, event.getParticipants());
    }

    @Test
    void addParticipant() {
        int startingSize = event.getParticipants().size();
        event.addParticipant(new Participant("name", "email", "iban", "bic"));
        assertEquals(startingSize + 1, event.getParticipants().size());
    }

    @Test
    void removeParticipant() {
        int startingSize = event.getParticipants().size();
        Participant participant = new Participant("name", "email", "iban", "bic");
        event.addParticipant(participant);
        event.removeParticipant(participant);
        assertEquals(startingSize, event.getParticipants().size());
    }

    @Test
    void getInviteCode() {
        assertEquals("ABCDEF", event.getInviteCode());
    }

    @Test
    void setInviteCode() {
        event.setInviteCode("New value");
        assertEquals("New value", event.getInviteCode());
    }

    @Test
    void generateInviteCode() {
        event.setInviteCode("");
        event.generateInviteCode();
        assertNotEquals("", event.getInviteCode());
    }

    @Test
    void testEquals() {
        Event event2 = new Event("ABCDEF", "Test Event", dateTime, new HashSet<>(), new HashSet<>());
        event2.setExpenses(new HashSet<>());
        assertEquals(event, event2);
    }

    @Test
    void testNotEquals() {
        Event event2 = new Event("ABCDEF", "Different name", dateTime, new HashSet<>(), new HashSet<>());

        assertNotEquals(event, event2);
    }

    @Test
    void testHashCode() {
        Event event2 = new Event("ABCDEF", "Test Event", dateTime, new HashSet<>(), new HashSet<>());

        assertEquals(event.hashCode(), event2.hashCode());
    }

    @Test
    void addTypeAndRemoveTag() {
        ExpenseType type = new ExpenseType("Food", "orange", event);
        event.addType(type);
        assertTrue(event.getTags().contains(type));

        event.removeTag(type);
        assertFalse(event.getTags().contains(type));
    }

    @Test
    void setAndGetTags() {
        Set<ExpenseType> tags = new HashSet<>();
        tags.add(new ExpenseType("Food", "orange", event));
        event.setTags(tags);

        assertEquals(tags, event.getTags());
    }

    @Test
    void setAndGetExpenses() {
        Set<Expense> expenses = new HashSet<>();
        expenses.add(new Expense());
        event.setExpenses(expenses);

        assertEquals(expenses, event.getExpenses());
    }

    @Test
    void getCreationAndLastUpdateTime() {
        assertNotNull(event.getCreationTime());
        assertNotNull(event.getLastUpdateTime());
    }
    @Test
    void setLastUpdateTime() {
        LocalDateTime newUpdateTime = LocalDateTime.now().plusDays(1);
        event.setLastUpdateTime(newUpdateTime);
        assertEquals(newUpdateTime, event.getLastUpdateTime());
    }

    @Test
    void calculatePayments() {
        Participant participant1 = new Participant("Participant 1", "email1@example.com", "IBAN1", "BIC1");
        Participant participant2 = new Participant("Participant 2", "email2@example.com", "IBAN2", "BIC2");
        HashSet<Participant> participants = new HashSet<>();
        participants.add(participant1);
        participants.add(participant2);
        Event event = new Event("INVITECODE", "Sample Event", LocalDate.now().atStartOfDay(), participants, new HashSet<>());
        event.setExpenses(new HashSet<>());
        Monetary amount = new Monetary(10000);
        Expense expense = new Expense(event, "Dinner", participant1, amount, LocalDate.now(), participants);
        event.getExpenses().add(expense);
        Map<Map<Participant, Participant>, Monetary> payments = Event.calculatePayments(event);
        assertFalse(payments.isEmpty(), "Payments map should contain entries.");

        payments.forEach((participantsMap, monetaryAmount) -> {
            participantsMap.forEach((debtor, creditor) -> {
                if (debtor.equals(participant2) && creditor.equals(participant1)) {
                    assertEquals(5000, monetaryAmount.getInternalValue(),
                        "Participant 2 should owe Participant 1 half the expense.");
                }
            });
        });
    }
    @Test
    void populateDebts() {
        List<Map.Entry<Participant, Long>> sortedEntries = new ArrayList<>();
        Map<Participant, Long> debtors = new HashMap<>();
        Map<Participant, Long> creditors = new HashMap<>();

        Participant participant1 = new Participant("Participant 1", "email1@example.com", "IBAN1", "BIC1");
        Participant participant2 = new Participant("Participant 2", "email2@example.com", "IBAN2", "BIC2");

        sortedEntries.add(new AbstractMap.SimpleEntry<>(participant1, -100L));
        sortedEntries.add(new AbstractMap.SimpleEntry<>(participant2, 100L));

        Event.populateDebts(sortedEntries, debtors, creditors);

        assertTrue(debtors.containsKey(participant1) && debtors.get(participant1) == -100);
        assertTrue(creditors.containsKey(participant2) && creditors.get(participant2) == 100);
    }

    @Test
    void paymentsToDebts() {
        Participant participant1 = new Participant("Alice", "alice@example.com", "IBAN1", "BIC1");
        Participant participant2 = new Participant("Bob", "bob@example.com", "IBAN2", "BIC2");
        Participant participant3 = new Participant("Charlie", "charlie@example.com", "IBAN3", "BIC3");
        participant1.setId(1L);
        participant1.setId(2L);
        participant1.setId(3L);
        Set<Participant> participants = new HashSet<>(Arrays.asList(participant1, participant2, participant3));
        event = new Event("123456", "Weekend Trip", LocalDate.now().atStartOfDay(), participants, new HashSet<>());
        event.setExpenses(new HashSet<>());
        Expense dinner = new Expense(event, "Dinner", participant1, new Monetary(6000), LocalDate.now(), new HashSet<>(Arrays.asList(participant1, participant2, participant3)));
        Expense taxi = new Expense(event, "Taxi Ride", participant2, new Monetary(3000), LocalDate.now(), new HashSet<>(Arrays.asList(participant1, participant3)));
        event.getExpenses().addAll(Arrays.asList(dinner, taxi));

        List<Debt> debts = event.paymentsToDebt(event);

        assertNotNull(debts, "Debts list should not be null.");
        assertFalse(debts.isEmpty(), "Debts list should not be empty.");
        assertEquals(1, debts.size(), "There should be one debt entry.");
    }
}
