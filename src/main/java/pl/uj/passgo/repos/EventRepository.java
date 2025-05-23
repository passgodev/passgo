package pl.uj.passgo.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.uj.passgo.models.Event;
import pl.uj.passgo.models.Status;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByStatus(Status status);
    List<Event> findAllByOrganizerId(Long id);
    List<Event> findAllByOrganizerIdAndStatus(Long id, Status status);
}
