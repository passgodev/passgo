package pl.uj.passgo.repos.member;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.uj.passgo.models.member.Organizer;


public interface OrganizerRepository extends JpaRepository<Organizer, Long> {
}
