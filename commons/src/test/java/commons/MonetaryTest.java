package commons;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class MonetaryTest {
    @Test
    public void testConstructor() {
        var monetary = new Monetary(1223, "EUR");

        assertEquals(monetary.getInternalValue(), 1223);
        assertEquals(monetary.getUnit(), "EUR");
    }

    @Test
    public void testMinor() {
        var monetary = new Monetary(1234, "EUR");

        assertEquals(monetary.getMinor(), 34);
    }

    @Test
    public void testMajor() {
        var monetary = new Monetary(1234, "EUR");

        assertEquals(monetary.getMajor(), 12);
    }

    @Test
    public void testEqual() {
        var monetary1 = new Monetary(123, "EUR");
        var monetary2 = new Monetary(123, "EUR");

        assertEquals(monetary1, monetary2);
    }
}
