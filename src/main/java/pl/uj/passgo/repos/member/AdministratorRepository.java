package pl.uj.passgo.repos.member;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.uj.passgo.models.member.Administrator;
import pl.uj.passgo.models.member.MemberCredential;
import pl.uj.passgo.models.member.Organizer;

import java.util.Optional;


public interface AdministratorRepository extends JpaRepository<Administrator, Long> {
	Optional<Administrator> findByMemberCredential(MemberCredential credentials);
}
