package com.example.demo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Deshabilitar CSRF para APIs
            .authorizeHttpRequests(auth -> auth
                // 1. Permitir acceso libre a Login y Registro (Tus rutas actuales)
                .requestMatchers("/usuarios/login", "/usuarios").permitAll()
                
                // 2. 👇 Permitir acceso libre a la Documentación de SWAGGER 👇
                .requestMatchers(
                        "/swagger-ui/**", 
                        "/v3/api-docs/**",
                        "/swagger-ui.html"
                ).permitAll()
                
                // 3. Cualquier otra ruta requiere autenticación (Token JWT)
                .anyRequest().authenticated()
            );

        return http.build();
    }

    // Este Bean encriptará las contraseñas
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}