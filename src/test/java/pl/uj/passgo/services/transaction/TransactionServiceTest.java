package pl.uj.passgo.services.transaction;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.web.server.ResponseStatusException;
import pl.uj.passgo.mappers.client.ClientMapper;
import pl.uj.passgo.mappers.event.EventMapper;
import pl.uj.passgo.mappers.ticket.TicketMapper;
import pl.uj.passgo.mappers.transaction.TransactionMapper;
import pl.uj.passgo.models.member.Client;
import pl.uj.passgo.models.member.MemberCredential;
import pl.uj.passgo.models.transaction.Transaction;
import pl.uj.passgo.repos.transaction.TransactionRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {
	@Mock
	private TransactionRepository transactionRepository;

	private TransactionMapper transactionMapper;

	private TransactionService transactionService;

	@BeforeEach
	public void setup() {
		this.transactionMapper = getTransactionMapper();
		transactionService = new TransactionService(transactionRepository, transactionMapper);
	}

	@Test
	public void testGetTransactions() {
		// arrange
		when(transactionRepository.findAll(any(Pageable.class)))
			.thenReturn(new PageImpl<Transaction>(List.of(getValidTransaction())));
		// act
		var mappedTransactions = transactionService.getTransactions(Pageable.unpaged());

		// assert
		Assertions.assertEquals(1, mappedTransactions.getSize());
	}

	@Test
	public void testGetTransactionById_notFound() {
		// arrange
		var transactionId = 1L;
		when(transactionRepository.findById(transactionId))
			.thenReturn(Optional.empty());

		// act
		Executable shouldFail = () -> transactionService.getTransaction(transactionId);

		// assert
		Assertions.assertThrows(ResponseStatusException.class, shouldFail);
	}

	@Test
	public void testGetTransactionById_found() {
		// arrange
		var transactionId = 1L;
		var mockTransaction = getValidTransaction();
		mockTransaction.setId(transactionId);
		when(transactionRepository.findById(transactionId))
			.thenReturn(Optional.of(mockTransaction));

		// act
		var returnedTransacstion = transactionService.getTransaction(transactionId);

		// assert
		Assertions.assertEquals(transactionId, returnedTransacstion.id());
	}

	private static Transaction getValidTransaction() {
		var client = new Client();
		client.setMemberCredential(new MemberCredential());
		var transaction = new Transaction();
		transaction.setClient(client);
		transaction.setTransactionComponents(Collections.emptyList());

		return transaction;
	}

	private TransactionMapper getTransactionMapper() {
		var clientMapper = new ClientMapper();
		var eventMapper = new EventMapper();
		var ticketMapper = new TicketMapper(eventMapper, clientMapper);
		return new TransactionMapper(clientMapper, ticketMapper);
	}
}
