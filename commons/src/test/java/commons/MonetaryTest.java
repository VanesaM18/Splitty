package commons;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import java.util.Currency;

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
        assertThrows(
                IllegalArgumentException.class,
                () -> {
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

        assertEquals(12, monetary.getMajor());
    }

    @Test
    public void testAddition() {
        var monetary1 = new Monetary(1202, "EUR");
        var monetary2 = new Monetary(5205, "EUR");
        Monetary sum = Monetary.add(monetary1, monetary2);

        assertEquals(1202 + 5205, sum.getInternalValue());
    }

    @Test
    public void testAdditionOfDifferentCurrenciesThrows() {
        var eur = new Monetary(1, "EUR");
        var usd = new Monetary(1, "USD");
        assertThrows(IllegalArgumentException.class, () -> Monetary.add(eur, usd));
    }

    @Test
    public void testEqual() {
        var monetary1 = new Monetary(123, "EUR");
        var monetary2 = new Monetary(123, "EUR");

        assertEquals(monetary1, monetary2);
    }

    @Test
    public void setValue() {
        var monetary1 = new Monetary();
        monetary1.setInternalValue(123);
        assertEquals(123, monetary1.getInternalValue());
    }
}
