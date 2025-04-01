package pl.uj.passgo.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.uj.passgo.models.Seat;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
}
