package com.raven.services;

import com.raven.entities.Operation;
import com.raven.entities.OperationType;
import com.raven.exceptions.BadRequestException;
import com.raven.repository.OperationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HistoryService {

    private final OperationRepository operationRepository;

    public Page<Operation> getHistory(
            UUID userId,
            OperationType operationType,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable
    ) {

        if (operationType != null && startDate != null && endDate != null) {
            return operationRepository.findByUserIdAndOperationAndTimestampBetween(
                    userId, operationType, startDate, endDate, pageable);
        }

        if (operationType != null) {
            return operationRepository.findByUserIdAndOperation(
                    userId, operationType, pageable);
        }

        if (startDate != null && endDate != null) {
            return operationRepository.findByUserIdAndTimestampBetween(
                    userId, startDate, endDate, pageable);
        }

        return operationRepository.findByUserId(userId, pageable);
    }

    public Operation getById(UUID id, UUID userId) {

        Operation op = operationRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Operation not found"));

        if (!op.getUserId().equals(userId)) {
            throw new BadRequestException("Unauthorized access");
        }

        return op;
    }

    public void delete(UUID id, UUID userId) {

        Operation op = getById(id, userId);

        operationRepository.delete(op);
    }
}