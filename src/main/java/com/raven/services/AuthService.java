package com.raven.services;

import com.raven.dto.request.LoginRequest;
import com.raven.dto.request.RegisterRequest;
import com.raven.dto.response.AuthResponse;
import com.raven.entities.User;
import com.raven.exceptions.BadRequestException;
import com.raven.repository.UserRepository;
import com.raven.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailValidationService emailValidationService;

    public AuthResponse register(RegisterRequest request) {

        emailValidationService.validate(request.getEmail());

        userRepository.findByUsername(request.getUsername())
                .ifPresent(user -> {
                    throw new BadRequestException("Email validation failed",  List.of("Username already exists"));
                });

        userRepository.findByEmail(request.getEmail())
                .ifPresent(user -> {
                    throw new BadRequestException("Email validation failed", List.of("Email already in use"));
                });

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setCreatedAt(LocalDateTime.now());

        userRepository.save(user);

        String token = jwtService.generateToken(user);

        log.info("User registered: {}", user.getUsername());

        return buildAuthResponse(user, token);
    }

    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BadRequestException("Email validation failed",
                        List.of("Invalid username or password")));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadRequestException("Email validation failed",  List.of("Invalid username or password"));
        }

        String token = jwtService.generateToken(user);

        log.info("User logged in: {}", user.getUsername());

        return buildAuthResponse(user, token);
    }

    private AuthResponse buildAuthResponse(User user, String token) {
        return new AuthResponse(
                token,
                user.getId(),
                user.getUsername(),
                user.getEmail()
        );
    }
}