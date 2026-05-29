package com.raven.controllers;

import com.raven.dto.request.CalculationRequest;
import com.raven.entities.Operation;
import com.raven.entities.OperationType;
import com.raven.entities.User;
import com.raven.services.CalculationService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CalculatorControllerTest {

    @Mock
    private CalculationService calculationService;

    @InjectMocks
    private CalculatorController calculatorController;

    @Test
    void calculate_shouldReturnOk() {
        CalculationRequest request = new CalculationRequest();
        request.setOperandA(BigDecimal.valueOf(5));
        request.setOperandB(BigDecimal.valueOf(3));
        request.setOperation(OperationType.ADDITION);

        Operation mockOperation = new Operation();
        mockOperation.setId(UUID.randomUUID());
        mockOperation.setOperandA(BigDecimal.valueOf(5));
        mockOperation.setOperandB(BigDecimal.valueOf(3));
        mockOperation.setResult(BigDecimal.valueOf(8));
        mockOperation.setOperation(OperationType.ADDITION);
        mockOperation.setTimestamp(LocalDateTime.now());
        mockOperation.setUserId(UUID.randomUUID());

        when(calculationService.calculate(any(CalculationRequest.class), any(User.class)))
                .thenReturn(mockOperation);

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("test");

        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(user);

        ResponseEntity<?> response = calculatorController.calculate(request, auth);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertSame(mockOperation, response.getBody());
        verify(calculationService, times(1)).calculate(request, user);
    }
}