package pl.uj.passgo.repos.ticket_sale;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.uj.passgo.models.TicketSale;

import java.util.Collection;
import java.util.List;

public interface TicketSaleRepository extends JpaRepository<TicketSale, Long>, TicketSaleRepositoryCriteriaApi {
    List<TicketSale> getTicketSaleByIdIn(Collection<Long> ids);
}
