package com.raven.controllers;

import com.raven.entities.Operation;
import com.raven.entities.OperationType;
import com.raven.entities.User;
import com.raven.services.HistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/history")
@RequiredArgsConstructor
public class HistoryController {

    private final HistoryService historyService;

    @GetMapping
    public Page<Operation> getHistory(
            @RequestParam(required = false) OperationType operation,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime startDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime endDate,
            Pageable pageable,
            Authentication authentication
    ) {

        User user = (User) authentication.getPrincipal();

        return historyService.getHistory(user.getId(), operation, startDate, endDate, pageable);
    }

    @GetMapping("/{id}")
    public Operation getById(@PathVariable UUID id, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return historyService.getById(id, user.getId());
    }


    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        historyService.delete(id, user.getId());
    }
}
