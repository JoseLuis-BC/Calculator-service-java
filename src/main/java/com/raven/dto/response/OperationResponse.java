package com.raven.dto.response;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record OperationResponse(
        UUID id,
        String operation,
        BigDecimal operandA,
        BigDecimal operandB,
        BigDecimal result,
        LocalDateTime timestamp,
        UUID userId
) {}
