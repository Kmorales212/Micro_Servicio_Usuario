package com.example.demo.testUnitarios;

import com.example.demo.security.SecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class SecurityConfigTest {

    private SecurityConfig securityConfig;

    @BeforeEach
    void setUp() {
        securityConfig = new SecurityConfig();
    }

    @Test
    void testPasswordEncoderBean() {
        PasswordEncoder encoder = securityConfig.passwordEncoder();
        assertNotNull(encoder);
        assertTrue(encoder instanceof BCryptPasswordEncoder);
        
        String pass = "12345";
        String encoded = encoder.encode(pass);
        assertTrue(encoder.matches(pass, encoded));
    }

    @Test
    void testSecurityFilterChain_FullCoverage() throws Exception {
        HttpSecurity http = mock(HttpSecurity.class);
        
        var registry = mock(AuthorizeHttpRequestsConfigurer.AuthorizationManagerRequestMatcherRegistry.class);
        var authorizedUrl = mock(AuthorizeHttpRequestsConfigurer.AuthorizedUrl.class);
        var csrfConfigurer = mock(CsrfConfigurer.class);

 
        when(http.csrf(any(Customizer.class))).thenAnswer(invocation -> {
            Customizer<CsrfConfigurer<HttpSecurity>> customizer = invocation.getArgument(0);
            customizer.customize(csrfConfigurer); 
            return http;
        });

        when(http.authorizeHttpRequests(any(Customizer.class))).thenAnswer(invocation -> {
            Customizer<AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry> customizer = invocation.getArgument(0);
            customizer.customize(registry); 
            return http;
        });

        when(registry.requestMatchers(any(String[].class))).thenReturn(authorizedUrl);
        when(registry.anyRequest()).thenReturn(authorizedUrl);
        when(authorizedUrl.permitAll()).thenReturn(registry);
        when(authorizedUrl.authenticated()).thenReturn(registry);

        // 5. Ejecutamos el método
        SecurityFilterChain result = securityConfig.securityFilterChain(http);

        verify(http).csrf(any());
        verify(http).authorizeHttpRequests(any());
        verify(registry, atLeastOnce()).requestMatchers(any(String[].class));
        verify(registry).anyRequest();
    }
}