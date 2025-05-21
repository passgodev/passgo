package pl.uj.passgo.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pl.uj.passgo.models.*;
import pl.uj.passgo.models.DTOs.EventCreateRequest;
import pl.uj.passgo.models.DTOs.event.UpdateEventDto;
import pl.uj.passgo.models.responses.EventResponse;
import pl.uj.passgo.models.responses.FullEventResponse;
import pl.uj.passgo.repos.BuildingRepository;
import pl.uj.passgo.repos.EventRepository;
import pl.uj.passgo.repos.TicketRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class EventService {

    private final EventRepository eventRepository;
    private final BuildingRepository buildingRepository;
    private final TicketRepository ticketRepository;
    private final TicketService ticketService;

    public List<EventResponse> getAllEvents(Status status) {
        if(status == null) {
            return eventRepository.findAll().stream().map(EventService::mapEventToEventResponse).toList();
        } else {
            return eventRepository.findByStatus(status).stream().map(EventService::mapEventToEventResponse).toList();
        }

    }

    public EventResponse createEvent(EventCreateRequest event) {
        Building building = buildingRepository.findById(event.getBuildingId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        String.format("There is no building with id: %d", event.getBuildingId())
                ));

        Event builtEvent = Event.builder()
                .name(event.getName())
                .building(building)
                .date(event.getDate())
                .description(event.getDescription())
                .category(event.getCategory())
                .status(Status.PENDING)
                .build();

        Event resposeEvent = eventRepository.save(builtEvent);
        createAllTickets(building, builtEvent, event.getRowPrices());
        return mapEventToEventResponse(resposeEvent);
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

    public void deleteEvent(Long id) {
        ticketService.deleteAllTicketsConnectedToEvent(id);
        eventRepository.deleteById(id);
    }

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

}