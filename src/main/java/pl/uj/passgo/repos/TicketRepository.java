package pl.uj.passgo.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.uj.passgo.models.Ticket;

import java.util.Collection;
import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
	List<Ticket> getTicketsByIdIn(Collection<Long> ids);
    List<Ticket> findAllByOwnerId(Long id);
    long countByEventId(Long eventId);
    long countByEventIdAndOwnerIsNull(Long eventId);
    List<Ticket> findAllByEventIdAndOwnerIsNull(Long eventId);
}
