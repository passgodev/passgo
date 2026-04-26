package pl.uj.passgo.repos.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.uj.passgo.models.event.Event;
import pl.uj.passgo.models.enums.Status;
import pl.uj.passgo.models.member.MemberType;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {
    List<Event> findByStatus(Status status);

    @Query("SELECT e FROM Event e WHERE e.eventOrganizer.id = :id and e.eventOrganizer.organizerType = :memberType")
    List<Event> findAllByEventOrganizer(Long id, MemberType memberType);

    @Query("""
        SELECT e
        FROM Event e
        WHERE e.eventOrganizer.id = :id 
            AND e.eventOrganizer.organizerType = :memberType
            AND e.status = :status """)
    List<Event> findAllByEventOrganizerAndStatus(Long id, MemberType memberType, Status status);
}
