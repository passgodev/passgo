package pl.uj.passgo.repos.ticket_sale;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import pl.uj.passgo.models.DTOs.ticket.FlatSaleRow;
import pl.uj.passgo.models.Sector;
import pl.uj.passgo.models.Ticket;
import pl.uj.passgo.models.TicketSale;
import pl.uj.passgo.models.enums.TicketSaleStatus;
import pl.uj.passgo.models.event.Event;

import java.util.ArrayList;
import java.util.List;

public class TicketSaleRepositoryImpl implements TicketSaleRepositoryCriteriaApi{

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<FlatSaleRow> findFlatSaleData(Long eventId, Long userId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<FlatSaleRow> query = cb.createQuery(FlatSaleRow.class);
        Root<TicketSale> ticketSale = query.from(TicketSale.class);

        Join<TicketSale, Ticket> ticket = ticketSale.join("ticket");
        Join<Ticket, Event> event = ticket.join("event");
        Join<Ticket, Sector> sector = ticket.join("sector");

        query.select(cb.construct(FlatSaleRow.class,
                event,
                ticketSale.get("id"),
                ticket.get("id"),
                ticket.get("price"),
                ticketSale.get("price"),
                sector.get("name")
        ));

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(ticketSale.get("status"), TicketSaleStatus.ACTIVE));
        predicates.add(cb.notEqual(ticketSale.get("seller").get("id"), userId));

        if (eventId != null) {
            predicates.add(cb.equal(event.get("id"), eventId));
        }

        query.where(cb.and(predicates.toArray(new Predicate[0])));
        return entityManager.createQuery(query).getResultList();
    }
}
