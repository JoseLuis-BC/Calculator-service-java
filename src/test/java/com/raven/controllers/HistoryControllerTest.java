package com.raven.controllers;

import com.raven.entities.Operation;
import com.raven.entities.OperationType;
import com.raven.entities.User;
import com.raven.exceptions.BadRequestException;
import com.raven.services.HistoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HistoryControllerTest {

    @Mock
    private HistoryService historyService;

    @InjectMocks
    private HistoryController historyController;

    private User createTestUser() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("testuser");
        return user;
    }

    private Operation createTestOperation(UUID userId) {
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
    void getHistory_shouldReturnPageOfOperations() {
        User user = createTestUser();
        Operation op = createTestOperation(user.getId());
        Pageable pageable = PageRequest.of(0, 10);
        Page<Operation> page = new PageImpl<>(List.of(op), pageable, 1);

        when(historyService.getHistory(user.getId(), null, null, null, pageable))
                .thenReturn(page);

        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(user);

        Page<Operation> result = historyController.getHistory(null, null, null, pageable, auth);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(op.getId(), result.getContent().get(0).getId());
        verify(historyService, times(1)).getHistory(user.getId(), null, null, null, pageable);
    }

    @Test
    void getHistory_withOperationFilter() {
        User user = createTestUser();
        Operation op = createTestOperation(user.getId());
        op.setOperation(OperationType.SUBTRACTION);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Operation> page = new PageImpl<>(List.of(op), pageable, 1);

        when(historyService.getHistory(user.getId(), OperationType.SUBTRACTION, null, null, pageable))
                .thenReturn(page);

        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(user);

        Page<Operation> result = historyController.getHistory(OperationType.SUBTRACTION, null, null, pageable, auth);

        assertEquals(1, result.getTotalElements());
        verify(historyService, times(1)).getHistory(user.getId(), OperationType.SUBTRACTION, null, null, pageable);
    }

    @Test
    void getHistory_withDateFilter() {
        User user = createTestUser();
        Operation op = createTestOperation(user.getId());
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        LocalDateTime endDate = LocalDateTime.now().plusDays(1);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Operation> page = new PageImpl<>(List.of(op), pageable, 1);

        when(historyService.getHistory(user.getId(), null, startDate, endDate, pageable))
                .thenReturn(page);

        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(user);

        Page<Operation> result = historyController.getHistory(null, startDate, endDate, pageable, auth);

        assertEquals(1, result.getTotalElements());
        verify(historyService, times(1)).getHistory(user.getId(), null, startDate, endDate, pageable);
    }

    @Test
    void getById_shouldReturnOperation() {
        User user = createTestUser();
        Operation op = createTestOperation(user.getId());

        when(historyService.getById(op.getId(), user.getId())).thenReturn(op);

        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(user);

        Operation result = historyController.getById(op.getId(), auth);

        assertNotNull(result);
        assertEquals(op.getId(), result.getId());
        verify(historyService, times(1)).getById(op.getId(), user.getId());
    }

    @Test
    void getById_shouldThrowExceptionForUnauthorizedUser() {
        User user = createTestUser();
        UUID operationId = UUID.randomUUID();

        when(historyService.getById(operationId, user.getId()))
                .thenThrow(new BadRequestException("Unauthorized access"));

        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(user);

        assertThrows(BadRequestException.class, () -> historyController.getById(operationId, auth));
        verify(historyService, times(1)).getById(operationId, user.getId());
    }

    @Test
    void delete_shouldCallHistoryService() {
        User user = createTestUser();
        UUID operationId = UUID.randomUUID();

        doNothing().when(historyService).delete(operationId, user.getId());

        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(user);

        historyController.delete(operationId, auth);

        verify(historyService, times(1)).delete(operationId, user.getId());
    }

    @Test
    void delete_shouldThrowExceptionForUnauthorizedUser() {
        User user = createTestUser();
        UUID operationId = UUID.randomUUID();

        doThrow(new BadRequestException("Unauthorized access"))
                .when(historyService).delete(operationId, user.getId());

        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(user);

        assertThrows(BadRequestException.class, () -> historyController.delete(operationId, auth));
        verify(historyService, times(1)).delete(operationId, user.getId());
    }
}