package pl.uj.passgo.repos.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.uj.passgo.models.event.EventOrganizer;
import pl.uj.passgo.models.member.MemberType;

import java.util.Optional;


@Repository
public interface EventOrganizerRepository extends JpaRepository<EventOrganizer, Long> {
    Optional<EventOrganizer> findByOrganizerIdAndOrganizerTypeEquals(Long organizerId, MemberType organizerType);
}
