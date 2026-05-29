package com.raven.services;

import com.raven.entities.Operation;
import com.raven.repository.OperationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OperationAsyncServiceTest {

    @Mock
    private OperationRepository operationRepository;

    @InjectMocks
    private OperationAsyncService asyncService;

    private Operation createOperation() {
        Operation op = new Operation();
        op.setId(UUID.randomUUID());
        op.setUserId(UUID.randomUUID());
        op.setOperation(com.raven.entities.OperationType.ADDITION);
        op.setOperandA(java.math.BigDecimal.valueOf(2));
        op.setOperandB(java.math.BigDecimal.valueOf(3));
        op.setResult(java.math.BigDecimal.valueOf(5));
        op.setTimestamp(LocalDateTime.now());
        return op;
    }

    @Test
    void saveAsync_shouldCallRepositorySave() {
        Operation op = createOperation();
        when(operationRepository.save(op)).thenReturn(op);
        asyncService.saveAsync(op);
        verify(operationRepository, times(1)).save(op);
    }

    @Test
    void saveAsync_shouldSwallowExceptionsFromRepository() {
        Operation op = createOperation();
        doThrow(new RuntimeException("DB error")).when(operationRepository).save(op);
        assertDoesNotThrow(() -> asyncService.saveAsync(op));
        verify(operationRepository, times(1)).save(op);
    }
}