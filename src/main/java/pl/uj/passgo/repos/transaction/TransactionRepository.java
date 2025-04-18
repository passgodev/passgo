package pl.uj.passgo.repos.transaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.uj.passgo.models.transaction.Transaction;


@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
