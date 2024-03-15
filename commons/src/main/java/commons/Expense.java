package commons;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

import java.time.LocalDate;
import java.util.Set;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;

@Entity
public class Expense {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "amount_value")),
            @AttributeOverride(name = "currency", column = @Column(name = "amount_currency")),
            @AttributeOverride(name = "fractionDivider", column = @Column(name = "amount_fraction_divider"))
    })
    private Monetary amount;
    @ManyToOne(optional = false)
    @JoinColumn(name = "PARTICIPANT_ID", referencedColumnName = "id")
    private Participant creator;

    @ManyToOne(optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    private String name;

    private LocalDate date;

    @ManyToMany
    @JoinTable(name = "EXPENSE_PARTICIPANTS")
    private Set<Participant> splitBetween;

    /**
     * For object mapper
     */
    public Expense() {
    }

    /**
     * Create a new expense
     * 
     * @param event   The event associated with this expense
     * @param creator The participant that made this expense
     * @param amount  The amount of this expense
     * @param date    The date this expense took place
     */
    public Expense(Event event, String name, Participant creator, Monetary amount, LocalDate date,
            Set<Participant> splitBetween) {
        this.event = event;
        this.name = name;
        this.creator = creator;
        this.amount = amount;
        this.date = date;
        if (splitBetween == null) {
            this.splitBetween = Set.of();
        } else {
            this.splitBetween = splitBetween;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the participants who will split the bill
     * 
     * @return The participants that will split the bill
     */
    public Set<Participant> getSplitBetween() {
        return splitBetween;
    }

    /**
     * Sets the participants who will split the bill
     * 
     * @param splitBetween The participants that will split the bill
     *
     */
    public void setSplitBetween(Set<Participant> splitBetween) {
        this.splitBetween = splitBetween;
    }

    /**
     * Adds a participant to the set of participants.
     * WARN: If the expense is constructed with an immutable set, this will error
     *
     * @param participant Add to the list of participants that will split the bill
     * 
     * @return Whether the participant was allready added to the list
     */
    public boolean addParticipant(Participant participant) {
        return this.splitBetween.add(participant);
    }

    /**
     * Get the date associated with this event.
     * 
     * @return The date
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Set the date for this event.
     * 
     * @param date the date to set
     */
    public void setDate(LocalDate date) {
        this.date = date;
    }

    /**
     * Get the event associated with this expense
     * 
     * @return The event
     */
    public Event getEvent() {
        return event;
    }

    /**
     * Set the event associated to this expense
     * 
     * @param event The event to set
     */
    public void setEvent(Event event) {
        this.event = event;
    }

    /**
     * Get the monetary amount
     * 
     * @return The amount
     */
    public Monetary getAmount() {
        return amount;
    }

    /**
     * Set the associated amount
     * 
     * @param amount The associated amount
     */
    public void setAmount(Monetary amount) {
        this.amount = amount;
    }

    /**
     * Get the associated participant
     * 
     * @return The associated participant
     */
    public Participant getCreator() {
        return creator;
    }

    /**
     * Set the associated participant
     * 
     * @param participant The associated participant
     */
    public void setCreator(Participant participant) {
        this.creator = participant;
    }

    /**
     * Set the id
     * 
     * @param id THe id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Get the id
     * 
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * Checks if another object is exactly the same as this one.
     * 
     * @param obj Object that needs to be checked.
     * @return Weather the given object is equal to this one.
     */
    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    /**
     * Computes a hash code for this object.
     * 
     * @return A hash code value for this object.
     */
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    /**
     * Turns the quote into a string.
     * 
     * @return String representation of the object.
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, MULTI_LINE_STYLE);
    }

}
