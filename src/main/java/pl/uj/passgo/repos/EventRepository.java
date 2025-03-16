package pl.uj.passgo.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.uj.passgo.models.Event;

public interface EventRepository extends JpaRepository<Event, String> {
}
