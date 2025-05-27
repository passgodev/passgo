package pl.uj.passgo.controllers.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import pl.uj.passgo.models.DTOs.EventCreateRequest;
import pl.uj.passgo.models.DTOs.event.ImageDto;
import pl.uj.passgo.models.DTOs.event.UpdateEventDto;
import pl.uj.passgo.models.DTOs.ticket.TicketResponse;
import pl.uj.passgo.models.DTOs.weahter.EventWeatherResponse;
import pl.uj.passgo.models.Status;
import pl.uj.passgo.models.responses.EventResponse;
import pl.uj.passgo.models.responses.FullEventResponse;
import pl.uj.passgo.services.EventService;
import pl.uj.passgo.services.MediaService;
import pl.uj.passgo.services.TicketService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class EventControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private EventService eventService;

    @MockitoBean
    private MediaService mediaService;

    @MockitoBean
    private TicketService ticketService;

    @Test
    void shouldReturnAllEvents() throws Exception {
        // Arrange
        List<EventResponse> mockEvents = List.of(
                new EventResponse(
                        1L,
                        "Concert",
                        "Main Hall",
                        null,
                        LocalDateTime.now(),
                        "Description",
                        "Category",
                        Status.APPROVED
                )
        );

        when(eventService.getAllEvents(null)).thenReturn(mockEvents);

        // Act & Assert
        mockMvc.perform(get("/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Concert"));
    }

    @Test
    void shouldReturnOrganizerEvents() throws Exception {
        // Arrange
        Long organizerId = 5L;
        List<EventResponse> mockEvents = List.of(
                new EventResponse(
                        2L,
                        "Theatre",
                        "Building",
                        null,
                        LocalDateTime.now(),
                        "Play",
                        "THEATER",
                        Status.PENDING
                )
        );

        when(eventService.getAllOrganizerEvents(organizerId, null)).thenReturn(mockEvents);

        // Act & Assert
        mockMvc.perform(get("/events/{id}/organizer", organizerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2L))
                .andExpect(jsonPath("$[0].name").value("Theatre"));
    }

    @Test
    @WithMockUser(roles = "ORGANIZER")
    void shouldCreateEvent() throws Exception {
        // Arrange
        EventCreateRequest request = new EventCreateRequest(
                1L,
                "Test Event",
                1L,
                LocalDateTime.now(),
                "Description",
                "CONCERT",
                Map.of()
        );
        EventResponse response = new EventResponse(
                1L,
                "Test Event",
                "Building A",
                null,
                LocalDateTime.now(),
                "Description",
                "CONCERT",
                Status.PENDING);

        when(eventService.createEvent(any())).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test Event"));
    }

    @Test
    void shouldReturnSingleEvent() throws Exception {
        // Arrange
        Long eventId = 10L;
        FullEventResponse fullEventResponse = new FullEventResponse(
                eventId,
                "Exhibition",
                null,
                LocalDateTime.now(),
                "Art show",
                "EXHIBITION",
                Status.APPROVED
        );

        when(eventService.getFullBuildingById(eventId)).thenReturn(fullEventResponse);

        // Act & Assert
        mockMvc.perform(get("/events/{id}", eventId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(eventId))
                .andExpect(jsonPath("$.name").value("Exhibition"));
    }

    @Test
    @WithMockUser(roles = "ORGANIZER")
    void shouldUpdateEvent() throws Exception {
        // Arrange
        UpdateEventDto dto = new UpdateEventDto(
                "Updated Name",
                "Updated Desc",
                "SPORT",
                LocalDateTime.now()
        );
        EventResponse response = new EventResponse(
                1L,
                "Updated Name",
                "Building A",
                null,
                LocalDateTime.now(),
                "Updated Desc",
                "SPORT",
                Status.PENDING
        );

        when(eventService.updateEvent(eq(dto), eq(1L))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(put("/events/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"));
    }

    @Test
    @WithMockUser(roles = "ORGANIZER")
    void shouldDeleteEvent() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/events/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(eventService).deleteEvent(1L);
    }

    @Test
    @WithMockUser(roles = "ADMINISTRATOR")
    void shouldUpdateEventStatus() throws Exception {
        // Arrange
        Long eventId = 7L;
        Status newStatus = Status.APPROVED;
        EventResponse updatedEvent = new EventResponse(
                eventId,
                "Event",
                "Venue",
                null,
                LocalDateTime.now(),
                "Updated",
                "CONCERT",
                newStatus
        );

        when(eventService.updateEventStatus(eventId, newStatus)).thenReturn(updatedEvent);

        // Act & Assert
        mockMvc.perform(patch("/events/{id}/status", eventId)
                        .param("status", newStatus.name()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(newStatus.name()));
    }

    @Test
    @WithMockUser(roles = "ORGANIZER")
    void shouldUploadImage() throws Exception {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "image.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "dummy image".getBytes()
        );

        when(mediaService.uploadImage(any(), eq(1L))).thenReturn("uploaded-image-url");

        // Act & Assert
        mockMvc.perform(multipart("/events/1/image").file(file))
                .andExpect(status().isOk())
                .andExpect(content().string("uploaded-image-url"));
    }

    @Test
    void shouldReturnEventImage() throws Exception {
        // Arrange
        byte[] imageBytes = "dummy image data".getBytes();
        ImageDto imageDto = new ImageDto(imageBytes, MediaType.IMAGE_PNG_VALUE);

        when(mediaService.getEventsMainImage(1L)).thenReturn(imageDto);

        // Act & Assert
        mockMvc.perform(get("/events/1/image"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_PNG))
                .andExpect(content().bytes(imageBytes));
    }

    @Test
    void shouldReturnAvailableTicketsForEvent() throws Exception {
        // Arrange
        TicketResponse ticket = new TicketResponse(
                1L,
                BigDecimal.TEN,
                1L,
                "Sector A",
                1L,
                1L,
                false
        );
        when(ticketService.getAllAvailableTicketsForEvent(1L)).thenReturn(List.of(ticket));

        // Act & Assert
        mockMvc.perform(get("/events/1/tickets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void shouldReturnWeather() throws Exception {
        // Arrange
        Long eventId = 11L;
        EventWeatherResponse.CurrentWeather currentWeather = new EventWeatherResponse.CurrentWeather("10 km/h", "22", true);
        EventWeatherResponse.WeatherUnit weatherUnit = new EventWeatherResponse.WeatherUnit("km/h", "°C");

        EventWeatherResponse weatherResponse = new EventWeatherResponse(currentWeather, weatherUnit);

        when(eventService.getWeather(eventId)).thenReturn(weatherResponse);

        // Act & Assert
        mockMvc.perform(get("/events/{eventId}/weather", eventId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.current_weather.windspeed").value("10 km/h"))
                .andExpect(jsonPath("$.current_weather.temperature").value("22"))
                .andExpect(jsonPath("$.current_weather.is_day").value(true));
    }

}