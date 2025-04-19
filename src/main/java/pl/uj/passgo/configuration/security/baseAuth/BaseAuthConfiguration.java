package pl.uj.passgo.configuration.security.baseAuth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import java.util.UUID;


@Slf4j
//@Configuration
//@EnableWebSecurity
public class BaseAuthConfiguration {
	final String userName;
	final String userPassword;

	BaseAuthConfiguration(
		@Value("${spring.security.user.name:#{null}}")
		String userName,
		@Value("${spring.security.user.password:#{null}}")
		String userPassword
	) {
		if (userName == null || userName.isBlank()) {
			userName = UUID.randomUUID().toString();
			log.info("Default generated userName: [{}] - provided value was absent or blank", userName);
		}
		if (userPassword == null || userName.isBlank()) {
			userPassword = UUID.randomUUID().toString();
			log.info("Default generated userPassword: [{}] - provided value was absent or blank", userPassword);
		}

		this.userName = userName;
		this.userPassword = userPassword;
	}

	@Bean
	@ConditionalOnProperty(name = "app.configuration.security.enabled", havingValue = "false")
	public SecurityFilterChain securityDisabled(HttpSecurity httpSecurity) throws Exception {
		log.trace("All profiles - Disabled security");

		// h2-console https://stackoverflow.com/questions/74680244/h2-database-console-not-opening-with-spring-security
		httpSecurity
			.headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
			.authorizeHttpRequests(a -> {
		 		a.requestMatchers(PathRequest.toH2Console()).permitAll()
				 .anyRequest().permitAll();
			})
			.csrf(csrf -> csrf.ignoringRequestMatchers(PathRequest.toH2Console()).disable());

		return httpSecurity.build();
	}

	@Bean
	@ConditionalOnProperty(name = "app.configuration.security.enabled", havingValue = "true", matchIfMissing = true)
	public SecurityFilterChain basicAuthEnabled(HttpSecurity httpSecurity) throws Exception {
		log.trace("All profiles - BasicAuth enabled");
		httpSecurity
			.authorizeHttpRequests(a -> {
				a.requestMatchers("/health").permitAll()
				 .requestMatchers(PathRequest.toH2Console()).permitAll()
				 .anyRequest().authenticated();
			})
			.sessionManagement(a -> a.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.httpBasic(Customizer.withDefaults())
			.csrf(AbstractHttpConfigurer::disable);

		return httpSecurity.build();
	}

	@Bean
	public UserDetailsService userDetailsService() {
		UserDetails userDetails = User.withDefaultPasswordEncoder()
									  .username(userName)
									  .password(userPassword)
									  .build();

		return new InMemoryUserDetailsManager(userDetails);
	}

}
