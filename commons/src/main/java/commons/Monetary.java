package commons;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

import java.util.Arrays;
import java.util.Currency;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Embeddable;

/** Abstraction for monetary values */
@Embeddable
public class Monetary {

    /**
     * Add up different monetary values
     *
     * <p>
     * All of the monetary values must be of the same currency; Currency conversion
     * is not
     * implemented in this method.
     *
     * @param monetaries the monetary values to add up
     * @return the sum of all the values
     * @throws IllegalArgumentException if no monetary values are passed, or if they
     *                                  are not the
     *                                  same currency
     */
    public static Monetary add(Monetary... monetaries) throws IllegalArgumentException {
        if (monetaries.length <= 1) {
            throw new IllegalArgumentException(
                    "Must have at least one monetary value as an argument");
        }
        Currency curr = monetaries[0].getCurrency();

        long sum = Arrays.stream(monetaries)
                .peek(
                        mon -> {
                            if (mon.getCurrency() != curr) {
                                throw new IllegalArgumentException(
                                        "All monetaries must have the same currency");
                            }
                        })
                .mapToLong(mon -> mon.internalValue)
                .sum();

        return new Monetary(sum, curr);
    }

    @JsonProperty
    private long internalValue;

    @JsonProperty
    private Currency currency;
    // 10^(currency fraction digits)
    @JsonProperty
    private long fractionDivider;

    /**
     * For object mapper
     */
    public Monetary() {
    }

    /**
     * Construct a new Monetary type with only a value
     *
     * @param value The internal value and corresponding currency
     */
    public Monetary(long value) {
        this(value, "EUR");
    }

    /**
     * Construct a new Monetary type with a custom currency. The internal value is
     * stored in the
     * minor unit (in case of EUR, this would be cents)
     *
     * @param value    The internal value to set.
     * @param currency The currency to use
     */
    public Monetary(long value, Currency currency) {
        this.internalValue = value;
        this.setCurrency(currency);
    }

    /**
     * Construct a new Monetary type with a custom currency. The internal value is
     * stored in the
     * minor unit (in case of EUR, this would be cents)
     *
     * @param value    The internal value to set.
     * @param currency The currency to use
     * @throws IllegalArgumentException When currency is invalid
     */
    public Monetary(long value, String currency) throws IllegalArgumentException {
        this(value, Currency.getInstance(currency));
    }

    /**
     * Sets the internal value
     *
     * @param internalValue the new internal value
     */
    public void setInternalValue(long internalValue) {
        this.internalValue = internalValue;
    }

    /**
     * gets the internal value
     *
     * @return the internal value
     */
    public long getInternalValue() {
        return this.internalValue;
    }

    /**
     * Gets the type of currency
     *
     * @return the monetary unit (ISO 4217)
     */
    public Currency getCurrency() {
        return this.currency;
    }

    /**
     * sets the correct currency
     *
     * @param currency The currency to use
     */
    public void setCurrency(Currency currency) {
        this.currency = currency;
        this.fractionDivider = 1;
        for (int i = 0; i < this.currency.getDefaultFractionDigits(); i++) {
            this.fractionDivider *= 10;
        }
    }

    /**
     * gets the major value
     *
     * @return the major value
     */
    @JsonIgnore
    public long getMajor() {
        return this.internalValue / this.fractionDivider;
    }

    /**
     * get the minor value
     *
     * @return the minor value
     */
    @JsonIgnore
    public long getMinor() {
        return this.internalValue % this.fractionDivider;
    }

    /**
     * Indicates whether some other object/monetary is equal to this one
     *
     * @param obj the object being compared
     * @return true or false depending on of if the objects are equal or not
     */
    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    /**
     * generates a hashcode for the monetary value
     *
     * @return a hashcode (random value) for the monetary value
     */
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    /**
     * turns the monetary value into a readable string
     *
     * @return string representation of the monetary value
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, MULTI_LINE_STYLE);
    }
}
