package pl.uj.passgo.services;

import lombok.RequiredArgsConstructor;
import org.hibernate.proxy.pojo.bytebuddy.ByteBuddyInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import pl.uj.passgo.models.*;
import pl.uj.passgo.models.DTOs.EventCreateRequest;
import pl.uj.passgo.models.DTOs.ticket.TicketDto;
import pl.uj.passgo.models.responses.EventResponse;
import pl.uj.passgo.models.responses.FullBuildingResponse;
import pl.uj.passgo.models.responses.FullEventResponse;
import pl.uj.passgo.repos.BuildingRepository;
import pl.uj.passgo.repos.EventRepository;
import pl.uj.passgo.repos.TicketRepository;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class EventService {

    private final EventRepository eventRepository;
    private final BuildingRepository buildingRepository;
    private final TicketRepository ticketRepository;

    @Value("${app.upload-dir}")
    private String imagesPath;
    private static String folderName = "events";

    public List<EventResponse> getAllEvents(Boolean approved) {
        if(approved == null)
            return eventRepository.findAll().stream().map(EventService::mapEventToEventResponse).toList();
        else
            return eventRepository.findByApproved(approved).stream().map(EventService::mapEventToEventResponse).toList();
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
                .approved(false)
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

    public FullEventResponse getFullBuidlingById(Long id){
        return mapEventToFullEventResponse(getEventById(id));
    }

    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }

    public EventResponse updateEvent(EventCreateRequest eventRequest, Long id) {
        Event event = eventRepository.findById(id)
                .map(existingEvent -> updateExistingEvent(existingEvent, eventRequest))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event " + id + " does not exist."));
        return mapEventToEventResponse(eventRepository.save(event));
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

    public EventResponse approveEvent(Long id) {
        Event event = getEventById(id);
        event.setApproved(true);
        return mapEventToEventResponse(eventRepository.save(event));
    }

    private static EventResponse mapEventToEventResponse(Event event){
        return new EventResponse(
                event.getId(),
                event.getName(),
                event.getBuilding().getName(),
                event.getBuilding().getAddress(),
                event.getDate(),
                event.getDescription(),
                event.getCategory(),
                event.getApproved()
        );
    }

    private static FullEventResponse mapEventToFullEventResponse(Event event){
        return new FullEventResponse(
                event.getId(),
                event.getName(),
                event.getBuilding(),
                event.getDate(),
                event.getDescription(),
                event.getCategory(),
                event.getApproved()
        );
    }

    public String uploadImage(MultipartFile file) {
        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filepath = Paths.get(imagesPath, folderName, filename);

        try {
            file.transferTo(filepath);
            return filepath.toString();
        }
        catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Upload failed");
        }

    }
}