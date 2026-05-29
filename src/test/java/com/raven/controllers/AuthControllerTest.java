package com.raven.controllers;

import com.raven.dto.request.LoginRequest;
import com.raven.dto.request.RegisterRequest;
import com.raven.dto.response.AuthResponse;
import com.raven.services.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @Test
    void register() {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("user1");
        req.setPassword("pass");
        req.setEmail("user1@example.com");

        AuthResponse expected = new AuthResponse("token-123", UUID.randomUUID(), "user1", "user1@example.com");
        when(authService.register(req)).thenReturn(expected);

        ResponseEntity<?> response = authController.register(req);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertSame(expected, response.getBody());
        verify(authService, times(1)).register(req);
    }

    @Test
    void login() {
        LoginRequest req = new LoginRequest();
        req.setUsername("user1");
        req.setPassword("pass");

        AuthResponse expected = new AuthResponse("token-456", UUID.randomUUID(), "user1", "user1@example.com");
        when(authService.login(req)).thenReturn(expected);

        ResponseEntity<?> response = authController.login(req);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertSame(expected, response.getBody());
        verify(authService, times(1)).login(req);
    }
}