package pl.uj.passgo.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import pl.uj.passgo.models.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.uj.passgo.models.DTOs.TicketPurchaseRequest;
import pl.uj.passgo.models.DTOs.ticket.TicketPurchaseResponse;
import pl.uj.passgo.models.Ticket;
import pl.uj.passgo.services.TicketService;

import java.util.List;

@RestController
@RequestMapping("/tickets")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class TicketController {

    private final TicketService ticketService;

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

    @PostMapping("/purchase")   // todo: later change this to default post endpoint name
    public ResponseEntity<TicketPurchaseResponse> purchaseTickets(@RequestBody pl.uj.passgo.models.DTOs.ticket.TicketPurchaseRequest tickets) {
        var purchasedTicketsResponse = ticketService.purchaseTickets(tickets);
        return ResponseEntity.ok(purchasedTicketsResponse);
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

    @PostMapping("/{id}/return")
    public ResponseEntity<String> returnTicket(@PathVariable Long id){
        ticketService.returnTicket(id);
        return ResponseEntity.ok(String.format("Ticket with id: %d was succesfully returned", id));
    }
}
