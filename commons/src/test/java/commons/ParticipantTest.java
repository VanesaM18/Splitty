package commons;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ParticipantTest {
    Participant p =
            new Participant();

    @Test
    public void name() {
        p.setName("George");
        assertEquals("George", p.getName());
    }
    @Test
    public void email() {
        p.setEmail("george123@gmail.com");
        assertEquals("george123@gmail.com", p.getEmail());
    }
    @Test
    public void iban() {
        p.setIban("NL1234567890123456");
        assertEquals("NL1234567890123456", p.getIban());

    }
    @Test
    public void bic() {
        p.setBic("ABCDEFGH");
        assertEquals("ABCDEFGH", p.getBic());
    }

    @Test
    public void id() {
        p.setId(1);
        assertEquals(1, p.getId());
    }

}
