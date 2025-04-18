package pl.uj.passgo.models.DTOs.transaction;

import pl.uj.passgo.models.DTOs.client.ClientDto;
import pl.uj.passgo.models.DTOs.event.EventDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


public record TransactionDto(
	Long id,
	BigDecimal totalPrice,
	LocalDateTime completedAt,
	ClientDto client,
	EventDto event,
	List<TransactionComponentDto> transactionComponents
) {
}
