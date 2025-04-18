package pl.uj.passgo.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import pl.uj.passgo.models.DTOs.TicketPurchaseRequest;
import pl.uj.passgo.models.DTOs.ticket.TicketPurchaseResponse;
import pl.uj.passgo.models.Event;
import pl.uj.passgo.models.Seat;
import pl.uj.passgo.models.Ticket;
import pl.uj.passgo.repos.EventRepository;
import pl.uj.passgo.repos.SeatRepository;
import pl.uj.passgo.repos.TicketRepository;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;


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

    private static void checkIfAllTicketsExist(List<Ticket> tickets, List<Long> ticketToBuyIds) {
        var validTicketsMap = new HashMap<>(tickets.stream().collect(Collectors.toMap(Ticket::getId, Function.identity())));
        var invalidTicketIds = ticketToBuyIds.stream().filter(id -> !validTicketsMap.containsKey(id)).toList();
        if ( !invalidTicketIds.isEmpty() ) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Provided tickets ids: " + invalidTicketIds.toString() + " do not exist.");
        }
    }

    private static void checkIfTicketsAreNotAlreadyBought(List<Ticket> tickets, List<Long> ticketToBuyIds) {
        var occupiedTickets = tickets.stream().map(Ticket::getOwner).filter(Objects::nonNull).toList();
        if ( !occupiedTickets.isEmpty() ) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Provided tickets: " + occupiedTickets.toString() + " are already occupied");
        }
    }

    private final LoggedInMemberContextService loggedInMemberContextService;

    @Transactional
    public TicketPurchaseResponse purchaseTickets(
        pl.uj.passgo.models.DTOs.ticket.TicketPurchaseRequest ticketsPurchaseRequest
    ) {
        var ticketsToBuyIds = ticketsPurchaseRequest.ticketIds();
        var tickets = ticketRepository.getTicketsByIdIn(ticketsToBuyIds);
        checkIfAllTicketsExist(tickets, ticketsToBuyIds);
        checkIfTicketsAreNotAlreadyBought(tickets, ticketsToBuyIds);

        // calculate total price
        var ticketsTotalPrice = tickets.stream().map(Ticket::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add);

        // check if user have sufficient amount of money
        var client = loggedInMemberContextService.isClientLoggedIn().orElseThrow();
        var clientMoney = client.getWallet().getMoney();
        if (clientMoney.compareTo(ticketsTotalPrice) >= 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Client money is insufficient");
        }

        // decrease wallet money amount
        client.getWallet().setMoney(clientMoney.subtract(ticketsTotalPrice));

        // perform assignment of client to tickets
        tickets.forEach(ticket -> ticket.setOwner(client));

        return new TicketPurchaseResponse(ticketsTotalPrice, tickets.size());
    }
}
