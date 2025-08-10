package pl.uj.passgo.configuration.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Slf4j
@Component
public class JwtAuthenticationRequestFilter extends OncePerRequestFilter {
	private static final String BEARER_SPACE = "Bearer ";

	private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
	@Value("${app.configuration.security.enabled:true}")
	private boolean enabled;

	@Autowired
	public JwtAuthenticationRequestFilter(JwtService jwtService,
										  @Qualifier(JwtConfiguration.JwtUserDetailsServiceName) UserDetailsService userDetailsService) {
		this.jwtService = jwtService;
		this.userDetailsService = userDetailsService;
	}

	@Override
	protected void doFilterInternal(
		HttpServletRequest request,
		HttpServletResponse response,
		FilterChain filterChain
	) throws ServletException, IOException {
		log.trace("JWT authentication filter invoked");
		var authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

		if (!enabled && (authHeader == null || !authHeader.startsWith(BEARER_SPACE))) {
			log.trace("JWT authentication filter is DISABLED");
			filterChain.doFilter(request, response);
			return;
		}

		if (authHeader == null || !authHeader.startsWith(BEARER_SPACE)) {
			log.trace("JWT authentication header is missing or invalid");
			filterChain.doFilter(request, response);
			return;
		}

		var requestJwtToken = authHeader.substring(BEARER_SPACE.length());
		var userLogin = jwtService.extractUserLogin(requestJwtToken);
		var authentication = SecurityContextHolder.getContext().getAuthentication();
		if (userLogin == null || authentication != null) {
			log.trace("JWT authentication header is invalid or authentication object does not exist, userLogin: {}, authentication: {}", userLogin, authentication);
			filterChain.doFilter(request, response);
			return;
		}

		final @NotNull UserDetails userDetails;
		try {
			userDetails = userDetailsService.loadUserByUsername(userLogin);
			log.trace("JWT authentication header is valid, userLogin: {}, userDetails: {}", userLogin, userDetails);
		} catch (UsernameNotFoundException e) {
			log.trace("JWT authentication header is valid, but user does not exist, userLogin: {}", userLogin);
			filterChain.doFilter(request, response);
			return;
		}

		var authToken = new UsernamePasswordAuthenticationToken(
			userDetails,
			null,
			userDetails.getAuthorities()
		);
		authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
		SecurityContextHolder.getContext().setAuthentication(authToken);

		log.trace("JWT authentication header is valid, setting authentication object for userLogin: {}", userLogin);
		filterChain.doFilter(request, response);
	}
}
