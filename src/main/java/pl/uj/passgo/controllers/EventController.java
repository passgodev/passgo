package pl.uj.passgo.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.uj.passgo.models.Building;
import pl.uj.passgo.models.DTOs.EventCreateRequest;
import pl.uj.passgo.models.Event;
import pl.uj.passgo.services.EventService;

import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class EventController {

    private final EventService eventService;

    @GetMapping
    public ResponseEntity<List<Event>> getAllEvents(@RequestParam(required = false) Boolean approved) {
        List<Event> events;

        if(approved == null){
            events = eventService.getAllEvents();
        }
        else{
            events = eventService.getEventsByApproved(approved);
        }

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

    @PutMapping("/{id}")
    public ResponseEntity<Event> updateEvent(@RequestBody EventCreateRequest eventRequest, @PathVariable Long id) {
        Event event = eventService.updateEvent(eventRequest, id);
        return ResponseEntity.ok(event);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/approve")
    public ResponseEntity<Event> approveBuilding(@PathVariable Long id){
        Event approvedEvent = eventService.approveEvent(id);
        return ResponseEntity.ok(approvedEvent);
    }
}
