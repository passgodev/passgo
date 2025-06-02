package pl.uj.passgo.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import pl.uj.passgo.exception.weather.EventWeatherException;
import pl.uj.passgo.models.*;
import pl.uj.passgo.models.DTOs.EventCreateRequest;
import pl.uj.passgo.models.DTOs.event.UpdateEventDto;
import pl.uj.passgo.models.DTOs.weahter.EventWeatherRequest;
import pl.uj.passgo.models.DTOs.weahter.EventWeatherResponse;
import pl.uj.passgo.models.member.Organizer;
import pl.uj.passgo.models.responses.DetailsEventResponse;
import pl.uj.passgo.models.responses.EventResponse;
import pl.uj.passgo.models.responses.FullEventResponse;
import pl.uj.passgo.repos.BuildingRepository;
import pl.uj.passgo.repos.EventRepository;
import pl.uj.passgo.repos.TicketRepository;
import pl.uj.passgo.repos.member.OrganizerRepository;
import pl.uj.passgo.services.weather.EventWeatherService;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class EventService {
    private static final int EVENT_COOLDOWN_HOURS = 10;

    private final EventRepository eventRepository;
    private final BuildingRepository buildingRepository;
    private final TicketRepository ticketRepository;
    private final TicketService ticketService;
    private final OrganizerRepository organizerRepository;
    private final EventWeatherService weatherService;

    public List<EventResponse> getAllEvents(Status status) {
        var events = status == null ? eventRepository.findAll() : eventRepository.findByStatus(status);
        return events.stream()
                .map(EventService::mapEventToEventResponse)
                .toList();
    }

    public List<EventResponse> getAllOrganizerEvents(Long organizerId , Status status) {
        organizerRepository.findById(organizerId)
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "No organizer found for the provided organizer_id: " + organizerId
            ));

        var organizerEvents = status == null ?
            eventRepository.findAllByOrganizerId(organizerId)
            : eventRepository.findAllByOrganizerIdAndStatus(organizerId, status);

        return organizerEvents.stream()
                .map(EventService::mapEventToEventResponse)
                .toList();
    }

    @Transactional
    public EventResponse createEvent(EventCreateRequest event) {
        Building building = buildingRepository.findById(event.getBuildingId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        String.format("There is no building with id: %d", event.getBuildingId())
                ));

        if (!thisDateIsFree(event.getDate(), building)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "There is already an event announced for this date.");
        }

        if (building.getStatus() != Status.APPROVED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Building is not approved yet");
        }

        Organizer organizer = organizerRepository.findById(event.getOrganizerId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        String.format("There is no organizer with id: %d", event.getOrganizerId())
                ));

        Event builtEvent = Event.builder()
            .name(event.getName())
            .building(building)
            .date(event.getDate())
            .description(event.getDescription())
            .category(event.getCategory())
            .status(Status.PENDING)
            .organizer(organizer)
            .build();

        Event responseEvent = eventRepository.save(builtEvent);
        createAllTickets(building, responseEvent, event.getRowPrices());
        return mapEventToEventResponse(responseEvent);
    }

    private boolean thisDateIsFree(LocalDateTime date, Building building){
        List<Event> events = eventRepository.findAll();

        for (var event : events) {
            if (!event.getBuilding().getId().equals(building.getId())) {
                continue;
            }

            LocalDateTime eventDate = event.getDate();

            if (eventDate.toLocalDate().equals(date.toLocalDate()) && event.getStatus() == Status.APPROVED) {
                long hoursDifference = Math.abs(Duration.between(eventDate, date).toHours());

                if (hoursDifference < EVENT_COOLDOWN_HOURS) {
                    return false;
                }
            }
        }

        return true;
    }

    public Event getEventById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        String.format("There is no event with id: %d", id)
                ));
    }

    public FullEventResponse getFullBuildingById(Long id){
        return mapEventToFullEventResponse(getEventById(id));
    }

    public DetailsEventResponse getDetailsAboutEvent(Long id){
        return mapEventToDetailsResponse(getEventById(id));
    }

    private static DetailsEventResponse mapEventToDetailsResponse(Event event) {
        return new DetailsEventResponse(
            event.getId(),
            event.getName(),
            event.getBuilding(),
            event.getDate(),
            event.getDescription(),
            event.getCategory(),
            event.getStatus()
        );
    }

    @Transactional
    public void deleteEvent(Long id) {
        ticketService.deleteAllTicketsConnectedToEvent(id);
        eventRepository.deleteById(id);
    }

    @Transactional
    public EventResponse updateEvent(UpdateEventDto updateEventDto, Long id) {
        Event event = eventRepository.findById(id)
                .map(existingEvent -> updateExistingEvent(existingEvent, updateEventDto))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event " + id + " does not exist."));
        return mapEventToEventResponse(eventRepository.save(event));
    }

    private Event updateExistingEvent(Event existingEvent, UpdateEventDto updateEventDto) {
        existingEvent.setName(updateEventDto.name());
        existingEvent.setDate(updateEventDto.date());
        existingEvent.setDescription(updateEventDto.description());
        existingEvent.setCategory(updateEventDto.category());
        existingEvent.setStatus(Status.PENDING);
        return existingEvent;
    }

    private void createAllTickets(Building building, Event event, Map<Long, BigDecimal> rowPrices){
        List<Ticket> tickets = new ArrayList<>();

        for(Sector sector : building.getSectors()){
            for(Row row : sector.getRows()){
                Long rowId = row.getId();
                for(Seat seat : row.getSeats()){
                    tickets.add(
                        Ticket.builder()
                            .event(event)
                            .price(rowPrices.get(rowId))
                            .owner(null)
                            .sector(sector)
                            .row(row)
                            .seat(seat)
                            .standingArea(sector.getStandingArea())
                            .build()
                    );
                }
            }
        }
        ticketRepository.saveAll(tickets);
    }

    @Transactional
    public EventResponse updateEventStatus(Long id, Status status) {
        Event event = getEventById(id);
        event.setStatus(status);
        return mapEventToEventResponse(eventRepository.save(event));
    }

    private static EventResponse mapEventToEventResponse(Event event){
        return new EventResponse(
            event.getId(),
            event.getName(),
            event.getBuilding().getName(),
            BuildingService.mapAddressToAddressResponse(event.getBuilding().getAddress()),
            event.getDate(),
            event.getDescription(),
            event.getCategory(),
            event.getStatus()
        );
    }

    private static FullEventResponse mapEventToFullEventResponse(Event event){
        return new FullEventResponse(
            event.getId(),
            event.getName(),
            BuildingService.mapBuildingToBuildingResponse(event.getBuilding()),
            event.getDate(),
            event.getDescription(),
            event.getCategory(),
            event.getStatus()
        );
    }

    public EventWeatherResponse getWeather(Long eventId) {
        var event = this.getEventById(eventId);
        var eventAddress = Optional.ofNullable(event)
            .map(Event::getBuilding)
            .map(Building::getAddress)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                                                           "Event's (id: " + eventId + ") associated building does not have an address."));

        var eventRequest = new EventWeatherRequest(
            eventAddress.getCountry(),
            eventAddress.getCity(),
            Optional.ofNullable(eventAddress.getPostalCode())
        );

        try {
            return weatherService.getWeather(eventRequest);
        } catch (EventWeatherException e) {
            log.error("EventService.getWeater - error: {}\n Cause: {}", e.getMessage(), Optional.ofNullable(e.getCause()).map(Throwable::toString).orElse("No cause"));
            var status = mapFaultReasonToHttpStatus(e.getFaultSide());
            throw new ResponseStatusException(status, e.getMessage());
        }
    }

    // if used in another place, move to seperate mapper class. Move FaultReason as well.
    private static HttpStatus mapFaultReasonToHttpStatus(EventWeatherException.FaultReason faultReason) {
        return switch (faultReason) {
            case INVALID_REQUEST, INVALID_RESPONSE -> HttpStatus.INTERNAL_SERVER_ERROR;
            case SERVICE_UNAVAILABLE -> HttpStatus.SERVICE_UNAVAILABLE;
        };
    }
}