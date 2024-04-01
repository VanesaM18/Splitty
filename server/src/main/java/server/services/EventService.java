package server.services;

import commons.*;
import org.springframework.stereotype.Service;
import server.BasicAuthParser;
import server.database.*;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class EventService {
    private final EventRepository eventRepository;
    private final AdminRepository adminRepository;
    private final ParticipantRepository participantRepository;
    private final ExpenseRepository expenseRepository;
    private final ExpenseTypeRepository expenseTypeRepository;

    /**
     * constructs a new EventService with the provided EventRepository and AdminRepository
     *
     * @param eventRepository       repository providing functionality for event-related operations
     * @param adminRepository       repository providing functionality for admin-related operations
     * @param participantRepository repository providing functionality for participant-related
     *                              operations
     * @param expenseRepository     repository providing functionality for expense-related
     *                              operations
     * @param expenseTypeRepository repository providing functionality for expenseType-related
     *                              operations
     */
    public EventService(EventRepository eventRepository,
                        AdminRepository adminRepository,
                        ParticipantRepository participantRepository,
                        ExpenseRepository expenseRepository,
                        ExpenseTypeRepository expenseTypeRepository) {
        this.eventRepository = eventRepository;
        this.adminRepository = adminRepository;
        this.participantRepository = participantRepository;
        this.expenseRepository = expenseRepository;
        this.expenseTypeRepository = expenseTypeRepository;
    }

    /**
     * gets a list of all events
     * 
     * @return list of all events
     */
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    /**
     * gets an event by its invite code (id)
     * 
     * @param inviteCode invite code of the event to retrieve
     * @return an Optional containing the event if found, or empty if not found
     */
    public Optional<Event> getEventByInviteCode(String inviteCode) {
        if (isNullOrEmpty(inviteCode)
                || !eventRepository.existsByInviteCodeEqualsIgnoreCase(inviteCode)) {
            return Optional.empty();
        }
        return eventRepository.findById(inviteCode);
    }

    /**
     * creates a new event
     * 
     * @param event event to create
     * @return an Optional containing the created event if successful, or empty if not
     */
    public Optional<Event> createEvent(Event event) {
        // NOTE: The participant list must be empty, people can only be added to an event by using
        // the invite code.
        if (!checkNewEvent(event)) {
            return Optional.empty();
        }
        // generate invite code
        event.generateInviteCode();
        // check if invite code is unique (does not already exist)
        while (eventRepository.existsByInviteCodeEqualsIgnoreCase(event.getInviteCode())) {
            event.generateInviteCode();
        }
        return Optional.of(eventRepository.save(event));
    }

    private boolean checkNewEvent(Event event) {
        return !(isNullOrEmpty(event.getName()) || event.getDateTime() == null
                || (event.getParticipants() != null && !event.getParticipants().isEmpty()));
    }

    /**
     * saves an array of events after checking the
     * uniqueness of their id and the validity of
     * the rest of its details.
     * @param events array of Event objects to be saved.
     * @return list of saved Event objects after filtering out
     * invalid events and saving valid ones.
     */
    public List<Event> saveEvents(Event[] events) {
        return Arrays.stream(events)
                .filter(this::checkUniqueId)
                .filter(this::checkEventDetails)
                .filter(this::falseIfDuplicateParticipantNames)
                .filter(this::falseIfDuplicateParticipantIds)
                .map(this::cleanEvent)
                .map(this::properSave)
                .collect(Collectors.toList());
    }

    private boolean checkEventDetails(Event event) {
        return (!isNullOrEmpty(event.getName())
                && !isNullOrEmpty(event.getInviteCode())
                );
    }

    private boolean falseIfDuplicateParticipantNames(Event e) {
        return e.getParticipants().size()
                == e.getParticipants()
                .stream().map(Participant::getName)
                .distinct().count();
    }

    private boolean falseIfDuplicateParticipantIds(Event e) {
        return e.getParticipants().size()
                == e.getParticipants()
                .stream().map(Participant::getId)
                .distinct().count();
    }


    private Event cleanEvent(Event event) {
        var participantSet = event.getParticipants();
        // Participants with IDs that are in the db.
        var conflictingParticipants = participantSet
                .stream().filter(p -> participantRepository
                        .existsById(p.getId())).collect(Collectors.toSet());
        // Participants with IDs that are not in the db
        Set<Participant> nonConflictingParticipants = participantSet
                .stream().filter(p -> !participantRepository
                        .existsById(p.getId())).collect(Collectors.toSet());
        // Participants that have ID that exists in the db
        // and the same details as their version in the db.
        Set<Participant> conflictingButEqual = conflictingParticipants
                .stream().filter(this::checkIfParticipantsExists)
                .collect(Collectors.toSet());
        // Participants that have ID that exists in the db but with other details;
        Set<Participant> otherParticpants = conflictingParticipants
                .stream().filter(this::checkIfParticipantHasConflictingId)
                .collect(Collectors.toSet());
        // Adding the other Participants to be saved into the db.
        nonConflictingParticipants.addAll(otherParticpants);
        // Collection of saved Participants
        Set<Participant> goodParticpants = new HashSet<>(
                participantRepository.saveAll(nonConflictingParticipants));
        goodParticpants.addAll(conflictingButEqual);
        // cleaned event
        event.setParticipants(goodParticpants);
        return cleanExpensesOfEvent(event);
    }

    private Event cleanExpensesOfEvent(Event event) {
        Set<Participant> participants = event.getParticipants();
        Set<String> participantNames = participants.stream()
                .map(Participant::getName).collect(Collectors.toSet());
        Map<String, Participant> participantMap = new HashMap<>();
        participants.forEach(participant -> {
            participantMap.put(participant.getName(),participant);
        });
        Set<Expense> expenses = event.getExpenses();
        Function<Participant, Participant> correctParticipant
                = participant -> participantMap.get(participant.getName());
        Function<Expense, Expense> mapParticipants = expense -> {
            expense.setSplitBetween(
                  expense.getSplitBetween().stream()
                          .map(correctParticipant)
                          .collect(Collectors.toSet())
            );
            expense.setReceiver(correctParticipant.apply(expense.getCreator()));
            return expense;
        };
        Predicate<Participant> nameCondition = participant
                -> participantNames.contains(participant.getName());
        Predicate<Expense> properExpense = expense
                -> expense.getSplitBetween().stream()
                .allMatch(nameCondition)
                && nameCondition.test(expense.getCreator());
        event.setExpenses(
                expenses.stream().filter(properExpense)
                        .map(mapParticipants)
                        .collect(Collectors.toSet())
        );
        return event;
    }

    private Event properSave(Event event) {
        Set<Expense> ball = event.getExpenses();
        Set<ExpenseType> ballTags = event.getTags();
        event.setExpenses(HashSet.newHashSet(0));
        event.setTags(HashSet.newHashSet(0));
        eventRepository.save(event);
        event.setExpenses(ball);
        cleanTagsOfEvent(event, ballTags);
        expenseRepository.saveAll(ball);
        return event;
    }


    private void cleanTagsOfEvent(Event event, Set<ExpenseType> tags) {
        tags = new HashSet<>(expenseTypeRepository.saveAll(tags));
        Map<String, ExpenseType> tagNames = new HashMap<>();
        tags.forEach(tag -> tagNames.put(tag.getName(), tag));
        Function<ExpenseType, ExpenseType> mapTag = tag
                -> tagNames.get(tag.getName());
        Function<Set<ExpenseType>, Set<ExpenseType>> mapTags =
                someTags -> someTags
                        .stream().map(mapTag)
                        .collect(Collectors.toSet());
        Set<Expense> expenses = event.getExpenses();
        Set<Expense> cleanExpenses = new HashSet<>(expenses);
        cleanExpenses.forEach(expense -> expense.setTags(
                        mapTags.apply(expense.getTags())));
        event.setExpenses(cleanExpenses);
        event.setTags(mapTags.apply(event.getTags()));

    }

    private boolean checkIfParticipantsExists(Participant e) {
        Optional<Participant> participantOptional
                = participantRepository.findById(e.getId());
        if(participantOptional.isEmpty())
            return false;
        Participant participant = participantOptional.get();
        return Objects.equals(participant.getName(), e.getName()) &&
                Objects.equals(participant.getBic(), e.getBic()) &&
                Objects.equals(participant.getIban(), e.getIban()) &&
                Objects.equals(participant.getEmail(), e.getEmail());
    }

    private boolean checkIfParticipantHasConflictingId(Participant e) {
        return participantRepository.existsById(e.getId())
                && !this.checkIfParticipantsExists(e);
    }

    private boolean checkUniqueId(Event event) {
        String eventId = event.getInviteCode();
        return !eventRepository.existsByInviteCodeEqualsIgnoreCase(eventId);
    }

    /**
     * updates an existing event
     * 
     * @param inviteCode invite code of the event to update
     * @param updatedEvent updated event
     * @return an Optional containing the updated event if successful, or empty if not found or
     *         update failed
     */
    public Optional<Event> updateEvent(String inviteCode, Event updatedEvent) {
        Optional<Event> optionalExistingEvent = getEventByInviteCode(inviteCode);
        // First, collect a list of participants in the event before editing, so we can later check
        // which participants were removed.
        List<Participant> oldParticipants = null;
        if (optionalExistingEvent.isPresent()) {
            oldParticipants = List.copyOf(optionalExistingEvent.get().getParticipants());
        }

        Optional<Event> optionalUpdated = optionalExistingEvent.map(existingEvent -> {
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

        // If the optional is empty or we do not have a list of old participants, return early.
        if (oldParticipants == null || optionalUpdated.isEmpty()) return optionalUpdated;

        // Manually delete the participant now that the relation between event and participant was
        // deleted
        Event updated = optionalUpdated.get();

        // Compile list of participants that were removed from this event.
        List<Participant> participantsToDelete = getParticipantsToDelete(oldParticipants, updated);
        participantRepository.deleteAll(participantsToDelete);

        return optionalUpdated;
    }

    private List<Participant> getParticipantsToDelete(List<Participant> oldParticipants,
            Event updated) {
        List<Participant> participantsToDelete = new ArrayList<Participant>();
        for (var p : oldParticipants) {
            if (!updated.getParticipants().contains(p)) {
                participantsToDelete.add(p);
            }
        }
        return participantsToDelete;
    }

    /**
     * deletes an event by its invite code (id)
     * 
     * @param inviteCode invite code of the event to delete
     * @return an Optional containing the deleted event if successful, or empty if not found
     */
    public Optional<Event> deleteEvent(String inviteCode) {
        if (isNullOrEmpty(inviteCode)
                || !eventRepository.existsByInviteCodeEqualsIgnoreCase(inviteCode)) {
            return Optional.empty();
        }
        var event = eventRepository.findById(inviteCode);
        eventRepository.deleteById(inviteCode);
        return event;
    }

    /**
     * Check whether the given auth header is valid, and the password matches. NOTE: not needed to
     * invert method (warning)
     * 
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
        Admin admin = adminRepository.findById(login.getUsername()).orElse(null);

        return admin != null && hash.equals(admin.getPassword());
    }

    private boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }

    /**
     * gets event repository
     * 
     * @return event repository
     */
    public EventRepository getEventRepository() {
        return eventRepository;
    }
}
