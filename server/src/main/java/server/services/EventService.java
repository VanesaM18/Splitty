package server.services;

import org.springframework.stereotype.Service;
import server.BasicAuthParser;
import server.database.AdminRepository;
import server.database.EventRepository;

import java.util.Optional;

@Service
public class EventService {
    private final EventRepository eventRepository;
    private final AdminRepository adminRepository;

    /**
     * constructs a new EventService with the provided EventRepository and AdminRepository
     * @param eventRepository repository providing functionality for event-related operations
     * @param adminRepository repository providing functionality for admin-related operations
     */
    public EventService(EventRepository eventRepository, AdminRepository adminRepository) {
        this.eventRepository = eventRepository;
        this.adminRepository = adminRepository;
    }

    /**
     * gets a list of all events
     * @return list of all events
     */
    public java.util.List<commons.Event> getAllEvents() {
        return eventRepository.findAll();
    }

    /**
     * gets an event by its invite code (id)
     * @param inviteCode invite code of the event to retrieve
     * @return an Optional containing the event if found, or empty if not found
     */
    public Optional<commons.Event> getEventByInviteCode(String inviteCode) {
        if (isNullOrEmpty(inviteCode) ||
                !eventRepository.existsByInviteCodeEqualsIgnoreCase(inviteCode)) {
            return Optional.empty();
        }
        return eventRepository.findById(inviteCode);
    }

    /**
     * creates a new event
     * @param event event to create
     * @return an Optional containing the created event if successful, or empty if not
     */
    public Optional<commons.Event> createEvent(commons.Event event) {
        // NOTE: The participant list must be empty, people can only be added to an event by using
        // the invite code.
        if (isNullOrEmpty(event.getName()) || event.getDateTime() == null
                || (event.getParticipants() != null && !event.getParticipants().isEmpty())) {
            return Optional.empty();
        }
        //generate invite code
        event.generateInviteCode();
        //check if invite code is unique (does not already exist)
        while (eventRepository.existsByInviteCodeEqualsIgnoreCase(event.getInviteCode())) {
            event.generateInviteCode();
        }
        return Optional.of(eventRepository.save(event));
    }

    /**
     * updates an existing event
     * @param inviteCode   invite code of the event to update
     * @param updatedEvent updated event
     * @return an Optional containing the updated event if successful,
     * or empty if not found or update failed
     */
    public Optional<commons.Event> updateEvent(String inviteCode, commons.Event updatedEvent) {
        Optional<commons.Event> optionalExistingEvent = getEventByInviteCode(inviteCode);
        return optionalExistingEvent.map(existingEvent -> {
            if (isNullOrEmpty(updatedEvent.getName()) || updatedEvent.getDateTime() == null
                    || !inviteCode.equals(updatedEvent.getInviteCode())) {
                return null;
            }
            existingEvent.setName(updatedEvent.getName());
            existingEvent.setDateTime(updatedEvent.getDateTime());
            existingEvent.setLastUpdateTime(java.time.LocalDateTime.now());
            if (updatedEvent.getParticipants() != null) {
                existingEvent.getParticipants().clear();
                existingEvent.getParticipants().addAll(updatedEvent.getParticipants());
            }
            return eventRepository.save(existingEvent);
        });
    }

    /**
     * deletes an event by its invite code (id)
     * @param inviteCode invite code of the event to delete
     * @return an Optional containing the deleted event if successful, or empty if not found
     */
    public Optional<commons.Event> deleteEvent(String inviteCode) {
        if (isNullOrEmpty(inviteCode) ||
                !eventRepository.existsByInviteCodeEqualsIgnoreCase(inviteCode)) {
            return Optional.empty();
        }
        eventRepository.deleteById(inviteCode);
        return Optional.of(new commons.Event());
    }

    /**
     * Check whether the given auth header is valid, and the password matches.
     * NOTE: not needed to invert method (warning)
     * @param authHeader The auth header to check.
     * @return true if the login credentials are correct, false otherwise.
     */
    public boolean isAuthenticated(String authHeader) {
        if (isNullOrEmpty(authHeader)) {
            return false;
        }
        var login = BasicAuthParser.parse(authHeader);
        if (login == null) {
            return false;
        }
        String hash = login.getPassword();

        // Check if the admin exists and the password matches.
        commons.Admin admin = adminRepository.findById(login.getUsername()).orElse(null);

        return admin != null && hash.equals(admin.getPassword());
    }

    private boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }
}
