package com.raven.exceptions;

import java.util.List;

public class BadRequestException extends RuntimeException {

    private final List<String> details;

    public BadRequestException(String message) {
        super(message);
        this.details = List.of(message);
    }

    public BadRequestException(String message, List<String> details) {
        super(message);
        this.details = details;
    }

    public List<String> getDetails() {
        return details;
    }
}
