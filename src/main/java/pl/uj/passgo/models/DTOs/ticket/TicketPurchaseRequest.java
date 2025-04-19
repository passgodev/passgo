package pl.uj.passgo.models.DTOs.ticket;

import java.util.List;


public record TicketPurchaseRequest(
	List<Long> ticketIds
) {
}
