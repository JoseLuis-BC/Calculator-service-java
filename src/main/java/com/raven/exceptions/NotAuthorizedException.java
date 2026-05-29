package com.raven.exceptions;


public class NotAuthorizedException extends RuntimeException {

    public NotAuthorizedException(String message) {
        super(message);
    }

    public NotAuthorizedException() {
        super("Unauthorized access");
    }
}

