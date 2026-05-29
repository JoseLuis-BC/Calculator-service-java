package com.raven.security;

import com.raven.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        String secret = "0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF";
        ReflectionTestUtils.setField(jwtService, "SECRET", secret);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 1000L * 60 * 60);
    }

    @Test
    void generateToken_and_extractClaims_shouldReturnSubjectAndUserId() {
        User user = new User();
        UUID id = UUID.randomUUID();
        user.setId(id);
        user.setUsername("jane.doe");

        String token = jwtService.generateToken(user);
        assertNotNull(token);
        assertTrue(token.contains("."), "Token debe tener formato JWT (contener '.')");
        Claims claims = jwtService.extractClaims(token);
        assertNotNull(claims);
        assertEquals("jane.doe", claims.getSubject());
        assertEquals(id.toString(), claims.get("userId", String.class));
    }

    @Test
    void extractClaims_withInvalidToken_shouldThrowJwtException() {
        String badToken = "not.a.jwt";
        assertThrows(JwtException.class, () -> jwtService.extractClaims(badToken));
    }

    @Test
    void extractClaims_afterChangingSecret_shouldThrowJwtException() {
        User user = new User();
        UUID id = UUID.randomUUID();
        user.setId(id);
        user.setUsername("john.doe");

        String token = jwtService.generateToken(user);
        String differentSecret = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
        ReflectionTestUtils.setField(jwtService, "SECRET", differentSecret);

        assertThrows(JwtException.class, () -> jwtService.extractClaims(token));
    }
}