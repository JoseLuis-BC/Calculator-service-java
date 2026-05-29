package com.raven.services;

import com.raven.entities.Operation;
import com.raven.repository.OperationRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OperationAsyncService {

    private static final Logger log = LoggerFactory.getLogger(OperationAsyncService.class);

    private final OperationRepository repository;

    @Async
    public void saveAsync(Operation operation) {
        try {
            repository.save(operation);
            log.info("Operation saved async: {}", operation.getId());
        } catch (Exception e) {
            log.error("Error saving operation async", e);
        }
    }
}
