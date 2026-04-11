package pl.uj.passgo.models.DTOs.ticket;

import java.math.BigDecimal;

public record TicketForSale(
    Long ticketSaleId,
    Long ticketId,
    BigDecimal originalPrice,
    BigDecimal actualPrice,
    String sectorName
) {
}
