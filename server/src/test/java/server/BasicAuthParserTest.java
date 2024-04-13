package server;

import org.junit.jupiter.api.Test;
import jakarta.persistence.Basic;
import server.BasicAuthParser.UsernamePassword;
import static org.junit.jupiter.api.Assertions.*;

class BasicAuthParserTest {

    @Test
    void parseNull() {
        String header = null;

        UsernamePassword parsed = BasicAuthParser.parse(header);
        assertEquals(null, parsed);
    }

    @Test
    void parseInvalidNotBasic() {
        String header = "Bearer asdasdsadasdsadas";

        UsernamePassword parsed = BasicAuthParser.parse(header);
        assertEquals(null, parsed);
    }

    @Test
    void parseInvalidNoTwoParts() {
        String header = "Basic";

        UsernamePassword parsed = BasicAuthParser.parse(header);
        assertEquals(null, parsed);
    }

    @Test
    void parseInvalidBase64ContainsNoColon() {
        String header = "Basic dGVzdA==";

        UsernamePassword parsed = BasicAuthParser.parse(header);
        assertEquals(null, parsed);
    }

    @Test
    void parseValid() {
        String header = "Basic QWxhZGRpbjpvcGVuc2VzYW1l";
        UsernamePassword correct = new UsernamePassword("Aladdin", "opensesame");

        UsernamePassword parsed = BasicAuthParser.parse(header);
        assertEquals(correct, parsed);
    }

    @Test
    void usernamePasswordEquals() {
        UsernamePassword up1 = new UsernamePassword("username", "password");
        UsernamePassword up2 = new UsernamePassword("username", "password");

        assertEquals(up1, up2);
    }

    @Test
    void usernamePasswordEqualsSame() {
        UsernamePassword up1 = new UsernamePassword("username", "password");

        assertEquals(up1, up1);
    }

    @Test
    void usernamePasswordEqualsDifferentUsername() {
        UsernamePassword up1 = new UsernamePassword("username", "password");
        UsernamePassword up2 = new UsernamePassword("username1", "password");

        assertNotEquals(up1, up2);
    }

    @Test
    void usernamePasswordEqualsDifferentPassword() {
        UsernamePassword up1 = new UsernamePassword("username", "password");
        UsernamePassword up2 = new UsernamePassword("username", "password1");

        assertNotEquals(up1, up2);
    }

    @Test
    void usernamePasswordHashcode() {
        UsernamePassword up1 = new UsernamePassword("username", "password");
        UsernamePassword up2 = new UsernamePassword("username", "password");

        assertEquals(up1.hashCode(), up2.hashCode());
    }
}
