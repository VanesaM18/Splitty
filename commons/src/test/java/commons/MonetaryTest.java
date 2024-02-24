package commons;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Currency;

import org.junit.jupiter.api.Test;

public class MonetaryTest {

    @Test
    public void testConstructor() {
        var monetary = new Monetary(42);
        assertEquals(42, monetary.getInternalValue());
        // We live in Europe, baby
        assertEquals("EUR", monetary.getCurrency().getCurrencyCode());
    }

    @Test
    public void testConstructorWithCurrency() {
        Currency euro = Currency.getInstance("EUR");
        var monetary = new Monetary(42, euro);
        assertEquals(euro, monetary.getCurrency());
    }

    @Test
    public void testConstructorWithCurrencyString() {
        var monetary = new Monetary(1223, "EUR");

        assertEquals(1223, monetary.getInternalValue());
        assertEquals("EUR", monetary.getCurrency().getCurrencyCode());
    }

    @Test
    public void testCurrencyValidation() {
        assertThrows(IllegalArgumentException.class, () -> {
            @SuppressWarnings("unused")
            var monetary = new Monetary(42, "Non existant currency");
        });
    }

    @Test
    public void testMinor() {
        var monetary = new Monetary(1234, "EUR");

        assertEquals(34, monetary.getMinor());
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
