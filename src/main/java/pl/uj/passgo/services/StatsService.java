package pl.uj.passgo.services;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import pl.uj.passgo.models.DTOs.statistics.EventTicketCount;
import pl.uj.passgo.models.DTOs.statistics.FullStatsResponse;
import pl.uj.passgo.models.DTOs.statistics.StatsFilter;
import pl.uj.passgo.models.DTOs.statistics.StatsResponse;
import pl.uj.passgo.models.event.Event;
import pl.uj.passgo.repos.event.EventRepository;
import pl.uj.passgo.repos.TicketRepository;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatsService {
    private final EventRepository eventRepository;
    private final TicketRepository ticketRepository;

    public FullStatsResponse getEventsStats(StatsFilter filter, Pageable pageable) {
        Page<Event> events = eventRepository.findAll(buildSpec(filter), pageable);
        if (events.isEmpty()) {
            log.info("No events found for the given filter: {}", filter);
            return FullStatsResponse.empty();
        }

        List<Long> eventIds = events.getContent().stream().map(Event::getId).toList();
        Map<Long, EventTicketCount> ticketCounts = ticketRepository.countTicketsByEventIds(eventIds).stream()
                .collect(Collectors.toMap(EventTicketCount::eventId, c -> c));

        long totalTickets = ticketCounts.values().stream().mapToLong(EventTicketCount::total).sum();
        long totalBought = ticketCounts.values().stream().mapToLong(c -> c.total() - c.available()).sum();
        double averageOccupancy = totalTickets == 0 ? 0.0 : (double) totalBought / totalTickets * 100;

        Page<StatsResponse> eventsStats = events.map(event -> {
            EventTicketCount counts = ticketCounts.getOrDefault(event.getId(), new EventTicketCount(event.getId(), 0L, 0L));
            long purchased = counts.total() - counts.available();
            return StatsResponse.builder()
                    .eventName(event.getName())
                    .category(event.getCategory())
                    .ticketsNumber(counts.total())
                    .availableTickets(counts.available())
                    .arenaOccupancy(counts.total() == 0 ? 0.0 : (double) purchased / counts.total() * 100)
                    .date(event.getDate().toLocalDate())
                    .build();
        });

        return new FullStatsResponse(eventsStats, totalTickets, totalBought, averageOccupancy);
    }

    private Specification<Event> buildSpec(StatsFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("date"), LocalDateTime.ofInstant(filter.getFrom(), ZoneOffset.UTC)));
            }
            if (filter.getTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("date"), LocalDateTime.ofInstant(filter.getTo(), ZoneOffset.UTC)));
            }
            if (filter.getEventIds() != null && !filter.getEventIds().isEmpty()) {
                predicates.add(root.get("id").in(filter.getEventIds()));
            }
            if (filter.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), filter.getStatus()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}