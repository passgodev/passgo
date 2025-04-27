package pl.uj.passgo.repos.authentication;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.uj.passgo.models.authentication.RefreshToken;

import java.util.Optional;
import java.util.UUID;


@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
	Optional<RefreshToken> findByToken(UUID token);
}
