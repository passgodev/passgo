package pl.uj.passgo.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.uj.passgo.models.Event;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
}
