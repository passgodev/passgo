package pl.uj.passgo.repos.member;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.uj.passgo.models.Client;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
}
