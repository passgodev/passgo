package pl.uj.passgo.models.DTOs.ticket;

import java.util.List;

public record BulkTicketOnSalePurchaseRequest(
        List<Long> ticketSaleIds
) {
}
