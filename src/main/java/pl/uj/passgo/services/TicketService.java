package pl.uj.passgo.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import pl.uj.passgo.models.*;
import pl.uj.passgo.models.DTOs.TicketPurchaseRequest;
import pl.uj.passgo.models.DTOs.ticket.TicketInfoDto;
import pl.uj.passgo.models.DTOs.ticket.TicketResponse;
import pl.uj.passgo.models.member.Client;
import pl.uj.passgo.models.transaction.TransactionType;
import pl.uj.passgo.repos.*;
import pl.uj.passgo.repos.member.ClientRepository;

import java.util.List;
import pl.uj.passgo.models.DTOs.ticket.TicketPurchaseResponse;
import pl.uj.passgo.models.transaction.Transaction;
import pl.uj.passgo.models.transaction.TransactionComponent;
import pl.uj.passgo.repos.EventRepository;
import pl.uj.passgo.repos.SeatRepository;
import pl.uj.passgo.repos.TicketRepository;
import pl.uj.passgo.repos.transaction.TransactionComponentRepository;
import pl.uj.passgo.repos.transaction.TransactionRepository;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class TicketService {

    private final TicketRepository ticketRepository;
    private final EventRepository eventRepository;
    private final SeatRepository seatRepository;
    private final SectorRepository sectorRepository;
    private final RowRepository rowRepository;
    private final ClientRepository clientRepository;
    private final WalletOperationService walletOperationService;


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
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Provided tickets: are already occupied");
        }
    }

    private final LoggedInMemberContextService loggedInMemberContextService;
    private final Clock clock = Clock.systemDefaultZone();
    private final TransactionRepository transactionRepository;
    private final TransactionComponentRepository transactionComponentRepository;

    @Transactional
    public TicketPurchaseResponse purchaseTickets(
        pl.uj.passgo.models.DTOs.ticket.TicketPurchaseRequest ticketsPurchaseRequest
    ) {
        var ticketsToBuyIds = ticketsPurchaseRequest.ticketIds();
        var tickets = ticketRepository.getTicketsByIdIn(ticketsToBuyIds);
        checkIfAllTicketsExist(tickets, ticketsToBuyIds);
        checkIfTicketsAreNotAlreadyBought(tickets, ticketsToBuyIds);

        // calculate tickets total price
        var ticketsTotalPrice = tickets.stream().map(Ticket::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add);

        // check if client have sufficient amount of money
        var client = loggedInMemberContextService.isClientLoggedIn().orElseThrow();
        var clientMoney = client.getWallet().getMoney();
        if (clientMoney.compareTo(ticketsTotalPrice) < 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Client money is insufficient");
        }

        // decrease client's wallet money amount and save result to wallet history
        walletOperationService.chargeWalletForTicketPurchase(client, ticketsTotalPrice);

        // perform assignment of client to tickets
        tickets.forEach(ticket -> ticket.setOwner(client));

        // create transaction and transaction components
        var transaction = Transaction.builder()
            .client(client)
            .totalPrice(ticketsTotalPrice)
            .completedAt(LocalDateTime.now(clock))
            .transactionType(TransactionType.PURCHASE)
            .build();

        var savedTransaction = transactionRepository.save(transaction);

        var transactionComponents = new ArrayList<TransactionComponent>();
        for (var ticket : tickets) {
            var transactionComponent = TransactionComponent.builder()
                .transaction(savedTransaction)
                .ticket(ticket)
                .build();
            transactionComponents.add(transactionComponent);
        }
        transactionComponentRepository.saveAll(transactionComponents);

        return new TicketPurchaseResponse(ticketsTotalPrice, tickets.size());
    }

    public Page<Ticket> getAllTickets(Pageable pageable) {
        return ticketRepository.findAll(pageable);
    }

    public Ticket getTicketById(Long id) {
        return ticketRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found"));
    }

    public Ticket updateTicket(TicketPurchaseRequest ticketRequest, Long id) {
        Ticket ticket = getTicketById(id);
        ticket.setPrice(ticketRequest.getPrice());

        if(!ticket.getEvent().getId().equals(ticketRequest.getEventId())) {
            Event event = eventRepository.findById(ticketRequest.getEventId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));
            ticket.setEvent(event);
        }
        if(!ticket.getSector().getId().equals(ticketRequest.getSectorId())) {
            Sector sector = sectorRepository.findById(ticketRequest.getSectorId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sector not found"));
            ticket.setSector(sector);
        }
        if(!ticket.getRow().getId().equals(ticketRequest.getRowId())) {
            Row row = rowRepository.findById(ticketRequest.getRowId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Row not found"));
            ticket.setRow(row);
        }
        if(!ticket.getSeat().getId().equals(ticketRequest.getSeatId())) {
            Seat seat = seatRepository.findById(ticketRequest.getSeatId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Seat not found"));
            ticket.setSeat(seat);
        }
        if(!ticket.getOwner().getId().equals(ticketRequest.getOwnerId())) {
            Client client = clientRepository.findById(ticketRequest.getOwnerId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Client not found"));
            ticket.setOwner(client);
        }

        ticket.setStandingArea(ticketRequest.getStandingArea());

        return ticketRepository.save(ticket);
    }

    public void deleteTicket(Long id) {
        ticketRepository.deleteById(id);
    }

    public List<Ticket> getTicketByClientId(Long id) {
        return ticketRepository.findAllByOwnerId(id);
    }

    @Transactional
    public void returnTicket(Long id) {
        Ticket ticket = getTicketById(id);

        if(ticket.getOwner() == null){
            throw new ResponseStatusException(HttpStatus.CONFLICT, String.format("Ticket with id: %d is not purchased.", id));
        }

        Client client = loggedInMemberContextService.isClientLoggedIn().orElseThrow();

        var returnPrice = ticket.getPrice();
        var clientMoney = client.getWallet().getMoney();

        ticket.setOwner(null);
        client.getWallet().setMoney(clientMoney.add(returnPrice));

        var transaction = Transaction.builder()
                .client(client)
                .totalPrice(returnPrice)
                .completedAt(LocalDateTime.now(clock))
                .transactionType(TransactionType.RETURN)
                .build();

        var savedTransaction = transactionRepository.save(transaction);
        var transactionComponent = TransactionComponent.builder()
                .transaction(savedTransaction)
                .ticket(ticket)
                .build();

        ticketRepository.save(ticket);
        clientRepository.save(client);
        transactionComponentRepository.save(transactionComponent);
    }

    public List<TicketResponse> getAllAvailableTicketsForEvent(Long id) {
        List<Ticket> tickets = ticketRepository.findAllByEventIdAndOwnerIsNull(id);
        List<TicketResponse> ticketResponses = new ArrayList<>();

        for(Ticket ticket : tickets){
            ticketResponses.add(TicketResponse.builder()
                    .standingArea(ticket.getStandingArea())
                    .rowId(ticket.getRow().getId())
                    .seatId(ticket.getSeat().getId())
                    .sectorId(ticket.getSector().getId())
                    .price(ticket.getPrice())
                    .id(ticket.getId())
                    .sectorName(ticket.getSector().getName())
                    .build()
            );
        }

        return ticketResponses;
    }

    @Transactional
    public void deleteAllTicketsConnectedToEvent(Long id) {
        List<Ticket> tickets = ticketRepository.findAllByEventId(id);

        for (Ticket ticket : tickets) {
            if (ticket.getOwner() != null) {
                walletOperationService.rechargeWalletForTicketReturn(ticket.getOwner(), ticket.getPrice());
            }
        }
        ticketRepository.deleteAll(tickets);
    }

    public List<TicketInfoDto> getTicketsInfoByEventId(Long eventId) {
        return ticketRepository.getTicketSummaryByEvent(eventId);
    }
}
