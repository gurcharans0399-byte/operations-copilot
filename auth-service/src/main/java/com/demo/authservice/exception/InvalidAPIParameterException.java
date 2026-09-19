package com.demo.authservice.exception;

public class InvalidAPIParameterException extends RuntimeException {
    public InvalidAPIParameterException(String message) {
        super(message);
    }
}
