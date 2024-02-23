package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class EventTest {

    Event event;
    LocalDateTime dateTime = LocalDateTime.of(1, 1, 1, 1, 1, 1);

    @BeforeEach
    void setup() {
        event = new Event(1L, "Test Event", "ABCDEF", dateTime, new HashSet<>());
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
        Event event2 = new Event(1L, "Test Event", "ABCDEF", dateTime, new HashSet<>());

        assertEquals(event, event2);
    }

    @Test
    void testNotEquals() {
        Event event2 = new Event(2L, "Different name", "ABCDEF", dateTime, new HashSet<>());

        assertNotEquals(event, event2);
    }

    @Test
    void testHashCode() {
        Event event2 = new Event(1L, "Test Event", "ABCDEF", dateTime, new HashSet<>());

        assertEquals(event.hashCode(), event2.hashCode());
    }
}