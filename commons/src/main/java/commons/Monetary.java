package commons;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

import java.util.Arrays;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Abstraction for monetary values
 * 
 */
public class Monetary {
    private long value;
    private String unit;

    public Monetary(long value) {
        this(value, "EUR");
    }

    public Monetary(long value, String unit) {
        this.value = value;
        this.unit = unit;
    }

    public long getInternalValue() {
        return this.value;
    }

    /**
     * Get the type of currency
     * 
     * @return the monetary unit (ISO 4217)
     */
    public String getUnit() {
        return this.unit;
    }

    public long getMajor() {
        // TODO: Right now we only support currencies with 2 digits after the decimal
        // separator
        return this.value / 100;
    }

    public long getMinor() {
        // TODO: Right now we only support currencies with 2 digits after the decimal
        // separator
        return this.value % 100;
    }

    public static Monetary add(Monetary... monetaries) {
        return new Monetary(Arrays.stream(monetaries).mapToLong(mon -> mon.value).sum());
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
