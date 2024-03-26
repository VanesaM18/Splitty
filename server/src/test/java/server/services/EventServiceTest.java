package server.services;

import commons.Admin;
import commons.Event;
import commons.Participant;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import server.database.AdminRepository;
import server.database.EventRepository;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class EventServiceTest {
    @Mock
    EventRepository eventRepository;
    @Mock
    AdminRepository adminRepository;
    @InjectMocks
    EventService eventService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllEvents() {
        Event event = new Event("inviteCode", "name", LocalDateTime.of(2024, Month.MARCH, 23, 22, 2, 40), Set.of(new Participant("name", "email", "iban", "bic")), new HashSet<>());
        when(eventRepository.findAll()).thenReturn(List.of(event));

        List<Event> result = eventService.getAllEvents();
        Assertions.assertEquals(List.of(event), result);
    }

    @Test
    void testGetEventByInviteCode() {
        Event event = new Event("inviteCode", "name", LocalDateTime.of(2024, Month.MARCH, 23, 22, 2, 40), Set.of(new Participant("name", "email", "iban", "bic")), new HashSet<>());
        when(eventRepository.existsByInviteCodeEqualsIgnoreCase(anyString())).thenReturn(true);
        when(eventRepository.findById(any())).thenReturn(Optional.of(event));
        when(adminRepository.findById(any())).thenReturn(null);

        Optional<Event> result = eventService.getEventByInviteCode("inviteCode");
        Assertions.assertEquals(Optional.of(event), result);
    }

    @Test
    void testCreateEvent() {
        when(eventRepository.existsByInviteCodeEqualsIgnoreCase(anyString())).thenReturn(false);
        when(eventRepository.save(any())).thenReturn(new Event());
        when(adminRepository.save(any())).thenReturn(new Admin());

        Optional<Event> result = eventService.createEvent(new Event("inviteCode", "name", LocalDateTime.of(2024, Month.MARCH, 23, 22, 2, 40), Set.of(new Participant("name", "email", "iban", "bic")), new HashSet<>()));
        Assertions.assertEquals(Optional.empty(), result);
    }

    @Test
    void testIsAuthenticated() {
        when(eventRepository.findById(any())).thenReturn(Optional.empty());
        when(adminRepository.findById(any())).thenReturn(Optional.empty());

        boolean result = eventService.isAuthenticated("authHeader");
        Assertions.assertFalse(result);
    }
}
