package commons;

import jakarta.persistence.*;

@Entity
public class Expense {
    @Embedded
    private Monetary amount;
    @Id
    private Long id;
    @ManyToOne(optional = false)
    @JoinColumn(name = "PARTICIPANT_ID", referencedColumnName = "id")
    private Participant participant;

    @ManyToOne(optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    public Expense() {

    }

    /**
     * Get the event associated with this expense
     * @return The event
     */
    public Event getEvent() {
        return event;
    }

    /**
     * Set the event associated to this expense
     * @param event The event to set
     */
    public void setEvent(Event event) {
        this.event = event;
    }

    /**
     * Get the monetary amount
     * @return The amount
     */
    public Monetary getAmount() {
        return amount;
    }

    /**
     * Set the associated amount
     * @param amount The associated amount
     */
    public void setAmount(Monetary amount) {
        this.amount = amount;
    }

    /**
     * Get the associated participant
     * @return The associated participant
     */
    public Participant getParticipant() {
        return participant;
    }

    /**
     * Set the associated participant
     * @param participant The associated participant
     */
    public void setParticipant(Participant participant) {
        this.participant = participant;
    }

    /**
     * Set the id
     * @param id THe id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Get the id
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * Create a new expense
     * @param event The event associated with this expense
     * @param participant The participant that made this expense
     * @param amount The amount of this expense
     */
    public Expense(Event event, Participant participant, Monetary amount) {
        this.event = event;
        this.participant = participant;
        this.amount = amount;
    }
}
