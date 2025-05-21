package pl.uj.passgo.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.uj.passgo.models.DTOs.EventCreateRequest;
import pl.uj.passgo.models.DTOs.event.ImageDto;
import pl.uj.passgo.models.DTOs.event.UpdateEventDto;
import pl.uj.passgo.models.DTOs.ticket.TicketResponse;
import pl.uj.passgo.models.Status;
import pl.uj.passgo.models.responses.EventResponse;
import pl.uj.passgo.models.responses.FullEventResponse;
import pl.uj.passgo.services.EventService;
import pl.uj.passgo.services.MediaService;
import pl.uj.passgo.services.TicketService;

import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class EventController {

    private final EventService eventService;
    private final MediaService mediaService;
    private final TicketService ticketService;

    @GetMapping
    public ResponseEntity<List<EventResponse>> getAllEvents(@RequestParam(required = false) Status status) {
        List<EventResponse> events = eventService.getAllEvents(status);
        return ResponseEntity.ok(events);
    }

    @PostMapping
    public ResponseEntity<EventResponse> createEvent(@RequestBody EventCreateRequest event){
        EventResponse createdEvent = eventService.createEvent(event);
        return new ResponseEntity<>(createdEvent, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FullEventResponse> getEventById(@PathVariable Long id) {
        FullEventResponse fetchedEvent = eventService.getFullBuildingById(id);
        return ResponseEntity.ok(fetchedEvent);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventResponse> updateEvent(@RequestBody UpdateEventDto updateEventDto, @PathVariable Long id) {
        EventResponse event = eventService.updateEvent(updateEventDto, id);
        return ResponseEntity.ok(event);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<EventResponse> updateEventStatus(@PathVariable Long id, @RequestParam Status status){
        EventResponse approvedEvent = eventService.updateEventStatus(id, status);
        return ResponseEntity.ok(approvedEvent);
    }

    @PostMapping("/{id}/image")
    public ResponseEntity<String> uploadImage(@PathVariable Long id, @RequestParam("file") MultipartFile file){
        return ResponseEntity.ok(mediaService.uploadImage(file, id));
    }

    @GetMapping("/{id}/image")
    public @ResponseBody ResponseEntity<byte[]> getEventImage(@PathVariable Long id){
        ImageDto imageDto = mediaService.getEventsMainImage(id);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(imageDto.contentType()))
                .body(imageDto.data());
    }

    @GetMapping("{id}/tickets")
    public ResponseEntity<List<TicketResponse>> getAllAvailableTicketsForEvent(@PathVariable Long id){
        List<TicketResponse> tickets = ticketService.getAllAvailableTicketsForEvent(id);
        return ResponseEntity.ok(tickets);
    }
}
