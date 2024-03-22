package commons;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class AdminTest {
    @Test
    public void getters() {
        Admin ad = new Admin("mat50013", "Ca_01234535", "stefmatei22@gmail.com");
        assertEquals("mat50013", ad.getUsername());
        assertEquals("stefmatei22@gmail.com", ad.getEmail());
        PasswordHasher tst = new PasswordHasher();
        assertEquals(tst.compute("Ca_01234535"), ad.getPassword());
    }

    @Test
    public void equals() {
        Admin a = new Admin();
        assertTrue(a.equals(a));
    }

    @Test
    public void hash() {
        Admin a = new Admin();
        assertEquals(a.hashCode(), a.hashCode());
    }

    @Test
    public void string() {
        Admin a = new Admin();
        assertEquals(a.toString(), a.toString());
    }

}
