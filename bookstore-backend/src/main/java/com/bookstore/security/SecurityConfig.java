package com.bookstore.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security configuration.
 * Stateless JWT — no sessions. Public endpoints explicitly listed; everything else requires auth.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Public — no token required
                .requestMatchers(HttpMethod.POST,  "/api/v1/auth/register").permitAll()
                .requestMatchers(HttpMethod.POST,  "/api/v1/auth/login").permitAll()
                .requestMatchers(HttpMethod.POST,  "/api/v1/auth/refresh").permitAll()
                .requestMatchers(HttpMethod.GET,   "/api/v1/books/**").permitAll()
                .requestMatchers(HttpMethod.GET,   "/api/v1/categories/**").permitAll()
                .requestMatchers(HttpMethod.GET,   "/api/v1/authors/**").permitAll()
                .requestMatchers(HttpMethod.GET,   "/api/v1/publishers/**").permitAll()
                .requestMatchers(HttpMethod.GET,   "/api/v1/search/**").permitAll()
                .requestMatchers(HttpMethod.POST,  "/api/v1/shipments/calculate-rate").permitAll()
                .requestMatchers(HttpMethod.GET,   "/api/v1/recommendations/featured").permitAll()
                .requestMatchers(HttpMethod.GET,   "/api/v1/reviews/books/**").permitAll()
                .requestMatchers(HttpMethod.GET,   "/api/v1/coupons/validate").permitAll()
                // Actuator + Swagger
                .requestMatchers("/actuator/**", "/swagger-ui/**",
                                 "/api-docs/**", "/v3/api-docs/**").permitAll()
                // Admin only
                .requestMatchers("/api/v1/users").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/v1/users/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST,   "/api/v1/categories").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT,    "/api/v1/categories/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/v1/categories/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST,   "/api/v1/books").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT,    "/api/v1/books/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/v1/books/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST,   "/api/v1/authors").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT,    "/api/v1/authors/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/v1/authors/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST,   "/api/v1/publishers").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT,    "/api/v1/publishers/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/v1/publishers/**").hasRole("ADMIN")
                .requestMatchers("/api/v1/coupons/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST,   "/api/v1/payments/*/refund").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PATCH,  "/api/v1/shipments/**").hasRole("ADMIN")
                .requestMatchers("/api/v1/reviews/*/approve",
                                 "/api/v1/reviews/*/reject").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/v1/reviews/**").hasRole("ADMIN")
                .requestMatchers("/api/v1/orders/admin").hasRole("ADMIN")
                // Everything else — authenticated
                .anyRequest().authenticated()
            )
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
