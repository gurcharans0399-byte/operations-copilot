package com.opcopilot.orderservice.exception;

public class InvalidFeignCallException extends RuntimeException{
    public InvalidFeignCallException(String message) {
        super(message);
    }
}
