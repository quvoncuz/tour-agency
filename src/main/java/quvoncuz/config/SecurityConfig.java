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
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import quvoncuz.security.jwt.JwtAuthenticationFilter;
import quvoncuz.security.jwt.JwtUtil;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public static final String[] WHITE_LIST = {
            "/tours/**",
            "/agencies/**",
            "/ratings/**"
    };

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);
        http.cors(AbstractHttpConfigurer::disable);

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.POST, "/auth/**").permitAll()

                .requestMatchers(HttpMethod.POST, "/tours").hasRole("AGENCY")
                .requestMatchers(HttpMethod.PUT, "/tours/**").hasRole("AGENCY")
                .requestMatchers(HttpMethod.DELETE, "/tours/*").hasRole("AGENCY")
                .requestMatchers(HttpMethod.GET, "/tours/all", "/tours/*").permitAll()

                .requestMatchers(HttpMethod.POST, "/agencies").hasRole("USER")
                .requestMatchers(HttpMethod.POST, "/agencies/approve").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/agencies").hasRole("AGENCY")
                .requestMatchers(HttpMethod.DELETE, "/agencies").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/agencies/pending").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/agencies/all", "/agencies/*").permitAll()

                .requestMatchers(HttpMethod.POST, "/bookings").hasAnyRole("USER", "AGENCY")
                .requestMatchers(HttpMethod.PUT, "/bookings/*").hasAnyRole("USER", "AGENCY")
                .requestMatchers(HttpMethod.GET, "/bookings/all").hasAnyRole("AGENCY", "USER")
                .requestMatchers(HttpMethod.GET, "/bookings/by-user/*", "/bookings/by-agency/*").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/bookings/*").hasAnyRole("AGENCY", "USER")

                .requestMatchers(HttpMethod.POST, "/click/pay").hasRole("USER")
                .requestMatchers(HttpMethod.POST, "/click/prepare", "/click/complete").permitAll()

                .requestMatchers("/payments/**", "/profiles/**").hasRole("ADMIN")

                .requestMatchers(HttpMethod.POST, "/ratings").hasRole("USER")
                .requestMatchers(HttpMethod.PUT, "/ratings/*").hasRole("USER")
                .requestMatchers(HttpMethod.DELETE, "/ratings/*").hasRole("USER")
                .requestMatchers(HttpMethod.GET, "/ratings/**").permitAll()

                .anyRequest().authenticated()
        );

//        http.httpBasic(Customizer.withDefaults());

        http.addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class);


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
