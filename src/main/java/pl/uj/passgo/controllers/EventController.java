package pl.uj.passgo.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.uj.passgo.models.Building;
import pl.uj.passgo.models.DTOs.EventCreateRequest;
import pl.uj.passgo.models.Event;
import pl.uj.passgo.models.responses.EventResponse;
import pl.uj.passgo.models.responses.FullEventResponse;
import pl.uj.passgo.services.EventService;

import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class EventController {

    private final EventService eventService;

    @GetMapping
    public ResponseEntity<List<EventResponse>> getAllEvents(@RequestParam(required = false) Boolean approved) {
        List<EventResponse> events = eventService.getAllEvents(approved);
        return ResponseEntity.ok(events);
    }

    @PostMapping
    public ResponseEntity<EventResponse> createEvent(@RequestBody EventCreateRequest event){
        EventResponse createdEvent = eventService.createEvent(event);
        return new ResponseEntity<>(createdEvent, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FullEventResponse> getEventById(@PathVariable Long id) {
        FullEventResponse fetchedEvent = eventService.getFullBuidlingById(id);
        return ResponseEntity.ok(fetchedEvent);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventResponse> updateEvent(@RequestBody EventCreateRequest eventRequest, @PathVariable Long id) {
        EventResponse event = eventService.updateEvent(eventRequest, id);
        return ResponseEntity.ok(event);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/approve")
    public ResponseEntity<EventResponse> approveBuilding(@PathVariable Long id){
        EventResponse approvedEvent = eventService.approveEvent(id);
        return ResponseEntity.ok(approvedEvent);
    }

    @PostMapping("/image")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file){
        return ResponseEntity.ok(eventService.uploadImage(file));
    }
}
