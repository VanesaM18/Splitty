package commons;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExpenseTypeTest {
    LocalDateTime dateTime = LocalDateTime.of(1, 1, 1, 1, 1, 1);
    Event event = new Event("ABCDEF","Test Event", dateTime, new HashSet<>(), new HashSet<>());
    ExpenseType tag = new ExpenseType("food", "red", event);

    @Test
    public void setName() {
        tag.setName("travel");
        assertEquals("travel", tag.getName());
    }

    @Test
    public void setColor() {
        tag.setColor("blue");
        assertEquals("blue", tag.getColor());
    }

    @Test
    public void setEvent() {
        Event event2 = new Event("ABCDEF","Test Event 2", dateTime, new HashSet<>(), new HashSet<>());

        tag.setEvent(event2);
        assertEquals(event2, tag.getEvent());
    }

    @Test
    public void setId() {
        tag.setId(1);
        assertEquals(1, tag.getId());
    }

    @Test
    public void hash() {
        ExpenseType tag2 = new ExpenseType();
        assertEquals(tag2.hashCode(), tag2.hashCode());
    }

    @Test
    public void string() {
        assertEquals(tag.toString(), tag.toString());
    }

    @Test
    public void equals() {
        assertEquals(tag,tag);
    }
}
