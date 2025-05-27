package pl.uj.passgo.services.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import pl.uj.passgo.exception.weather.EventWeatherException;
import pl.uj.passgo.models.*;
import pl.uj.passgo.models.DTOs.EventCreateRequest;
import pl.uj.passgo.models.DTOs.event.UpdateEventDto;
import pl.uj.passgo.models.DTOs.weahter.EventWeatherRequest;
import pl.uj.passgo.models.DTOs.weahter.EventWeatherResponse;
import pl.uj.passgo.models.member.Organizer;
import pl.uj.passgo.models.responses.EventResponse;
import pl.uj.passgo.repos.BuildingRepository;
import pl.uj.passgo.repos.EventRepository;
import pl.uj.passgo.repos.TicketRepository;
import pl.uj.passgo.repos.member.OrganizerRepository;
import pl.uj.passgo.services.EventService;
import pl.uj.passgo.services.TicketService;
import pl.uj.passgo.services.weather.EventWeatherService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private BuildingRepository buildingRepository;

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private TicketService ticketService;

    @Mock
    private OrganizerRepository organizerRepository;

    @Mock
    private EventWeatherService weatherService;

    @InjectMocks
    private EventService eventService;

    @Test
    void shouldReturnAllEvents_whenStatusIsNull() {
        // Arrange
        Building building = mock(Building.class);
        when(building.getName()).thenReturn("Test Building");
        when(building.getAddress()).thenReturn(mock(Address.class));

        Event event = mock(Event.class);
        when(event.getBuilding()).thenReturn(building);

        List<Event> events = List.of(event);
        when(eventRepository.findAll()).thenReturn(events);

        // Act
        List<EventResponse> result = eventService.getAllEvents(null);

        // Assert
        assertEquals(events.size(), result.size());
        verify(eventRepository).findAll();
    }

    @Test
    void shouldReturnEventsByStatus_whenStatusIsProvided() {
        // Arrange
        Building building = mock(Building.class);
        when(building.getName()).thenReturn("Test Building");
        when(building.getAddress()).thenReturn(mock(Address.class));

        Event event = mock(Event.class);
        when(event.getBuilding()).thenReturn(building);

        List<Event> events = List.of(event);
        when(eventRepository.findByStatus(Status.PENDING)).thenReturn(events);

        // Act
        List<EventResponse> result = eventService.getAllEvents(Status.PENDING);

        // Assert
        assertEquals(events.size(), result.size());
        verify(eventRepository).findByStatus(Status.PENDING);
    }
    @Test
    void shouldReturnAllOrganizerEvents_whenStatusIsNull() {
        // Arrange
        Long organizerCredentialId = 1L;
        Organizer organizer = new Organizer();
        organizer.setId(1L);

        when(organizerRepository.findByMemberCredentialId(organizerCredentialId))
                .thenReturn(Optional.of(organizer));

        Building building = mock(Building.class);
        when(building.getName()).thenReturn("Test Building");
        when(building.getAddress()).thenReturn(mock(Address.class));

        Event event = mock(Event.class);
        when(event.getBuilding()).thenReturn(building);

        List<Event> events = List.of(event);

        when(eventRepository.findAllByOrganizerId(organizer.getId()))
                .thenReturn(events);

        // Act
        List<EventResponse> result = eventService.getAllOrganizerEvents(organizerCredentialId, null);

        // Assert
        assertEquals(1, result.size());
        verify(organizerRepository).findByMemberCredentialId(organizerCredentialId);
        verify(eventRepository).findAllByOrganizerId(organizer.getId());
        verify(eventRepository, never()).findAllByOrganizerIdAndStatus(any(), any());
    }

    @Test
    void shouldReturnAllOrganizerEventsByStatus_whenStatusIsProvided() {
        // Arrange
        Long organizerCredentialId = 1L;
        Status status = Status.PENDING;
        Organizer organizer = new Organizer();
        organizer.setId(1L);

        when(organizerRepository.findByMemberCredentialId(organizerCredentialId))
                .thenReturn(Optional.of(organizer));

        Building building = mock(Building.class);
        when(building.getName()).thenReturn("Test Building");
        when(building.getAddress()).thenReturn(mock(Address.class));

        Event event = mock(Event.class);
        when(event.getBuilding()).thenReturn(building);

        List<Event> events = List.of(event);

        when(eventRepository.findAllByOrganizerIdAndStatus(organizer.getId(), status))
                .thenReturn(events);

        // Act
        List<EventResponse> result = eventService.getAllOrganizerEvents(organizerCredentialId, status);

        // Assert
        assertEquals(1, result.size());
        verify(organizerRepository).findByMemberCredentialId(organizerCredentialId);
        verify(eventRepository).findAllByOrganizerIdAndStatus(organizer.getId(), status);
        verify(eventRepository, never()).findAllByOrganizerId(any());
    }

    @Test
    void shouldThrowException_whenOrganizerNotFound() {
        // Arrange
        Long id = 1L;
        when(organizerRepository.findByMemberCredentialId(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> eventService.getAllOrganizerEvents(id, null));
    }

    @Test
    void shouldCreateEvent_whenRequestIsValid() {
        // Arrange
        EventCreateRequest request = mock(EventCreateRequest.class);
        Building building = mock(Building.class);
        Organizer organizer = mock(Organizer.class);
        Event savedEvent = mock(Event.class);

        when(building.getName()).thenReturn("Test Building");
        when(building.getAddress()).thenReturn(mock(Address.class));
        when(savedEvent.getBuilding()).thenReturn(building);
        when(request.getBuildingId()).thenReturn(10L);
        when(request.getOrganizerId()).thenReturn(20L);
        when(buildingRepository.findById(10L)).thenReturn(Optional.of(building));
        when(organizerRepository.findByMemberCredentialId(20L)).thenReturn(Optional.of(organizer));
        when(eventRepository.save(any(Event.class))).thenReturn(savedEvent);

        // Act
        EventResponse result = eventService.createEvent(request);

        // Assert
        assertNotNull(result);
        verify(ticketRepository).saveAll(anyList());
    }

    @Test
    void shouldReturnEvent_whenEventExists() {
        // Arrange
        Long eventId = 1L;
        Event expectedEvent = new Event();

        when(eventRepository.findById(eventId))
                .thenReturn(Optional.of(expectedEvent));

        // Act
        Event result = eventService.getEventById(eventId);

        // Assert
        assertEquals(expectedEvent, result);
        verify(eventRepository).findById(eventId);
    }

    @Test
    void shouldThrowException_whenEventNotFound() {
        // Arrange
        Long eventId = 1L;
        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> eventService.getEventById(eventId));
    }

    @Test
    void shouldDeleteEventAndTickets() {
        // Arrange
        Long eventId = 123L;

        // Act
        eventService.deleteEvent(eventId);

        // Assert
        verify(ticketService).deleteAllTicketsConnectedToEvent(eventId);
        verify(eventRepository).deleteById(eventId);
    }

    @Test
    void shouldUpdateEvent_whenEventExists() {
        // Arrange
        Long eventId = 1L;
        UpdateEventDto updateDto = new UpdateEventDto(
                "New Name",
                "New Description",
                "Category",
                LocalDateTime.now()
        );

        Event existingEvent = new Event();
        existingEvent.setId(eventId);
        existingEvent.setName("Old Name");
        existingEvent.setDescription("Old Description");

        Building building = new Building();
        building.setName("Test Building");
        building.setAddress(new Address());
        existingEvent.setBuilding(building);

        when(eventRepository.findById(eventId))
                .thenReturn(Optional.of(existingEvent));
        when(eventRepository.save(any(Event.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        EventResponse result = eventService.updateEvent(updateDto, eventId);

        // Assert
        assertNotNull(result);
        assertEquals("New Name", existingEvent.getName());
        assertEquals("New Description", existingEvent.getDescription());
        assertEquals(Status.PENDING, existingEvent.getStatus());
        verify(eventRepository).findById(eventId);
        verify(eventRepository).save(existingEvent);
    }

    @Test
    void shouldThrowException_whenUpdatingNonExistingEvent() {
        // Given
        Long eventId = 1L;
        UpdateEventDto updateDto = new UpdateEventDto(
                "New Name",
                "New Description",
                "Category",
                LocalDateTime.now()
        );

        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> eventService.updateEvent(updateDto, eventId));
    }

    @Test
    void shouldUpdateEventStatus() {
        // Arrange
        Building building = mock(Building.class);
        Event event = mock(Event.class);

        when(building.getName()).thenReturn("Test Building");
        when(building.getAddress()).thenReturn(mock(Address.class));
        when(event.getBuilding()).thenReturn(building);
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(eventRepository.save(event)).thenReturn(event);

        // Act
        EventResponse response = eventService.updateEventStatus(1L, Status.APPROVED);

        // Assert
        assertNotNull(response);
        verify(event).setStatus(Status.APPROVED);
    }

    @Test
    void shouldReturnWeatherResponse() throws EventWeatherException {
        // Arrange
        Event event = mock(Event.class);
        Building building = mock(Building.class);
        Address address = new Address(1L,"Poland", "Krakow", "Street", "30-000", "1");

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(event.getBuilding()).thenReturn(building);
        when(building.getAddress()).thenReturn(address);
        when(weatherService.getWeather(any(EventWeatherRequest.class))).thenReturn(mock(EventWeatherResponse.class));

        // Act
        EventWeatherResponse response = eventService.getWeather(1L);

        // Assert
        assertNotNull(response);
    }
}