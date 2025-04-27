package pl.uj.passgo.repos.member;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.uj.passgo.models.member.Client;
import pl.uj.passgo.models.member.MemberCredential;

import java.util.Optional;


@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
	Optional<Client> findByMemberCredential(MemberCredential memberCredential);
}
