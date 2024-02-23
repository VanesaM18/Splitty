package commons;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Random;
import java.util.Set;


@Entity
public class Event {

    private static final int INVITE_CODE_LENGTH = 6;
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String inviteCode;
    private LocalDateTime dateTime;

    @ManyToMany
    private Set<Participant> participants;

    public Event(Long id, String name, String inviteCode, LocalDateTime dateTime, Set<Participant> participants) {
        this.id = id;
        this.name = name;
        this.inviteCode = inviteCode;
        this.dateTime = dateTime;
        this.participants = participants;
    }

    private Event() {
        // for object mapper
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public Set<Participant> getParticipants() {
        return participants;
    }

    public void setParticipants(Set<Participant> participants) {
        this.participants = participants;
    }

    public void addParticipant(Participant participant) {
        this.participants.add(participant);
    }

    public void removeParticipant(Participant participant) {
        this.participants.remove(participant);
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }

    public void generateInviteCode() {
        CodeGenerator generator = new CodeGenerator(new Random());
        String code = generator.generateCode(INVITE_CODE_LENGTH);

        setInviteCode(code);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return Objects.equals(id, event.id) && Objects.equals(getName(), event.getName()) && Objects.equals(getInviteCode(), event.getInviteCode()) && Objects.equals(getDateTime(), event.getDateTime()) && Objects.equals(getParticipants(), event.getParticipants());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, getName(), getInviteCode(), getDateTime(), getParticipants());
    }

}
