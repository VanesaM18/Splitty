package commons;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PasswordGeneratorTest {
    @Test
    public void isStrongEnough() {
        PasswordGenerator tst = new PasswordGenerator(8);
        String pass = tst.generate();
        assertTrue(pass.length() >= 8, "Password length should be at least 8 characters");
        assertTrue(pass.matches(".*[A-Z].*"), "Password should contain at least one uppercase letter");
        assertTrue(pass.matches(".*[a-z].*"), "Password should contain at least one lowercase letter");
        assertTrue(pass.matches(".*[!@#$%^&*_=+-/.?<>)].*"), "Password should contain at least one special character");
    }
}
