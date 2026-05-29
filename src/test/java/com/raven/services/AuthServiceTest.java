package com.raven.services;

import com.raven.dto.request.LoginRequest;
import com.raven.dto.request.RegisterRequest;
import com.raven.dto.response.AuthResponse;
import com.raven.entities.User;
import com.raven.exceptions.BadRequestException;
import com.raven.repository.UserRepository;
import com.raven.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private EmailValidationService emailValidationService;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest createRegisterRequest() {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("newuser");
        req.setEmail("newuser@example.com");
        req.setPassword("password123");
        return req;
    }

    private LoginRequest createLoginRequest() {
        LoginRequest req = new LoginRequest();
        req.setUsername("newuser");
        req.setPassword("password123");
        return req;
    }

    private User createUser(String username, String email, String password) {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        user.setCreatedAt(LocalDateTime.now());
        return user;
    }

    @Test
    void register_shouldSuccessfullyRegisterNewUser() {
        RegisterRequest req = createRegisterRequest();
        UUID userId = UUID.randomUUID();

        doNothing().when(emailValidationService).validate(req.getEmail());
        when(userRepository.findByUsername(req.getUsername())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(req.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(req.getPassword())).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(userId);
            return user;
        });
        when(jwtService.generateToken(any(User.class))).thenReturn("jwt-token");

        AuthResponse response = authService.register(req);
        assertNotNull(response);
        assertEquals("jwt-token", response.token_raven());
        assertEquals(userId, response.userId());
        assertEquals(req.getUsername(), response.username());
        assertEquals(req.getEmail(), response.email());

        verify(emailValidationService, times(1)).validate(req.getEmail());
        verify(userRepository, times(1)).findByUsername(req.getUsername());
        verify(userRepository, times(1)).findByEmail(req.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
        verify(jwtService, times(1)).generateToken(any(User.class));
    }

    @Test
    void register_shouldThrowExceptionWhenEmailValidationFails() {
        RegisterRequest req = createRegisterRequest();
        doThrow(new BadRequestException("Email validation failed",
                java.util.List.of("Invalid email format")))
                .when(emailValidationService).validate(req.getEmail());

        assertThrows(BadRequestException.class, () -> authService.register(req));
        verify(emailValidationService, times(1)).validate(req.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void register_shouldThrowExceptionWhenUsernameAlreadyExists() {
        RegisterRequest req = createRegisterRequest();
        User existingUser = createUser(req.getUsername(), "other@example.com", "password");

        doNothing().when(emailValidationService).validate(req.getEmail());
        when(userRepository.findByUsername(req.getUsername())).thenReturn(Optional.of(existingUser));

        BadRequestException ex = assertThrows(BadRequestException.class, () -> authService.register(req));
        assertTrue(ex.getDetails().contains("Username already exists"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void register_shouldThrowExceptionWhenEmailAlreadyInUse() {
        RegisterRequest req = createRegisterRequest();
        User existingUser = createUser("otheruser", req.getEmail(), "password");

        doNothing().when(emailValidationService).validate(req.getEmail());
        when(userRepository.findByUsername(req.getUsername())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(req.getEmail())).thenReturn(Optional.of(existingUser));

        BadRequestException ex = assertThrows(BadRequestException.class, () -> authService.register(req));
        assertTrue(ex.getDetails().contains("Email already in use"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void login_shouldSuccessfullyLoginUser() {
        LoginRequest req = createLoginRequest();
        User user = createUser(req.getUsername(), "user@example.com", "hashedPassword");

        when(userRepository.findByUsername(req.getUsername())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(req.getPassword(), user.getPassword())).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn("jwt-token");
        AuthResponse response = authService.login(req);
        assertNotNull(response);
        assertEquals("jwt-token", response.token_raven());
        assertEquals(user.getId(), response.userId());
        assertEquals(user.getUsername(), response.username());
        assertEquals(user.getEmail(), response.email());

        verify(userRepository, times(1)).findByUsername(req.getUsername());
        verify(passwordEncoder, times(1)).matches(req.getPassword(), user.getPassword());
        verify(jwtService, times(1)).generateToken(user);
    }

    @Test
    void login_shouldThrowExceptionWhenUsernameNotFound() {
        LoginRequest req = createLoginRequest();
        when(userRepository.findByUsername(req.getUsername())).thenReturn(Optional.empty());
        BadRequestException ex = assertThrows(BadRequestException.class, () -> authService.login(req));
        assertTrue(ex.getDetails().contains("Invalid username or password"));
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void login_shouldThrowExceptionWhenPasswordIsIncorrect() {
        LoginRequest req = createLoginRequest();
        User user = createUser(req.getUsername(), "user@example.com", "hashedPassword");
        when(userRepository.findByUsername(req.getUsername())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(req.getPassword(), user.getPassword())).thenReturn(false);
        BadRequestException ex = assertThrows(BadRequestException.class, () -> authService.login(req));
        assertTrue(ex.getDetails().contains("Invalid username or password"));
        verify(jwtService, never()).generateToken(any(User.class));
    }
}