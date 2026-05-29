package com.raven.services;

import com.raven.entities.Operation;
import com.raven.entities.OperationType;
import com.raven.exceptions.BadRequestException;
import com.raven.repository.OperationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HistoryServiceTest {

    @Mock
    private OperationRepository operationRepository;

    @InjectMocks
    private HistoryService historyService;

    private Operation createOperation(UUID userId) {
        Operation op = new Operation();
        op.setId(UUID.randomUUID());
        op.setUserId(userId);
        op.setOperation(OperationType.ADDITION);
        op.setOperandA(BigDecimal.valueOf(5));
        op.setOperandB(BigDecimal.valueOf(3));
        op.setResult(BigDecimal.valueOf(8));
        op.setTimestamp(LocalDateTime.now());
        return op;
    }

    @Test
    void getHistory_withOperationAndDateRange_callsFindByUserIdAndOperationAndTimestampBetween() {
        UUID userId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 10);
        Operation op = createOperation(userId);
        Page<Operation> page = new PageImpl<>(List.of(op), pageable, 1);

        LocalDateTime start = LocalDateTime.now().minusDays(2);
        LocalDateTime end = LocalDateTime.now();

        when(operationRepository.findByUserIdAndOperationAndTimestampBetween(
                eq(userId), eq(OperationType.ADDITION), eq(start), eq(end), eq(pageable)))
                .thenReturn(page);

        Page<Operation> result = historyService.getHistory(userId, OperationType.ADDITION, start, end, pageable);

        assertNotNull(result);
        assertEquals(1, result.getNumberOfElements());
        assertEquals(1, result.getContent().size());
        verify(operationRepository, times(1))
                .findByUserIdAndOperationAndTimestampBetween(userId, OperationType.ADDITION, start, end, pageable);
        verifyNoMoreInteractions(operationRepository);
    }

    @Test
    void getHistory_withOperationOnly_callsFindByUserIdAndOperation() {
        UUID userId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 5);
        Operation op = createOperation(userId);
        Page<Operation> page = new PageImpl<>(List.of(op), pageable, 1);

        when(operationRepository.findByUserIdAndOperation(eq(userId), eq(OperationType.ADDITION), eq(pageable)))
                .thenReturn(page);

        Page<Operation> result = historyService.getHistory(userId, OperationType.ADDITION, null, null, pageable);

        assertNotNull(result);
        assertEquals(1, result.getNumberOfElements());
        assertEquals(1, result.getContent().size());
        verify(operationRepository, times(1)).findByUserIdAndOperation(userId, OperationType.ADDITION, pageable);
        verifyNoMoreInteractions(operationRepository);
    }

    @Test
    void getHistory_withDateRangeOnly_callsFindByUserIdAndTimestampBetween() {
        UUID userId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 20);
        Operation op = createOperation(userId);
        Page<Operation> page = new PageImpl<>(List.of(op), pageable, 1);

        LocalDateTime start = LocalDateTime.now().minusDays(5);
        LocalDateTime end = LocalDateTime.now();

        when(operationRepository.findByUserIdAndTimestampBetween(eq(userId), eq(start), eq(end), eq(pageable)))
                .thenReturn(page);
        Page<Operation> result = historyService.getHistory(userId, null, start, end, pageable);

        assertNotNull(result);
        assertEquals(1, result.getNumberOfElements());
        assertEquals(1, result.getContent().size());
        verify(operationRepository, times(1)).findByUserIdAndTimestampBetween(userId, start, end, pageable);
        verifyNoMoreInteractions(operationRepository);
    }

    @Test
    void getHistory_withoutFilters_callsFindByUserId() {
        UUID userId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 10, Sort.by("timestamp").descending());
        Operation op = createOperation(userId);
        Page<Operation> page = new PageImpl<>(List.of(op), pageable, 1);

        when(operationRepository.findByUserId(eq(userId), eq(pageable))).thenReturn(page);

        Page<Operation> result = historyService.getHistory(userId, null, null, null, pageable);

        assertNotNull(result);
        assertEquals(1, result.getNumberOfElements());
        assertEquals(1, result.getContent().size());
        verify(operationRepository, times(1)).findByUserId(userId, pageable);
        verifyNoMoreInteractions(operationRepository);
    }

    @Test
    void getById_shouldReturnOperationWhenExistsAndUserMatches() {
        UUID userId = UUID.randomUUID();
        Operation op = createOperation(userId);

        when(operationRepository.findById(eq(op.getId()))).thenReturn(Optional.of(op));

        Operation result = historyService.getById(op.getId(), userId);

        assertNotNull(result);
        assertEquals(op.getId(), result.getId());
        verify(operationRepository, times(1)).findById(op.getId());
    }

    @Test
    void getById_shouldThrowWhenNotFound() {
        UUID userId = UUID.randomUUID();
        UUID opId = UUID.randomUUID();

        when(operationRepository.findById(eq(opId))).thenReturn(Optional.empty());

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> historyService.getById(opId, userId));

        assertTrue(ex.getMessage().toLowerCase().contains("operation not found"));
        verify(operationRepository, times(1)).findById(opId);
    }

    @Test
    void getById_shouldThrowWhenUserMismatch() {
        UUID userId = UUID.randomUUID();
        Operation op = createOperation(UUID.randomUUID());

        when(operationRepository.findById(eq(op.getId()))).thenReturn(Optional.of(op));

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> historyService.getById(op.getId(), userId));

        assertTrue(ex.getMessage().toLowerCase().contains("unauthorized"));
        verify(operationRepository, times(1)).findById(op.getId());
    }

    @Test
    void delete_shouldCallRepositoryDeleteWhenOperationExistsAndUserMatches() {
        UUID userId = UUID.randomUUID();
        Operation op = createOperation(userId);

        when(operationRepository.findById(eq(op.getId()))).thenReturn(Optional.of(op));
        doNothing().when(operationRepository).delete(eq(op));

        historyService.delete(op.getId(), userId);

        verify(operationRepository, times(1)).findById(op.getId());
        verify(operationRepository, times(1)).delete(op);
    }

    @Test
    void delete_shouldThrowWhenOperationDoesNotExist() {
        UUID userId = UUID.randomUUID();
        UUID opId = UUID.randomUUID();

        when(operationRepository.findById(eq(opId))).thenReturn(Optional.empty());

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> historyService.delete(opId, userId));

        assertTrue(ex.getMessage().toLowerCase().contains("operation not found"));
        verify(operationRepository, times(1)).findById(opId);
        verify(operationRepository, never()).delete(any(Operation.class));
    }

    @Test
    void delete_shouldThrowWhenUserMismatch() {
        UUID userId = UUID.randomUUID();
        Operation op = createOperation(UUID.randomUUID());

        when(operationRepository.findById(eq(op.getId()))).thenReturn(Optional.of(op));

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> historyService.delete(op.getId(), userId));

        assertTrue(ex.getMessage().toLowerCase().contains("unauthorized"));
        verify(operationRepository, times(1)).findById(op.getId());
        verify(operationRepository, never()).delete(any(Operation.class));
    }

    @Test
    void getHistory_withOperationButNoStartDate_callsFindByUserIdAndOperation() {
        UUID userId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 10);
        Operation op = createOperation(userId);
        Page<Operation> page = new PageImpl<>(List.of(op), pageable, 1);

        LocalDateTime endDate = LocalDateTime.now();
        when(operationRepository.findByUserIdAndOperation(eq(userId), eq(OperationType.ADDITION), eq(pageable)))
                .thenReturn(page);
        Page<Operation> result = historyService.getHistory(userId, OperationType.ADDITION, null, endDate, pageable);

        assertNotNull(result);
        assertEquals(1, result.getNumberOfElements());
        verify(operationRepository, times(1)).findByUserIdAndOperation(userId, OperationType.ADDITION, pageable);
        verifyNoMoreInteractions(operationRepository);
    }

    @Test
    void getHistory_withOperationButNoEndDate_callsFindByUserIdAndOperation() {
        UUID userId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 10);
        Operation op = createOperation(userId);
        Page<Operation> page = new PageImpl<>(List.of(op), pageable, 1);

        LocalDateTime startDate = LocalDateTime.now().minusDays(5);
        when(operationRepository.findByUserIdAndOperation(eq(userId), eq(OperationType.SUBTRACTION), eq(pageable)))
                .thenReturn(page);
        Page<Operation> result = historyService.getHistory(userId, OperationType.SUBTRACTION, startDate, null, pageable);

        assertNotNull(result);
        assertEquals(1, result.getNumberOfElements());
        verify(operationRepository, times(1)).findByUserIdAndOperation(userId, OperationType.SUBTRACTION, pageable);
        verifyNoMoreInteractions(operationRepository);
    }

    @Test
    void getHistory_withDateRangeOnly_explicitlyNoOperation_callsFindByUserIdAndTimestampBetween() {
        UUID userId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 10);
        Operation op = createOperation(userId);
        Page<Operation> page = new PageImpl<>(List.of(op), pageable, 1);

        LocalDateTime start = LocalDateTime.now().minusDays(10);
        LocalDateTime end = LocalDateTime.now();

        when(operationRepository.findByUserIdAndTimestampBetween(eq(userId), eq(start), eq(end), eq(pageable)))
                .thenReturn(page);
        Page<Operation> result = historyService.getHistory(userId, null, start, end, pageable);

        assertNotNull(result);
        assertEquals(1, result.getNumberOfElements());
        verify(operationRepository, times(1)).findByUserIdAndTimestampBetween(userId, start, end, pageable);
        verifyNoMoreInteractions(operationRepository);
    }

    @Test
    void getHistory_withOnlyStartDate_callsFindByUserId() {
        UUID userId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 10);
        Operation op = createOperation(userId);
        Page<Operation> page = new PageImpl<>(List.of(op), pageable, 1);

        LocalDateTime start = LocalDateTime.now().minusDays(5);
        when(operationRepository.findByUserId(eq(userId), eq(pageable)))
                .thenReturn(page);
        Page<Operation> result = historyService.getHistory(userId, null, start, null, pageable);

        assertNotNull(result);
        assertEquals(1, result.getNumberOfElements());
        verify(operationRepository, times(1)).findByUserId(userId, pageable);
        verifyNoMoreInteractions(operationRepository);
    }

    @Test
    void getHistory_withOnlyEndDate_callsFindByUserId() {
        UUID userId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 10);
        Operation op = createOperation(userId);
        Page<Operation> page = new PageImpl<>(List.of(op), pageable, 1);

        LocalDateTime end = LocalDateTime.now();
        when(operationRepository.findByUserId(eq(userId), eq(pageable)))
                .thenReturn(page);
       Page<Operation> result = historyService.getHistory(userId, null, null, end, pageable);

        assertNotNull(result);
        assertEquals(1, result.getNumberOfElements());
        verify(operationRepository, times(1)).findByUserId(userId, pageable);
        verifyNoMoreInteractions(operationRepository);
    }
}