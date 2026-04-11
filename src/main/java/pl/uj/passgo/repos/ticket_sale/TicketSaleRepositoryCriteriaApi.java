package pl.uj.passgo.repos.ticket_sale;

import pl.uj.passgo.models.DTOs.ticket.FlatSaleRow;

import java.util.List;

public interface TicketSaleRepositoryCriteriaApi {
    List<FlatSaleRow> findFlatSaleData(Long eventId);
}
