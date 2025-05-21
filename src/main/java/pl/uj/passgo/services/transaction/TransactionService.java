package pl.uj.passgo.services.transaction;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pl.uj.passgo.mappers.transaction.TransactionMapper;
import pl.uj.passgo.models.DTOs.transaction.SimpleTransactionDto;
import pl.uj.passgo.models.DTOs.transaction.TransactionDto;
import pl.uj.passgo.models.transaction.Transaction;
import pl.uj.passgo.repos.transaction.TransactionRepository;

import java.util.List;


@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class TransactionService {
	private final TransactionRepository transactionRepository;
	private final TransactionMapper transactionMapper;

	public Page<TransactionDto> getTransactions(Pageable pageable) {
		return transactionRepository.findAll(pageable).map(transactionMapper::toDto);
	}

	public TransactionDto getTransaction(Long id) {
		return transactionRepository.findById(id)
			.map(transactionMapper::toDto)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
	}

	public List<SimpleTransactionDto> getTransactionsByUser(Long id) {
		List<Transaction> transactions = transactionRepository.findTransactionByClientId(id);
		return transactionMapper.toSimpleTransactionDto(transactions);
	}
}
