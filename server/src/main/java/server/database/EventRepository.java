package server.database;

import commons.Event;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {

    boolean existsByInviteCodeEqualsIgnoreCase(String inviteCode);

    Event findFirstByInviteCodeEqualsIgnoreCase(String inviteCode);
}
