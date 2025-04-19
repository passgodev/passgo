package pl.uj.passgo.models.DTOs.ticket;

import java.math.BigDecimal;


public record TicketPurchaseResponse(
	BigDecimal totalPrice,
	long ticketQuantity
) {
}
