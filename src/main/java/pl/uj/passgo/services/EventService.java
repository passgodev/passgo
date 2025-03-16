package pl.uj.passgo.services;

import org.springframework.stereotype.Service;
import pl.uj.passgo.repos.EventRepository;

@Service
public class EventService {

    private EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }
}
