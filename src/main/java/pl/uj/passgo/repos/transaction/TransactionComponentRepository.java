package pl.uj.passgo.repos.transaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.uj.passgo.models.Ticket;
import pl.uj.passgo.models.transaction.TransactionComponent;

import java.util.List;

@Repository
public interface TransactionComponentRepository extends JpaRepository<TransactionComponent, Long> {
    void deleteAllByTicket(Ticket ticket);
    void deleteAllByTicketIn(List<Ticket> tickets);
}