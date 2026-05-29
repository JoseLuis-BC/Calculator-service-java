package com.raven.controllers;

import com.raven.dto.request.CalculationRequest;
import com.raven.entities.User;
import com.raven.services.CalculationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CalculatorController {

    private final CalculationService calculationService;

    @PostMapping("/calculate")
    public ResponseEntity<?> calculate(@RequestBody CalculationRequest calculationRequest, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(calculationService.calculate(calculationRequest, user));
    }
}