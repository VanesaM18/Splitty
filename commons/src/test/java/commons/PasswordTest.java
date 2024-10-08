package commons;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class PasswordTest {
    @Test
    public void equalCase() {
        PasswordHasher tst = new PasswordHasher();
        assertEquals(tst.compute("abi32hu32irn fgvbfd"), tst.compute("abi32hu32irn fgvbfd"));
    }

    @Test
    public void notEqualCase() {
        PasswordHasher tst = new PasswordHasher();
        assertNotEquals(tst.compute("abi32hu32irn fgvbfd"), tst.compute("abi32hu34irn fgvbfd"));
    }
}
