package pl.uj.passgo.models.DTOs.ticket;

import java.math.BigDecimal;

public record TicketInfoDto(
        String sectorName,
        Long rowNumber,
        Long ticketAmount,
        BigDecimal price
) {
}
