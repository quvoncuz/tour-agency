package quvoncuz.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import quvoncuz.security.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final PasswordEncoder passwordEncoder;

    public static final String[] WHITE_LIST = {
            "/tours/**",
            "/agencies/**",
            "/ratings/**"
    };

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);
        http.cors(AbstractHttpConfigurer::disable);

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers(HttpMethod.GET, WHITE_LIST).permitAll()

                .requestMatchers("/click/prepare", "/click/complete").permitAll()

                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**"
                        , "/swagger-ui.html").permitAll()

                .requestMatchers("/agencies/pending","/profiles/all"
                        , "/agencies/approve", "/payments/all/refund"
                        , "/admin/**").hasRole("ADMIN")

                .requestMatchers(HttpMethod.POST, "/tours/**").hasRole("AGENCY")
                .requestMatchers(HttpMethod.PUT, "/tours/**").hasRole("AGENCY")
                .requestMatchers(HttpMethod.DELETE, "/tours/**", "/profiles/*").hasRole("ADMIN")

                .requestMatchers(HttpMethod.POST, "/agencies").hasRole("USER")

                .requestMatchers("/click/pay").authenticated()

                .requestMatchers("/saved-tours/**").authenticated()
                .requestMatchers("/bookings/**").authenticated()
                .requestMatchers("/payments/**").authenticated()
                .requestMatchers("/profiles/**").authenticated()
                .requestMatchers("/ratings/**").authenticated()

                .anyRequest().authenticated()
        );

        http.httpBasic(Customizer.withDefaults());

        return http.build();
    }


//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http.csrf(AbstractHttpConfigurer::disable);
//        http.cors(AbstractHttpConfigurer::disable);
//
//        http.authorizeHttpRequests(authorizationManagerRequestMatcherRegistry -> {
//            authorizationManagerRequestMatcherRegistry
//                    .requestMatchers(HttpMethod.GET, WHITE_LIST).permitAll()
//                    .requestMatchers("/admin", "/agencies/pending").hasRole("ADMIN")
//                    .anyRequest()
//                    .authenticated();
//        });
//
//        http.httpBasic(Customizer.withDefaults());
//
//        return http.build();
//    }

}
