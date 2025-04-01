package pl.uj.passgo.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.uj.passgo.models.Ticket;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
}
