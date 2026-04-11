package pl.uj.passgo.repos.ticket_sale;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import pl.uj.passgo.models.DTOs.ticket.FlatSaleRow;
import pl.uj.passgo.models.Event;
import pl.uj.passgo.models.Sector;
import pl.uj.passgo.models.Ticket;
import pl.uj.passgo.models.TicketSale;
import pl.uj.passgo.models.enums.TicketSaleStatus;

import java.util.List;

public class TicketSaleRepositoryImpl implements TicketSaleRepositoryCriteriaApi{

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<FlatSaleRow> findFlatSaleData(Long eventId) {
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

        query.where(cb.equal(ticketSale.get("status"), TicketSaleStatus.ACTIVE));

        if (eventId != null) {
            query.where(cb.equal(event.get("id"), eventId));
        }

        return entityManager.createQuery(query).getResultList();
    }
}
