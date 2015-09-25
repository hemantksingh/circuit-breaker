package com.kodekitab.resilience;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class CircuitBreakerSpec {

    HelloWorldService service = new HelloWorldService();

    @Test
    public void externalServiceIsNotInvokedIfFailureThresholdIsExceeded() {

        CircuitBreaker<String> circuitBreaker = new CircuitBreaker<String>()
                .requestThreshold(5)
                .resetIntervalIn(6000);

        circuitBreaker.run(() -> service.getMessage());
        circuitBreaker.run(() -> service.getMessage());
        circuitBreaker.run(() -> service.getMessage());
        circuitBreaker.run(() -> service.getMessage());
        circuitBreaker.run(() -> service.getMessage());

        assertThat(service.noOfCalls, is(5));
    }

    @Test
    public void externalServiceIsNotInvokedIfFailureThresholdIsExceededWithinResetInterval()
            throws InterruptedException {

        CircuitBreaker circuitBreaker = new CircuitBreaker()
                .requestThreshold(5)
                .resetIntervalIn(1500);

        circuitBreaker.run(() -> service.getMessage());
        circuitBreaker.run(() -> service.getMessage());
        circuitBreaker.run(() -> service.getMessage());
        circuitBreaker.run(() -> service.getMessage());
        circuitBreaker.run(() -> service.getMessage());

        Thread.sleep(1000);

        circuitBreaker.run(() -> service.getMessage());

        assertThat(service.noOfCalls, is(5));
    }

    @Test
    public void externalServiceIsInvokedIfResetIntervalHasElapsedAfterExceedingThreshold()
            throws InterruptedException {

        CircuitBreaker circuitBreaker = new CircuitBreaker()
                .requestThreshold(5)
                .resetIntervalIn(2000);

        circuitBreaker.run(() -> service.getMessage());
        circuitBreaker.run(() -> service.getMessage());
        circuitBreaker.run(() -> service.getMessage());
        circuitBreaker.run(() -> service.getMessage());
        circuitBreaker.run(() -> service.getMessage());

        Thread.sleep(3000);

        circuitBreaker.run(() -> service.getMessage());

        assertThat(service.noOfCalls, is(6));
    }
}