package pl.uj.passgo.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pl.uj.passgo.models.*;
import pl.uj.passgo.models.DTOs.TicketPurchaseRequest;
import pl.uj.passgo.repos.EventRepository;
import pl.uj.passgo.repos.SeatRepository;
import pl.uj.passgo.repos.SectorRepository;
import pl.uj.passgo.repos.TicketRepository;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class TicketService {

    private final TicketRepository ticketRepository;
    private final EventRepository eventRepository;
    private final SellingService sellingService;
    private final SeatRepository seatRepository;

    // private final ClientRepository clientRepository;
    // TODO: create ClientRepository


    public Ticket purchaseTicket(TicketPurchaseRequest ticketRequest) {
        Long eventId = ticketRequest.getEventId();
        Long userId = ticketRequest.getOwnerId();
        boolean standingArea = ticketRequest.getStandingArea().booleanValue();

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        String.format("There is no event with id: %d", eventId)
                ));


        //Client client = clientRepository.findById(userId)
        //        .orElseThrow(() -> new ResponseStatusException(
        //                HttpStatus.BAD_REQUEST,
        //                String.format("There is no user with id: %d", userId)
        //        ));

        //TODO: in sellTicket method we should check if place is occupied (field in seat entity)
        if (!sellingService.sellTicket()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ticket sale failed.");
        }

        Ticket.TicketBuilder ticketBuilder = Ticket.builder()
                .price(ticketRequest.getPrice())
                .event(event)
                .standingArea(standingArea);

        if (!standingArea) {
            Seat seat = seatRepository.findById(ticketRequest.getSeatId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Seat not found"));

            ticketBuilder.seat(seat)
                    .row(seat.getRow())
                    .sector(seat.getRow().getSector());
        }

        return ticketRepository.save(ticketBuilder.build());
    }

}
