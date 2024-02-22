package commons;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AdminTest {
    @Test
    public void getters() {
        Admin ad = new Admin("mat50013", "Ca_01234535", "stefmatei22@gmail.com");
        assertEquals("George", ad.getUsername());
        assertEquals("stefmatei22@gmail.com", ad.getEmail());
        PasswordHasher tst = new PasswordHasher();
        assertEquals(tst.compute("Ca_01234535"), ad.getPassword());
    }
}