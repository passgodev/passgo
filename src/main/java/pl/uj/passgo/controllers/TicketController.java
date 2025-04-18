package pl.uj.passgo.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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

@RestController
@RequestMapping("/tickets")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class TicketController {

    private final TicketService ticketService;

    @PostMapping
    public ResponseEntity<Ticket> purchaseTicket(@RequestBody TicketPurchaseRequest ticket) {
        Ticket purchasedTicket = ticketService.purchaseTicket(ticket);
        return new ResponseEntity<>(purchasedTicket, HttpStatus.CREATED);
    }

    @PostMapping("/purchase")   // todo: later change this to default post endpoint name
    public ResponseEntity<TicketPurchaseResponse> purchaseTickets(@RequestBody pl.uj.passgo.models.DTOs.ticket.TicketPurchaseRequest tickets) {
        var purchasedTicketsResponse = ticketService.purchaseTickets(tickets);
        return ResponseEntity.ok(purchasedTicketsResponse);
    }
}
