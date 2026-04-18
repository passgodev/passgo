package pl.uj.passgo.services;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import pl.uj.passgo.models.*;
import pl.uj.passgo.models.DTOs.TicketPurchaseRequest;
import pl.uj.passgo.models.DTOs.ticket.*;
import pl.uj.passgo.models.enums.TicketSaleStatus;
import pl.uj.passgo.models.enums.TicketStatus;
import pl.uj.passgo.models.member.Client;
import pl.uj.passgo.models.enums.TransactionType;
import pl.uj.passgo.repos.*;
import pl.uj.passgo.repos.member.ClientRepository;

import java.util.*;

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
import java.util.function.Function;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final EventRepository eventRepository;
    private final SeatRepository seatRepository;
    private final SectorRepository sectorRepository;
    private final RowRepository rowRepository;
    private final ClientRepository clientRepository;
    private final WalletOperationService walletOperationService;
    private final LoggedInMemberContextService loggedInMemberContextService;
    private final TransactionRepository transactionRepository;
    private final TransactionComponentRepository transactionComponentRepository;
    private final Clock clock;

    private static void checkIfAllTicketsExist(List<Ticket> tickets, List<Long> ticketToBuyIds) {
        var validTicketsMap = new HashMap<>(tickets.stream().collect(Collectors.toMap(Ticket::getId, Function.identity())));
        var invalidTicketIds = ticketToBuyIds.stream().filter(id -> !validTicketsMap.containsKey(id)).toList();
        if ( !invalidTicketIds.isEmpty() ) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Provided tickets ids: " + invalidTicketIds + " do not exist.");
        }
    }

    private static void checkIfTicketsAreNotAlreadyBought(List<Ticket> tickets) {
        var occupiedTickets = tickets.stream().map(Ticket::getOwner).filter(Objects::nonNull).toList();
        if ( !occupiedTickets.isEmpty() ) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Provided tickets: are already occupied");
        }
    }

    private static void checkIfAllTicketsHaveStatus(List<Ticket> tickets, @NotNull TicketStatus status) {
        boolean allTicketsHaveStatus = tickets.stream().allMatch(ticket -> status.equals(ticket.getStatus()));
        if (!allTicketsHaveStatus) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Provided tickets: are not for sale");
        }
    }

    private static void checkIfAllSalesAreNotUserSales(List<TicketSale> ticketSales, Client client) {
        boolean allSalesAreNotUserSales = ticketSales.stream().noneMatch(ts -> ts.getSeller().getId().equals(client.getId()));
        if (!allSalesAreNotUserSales) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Provided ticket sales: can not purchase your own sale");
        }
    }

    @Transactional
    public TicketPurchaseResponse orderTickets(List<Long> ticketIds) {
        List<Ticket> tickets = ticketRepository.getTicketsByIdIn(ticketIds);
        checkIfAllTicketsExist(tickets, ticketIds);
        checkIfTicketsAreNotAlreadyBought(tickets);

        return purchaseTickets(tickets);
    }

    @Transactional
    public TicketPurchaseResponse orderTicketsOnSale(List<TicketSale> ticketSales) {
        Client client = loggedInMemberContextService.isClientLoggedIn().orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT));
        List<Ticket> tickets = ticketSales.stream().map(TicketSale::getTicket).toList();

        checkIfTicketsAreNotAlreadyBought(tickets);
        checkIfAllTicketsHaveStatus(tickets, TicketStatus.FOR_SALE);
        checkIfAllSalesAreNotUserSales(ticketSales, client);

        return purchaseTicketsOnSale(ticketSales, client);
    }

    @Transactional
    public TicketPurchaseResponse purchaseTickets(List<Ticket> tickets) {
        // calculate tickets total price
        var ticketsTotalPrice = tickets.stream().map(Ticket::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add);

        // check if client have sufficient amount of money
        var client = loggedInMemberContextService.isClientLoggedIn().orElseThrow(() -> {
            return new ResponseStatusException(HttpStatus.CONFLICT);
        });
        var clientMoney = client.getWallet().getMoney();
        if (clientMoney.compareTo(ticketsTotalPrice) < 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Client money is insufficient");
        }

        // decrease client's wallet money amount and save result to wallet history
        walletOperationService.createWalletHistoryEntry(client, ticketsTotalPrice.negate(), "Ticket Purchase");

        // perform assignment of client to tickets
        tickets.forEach(ticket -> {
            ticket.setOwner(client);
            ticket.setStatus(TicketStatus.ASSIGNED);
        });

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

    @Transactional
    public TicketPurchaseResponse purchaseTicketsOnSale(List<TicketSale> ticketSales, Client client) {
        BigDecimal ticketsTotalPrice = ticketSales.stream()
                .map(TicketSale::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal clientMoney = client.getWallet().getMoney();
        if (clientMoney.compareTo(ticketsTotalPrice) < 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Client money is insufficient");
        }

        LocalDateTime now = LocalDateTime.now(clock);
        List<TransactionComponent> allTransactionComponents = new ArrayList<>();
        walletOperationService.createWalletHistoryEntry(client, ticketsTotalPrice.negate(), "Ticket Purchase (Bulk)");

        Transaction buyerTransaction = Transaction.builder()
                .client(client)
                .totalPrice(ticketsTotalPrice)
                .completedAt(now)
                .transactionType(TransactionType.PURCHASE)
                .build();

        Transaction savedBuyerTransaction = transactionRepository.save(buyerTransaction);
        Map<Client, List<TicketSale>> salesGroupedBySeller = ticketSales.stream()
                .collect(Collectors.groupingBy(TicketSale::getSeller));

        for (Map.Entry<Client, List<TicketSale>> entry : salesGroupedBySeller.entrySet()) {
            Client seller = entry.getKey();
            List<TicketSale> sellerTickets = entry.getValue();

            BigDecimal sellerTotalEarnings = sellerTickets.stream()
                    .map(TicketSale::getPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            walletOperationService.createWalletHistoryEntry(seller, sellerTotalEarnings, "Ticket Sale (Grouped)");

            Transaction sellerTransaction = Transaction.builder()
                    .client(seller)
                    .totalPrice(sellerTotalEarnings)
                    .completedAt(now)
                    .transactionType(TransactionType.SALE)
                    .build();
            Transaction savedSellerTransaction = transactionRepository.save(sellerTransaction);

            for (TicketSale ticketSale : sellerTickets) {
                Ticket ticket = ticketSale.getTicket();
                ticket.setOwner(client);
                ticket.setStatus(TicketStatus.ASSIGNED);

                ticketSale.setBuyer(client);
                ticketSale.setStatus(TicketSaleStatus.FINISHED);

                // ticket relation with buyer
                allTransactionComponents.add(TransactionComponent.builder()
                        .transaction(savedBuyerTransaction)
                        .ticket(ticket)
                        .build());

                // ticket relation with seller
                allTransactionComponents.add(TransactionComponent.builder()
                        .transaction(savedSellerTransaction)
                        .ticket(ticket)
                        .build());
            }
        }

        transactionComponentRepository.saveAll(allTransactionComponents);
        return new TicketPurchaseResponse(ticketsTotalPrice, ticketSales.size());
    }

    public Page<TicketFullResponse> getAllTickets(Pageable pageable) {
        return ticketRepository.findAll(pageable).map(this::mapTicketToTicketFullResponse);
    }

    private TicketFullResponse mapTicketToTicketFullResponse(Ticket ticket) {
        return TicketFullResponse.builder()
                .id(ticket.getId())
                .eventName(ticket.getEvent() != null ? ticket.getEvent().getName() : null)
                .price(ticket.getPrice())
                .sectorName(ticket.getSector() != null ? ticket.getSector().getName() : null)
                .rowNumber(ticket.getRow() != null ? ticket.getRow().getRowNumber() : null)
                .seatNumber(ticket.getSeat() != null ? ticket.getSeat().getSeatNumber() : null)
                .standingArea(ticket.getStandingArea())
                .build();
    }

    public Ticket getTicketById(Long id) {
        return ticketRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found"));
    }

    public TicketFullResponse getTicketFullResponseById(Long id) {
        return ticketRepository.findById(id).map(this::mapTicketToTicketFullResponse).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found"));
    }

    public TicketFullResponse updateTicket(TicketPurchaseRequest ticketRequest, Long id) {
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
        ticketRepository.save(ticket);

        return mapTicketToTicketFullResponse(ticket);
    }

    @Transactional
    public void deleteTicketWithRefund(Long id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found"));

        boolean hasOwner = ticket.getOwner() != null;
        if (hasOwner && ticket.getEvent().getDate().isAfter(LocalDateTime.now(clock))) {
            processTicketReturn(ticket);
        } else if (hasOwner) {
            ticket.setOwner(null);
            ticketRepository.save(ticket);
        }

        transactionComponentRepository.deleteAllByTicket(ticket);
        ticketRepository.deleteById(id);
    }

    public List<TicketFullResponse> getTicketByClientId(Long id) {
        return ticketRepository.findAllByOwnerId(id).stream()
                .map(this::mapTicketToTicketFullResponse)
                .toList();
    }

    @Transactional
    public void returnTicket(Long id) {
        Ticket ticket = getTicketById(id);

        if (ticket.getOwner() == null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, String.format("Ticket with id: %d is not purchased.", id));
        }

        processTicketReturn(ticket);
    }

    private void processTicketReturn(Ticket ticket) {
        Client client = loggedInMemberContextService.isClientLoggedIn().orElseThrow();
        BigDecimal returnPrice = ticket.getPrice();

        ticket.setOwner(null);
        ticket.setStatus(TicketStatus.AVAILABLE);
        walletOperationService.createWalletHistoryEntry(client, returnPrice, "Ticket Return");

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
        var tickets = ticketRepository.findAllByEventId(id);

        for (Ticket ticket : tickets) {
            if (shouldProcessRefund(ticket)) {
                processTicketReturn(ticket);
            } else if (ticket.getOwner() != null) {
                ticket.setOwner(null);
                ticketRepository.save(ticket);
            }
        }
        transactionComponentRepository.deleteAllByTicketIn(tickets);
        ticketRepository.deleteAll(tickets);
    }

    private boolean shouldProcessRefund(Ticket ticket) {
        return ticket.getOwner() != null
                && ticket.getEvent() != null
                && ticket.getEvent().getDate().isAfter(LocalDateTime.now(clock));
    }

    public List<TicketInfoDto> getTicketsInfoByEventId(Long eventId) {
        return ticketRepository.getTicketSummaryByEvent(eventId);
    }
}
