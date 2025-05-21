package pl.uj.passgo.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.uj.passgo.models.DTOs.ticket.TicketInfoDto;
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
    List<Ticket> findAllByEventId(Long eventId);
    @Query("""
    SELECT new pl.uj.passgo.models.DTOs.ticket.TicketInfoDto(
        t.sector.name,
        t.row.rowNumber,
        COUNT(t.id),
        t.price
    )
    FROM Ticket t
    WHERE t.event.id = :eventId
    GROUP BY t.sector.name, t.row.rowNumber, t.price
""")
    List<TicketInfoDto> getTicketSummaryByEvent(@Param("eventId") Long eventId);
}
