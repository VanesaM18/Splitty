package server.database;

import commons.Event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface EventRepository extends JpaRepository<Event, String> {

    /**
     * Find out if an event with the given invite code exists.
     *
     * @param inviteCode The invite code to check.
     * @return true iff an event with the given invite code exists.
     */
    boolean existsByInviteCodeEqualsIgnoreCase(String inviteCode);

    /**
     * Find the first event with the given invite code.
     *
     * @param inviteCode The invite code to check.
     * @return the event with the given invite code.
     */
    Event findFirstByInviteCodeEqualsIgnoreCase(String inviteCode);
}
