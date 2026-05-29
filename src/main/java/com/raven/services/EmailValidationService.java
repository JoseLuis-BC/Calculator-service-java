package com.raven.services;

import com.raven.dto.external.EmailDeliverability;
import com.raven.dto.external.EmailQuality;
import com.raven.dto.external.EmailValidationResponse;
import com.raven.exceptions.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailValidationService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${external.email.api.key}")
    private String apiKey;

    public void validate(String email) {

        String url = "https://emailreputation.abstractapi.com/v1/?api_key="
                + apiKey + "&email=" + email;

        EmailValidationResponse response =
                restTemplate.getForObject(url, EmailValidationResponse.class);

        log.info("Email API response: {}", response);

        if (response == null || response.getDeliverability() == null) {
            throw new BadRequestException(
                    "Email validation failed",
                    List.of("Invalid response from validation service")
            );
        }

        EmailDeliverability d = response.getDeliverability();
        EmailQuality q = response.getQuality();

        if (Boolean.FALSE.equals(d.getFormatValid())) {
            throw new BadRequestException(
                    "Email validation failed",
                    List.of("Invalid email format")
            );
        }

        if (q != null && Boolean.TRUE.equals(q.getDisposable())) {
            throw new BadRequestException(
                    "Email validation failed",
                    List.of("Disposable email addresses are not allowed")
            );
        }

        if (Boolean.FALSE.equals(d.getMxValid())
                || "undeliverable".equalsIgnoreCase(d.getStatus())) {

            throw new BadRequestException(
                    "Email validation failed",
                    List.of("Email domain is not valid or not deliverable")
            );
        }
    }
}