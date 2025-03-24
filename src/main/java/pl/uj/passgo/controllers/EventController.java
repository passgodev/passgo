package pl.uj.passgo.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.uj.passgo.models.DTOs.EventCreateRequest;
import pl.uj.passgo.models.Event;
import pl.uj.passgo.services.EventService;

import java.util.List;

@RestController
@RequestMapping("/events")
public class EventController {

    private EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public ResponseEntity<List<Event>> getAllEvents(){
        List<Event> events = eventService.getAllEvents();
        return ResponseEntity.ok(events);
    }

    @PostMapping
    public ResponseEntity<Event> createEvent(@RequestBody EventCreateRequest event){
        Event createdEvent = eventService.createEvent(event);
        return new ResponseEntity<>(createdEvent, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable Long id) {
        Event fetchedEvent = eventService.getEventById(id);
        return ResponseEntity.ok(fetchedEvent);
    }


}
