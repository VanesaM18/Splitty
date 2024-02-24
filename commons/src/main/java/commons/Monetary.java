package commons;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

import java.util.Arrays;
import java.util.Currency;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Abstraction for monetary values
 * 
 */
public class Monetary {
    private long value;
    private Currency currency;
    // 10^(currency fraction digits)
    private long fractionDivider;

    public Monetary(long value) {
        this(value, "EUR");
    }

    /**
     * Construct a new Monetary type with a custom currency. The internal value
     * is stored in the minor unit (in case of EUR, this would be cents)
     *
     * @param value    The internal value to set.
     * @param currency The currency to use
     * @throws IllegalArgumentException
     */
    public Monetary(long value, Currency currency) {
        this.value = value;
        this.setCurrency(currency);
    }

    public Monetary(long value, String currency) throws IllegalArgumentException {
        this(value, Currency.getInstance(currency));
    }

    public long getInternalValue() {
        return this.value;
    }

    /**
     * Get the type of currency
     * 
     * @return the monetary unit (ISO 4217)
     */
    public Currency getCurrency() {
        return this.currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
        this.fractionDivider = 1;
        for (int i = 0; i < this.currency.getDefaultFractionDigits(); i++) {
            this.fractionDivider *= 10;
        }
    }

    public long getMajor() {
        return this.value / this.fractionDivider;
    }

    public long getMinor() {
        return this.value % this.fractionDivider;
    }

    /**
     * Add up different monetary values
     * 
     * All of the monetary values must be of the same currency; Currency conversion
     * is not implemented in this method.
     *
     * @param monetaries the monetary values to add up
     * @return the sum of all the values
     * @throws IllegalArgumentException if no monetary values are passed, or if they
     *                                  are not the same currency
     */
    public static Monetary add(Monetary... monetaries) throws IllegalArgumentException {
        if (monetaries.length <= 1) {
            throw new IllegalArgumentException("Must have at least one monetary value as an argument");
        }
        Currency curr = monetaries[0].getCurrency();

        long sum = Arrays.stream(monetaries).peek(mon -> {
            if (mon.getCurrency() != curr) {
                throw new IllegalArgumentException("All monetaries must have the same currency");
            }
        }).mapToLong(mon -> mon.value).sum();

        return new Monetary(sum, curr);
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, MULTI_LINE_STYLE);
    }
}
