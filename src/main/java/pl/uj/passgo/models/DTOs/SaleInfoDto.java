package pl.uj.passgo.models.DTOs;

import pl.uj.passgo.models.DTOs.event.EventDto;
import pl.uj.passgo.models.DTOs.ticket.TicketForSale;

import java.util.List;

public record SaleInfoDto(
        EventDto eventDto,
        List<TicketForSale> tickets
) {
}
