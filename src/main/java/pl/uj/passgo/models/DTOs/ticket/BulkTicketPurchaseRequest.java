package pl.uj.passgo.models.DTOs.ticket;

import java.util.List;


public record BulkTicketPurchaseRequest(
	List<Long> ticketIds
) {
}
