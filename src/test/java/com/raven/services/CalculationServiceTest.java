package com.raven.services;

import com.raven.dto.request.CalculationRequest;
import com.raven.entities.Operation;
import com.raven.entities.OperationType;
import com.raven.entities.User;
import com.raven.exceptions.BadRequestException;
import com.raven.strategy.OperationStrategy;
import com.raven.strategy.OperationStrategyFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CalculationServiceTest {

    @Mock
    private OperationStrategyFactory operationStrategyFactory;

    @Mock
    private OperationAsyncService operationAsyncService;

    @InjectMocks
    private CalculationService calculationService;

    private User createTestUser() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("testuser");
        return user;
    }

    private CalculationRequest createCalculationRequest(BigDecimal a, BigDecimal b, OperationType op) {
        CalculationRequest req = new CalculationRequest();
        req.setOperandA(a);
        req.setOperandB(b);
        req.setOperation(op);
        return req;
    }

    @Test
    void calculate_addition_shouldReturnCorrectResult() {
        User user = createTestUser();
        CalculationRequest req = createCalculationRequest(
                BigDecimal.valueOf(5),
                BigDecimal.valueOf(3),
                OperationType.ADDITION
        );

        OperationStrategy mockStrategy = mock(OperationStrategy.class);
        when(mockStrategy.execute(BigDecimal.valueOf(5), BigDecimal.valueOf(3)))
                .thenReturn(BigDecimal.valueOf(8));
        when(operationStrategyFactory.getStrategy("ADDITION")).thenReturn(mockStrategy);
        doNothing().when(operationAsyncService).saveAsync(any(Operation.class));
        Operation result = calculationService.calculate(req, user);
        assertNotNull(result);
        assertEquals(user.getId(), result.getUserId());
        assertEquals(BigDecimal.valueOf(5), result.getOperandA());
        assertEquals(BigDecimal.valueOf(3), result.getOperandB());
        assertEquals(BigDecimal.valueOf(8), result.getResult());
        assertEquals(OperationType.ADDITION, result.getOperation());
        assertNotNull(result.getTimestamp());
        assertNotNull(result.getId());

        verify(operationStrategyFactory, times(1)).getStrategy("ADDITION");
        verify(mockStrategy, times(1)).execute(BigDecimal.valueOf(5), BigDecimal.valueOf(3));
        verify(operationAsyncService, times(1)).saveAsync(any(Operation.class));
    }

    @Test
    void calculate_subtraction_shouldReturnCorrectResult() {
        User user = createTestUser();
        CalculationRequest req = createCalculationRequest(
                BigDecimal.valueOf(10),
                BigDecimal.valueOf(4),
                OperationType.SUBTRACTION
        );

        OperationStrategy mockStrategy = mock(OperationStrategy.class);
        when(mockStrategy.execute(BigDecimal.valueOf(10), BigDecimal.valueOf(4)))
                .thenReturn(BigDecimal.valueOf(6));
        when(operationStrategyFactory.getStrategy("SUBTRACTION")).thenReturn(mockStrategy);
        doNothing().when(operationAsyncService).saveAsync(any(Operation.class));
        Operation result = calculationService.calculate(req, user);

        assertEquals(BigDecimal.valueOf(6), result.getResult());
        verify(mockStrategy, times(1)).execute(BigDecimal.valueOf(10), BigDecimal.valueOf(4));
    }

    @Test
    void calculate_multiplication_shouldReturnCorrectResult() {
        User user = createTestUser();
        CalculationRequest req = createCalculationRequest(
                BigDecimal.valueOf(6),
                BigDecimal.valueOf(7),
                OperationType.MULTIPLICATION
        );

        OperationStrategy mockStrategy = mock(OperationStrategy.class);
        when(mockStrategy.execute(BigDecimal.valueOf(6), BigDecimal.valueOf(7)))
                .thenReturn(BigDecimal.valueOf(42));
        when(operationStrategyFactory.getStrategy("MULTIPLICATION")).thenReturn(mockStrategy);
        doNothing().when(operationAsyncService).saveAsync(any(Operation.class));
        Operation result = calculationService.calculate(req, user);
        assertEquals(BigDecimal.valueOf(42), result.getResult());
        verify(mockStrategy, times(1)).execute(BigDecimal.valueOf(6), BigDecimal.valueOf(7));
    }

    @Test
    void calculate_division_shouldReturnCorrectResult() {
        User user = createTestUser();
        CalculationRequest req = createCalculationRequest(
                BigDecimal.valueOf(20),
                BigDecimal.valueOf(4),
                OperationType.DIVISION
        );

        OperationStrategy mockStrategy = mock(OperationStrategy.class);
        when(mockStrategy.execute(BigDecimal.valueOf(20), BigDecimal.valueOf(4)))
                .thenReturn(BigDecimal.valueOf(5));
        when(operationStrategyFactory.getStrategy("DIVISION")).thenReturn(mockStrategy);
        doNothing().when(operationAsyncService).saveAsync(any(Operation.class));
        Operation result = calculationService.calculate(req, user);
        assertEquals(BigDecimal.valueOf(5), result.getResult());
        verify(mockStrategy, times(1)).execute(BigDecimal.valueOf(20), BigDecimal.valueOf(4));
    }

    @Test
    void calculate_sqrt_shouldNotRequireOperandB() {
        User user = createTestUser();
        CalculationRequest req = createCalculationRequest(
                BigDecimal.valueOf(16),
                null,
                OperationType.SQRT
        );

        OperationStrategy mockStrategy = mock(OperationStrategy.class);
        when(mockStrategy.execute(BigDecimal.valueOf(16), null))
                .thenReturn(BigDecimal.valueOf(4));
        when(operationStrategyFactory.getStrategy("SQRT")).thenReturn(mockStrategy);
        doNothing().when(operationAsyncService).saveAsync(any(Operation.class));
        Operation result = calculationService.calculate(req, user);
        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(4), result.getResult());
        verify(mockStrategy, times(1)).execute(BigDecimal.valueOf(16), null);
    }

    @Test
    void calculate_absolute_shouldNotRequireOperandB() {
        User user = createTestUser();
        CalculationRequest req = createCalculationRequest(
                BigDecimal.valueOf(-10),
                null,
                OperationType.ABSOLUTE
        );

        OperationStrategy mockStrategy = mock(OperationStrategy.class);
        when(mockStrategy.execute(BigDecimal.valueOf(-10), null))
                .thenReturn(BigDecimal.valueOf(10));
        when(operationStrategyFactory.getStrategy("ABSOLUTE")).thenReturn(mockStrategy);
        doNothing().when(operationAsyncService).saveAsync(any(Operation.class));
        Operation result = calculationService.calculate(req, user);
        assertEquals(BigDecimal.valueOf(10), result.getResult());
        verify(mockStrategy, times(1)).execute(BigDecimal.valueOf(-10), null);
    }

    @Test
    void calculate_shouldThrowExceptionWhenOperandAIsNull() {
        User user = createTestUser();
        CalculationRequest req = createCalculationRequest(
                null,
                BigDecimal.valueOf(5),
                OperationType.ADDITION
        );
        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> calculationService.calculate(req, user));
        assertTrue(ex.getDetails().stream().anyMatch(d -> d.contains("OperandA is required")));
        verify(operationStrategyFactory, never()).getStrategy(anyString());
        verify(operationAsyncService, never()).saveAsync(any(Operation.class));
    }

    @Test
    void calculate_shouldThrowExceptionWhenOperandBIsNullForBinaryOperation() {
        User user = createTestUser();
        CalculationRequest req = createCalculationRequest(
                BigDecimal.valueOf(5),
                null,
                OperationType.ADDITION
        );
        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> calculationService.calculate(req, user));
        assertTrue(ex.getDetails().stream().anyMatch(d -> d.contains("OperandB is required")));
        verify(operationStrategyFactory, never()).getStrategy(anyString());
    }

    @Test
    void calculate_shouldThrowExceptionWhenOperandAExceedsMaxRange() {
        User user = createTestUser();
        CalculationRequest req = createCalculationRequest(
                BigDecimal.valueOf(2_000_000),
                BigDecimal.valueOf(5),
                OperationType.ADDITION
        );
        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> calculationService.calculate(req, user));
        assertTrue(ex.getDetails().stream().anyMatch(d -> d.contains("Operand out of allowed range")));
        verify(operationStrategyFactory, never()).getStrategy(anyString());
    }

    @Test
    void calculate_shouldThrowExceptionWhenOperandAExceedsMinRange() {
        User user = createTestUser();
        CalculationRequest req = createCalculationRequest(
                BigDecimal.valueOf(-2_000_000),
                BigDecimal.valueOf(5),
                OperationType.ADDITION
        );
        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> calculationService.calculate(req, user));
        assertTrue(ex.getDetails().stream().anyMatch(d -> d.contains("Operand out of allowed range")));
        verify(operationStrategyFactory, never()).getStrategy(anyString());
    }

    @Test
    void calculate_shouldThrowExceptionWhenOperandBExceedsRange() {
        User user = createTestUser();
        CalculationRequest req = createCalculationRequest(
                BigDecimal.valueOf(5),
                BigDecimal.valueOf(1_500_000),
                OperationType.ADDITION
        );
        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> calculationService.calculate(req, user));
        assertTrue(ex.getDetails().stream().anyMatch(d -> d.contains("Operand out of allowed range")));
        verify(operationStrategyFactory, never()).getStrategy(anyString());
    }

    @Test
    void calculate_shouldThrowExceptionWhenOperandBIsNegativeAndExceedsMinRange() {
        User user = createTestUser();
        CalculationRequest req = createCalculationRequest(
                BigDecimal.valueOf(100),
                BigDecimal.valueOf(-1_500_000),
                OperationType.ADDITION
        );
        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> calculationService.calculate(req, user));
        assertTrue(ex.getDetails().stream().anyMatch(d -> d.contains("Operand out of allowed range")));
        verify(operationStrategyFactory, never()).getStrategy(anyString());
    }

    @Test
    void calculate_multiplicationWithLargeNumbers() {
        User user = createTestUser();
        CalculationRequest req = createCalculationRequest(
                BigDecimal.valueOf(1_000),
                BigDecimal.valueOf(500),
                OperationType.MULTIPLICATION
        );

        OperationStrategy mockStrategy = mock(OperationStrategy.class);
        when(mockStrategy.execute(BigDecimal.valueOf(1_000), BigDecimal.valueOf(500)))
                .thenReturn(BigDecimal.valueOf(500_000));
        when(operationStrategyFactory.getStrategy("MULTIPLICATION")).thenReturn(mockStrategy);
        doNothing().when(operationAsyncService).saveAsync(any(Operation.class));
        Operation result = calculationService.calculate(req, user);
        assertEquals(BigDecimal.valueOf(500_000), result.getResult());
        verify(operationAsyncService, times(1)).saveAsync(any(Operation.class));
    }

    @Test
    void calculate_shouldCalloperationAsyncServiceForPersistence() {
        User user = createTestUser();
        CalculationRequest req = createCalculationRequest(
                BigDecimal.valueOf(2),
                BigDecimal.valueOf(3),
                OperationType.ADDITION
        );

        OperationStrategy mockStrategy = mock(OperationStrategy.class);
        when(mockStrategy.execute(any(), any())).thenReturn(BigDecimal.valueOf(5));
        when(operationStrategyFactory.getStrategy("ADDITION")).thenReturn(mockStrategy);
        doNothing().when(operationAsyncService).saveAsync(any(Operation.class));
        calculationService.calculate(req, user);
        verify(operationAsyncService, times(1)).saveAsync(any(Operation.class));
    }

    @Test
    void calculate_shouldAssignUserIdToOperation() {
        User user = createTestUser();
        CalculationRequest req = createCalculationRequest(
                BigDecimal.valueOf(5),
                BigDecimal.valueOf(3),
                OperationType.ADDITION
        );

        OperationStrategy mockStrategy = mock(OperationStrategy.class);
        when(mockStrategy.execute(any(), any())).thenReturn(BigDecimal.valueOf(8));
        when(operationStrategyFactory.getStrategy("ADDITION")).thenReturn(mockStrategy);
        doNothing().when(operationAsyncService).saveAsync(any(Operation.class));
        Operation result = calculationService.calculate(req, user);
        assertEquals(user.getId(), result.getUserId());
    }

    @Test
    void calculate_shouldSetOperationTypeCorrectly() {
        User user = createTestUser();
        OperationType opType = OperationType.MODULO;
        CalculationRequest req = createCalculationRequest(
                BigDecimal.valueOf(17),
                BigDecimal.valueOf(5),
                opType
        );

        OperationStrategy mockStrategy = mock(OperationStrategy.class);
        when(mockStrategy.execute(any(), any())).thenReturn(BigDecimal.valueOf(2));
        when(operationStrategyFactory.getStrategy("MODULO")).thenReturn(mockStrategy);
        doNothing().when(operationAsyncService).saveAsync(any(Operation.class));
        Operation result = calculationService.calculate(req, user);
        assertEquals(opType, result.getOperation());
    }

    @Test
    void calculate_powerOperation() {
        User user = createTestUser();
        CalculationRequest req = createCalculationRequest(
                BigDecimal.valueOf(2),
                BigDecimal.valueOf(8),
                OperationType.POWER
        );

        OperationStrategy mockStrategy = mock(OperationStrategy.class);
        when(mockStrategy.execute(BigDecimal.valueOf(2), BigDecimal.valueOf(8)))
                .thenReturn(BigDecimal.valueOf(256));
        when(operationStrategyFactory.getStrategy("POWER")).thenReturn(mockStrategy);
        doNothing().when(operationAsyncService).saveAsync(any(Operation.class));
        Operation result = calculationService.calculate(req, user);
        assertEquals(BigDecimal.valueOf(256), result.getResult());
        verify(mockStrategy, times(1)).execute(BigDecimal.valueOf(2), BigDecimal.valueOf(8));
    }

    @Test
    void calculate_minOperation() {
        User user = createTestUser();
        CalculationRequest req = createCalculationRequest(
                BigDecimal.valueOf(15),
                BigDecimal.valueOf(7),
                OperationType.MIN
        );

        OperationStrategy mockStrategy = mock(OperationStrategy.class);
        when(mockStrategy.execute(BigDecimal.valueOf(15), BigDecimal.valueOf(7)))
                .thenReturn(BigDecimal.valueOf(7));
        when(operationStrategyFactory.getStrategy("MIN")).thenReturn(mockStrategy);
        doNothing().when(operationAsyncService).saveAsync(any(Operation.class));
        Operation result = calculationService.calculate(req, user);
        assertEquals(BigDecimal.valueOf(7), result.getResult());
    }

    @Test
    void calculate_maxOperation() {
        User user = createTestUser();
        CalculationRequest req = createCalculationRequest(
                BigDecimal.valueOf(15),
                BigDecimal.valueOf(7),
                OperationType.MAX
        );

        OperationStrategy mockStrategy = mock(OperationStrategy.class);
        when(mockStrategy.execute(BigDecimal.valueOf(15), BigDecimal.valueOf(7)))
                .thenReturn(BigDecimal.valueOf(15));
        when(operationStrategyFactory.getStrategy("MAX")).thenReturn(mockStrategy);
        doNothing().when(operationAsyncService).saveAsync(any(Operation.class));
        Operation result = calculationService.calculate(req, user);
        assertEquals(BigDecimal.valueOf(15), result.getResult());
    }
}