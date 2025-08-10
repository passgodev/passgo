package pl.uj.passgo.configuration.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import pl.uj.passgo.configuration.security.jwt.JwtAuthenticationRequestFilter;
import pl.uj.passgo.configuration.security.jwt.JwtConfiguration;

import java.util.Arrays;
import java.util.List;


@Slf4j
@Configuration
@EnableWebSecurity
public class ApiFilterChain {
    private final UserDetailsService userDetailsService;
    private final JwtAuthenticationRequestFilter jwtAuthenticationRequestFilter;

    private final List<String> allowedOrigins;


    @Autowired
    public ApiFilterChain(
        @Qualifier(JwtConfiguration.JwtUserDetailsServiceName) UserDetailsService userDetailsService,
        JwtAuthenticationRequestFilter jwtAuthenticationRequestFilter,
        @Value("${app.security.cors.allowed-origins:http://localhost:3000,http://localhost:8080}")
        List<String> allowedOrigins
    ) {
        this.userDetailsService = userDetailsService;
        this.jwtAuthenticationRequestFilter = jwtAuthenticationRequestFilter;

        this.allowedOrigins = allowedOrigins;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(allowedOrigins);
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        configuration.setExposedHeaders(Arrays.asList("Authorization", "X-Total-Count"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


    @Bean
    @Order(1)
    @ConditionalOnProperty(name = "app.configuration.security.enabled", havingValue = "true", matchIfMissing = true)
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.trace("SecurityFilterChain configured, allow /auth/**, other authenticated");

        return http
            .securityMatcher("/auth/**")
            .authorizeHttpRequests(authorizeRequests -> {
                authorizeRequests
                    .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                    .requestMatchers(HttpMethod.POST, "/auth/signup").permitAll()
                    .requestMatchers(HttpMethod.POST, "/auth/refresh").permitAll()
                    .requestMatchers(HttpMethod.POST, "/auth/logout").permitAll()
                    .anyRequest().authenticated();
            })
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            // For rest-api can be disabled, no session
            .csrf(CsrfConfigurer::disable)
            .headers(headers -> {
                // Older browsers support, when request is recognized as xss, stop processing
                headers.xssProtection(xss -> xss.headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK));
                headers.contentSecurityPolicy(csp -> csp.policyDirectives("default-src 'none'"));
                headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::deny);
            })
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtAuthenticationRequestFilter, UsernamePasswordAuthenticationFilter.class)
            .userDetailsService(userDetailsService)
            .build();
    }

    @Bean
    @Order(1)
    @ConditionalOnProperty(name = "app.configuration.security.enabled", havingValue = "true", matchIfMissing = true)
    public SecurityFilterChain swaggerSecurityFilterChain(HttpSecurity http) throws Exception {
        log.trace("SecurityFilterChain configured, allow /swagger-ui/**");

        return http
            .securityMatcher("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**")
            .authorizeHttpRequests(authorizeRequests -> authorizeRequests.anyRequest().permitAll())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(CsrfConfigurer::disable)
            .headers(headers -> headers
                .xssProtection(xss -> xss.headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK))
                // From where to load resources, eg styles for swagger
                .contentSecurityPolicy(csp -> csp.policyDirectives("default-src 'self'; script-src 'self'; style-src 'self'"))
                // If swagger is rendered in iframe, than change it
                .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
                .contentTypeOptions(Customizer.withDefaults())
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .build();
    }

    @Bean
    @Order(1)
    @ConditionalOnProperty(name = "app.configuration.security.enabled", havingValue = "true", matchIfMissing = true)
    public SecurityFilterChain h2ConsoleSecurityFilterChain(HttpSecurity http) throws Exception {
        log.trace("SecurityFilterChain configured, allow /h2-console/**, other deny");

        return http
            .securityMatcher(PathRequest.toH2Console())
            .authorizeHttpRequests( auth -> {
                auth.requestMatchers(PathRequest.toH2Console()).permitAll()
                    .anyRequest().denyAll();
            })
            .csrf(csrf -> csrf.ignoringRequestMatchers(PathRequest.toH2Console()))
            .headers(headers -> headers
                .xssProtection(xss -> xss.headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK))
                .contentSecurityPolicy(csp -> csp.policyDirectives("default-src 'self'; script-src 'self' 'unsafe-inline'; style-src 'self' 'unsafe-inline';"))
                // Needs to be set, otherwise after logging in, we cannot access ui
                .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
                .contentTypeOptions(Customizer.withDefaults())
            )
            .build();
    }

    @Bean
    @Order(Ordered.LOWEST_PRECEDENCE)
    @ConditionalOnProperty(name = "app.configuration.security.enabled", havingValue = "true", matchIfMissing = true)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        log.warn("SecurityFilterChain configured, default other deny");

        return http
            .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(CsrfConfigurer::disable)
            .headers(headers -> headers
                .xssProtection(xss -> xss.headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK))
                .contentSecurityPolicy(csp -> csp.policyDirectives("default-src 'none'"))
                .frameOptions(HeadersConfigurer.FrameOptionsConfig::deny)
                .contentTypeOptions(Customizer.withDefaults())
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtAuthenticationRequestFilter, UsernamePasswordAuthenticationFilter.class)
            .userDetailsService(userDetailsService)
            .build();
    }

    @Bean
    @Order(Ordered.LOWEST_PRECEDENCE)
    @ConditionalOnProperty(name = "app.configuration.security.enabled", havingValue = "false", matchIfMissing = true)
    public SecurityFilterChain defaultDisabledSecurityFilterChain(HttpSecurity http) throws Exception {
        log.warn("SecurityFilterChain configured, default other permitAll");

        return http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
            .cors(CorsConfigurer::disable)
            .csrf(CsrfConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .build();
    }
}
