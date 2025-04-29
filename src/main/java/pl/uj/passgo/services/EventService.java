package pl.uj.passgo.services;

import lombok.RequiredArgsConstructor;
import org.hibernate.proxy.pojo.bytebuddy.ByteBuddyInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pl.uj.passgo.models.*;
import pl.uj.passgo.models.DTOs.EventCreateRequest;
import pl.uj.passgo.models.DTOs.ticket.TicketDto;
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

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public Event createEvent(EventCreateRequest event) {
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
                .approved(false)
                .build();

        Event resposeEvent = eventRepository.save(builtEvent);
        createAllTickets(building, builtEvent, event.getRowPrices());
        return resposeEvent;
    }

    public Event getEventById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        String.format("There is no event with id: %d", id)
                ));
    }

    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }

    public Event updateEvent(EventCreateRequest eventRequest, Long id) {
        Event event = eventRepository.findById(id)
                .map(existingEvent -> updateExistingEvent(existingEvent, eventRequest))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event " + id + " does not exist."));
        return eventRepository.save(event);
    }

    private Event updateExistingEvent(Event existingEvent, EventCreateRequest eventRequest) {
        Building building = buildingRepository.findById(eventRequest.getBuildingId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Building " + eventRequest.getBuildingId() + " does not exist."));
        existingEvent.setBuilding(building);
        existingEvent.setName(eventRequest.getName());
        existingEvent.setDate(eventRequest.getDate());
        existingEvent.setDescription(eventRequest.getDescription());
        existingEvent.setCategory(eventRequest.getCategory());
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

    public Event approveEvent(Long id) {
        Event event = getEventById(id);
        event.setApproved(true);
        return eventRepository.save(event);
    }

    public List<Event> getEventsByApproved(Boolean approved) {
        return eventRepository.findByApproved(approved);
    }
}