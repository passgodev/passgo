package pl.uj.passgo.repos.member;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.uj.passgo.models.member.MemberCredential;
import pl.uj.passgo.models.member.Organizer;

import java.util.Optional;


public interface OrganizerRepository extends JpaRepository<Organizer, Long> {
	Optional<Organizer> findByMemberCredential(MemberCredential credentials);
	Optional<Organizer> findByMemberCredentialId(Long credentialId);
}
