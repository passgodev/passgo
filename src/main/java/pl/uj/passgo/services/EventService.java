package pl.uj.passgo.services;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pl.uj.passgo.models.Building;
import pl.uj.passgo.models.DTOs.EventCreateDTO;
import pl.uj.passgo.models.Event;
import pl.uj.passgo.repos.BuildingRepository;
import pl.uj.passgo.repos.EventRepository;

import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    private EventRepository eventRepository;
    private BuildingRepository buildingRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public Event createEvent(EventCreateDTO event) {
        Long buildingId = event.getBuildingId();
        Optional<Building> buildingOptional = buildingRepository.findById(String.valueOf(buildingId));

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
}
