package commons;

import jakarta.persistence.*;

@Entity
public class Expense {
    @Embedded
    private Monetary amount;
    @Id
    private Long id;

    private Long eventId;
    @ManyToOne(optional = false)
    @JoinColumn(name = "PARTICIPANT_ID", referencedColumnName = "id")
    private Participant participant;

    @ManyToOne(optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Monetary getAmount() {
        return amount;
    }

    public void setAmount(Monetary amount) {
        this.amount = amount;
    }

    public Participant getParticipant() {
        return participant;
    }

    public void setParticipant(Participant participant) {
        this.participant = participant;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public Expense(Event event, Participant participant, Monetary amount) {
        this.event = event;
        this.participant = participant;
        this.amount = amount;
    }
}
