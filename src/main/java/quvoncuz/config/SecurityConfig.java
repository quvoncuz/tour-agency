package quvoncuz.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import quvoncuz.security.jwt.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private static final String[] WHITE_LIST = {
            "/swagger-ui/**", "/v3/**",
            "/tours/**", "/favorites",
            "/actuator/**"
    };

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));

        http.sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.POST, "/auth/**").permitAll()
                .requestMatchers(WHITE_LIST).permitAll()
                .requestMatchers(HttpMethod.GET, "/tours/all", "/tours/*", "/agencies/all", "/agencies/*", "/ratings/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/click/prepare", "/click/complete").permitAll()
                .anyRequest().authenticated()
        );

        http.addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class);


        return http.build();
    }

    public CorsConfigurationSource corsConfigurationSource() {
        return request -> {
            var cors = new CorsConfiguration();
            cors.setAllowedOrigins(java.util.List.of("*"));
            cors.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "DELETE"));
            cors.setAllowedHeaders(java.util.List.of("Content-Type", "Authorization"));
            return cors;
        };
    }
}
