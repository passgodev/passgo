package pl.uj.passgo.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import pl.uj.passgo.mappers.event.EventMapper;
import pl.uj.passgo.models.DTOs.SaleInfoDto;
import pl.uj.passgo.models.DTOs.ticket.FlatSaleRow;
import pl.uj.passgo.models.DTOs.ticket.TicketForSale;
import pl.uj.passgo.models.DTOs.ticket.TicketPurchaseResponse;
import pl.uj.passgo.models.Ticket;
import pl.uj.passgo.models.TicketSale;
import pl.uj.passgo.models.enums.TicketSaleStatus;
import pl.uj.passgo.models.enums.TicketStatus;
import pl.uj.passgo.models.member.Client;
import pl.uj.passgo.models.member.Member;
import pl.uj.passgo.repos.TicketRepository;
import pl.uj.passgo.repos.ticket_sale.TicketSaleRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketSaleService {

    private final TicketService ticketService;
    private final LoggedInMemberContextService loggedInMemberContextService;
    private final TicketSaleRepository ticketSaleRepository;
    private final EventMapper eventMapper;
    private final TicketRepository ticketRepository;

    public void offerTicket(Long ticketId, BigDecimal price) {
        Ticket ticket = ticketService.getTicketById(ticketId);
        Client seller = loggedInMemberContextService.isClientLoggedIn()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not logged in"));

        validateTicketResell(ticket, seller);

        TicketSale ticketSale = TicketSale.builder()
                .ticket(ticket)
                .seller(seller)
                .buyer(null)
                .status(TicketSaleStatus.ACTIVE)
                .price(price)
                .build();

        ticket.setStatus(TicketStatus.FOR_SALE);
        ticket.setOwner(null);

        ticketRepository.save(ticket);
        ticketSaleRepository.save(ticketSale);
    }

    private void validateTicketResell(Ticket ticket, Client seller) {
        if (ticket.getStatus() != TicketStatus.ASSIGNED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ticket is not assigned");
        }
        Long ownerId = Optional.ofNullable(ticket.getOwner())
                .map(Member::getId)
                .orElse(null);

        if (ownerId == null || !ownerId.equals(seller.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ticket does not belong to user");
        }
    }

    public List<SaleInfoDto> getTicketsForSale(Long eventId) {
        Client user = loggedInMemberContextService.isClientLoggedIn().orElseThrow();
        List<FlatSaleRow> flatRows = ticketSaleRepository.findFlatSaleData(eventId, user.getId());

        return flatRows.stream()
                .collect(Collectors.groupingBy(
                        FlatSaleRow::event,
                        Collectors.mapping(row -> new TicketForSale(
                                row.ticketSaleId(),
                                row.ticketId(),
                                row.originalPrice(),
                                row.actualPrice(),
                                row.sectorName()
                        ), Collectors.toList())
                ))
                .entrySet().stream()
                .map(entry -> new SaleInfoDto(
                        eventMapper.toEventDto(entry.getKey()),
                        entry.getValue()
                ))
                .toList();
    }

    @Transactional
    public TicketPurchaseResponse orderOfferedTickets(List<Long> ticketSaleIds) {
        List<TicketSale> ticketSales = ticketSaleRepository.getTicketSaleByIdIn(ticketSaleIds);
        return ticketService.orderTicketsOnSale(ticketSales);
    }
}
