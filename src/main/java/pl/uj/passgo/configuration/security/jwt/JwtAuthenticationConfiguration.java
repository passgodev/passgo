package pl.uj.passgo.configuration.security.jwt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class JwtAuthenticationConfiguration {
	private final AuthenticationProvider authenticationProvider;
	private final JwtAuthenticationFilter jwtAuthenticationFilter;

	@ConditionalOnProperty(name = "app.configuration.security.enabled", havingValue = "true", matchIfMissing = true)
	@EnableMethodSecurity
	public static class EnableMethodSecurityBean {}

	@Bean
	@ConditionalOnProperty(name = "app.configuration.security.enabled", havingValue = "true", matchIfMissing = true)
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		log.info("Security filter chain for jwt is ENABLED");

		http
			.headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
			.authorizeHttpRequests(authorizeRequests -> {
				authorizeRequests
					.requestMatchers("/auth/login").permitAll()
					.requestMatchers("/auth/signup").permitAll()
					.requestMatchers("/auth/refresh").permitAll()
					.requestMatchers(PathRequest.toH2Console()).permitAll()
					.anyRequest().authenticated();
			})
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.csrf(csrf -> csrf.ignoringRequestMatchers(PathRequest.toH2Console()).disable())
			.authenticationProvider(authenticationProvider)
			.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	@ConditionalOnProperty(name = "app.configuration.security.enabled", havingValue = "false")
	public SecurityFilterChain disabledSecurityFilterChain(HttpSecurity http) throws Exception {
		log.info("Security filter chain for jwt is DISABLED");

		http
			.headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
			.authorizeHttpRequests(authorizeRequests -> {
				authorizeRequests.anyRequest().permitAll();
			})
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.csrf(AbstractHttpConfigurer::disable);

		return http.build();
	}

}
