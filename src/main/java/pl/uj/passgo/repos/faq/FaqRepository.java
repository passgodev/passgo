package pl.uj.passgo.repos.faq;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.uj.passgo.models.Faq;
import pl.uj.passgo.models.responses.FaqResponse;

import java.util.Optional;


@Repository
public interface FaqRepository extends JpaRepository<Faq, Long> {
    Optional<Faq> getFaqById(Long id);
}
