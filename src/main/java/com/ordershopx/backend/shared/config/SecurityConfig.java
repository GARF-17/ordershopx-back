package com.ordershopx.backend.shared.config;

import com.ordershopx.backend.shared.security.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.*;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())

                // ── FIX 1: SockJS necesita sesión HTTP para el handshake inicial ──
                // STATELESS solo para rutas /api/**, el WS queda excluido
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authorizeHttpRequests(auth -> auth

                        // ── FIX 2: Permitir TODAS las subrutas que usa SockJS ──
                        // SockJS genera: /ws/info, /ws/{server}/{session}/websocket, etc.
                        .requestMatchers("/ws").permitAll()
                        .requestMatchers("/ws/**").permitAll()

                        // PÚBLICOS
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers("/api/v1/usuarios/**").permitAll()

                        // ADMIN
                        .requestMatchers("/api/v1/admin/**").hasRole("ADMINISTRADOR")

                        // RESTAURANTES
                        .requestMatchers(HttpMethod.GET, "/api/v1/restaurantes/**")
                        .hasAnyRole("COMENSAL", "RESTAURANTE")
                        .requestMatchers("/api/v1/restaurantes/**").hasRole("RESTAURANTE")

                        // PRODUCTOS
                        .requestMatchers(HttpMethod.GET, "/api/v1/productos/**")
                        .hasAnyRole("COMENSAL", "RESTAURANTE")
                        .requestMatchers("/api/v1/productos/**").hasRole("RESTAURANTE")

                        // CLIENTES
                        .requestMatchers("/api/v1/clientes/**").hasRole("COMENSAL")

                        .anyRequest().authenticated()
                )

                .userDetailsService(userDetailsService)

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();

        // ── FIX 3: SockJS requiere allowCredentials=true para el handshake ──
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}