package com.raven.dto.response;


import java.util.UUID;

public record AuthResponse(String token_raven, UUID userId, String username, String email) { }
