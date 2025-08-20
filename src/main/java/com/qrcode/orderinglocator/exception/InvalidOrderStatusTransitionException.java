package com.qrcode.orderinglocator.exception;

public class InvalidOrderStatusTransitionException extends RuntimeException {
    public InvalidOrderStatusTransitionException(String message) {
        super(message);
    }
}