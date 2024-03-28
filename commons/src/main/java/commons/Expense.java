package commons;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

import java.time.LocalDate;
import java.util.Set;

import jakarta.persistence.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
public class Expense {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "internalValue", column = @Column(name = "amount_value")),
        @AttributeOverride(name = "currency", column = @Column(name = "amount_currency")),
        @AttributeOverride(name = "fractionDivider",
                           column = @Column(name = "amount_fraction_divider"))
    })
    private Monetary amount;
    @ManyToOne(optional = true)
    @JoinColumn(name = "PARTICIPANT_ID", referencedColumnName = "id", nullable = true)
    private Participant creator;

    @ManyToOne(optional = true)
    @JoinColumn(name = "event_id", nullable = true)
    @JsonBackReference
    private Event event;

    private String name;

    private LocalDate date;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
    @JoinTable(name = "EXPENSE_PARTICIPANTS")
    private Set<Participant> splitBetween;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
    @JoinTable(name = "EXPENSE_TAGS")
    private Set<ExpenseType> tags;

    /**
     * For object mapper
     */
    public Expense() {
    }

    /**
     * Create a new expense
     * 
     * @param event        The event associated with this expense
     * @param name         The name of the expense
     * @param creator      The participant that made this expense
     * @param amount       The amount of this expense
     * @param date         The date this expense took place
     * @param splitBetween The participants who will split the bill
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

    /**
     * Adds new expense type.
     * @param type tag to be added.
     */
    public void addType(ExpenseType type) {
        tags.add(type);
    }

    /**
     * Deletes an expense type.
     * @param type tag to be deleted.
     */
    public void deleteType(ExpenseType type) {
        tags.remove(type);
    }

    /**
     * Getter of the tags.
     * @return all the tags.
     */
    public Set<ExpenseType> getTags() {
        return tags;
    }

    /**
     * Setter of the tags.
     * @param tags new tags.
     */
    public void setTags(Set<ExpenseType> tags) {
        this.tags = tags;
    }

    /**
     * Gets the name of the expense
     * 
     * @return The name of the expense
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the expense
     * 
     * @param name The name of the expense
     */
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
     * Removes a participant from the set of participants
     * WARN: IF the expense is constructed with an immutable set, this will error
     *
     * @param participant The participant to remove
     *
     * @return True if the participant was in the list
     */
    public boolean removeParticipant(Participant participant) {
        return this.splitBetween.remove(participant);
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
     * Removes expense type from the tags.
     * @param tag type to be removed.
     */
    public void removeTag(ExpenseType tag) {
        if(tags.contains(tag)) {
            tags.remove(tag);
        }
    }

    /**
     * Set the participant that will receive the payment
     * 
     * @param participant The participant that will receive the payment
     */
    public void setReceiver(Participant participant) {
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

    /**
     * Clear everything before deleting
     */
    @PreRemove
    public void removeEverything() {
        this.splitBetween.clear();
        this.amount = null;
        this.creator = null;
        this.event = null;
    }
}
