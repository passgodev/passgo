package pl.uj.passgo.mappers.transaction;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.uj.passgo.mappers.client.ClientMapper;
import pl.uj.passgo.mappers.event.EventMapper;
import pl.uj.passgo.models.DTOs.transaction.TransactionDto;
import pl.uj.passgo.models.transaction.Transaction;


@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class TransactionMapper {
	private final ClientMapper clientMapper;
	private final EventMapper eventMapper;

	public TransactionDto toDto(Transaction transaction) {
		var clientDto = clientMapper.toClientDto(transaction.getClient());
		var eventDto = eventMapper.toEventDto(transaction.getEvent());

		return new TransactionDto(
			transaction.getId(),
			transaction.getTotalPrice(),
			transaction.getCompletedAt(),
			clientDto,
			eventDto
		);
	}
}
