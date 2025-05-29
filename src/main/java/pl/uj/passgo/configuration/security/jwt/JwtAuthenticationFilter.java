package pl.uj.passgo.configuration.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Slf4j
@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	private static final String BEARER_SPACE = "Bearer ";

	private final JwtService jwtService;
	private final UserDetailsService userDetailsService;
	@Value("${app.configuration.security.enabled}")
	private boolean enabled;

	@Override
	protected void doFilterInternal(
		HttpServletRequest request,
		HttpServletResponse response,
		FilterChain filterChain
	) throws ServletException, IOException {
		var authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
		if (!enabled && (authHeader == null || !authHeader.startsWith(BEARER_SPACE))) {
			log.trace("JWT authentication filter is DISABLED");
			filterChain.doFilter(request, response);
			return;
		}

		if (authHeader == null || !authHeader.startsWith(BEARER_SPACE)) {
			filterChain.doFilter(request, response);
			return;
		}

		var requestJwtToken = authHeader.substring(BEARER_SPACE.length());
		var userLogin = jwtService.extractUserLogin(requestJwtToken);
		var authentication = SecurityContextHolder.getContext().getAuthentication();

		if ( userLogin != null && authentication == null ) {
			var userDetails = userDetailsService.loadUserByUsername(userLogin);

			if ( userDetails != null && jwtService.isTokenValid(requestJwtToken, userDetails) ) {
				var authToken = new UsernamePasswordAuthenticationToken(
					userDetails,
					null,
					userDetails.getAuthorities()
				);

				authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(authToken);
			}
		}

		filterChain.doFilter(request, response);
	}
}
