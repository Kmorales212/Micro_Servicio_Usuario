package com.example.demo.testUnitarios;

import com.example.demo.security.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.Key;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    
    private final String SECRET_KEY = "mi_clave_secreta_super_segura_para_el_proyecto_fullstack";

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
    }

    @Test
    void testGenerateToken_Success() {
        String email = "test@example.com";

        String token = jwtUtil.generateToken(email);

        assertNotNull(token);
        assertEquals(3, token.split("\\.").length);

        Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
        
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        assertEquals(email, claims.getSubject());
    }

    @Test
    void testTokenExpirationDate() {
        String email = "admin@test.com";

        String token = jwtUtil.generateToken(email);

        Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        assertTrue(claims.getExpiration().after(claims.getIssuedAt()));
    }
}