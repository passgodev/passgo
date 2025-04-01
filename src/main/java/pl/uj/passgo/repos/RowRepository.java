package pl.uj.passgo.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.uj.passgo.models.Row;

@Repository
public interface RowRepository extends JpaRepository<Row, Long> {
}
