package commons;

import jakarta.persistence.*;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

@Entity
public class Event {

    private static final int INVITE_CODE_LENGTH = 6;
    @Id @GeneratedValue private Long id;
    private String name;
    private String inviteCode;
    private LocalDateTime dateTime;

    @ManyToMany(fetch = FetchType.EAGER) private Set<Participant> participants;

    /**
     * Create an Event with the given details.
     *
     * @param id The ID of the event.
     * @param name The name/title of the event.
     * @param inviteCode The inviteCode that can be used to join the event.
     * @param dateTime The date and time of the event.
     * @param participants A set of the participants in the event.
     */
    public Event(
            Long id,
            String name,
            String inviteCode,
            LocalDateTime dateTime,
            Set<Participant> participants) {
        // -1 should mean auto-generate the id when inserting, not brute force it
        if (id != -1) {
            this.id = id;
        }
        this.name = name;
        this.inviteCode = inviteCode;
        this.dateTime = dateTime;
        this.participants = participants;
    }

    private Event() {
        // for object mapper
    }

    /**
     * Get the name (title) of the event.
     *
     * @return the name of the event.
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of this event.
     *
     * @param name The new name of this event.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the date and time of the event.
     *
     * @return the starting time of the event.
     */
    public LocalDateTime getDateTime() {
        return dateTime;
    }

    /**
     * Set the starting date and time of this event.
     *
     * @param dateTime The new starting date and time.
     */
    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    /**
     * Get the list of participants partaking in this Event.
     *
     * @return a set containing all participants in this event.
     */
    public Set<Participant> getParticipants() {
        return participants;
    }

    /**
     * Change the list of participants in this event.
     *
     * @param participants A set of Participants that participate in this event.
     */
    public void setParticipants(Set<Participant> participants) {
        this.participants = participants;
    }

    /**
     * Add a participant to the list of participants for this event.
     *
     * @param participant The participant to add.
     */
    public void addParticipant(Participant participant) {
        this.participants.add(participant);
    }

    /**
     * Remove a participant from the list of participants for this event.
     *
     * @param participant the participant to remove.
     */
    public void removeParticipant(Participant participant) {
        this.participants.remove(participant);
    }

    /**
     * Get the invite code for this event.
     * This code can be used to join this event.
     *
     * @return the invite code for this event.
     */
    public String getInviteCode() {
        return inviteCode;
    }

    /**
     * Set the invite code for this event.
     * This code can be used to join this event.
     *
     * @param inviteCode The new invite code.
     */
    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }

    /**
     * Generate a new invite code.
     * The code is generated using a new SecureRandom instance.
     * The generated code is then automatically set as the new invite code for this event.
     */
    public void generateInviteCode() {
        CodeGenerator generator = new CodeGenerator(new SecureRandom());
        String code = generator.generateCode(INVITE_CODE_LENGTH);

        setInviteCode(code);
    }

    /**
     * Check whether two events are the same.
     * Returns true iff o is also an event, and if all properties match.
     *
     * @param o The object to compare to this event.
     * @return true iff o is also an event, and all properties match.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return Objects.equals(id, event.id)
                && Objects.equals(getName(), event.getName())
                && Objects.equals(getInviteCode(), event.getInviteCode())
                && Objects.equals(getDateTime(), event.getDateTime())
                && Objects.equals(getParticipants(), event.getParticipants());
    }

    /**
     * Generate a numerical hashCode based on the properties of this event.
     *
     * @return a hashcode based on the properties of this event.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, getName(), getInviteCode(), getDateTime(), getParticipants());
    }
}
