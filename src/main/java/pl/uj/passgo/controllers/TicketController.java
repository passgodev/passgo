package pl.uj.passgo.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import pl.uj.passgo.models.*;
import pl.uj.passgo.models.DTOs.TicketPurchaseRequest;
import pl.uj.passgo.services.PDFGenerator;
import pl.uj.passgo.services.TicketService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/tickets")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class TicketController {

    private final TicketService ticketService;
    private final PDFGenerator pdfGenerator;

    @GetMapping
    public ResponseEntity<Page<Ticket>> getAllTickets(@PageableDefault Pageable pageable) {
        Page<Ticket> tickets = ticketService.getAllTickets(pageable);
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ticket> getTicketById(@PathVariable("id") Long id) {
        Ticket ticket = ticketService.getTicketById(id);
        return ResponseEntity.ok(ticket);
    }

    @GetMapping("/client/{id}")
    public ResponseEntity<List<Ticket>> getTicketByClientId(@PathVariable("id") Long id) {
        List<Ticket> tickets = ticketService.getTicketByClientId(id);
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> getTicketPdf(@PathVariable("id") Long id) {
//        Ticket ticket = ticketService.getTicketById(id);
        Event event = Event.builder()
                .id(1L)
                .name("wydarzenie")
                .date(LocalDateTime.now())
                .description("daw daw daw dasssssssssssssw ad a")
                .build();
        Sector sector = Sector.builder().name("A2").build();
        Row row = Row.builder().rowNumber(12L).build();
        Seat seat = Seat.builder().id(123L).build();
        Client client = Client.builder().id(1L).firstName("Wojtek").lastName("Wojetek").build();

        Ticket ticket = Ticket.builder()
                .id(id)
                .price(new BigDecimal("100.0"))
                .event(event)
                .sector(sector)
                .row(row)
                .seat(seat)
                .standingArea(false)
                .owner(client)
                .build();

        byte[] pdf = pdfGenerator.generateTicketPdf(ticket);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        //lub zamiast inline "attachment"
        headers.setContentDisposition(ContentDisposition.builder("inline").filename("ticket_" + id + ".pdf").build());

        return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Ticket> purchaseTicket(@RequestBody TicketPurchaseRequest ticket) {
        Ticket purchasedTicket = ticketService.purchaseTicket(ticket);
        return new ResponseEntity<>(purchasedTicket, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Ticket> updateTicket(@RequestBody TicketPurchaseRequest ticket, @PathVariable Long id) {
        Ticket updatedTicket = ticketService.updateTicket(ticket, id);
        return ResponseEntity.ok(updatedTicket);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicket(@PathVariable Long id) {
        ticketService.deleteTicket(id);
        return ResponseEntity.noContent().build();
    }
}
