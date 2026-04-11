package pl.uj.passgo.models.DTOs.ticket;

import pl.uj.passgo.models.Event;

import java.math.BigDecimal;

public record FlatSaleRow(
        Event event,
        Long ticketSaleId,
        Long ticketId,
        BigDecimal originalPrice,
        BigDecimal actualPrice,
        String sectorName
) {}