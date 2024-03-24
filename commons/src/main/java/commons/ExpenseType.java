package commons;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.awt.*;
import java.util.Objects;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

@Entity
public class ExpenseType {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String name;
    private String color;
    @ManyToOne(optional = true)
    @JoinColumn(name = "event_id", nullable = true)
    @JsonBackReference
    private Event event;

    /**
     * Creating new expense type.
     * @param name name of the expense type.
     * @param color color of the tag.
     * @param event event of the expense type.
     */
    public ExpenseType(String name, String color, Event event) {
        this.name = name;
        this.color = color;
        this.event = event;
    }

    /**
     * Creates an expense type.
     * Used for object mapping.
     */
    public ExpenseType() {
        // for object mapper
    }

    /**
     * Retrieves the id of this expense type.
     * @return the type's id.
     */
    public long getId() {
        return id;
    }

    /**
     * Retrieves the name of this expense type.
     * @return the type's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name associated with this expense type.
     * @param name The new name to be set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Retrieves the color of this expense type.
     * @return the type's color.
     */
    public String getColor() {
        return color;
    }

    /**
     * Sets the color of this expense type.
     * @param color The new color to be set.
     */
    public void setColor(String color) {
        this.color = color;
    }

    /**
     * Checks if another object is exactly the same as this one.
     * @param o Object that needs to be checked.
     * @return Weather the given object is equal to this one.
     */
    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    /**
     * Computes a hash code for this object.
     * @return A hash code value for this object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(name, color);
    }

    /**
     * Turns the expense type into a string.
     * @return String representation of the object.
     */
    @Override
    public String toString() { return ToStringBuilder.reflectionToString(this, MULTI_LINE_STYLE); }

    /**
     * Sets the related event.
     * @param event event ot the expense type.
     */
    public void setEvent(Event event) {
        this.event = event;
    }

    /**
     * Getter of the event.
     * @return the event of the tag.
     */
    public Event getEvent() {
        return event;
    }
}
