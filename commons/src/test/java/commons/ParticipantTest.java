package commons;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ParticipantTest {

    @Test
    public void getters() {
        Participant p =
                new Participant("George", "george123@gmail.com", "NL1234567890123456", "ABCDEFGH");
        assertEquals("George", p.getName());
        assertEquals("george123@gmail.com", p.getEmail());
        assertEquals("NL1234567890123456", p.getIban());
        assertEquals("ABCDEFGH", p.getBic());
    }
}
