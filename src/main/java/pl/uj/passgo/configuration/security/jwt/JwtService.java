package pl.uj.passgo.configuration.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import pl.uj.passgo.models.member.MemberCredential;

import java.security.Key;
import java.time.Duration;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;


@Service
public class JwtService {
	private final String secretKey;
	private final Duration expirationDurationMinutes;

	public JwtService(
		@Value("${JWT_SECRET}") String secretKey,
		@Value("${JWT_TOKEN_DURATION}") Duration expirationDuration
	) {
		this.secretKey = secretKey;
		this.expirationDurationMinutes = expirationDuration;
	}

	public String generateToken(MemberCredential memberCredential, Long memberId) {
		return generateToken(Map.of("memberType", memberCredential.getMemberType(), "memberId", memberId), memberCredential);
	}

	private String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
		return buildToken(extraClaims, userDetails, expirationDurationMinutes);
	}

	private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, Duration expirationTimeInMinutes) {
		return Jwts.builder()
			.setClaims(extraClaims)
			.setSubject(userDetails.getUsername())
			.setIssuedAt(new Date(System.currentTimeMillis()))
			.setExpiration(new Date(System.currentTimeMillis() + expirationTimeInMinutes.toMillis()))
			.signWith(getSignKey(), SignatureAlgorithm.HS256)
			.compact();
	}

	private Key getSignKey() {
		var keyBytes = Decoders.BASE64.decode(secretKey);
		return Keys.hmacShaKeyFor(keyBytes);
	}

	public boolean isTokenValid(String token, UserDetails userDetails) {
		var username = extractUserLogin(token);
		return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
	}

	private boolean isTokenExpired(String token) {
		var expirationDate = extractClaim(token, Claims::getExpiration);
		return expirationDate.before(new Date());
	}

	public String extractUserLogin(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		var claims = Jwts.parserBuilder()
						 .setSigningKey(secretKey)
						 .build()
						 .parseClaimsJws(token)
						 .getBody();

		return claimsResolver.apply(claims);
	}
}
