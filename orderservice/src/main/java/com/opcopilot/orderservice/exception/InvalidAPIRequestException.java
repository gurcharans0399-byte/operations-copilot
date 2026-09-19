package com.opcopilot.orderservice.exception;

public class InvalidAPIRequestException extends RuntimeException {
    public InvalidAPIRequestException(String message) {
        super(message);
    }
}
