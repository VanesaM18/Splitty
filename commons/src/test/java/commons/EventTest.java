package commons;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

class EventTest {

    Event event;
    LocalDateTime dateTime = LocalDateTime.of(1, 1, 1, 1, 1, 1);

    @BeforeEach
    void setup() {
        event = new Event("ABCDEF","Test Event", dateTime, new HashSet<>());
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
        Event event2 = new Event("ABCDEF", "Test Event", dateTime, new HashSet<>());

        assertEquals(event, event2);
    }

    @Test
    void testNotEquals() {
        Event event2 = new Event("ABCDEF", "Different name", dateTime, new HashSet<>());

        assertNotEquals(event, event2);
    }

    @Test
    void testHashCode() {
        Event event2 = new Event("ABCDEF", "Test Event", dateTime, new HashSet<>());

        assertEquals(event.hashCode(), event2.hashCode());
    }
    @Test
    void finalCalculationTest(){
        Event event1 = new Event("WDKFDLS", "Event1", dateTime, new HashSet<>());
        Participant participant1 = new Participant("participant1", "email1", "iban1", "bic1");
        Participant participant2 = new Participant("participant2", "email2", "iban2", "bic2");
        Participant participant3 = new Participant("participant3", "email3", "iban3", "bic3");
        Participant participant4 = new Participant("participant4", "email4", "iban4", "bic4");
        Monetary amount1 = new Monetary(10);
        Monetary amount2 = new Monetary(20);
        var date = LocalDate.now();
        Set<Participant> set1 = new HashSet<>();
        set1.add(participant2);
        set1.add(participant3);
        Set<Participant> set2 = new HashSet<>();
        set2.add(participant1);
        set2.add(participant4);
        Set<Participant> set3 = new HashSet<>();
        set3.add(participant1);
        set3.add(participant2);
        set3.add(participant3);
        set3.add(participant4);
        event1.setParticipants(set3);
        Expense expense1 = new Expense(event1, "expense1", participant1, amount1, date, set1);
        Expense expense2 = new Expense(event1, "expense2", participant2, amount2, date, set2);
        Set<Expense> setExpense = new HashSet<>(List.of(expense1, expense2));
        event1.setExpenses(setExpense);
        Debt debt1 = new Debt(participant4, new Monetary(10), participant2);
        Debt debt2 = new Debt(participant3, new Monetary(5), participant2);
        List<Debt> expected = new ArrayList<>(List.of(debt1, debt2));
        assertEquals(expected, Event.finalCalculation(event1));
    }
}
