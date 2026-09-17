package com.opcopilot.orderservice.exception;

public class InvalidAPIParameterException extends RuntimeException {
    public InvalidAPIParameterException(String message) {
        super(message);
    }
}
