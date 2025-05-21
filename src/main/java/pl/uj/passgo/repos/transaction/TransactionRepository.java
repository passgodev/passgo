package pl.uj.passgo.repos.transaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.uj.passgo.models.WalletHistory;
import pl.uj.passgo.models.transaction.Transaction;

import java.util.List;


@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findTransactionByClientId(Long id);
}
