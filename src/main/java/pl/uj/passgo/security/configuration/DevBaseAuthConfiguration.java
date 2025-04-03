package pl.uj.passgo.security.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;


@Slf4j
@Configuration
@EnableWebSecurity
@Profile("dev")
public class DevBaseAuthConfiguration {
	@Bean
	@ConditionalOnProperty(name = "app.configuration.security.enabled", havingValue = "false")
	public SecurityFilterChain securityDisabled(HttpSecurity httpSecurity) throws Exception {
		log.warn("Profile: dev - disabled security");
		httpSecurity.authorizeHttpRequests(a -> {
			a.anyRequest().permitAll();
		});

		return httpSecurity.build();
	}
}
