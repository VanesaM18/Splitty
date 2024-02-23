package commons;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

import java.util.Arrays;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class Monetary {
    private long value;

    public Monetary(long value) {
        this.value = value;
    }

    public long getValue() {
        return this.value;
    }

    public long getCents() {
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
