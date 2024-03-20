package commons;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

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

    @ParameterizedTest
    @CsvSource({
            "420,EUR,4.20",
            "1206,EUR,12.06",
            "14501,TND,14.501",
            "20,EUR,0.20",
            "4,EUR,0.04",
            "0,EUR,0.00"
    })
    public void testToString(long amount, String currency, String expectedValue) {
        var monetary = new Monetary(amount, currency);

        assertEquals(expectedValue, monetary.toString());
    }

    @ParameterizedTest
    @CsvSource({
            "400,EUR,4",
            "420,EUR,4.20",
            "1206,EUR,12.06",
            "14501,TND,14.501",
            "20,EUR,0.20",
            "4,EUR,0.04",
            "0,EUR,0.00",
            "10,EUR,0.1",
            "20,EUR,.20"
    })
    public void testFromString(long amount, String currency, String input) throws Exception {
        assertEquals(new Monetary(amount, currency),
                Monetary.fromString(input, Currency.getInstance(currency)));
    }

    @ParameterizedTest
    @CsvSource({
            "EUR,4.204",
            "TND,14.1501",
            "EUR,4abc.123",
            "EUR,123.4abc"
    })
    public void testFromStringThrows(String currency, String input) {
        assertThrows(Exception.class, () -> {
            Monetary.fromString(input, Currency.getInstance(currency));
        });
    }
}
