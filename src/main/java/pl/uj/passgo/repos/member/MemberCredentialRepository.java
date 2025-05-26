package pl.uj.passgo.repos.member;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.uj.passgo.models.member.MemberCredential;

import java.util.Optional;


@Repository
public interface MemberCredentialRepository extends JpaRepository<MemberCredential, Long> {
	Optional<MemberCredential> findByLogin(String login);
}
