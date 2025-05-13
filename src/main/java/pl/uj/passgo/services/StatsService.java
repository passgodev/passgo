package pl.uj.passgo.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.uj.passgo.models.DTOs.StatsResponse;
import pl.uj.passgo.models.Event;
import pl.uj.passgo.repos.EventRepository;
import pl.uj.passgo.repos.TicketRepository;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class StatsService {

    private final EventService eventService;
    private final TicketService ticketService;
    private final TicketRepository ticketRepository;
    private final EventRepository eventRepository;

    public StatsResponse getEventStats(Long id) {
        Event event = eventService.getEventById(id);

        long numberOfAllTickets = ticketRepository.countByEventId(event.getId());
        long numberOfAvailableTickets = ticketRepository.countByEventIdAndOwnerIsNull(event.getId());
        long numberOfPurchasedTickets = numberOfAllTickets - numberOfAvailableTickets;

        return StatsResponse.builder()
                .eventName(event.getName())
                .category(event.getCategory())
                .ticketsNumber(numberOfAllTickets)
                .availableTickets(numberOfAvailableTickets)
                .arenaOccupancy((double) numberOfPurchasedTickets / numberOfAllTickets)
                .build();
    }
}
