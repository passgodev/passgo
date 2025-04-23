package pl.uj.passgo.services.authentication;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pl.uj.passgo.models.authentication.RefreshToken;
import pl.uj.passgo.models.member.MemberCredential;
import pl.uj.passgo.repos.authentication.RefreshTokenRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;


@Service
public class RefreshTokenService {
	private final RefreshTokenRepository refreshTokenRepository;
	private final Duration expirationDuration;

	@Autowired
	public RefreshTokenService(
		RefreshTokenRepository refreshTokenRepository,
		@Value("${JWT_REFRESH_TOKEN_DURATION}")
		Long expirationDurationMinutes
	) {
		this.refreshTokenRepository = refreshTokenRepository;
		this.expirationDuration = Duration.ofMinutes(expirationDurationMinutes);
	}

	public RefreshToken getByToken(UUID token) {
		return refreshTokenRepository.findByToken(token)
									 .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Refresh token not found"));
	}

	public void isAfterExpirationDate(RefreshToken refreshToken) {
		if ( refreshToken.getExpiresAt().isBefore(LocalDateTime.now()) ) {
			refreshTokenRepository.delete(refreshToken);
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Refresh token expired");
		}
	}

	public RefreshToken createRefreshToken(MemberCredential memberCredential) {
		var refreshToken = new RefreshToken();

		refreshToken.setToken(UUID.randomUUID());
		refreshToken.setMemberCredential(memberCredential);
		refreshToken.setExpiresAt(LocalDateTime.now().plusMinutes(expirationDuration.toMinutes()));

		return refreshTokenRepository.save(refreshToken);
	}
}
