package pl.uj.passgo.configuration.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;


@Service
public class JwtService {
	private final String secretKey;
	private final long expirationDurationMinutes;

	public JwtService(
		@Value("${app.configuration.security.jwt.secret-key}") String secretKey,
		@Value("${app.configuration.security.jwt.duration}") long expirationDurationMinutes
	) {
		this.secretKey = secretKey;
		this.expirationDurationMinutes = expirationDurationMinutes;
	}

	public String generateToken(UserDetails userDetails) {
		return generateToken(Collections.emptyMap(), userDetails);
	}

	public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
		return buildToken(extraClaims, userDetails, expirationDurationMinutes);
	}

	private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, long expirationTimeInMinutes) {
		return Jwts.builder()
			.setClaims(extraClaims)
			.setSubject(userDetails.getUsername())
			.setIssuedAt(new Date(System.currentTimeMillis()))
			.setExpiration(new Date(System.currentTimeMillis() + expirationTimeInMinutes * DateUtils.MILLIS_PER_MINUTE))
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
