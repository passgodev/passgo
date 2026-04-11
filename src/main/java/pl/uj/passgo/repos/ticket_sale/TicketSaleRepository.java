package pl.uj.passgo.repos.ticket_sale;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.uj.passgo.models.TicketSale;

import java.util.List;

public interface TicketSaleRepository extends JpaRepository<TicketSale, Long>, TicketSaleRepositoryCriteriaApi {

    @Transactional
    @Modifying
    @Query("DELETE FROM TicketSale ts WHERE ts.ticket.id IN :ticketIds")
    void deleteByTicketIdIn(@Param("ticketIds") List<Long> ticketIds);
}
