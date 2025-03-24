package pl.uj.passgo.services;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pl.uj.passgo.models.Building;
import pl.uj.passgo.models.DTOs.EventCreateRequest;
import pl.uj.passgo.models.Event;
import pl.uj.passgo.repos.BuildingRepository;
import pl.uj.passgo.repos.EventRepository;

import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final BuildingRepository buildingRepository;

    public EventService(EventRepository eventRepository, BuildingRepository buildingRepository) {
        this.eventRepository = eventRepository;
        this.buildingRepository = buildingRepository;
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public Event createEvent(EventCreateRequest event) {
        Long buildingId = event.getBuildingId();
        Optional<Building> buildingOptional = buildingRepository.findById(buildingId);

        if(buildingOptional.isPresent()){
            Building building = buildingOptional.get();
            Event builtEvent = Event.builder()
                    .name(event.getName())
                    .building(building)
                    .date(event.getDate())
                    .description(event.getDescription())
                    .category(event.getCategory())
                    .build();

            return eventRepository.save(builtEvent);
        }
        else{
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    String.format("There is no building with id: %d", buildingId)
            );
        }
    }

    public Event getEventById(Long id) {
        Optional<Event> eventOptional = eventRepository.findById(id);

        if(eventOptional.isPresent()){
            return eventOptional.get();
        }
        else{
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    String.format("There is no event with id: %d", id)
            );
        }
    }
}
