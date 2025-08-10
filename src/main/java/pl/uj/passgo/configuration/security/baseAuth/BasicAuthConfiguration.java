package pl.uj.passgo.configuration.security.baseAuth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import java.util.UUID;


@Slf4j
@Configuration
@EnableWebSecurity
public class BasicAuthConfiguration {
    public static final String basicUserDetailsServiceName = "basicUserDetailsService";
    private final String userName;
    private final String userPassword;

    public BasicAuthConfiguration(
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

    @Bean(name = basicUserDetailsServiceName)
    public UserDetailsService userDetailsService() {
        UserDetails userDetails = User.withDefaultPasswordEncoder()
                                      .username(userName)
                                      .password(userPassword)
                                      .build();

        return new InMemoryUserDetailsManager(userDetails);
    }
}
