package com.deliverytech.delivery.config;

import com.deliverytech.delivery.security.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@EnableWebSecurity
@EnableMethodSecurity
@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, CorsConfigurationSource corsConfigurationSource) throws Exception{
        http
            .csrf( csrf -> csrf.disable())

            .sessionManagement( sm ->
                        sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            .exceptionHandling(ex -> ex.authenticationEntryPoint(
                    (req, res, e) ->
                    res.sendError(HttpServletResponse.SC_UNAUTHORIZED))
            )

            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/custom-health", "/custom-info").permitAll()
                    .requestMatchers("/actuator/**").permitAll()

                    .requestMatchers(HttpMethod.POST, "/api/auth/**").permitAll()
                    .requestMatchers("/swagger-ui/**", "/v3/api-docs", "/v3/api-docs/**").permitAll()

                    .requestMatchers("/api/clientes/cadastrar").hasAnyRole("ADMIN", "CLIENTE")
                    .requestMatchers(HttpMethod.GET, "/api/clientes/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PATCH, "/api/clientes/**").hasAnyRole("ADMIN", "CLIENTE")
                    .requestMatchers(HttpMethod.PUT, "/api/clientes/**").hasAnyRole("ADMIN", "CLIENTE")

                    .requestMatchers(HttpMethod.GET, "/api/restaurantes/**").hasAnyRole("ADMIN", "CLIENTE")
                    .requestMatchers( "/api/restaurantes/categoria").hasAnyRole("ADMIN", "CLIENTE")
                    .requestMatchers(HttpMethod.POST, "/api/restaurantes/**").hasAnyRole("ADMIN", "RESTAURANTE")
                    .requestMatchers(HttpMethod.PATCH, "/api/restaurantes/**").hasAnyRole("ADMIN", "RESTAURANTE")

                    .anyRequest().authenticated()
            )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();

    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }



}
