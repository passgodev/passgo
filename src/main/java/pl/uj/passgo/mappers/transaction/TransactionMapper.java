package pl.uj.passgo.mappers.transaction;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.uj.passgo.mappers.client.ClientMapper;
import pl.uj.passgo.mappers.ticket.TicketMapper;
import pl.uj.passgo.models.DTOs.transaction.SimpleTransactionDto;
import pl.uj.passgo.models.DTOs.transaction.TransactionComponentDto;
import pl.uj.passgo.models.DTOs.transaction.TransactionDto;
import pl.uj.passgo.models.transaction.Transaction;
import pl.uj.passgo.models.transaction.TransactionComponent;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class TransactionMapper {
	private final ClientMapper clientMapper;
	private final TicketMapper ticketMapper;

	public TransactionDto toDto(Transaction transaction) {
		var clientDto = clientMapper.toClientDto(transaction.getClient());
		var transactionComponentDtos = transaction.getTransactionComponents().stream().map(this::toDto).toList();

		return new TransactionDto(
			transaction.getId(),
			transaction.getTotalPrice(),
			transaction.getCompletedAt(),
			clientDto,
			transactionComponentDtos
		);
	}

	public TransactionComponentDto toDto(TransactionComponent transactionComponent) {
		var ticketDto = ticketMapper.toDto(transactionComponent.getTicket());

		return new TransactionComponentDto(
			transactionComponent.getId(),
			ticketDto
		);
	}

	public List<SimpleTransactionDto> toSimpleTransactionDto(List<Transaction> transactions){
		List<SimpleTransactionDto> responses = new ArrayList<>(transactions.size());
		for(var transaction : transactions){
			responses.add(SimpleTransactionDto.builder()
					.id(transaction.getId())
					.completedAt(transaction.getCompletedAt())
					.totalPrice(transaction.getTotalPrice())
					.transactionType(transaction.getTransactionType())
					.build()
			);
		}
		return responses;
	}
}
