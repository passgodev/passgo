package pl.uj.passgo.controllers.transaction;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.uj.passgo.models.DTOs.transaction.SimpleTransactionDto;
import pl.uj.passgo.models.DTOs.transaction.TransactionDto;
import pl.uj.passgo.services.transaction.TransactionService;

import java.util.List;


@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class TransactionController {
	private final TransactionService transactionService;

	@GetMapping
	public ResponseEntity<Page<TransactionDto>> getTransactions(@PageableDefault Pageable pageable) {
 		var response = transactionService.getTransactions(pageable);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/{transaction-id}")
	public ResponseEntity<TransactionDto> getTransaction(@PathVariable("transaction-id") Long id) {
		var response = transactionService.getTransaction(id);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/user/{id}")
	public ResponseEntity<List<SimpleTransactionDto>> getTransactionsByUser(@PathVariable Long id){
		List<SimpleTransactionDto> transactions = transactionService.getTransactionsByUser(id);
		return ResponseEntity.ok(transactions);
	}
}
