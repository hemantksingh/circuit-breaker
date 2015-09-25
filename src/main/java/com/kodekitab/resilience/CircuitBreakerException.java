package com.kodekitab.resilience;

public class CircuitBreakerException extends Throwable {
    public CircuitBreakerException(String message) {
        super(message);
    }
}
