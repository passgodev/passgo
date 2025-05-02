package pl.uj.passgo.models.DTOs.ticket;

import pl.uj.passgo.models.DTOs.client.ClientDto;
import pl.uj.passgo.models.DTOs.event.EventDto;

import java.math.BigDecimal;


public record TicketDto(
	Long id,
	BigDecimal price,
	EventDto event,
	Long sectorId,
	Long rowId,
	Long seatId,
	boolean standingArea,
	ClientDto client
) {}
