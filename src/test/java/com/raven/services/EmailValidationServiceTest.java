package com.raven.services;

import com.raven.dto.external.EmailDeliverability;
import com.raven.dto.external.EmailQuality;
import com.raven.dto.external.EmailValidationResponse;
import com.raven.exceptions.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmailValidationServiceTest {

    private EmailValidationService emailValidationService;

    @Mock
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        emailValidationService = new EmailValidationService();
        ReflectionTestUtils.setField(emailValidationService, "restTemplate", restTemplate);
        ReflectionTestUtils.setField(emailValidationService, "apiKey", "test-key");
    }

    private EmailValidationResponse buildResponse(Boolean formatValid, Boolean mxValid, String status, Boolean disposable) {
        EmailDeliverability deliverability = new EmailDeliverability();
        deliverability.setFormatValid(formatValid);
        deliverability.setMxValid(mxValid);
        deliverability.setStatus(status);

        EmailQuality quality = new EmailQuality();
        quality.setDisposable(disposable);

        EmailValidationResponse resp = new EmailValidationResponse();
        resp.setDeliverability(deliverability);
        resp.setQuality(quality);

        return resp;
    }

    @Test
    void validate_shouldNotThrow_whenResponseIsValid() {
        EmailValidationResponse resp = buildResponse(true, true, "DELIVERABLE", false);

        when(restTemplate.getForObject(anyString(), eq(EmailValidationResponse.class))).thenReturn(resp);

        assertDoesNotThrow(() -> emailValidationService.validate("user@example.com"));
    }

    @Test
    void validate_shouldThrow_whenResponseIsNull() {
        when(restTemplate.getForObject(anyString(), eq(EmailValidationResponse.class))).thenReturn(null);

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> emailValidationService.validate("user@example.com"));

        assertTrue(ex.getDetails().stream().anyMatch(d -> d.toLowerCase().contains("invalid response")));
    }

    @Test
    void validate_shouldThrow_whenDeliverabilityIsNull() {
        EmailValidationResponse resp = new EmailValidationResponse();
        resp.setDeliverability(null);
        resp.setQuality(null);

        when(restTemplate.getForObject(anyString(), eq(EmailValidationResponse.class))).thenReturn(resp);

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> emailValidationService.validate("user@example.com"));

        assertTrue(ex.getDetails().stream().anyMatch(d -> d.toLowerCase().contains("invalid response")));
    }

    @Test
    void validate_shouldThrow_whenFormatInvalid() {
        EmailValidationResponse resp = buildResponse(false, true, "DELIVERABLE", false);

        when(restTemplate.getForObject(anyString(), eq(EmailValidationResponse.class))).thenReturn(resp);

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> emailValidationService.validate("user@example.com"));

        assertTrue(ex.getDetails().stream().anyMatch(d -> d.toLowerCase().contains("invalid email format")));
    }

    @Test
    void validate_shouldThrow_whenDisposableTrue_coversDisposableBranch() {
        EmailValidationResponse resp = buildResponse(true, true, "DELIVERABLE", Boolean.TRUE);

        when(restTemplate.getForObject(anyString(), eq(EmailValidationResponse.class))).thenReturn(resp);

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> emailValidationService.validate("user@example.com"));

        assertTrue(ex.getDetails().stream().anyMatch(d -> d.toLowerCase().contains("disposable")));
    }

    @Test
    void validate_shouldNotThrow_whenDisposableFalse() {
        EmailValidationResponse resp = buildResponse(true, true, "DELIVERABLE", Boolean.FALSE);

        when(restTemplate.getForObject(anyString(), eq(EmailValidationResponse.class))).thenReturn(resp);

        assertDoesNotThrow(() -> emailValidationService.validate("user@example.com"));
    }

    @Test
    void validate_shouldThrow_whenQualityIsNullButOtherConditionsValid() {
        EmailDeliverability deliverability = new EmailDeliverability();
        deliverability.setFormatValid(true);
        deliverability.setMxValid(true);
        deliverability.setStatus("DELIVERABLE");

        EmailValidationResponse resp = new EmailValidationResponse();
        resp.setDeliverability(deliverability);
        resp.setQuality(null);

        when(restTemplate.getForObject(anyString(), eq(EmailValidationResponse.class))).thenReturn(resp);

        assertDoesNotThrow(() -> emailValidationService.validate("user@example.com"));
    }

    @Test
    void validate_shouldThrow_whenMxInvalid() {
        EmailValidationResponse resp = buildResponse(true, false, "DELIVERABLE", false);

        when(restTemplate.getForObject(anyString(), eq(EmailValidationResponse.class))).thenReturn(resp);

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> emailValidationService.validate("user@example.com"));

        assertTrue(ex.getDetails().stream().anyMatch(d -> d.toLowerCase().contains("email domain")));
    }

    @Test
    void validate_shouldThrow_whenStatusUndeliverable() {
        EmailValidationResponse resp = buildResponse(true, true, "undeliverable", false);

        when(restTemplate.getForObject(anyString(), eq(EmailValidationResponse.class))).thenReturn(resp);

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> emailValidationService.validate("user@example.com"));

        assertTrue(ex.getDetails().stream().anyMatch(d -> d.toLowerCase().contains("email domain")));
    }
}