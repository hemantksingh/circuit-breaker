package com.kodekitab.resilience;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class CircuitBreakerSpec {

    @Test
    public void externalServiceIsNotInvokedIfFailureThresholdIsExceeded() {

        HelloWorldService service = new HelloWorldService();
        CircuitBreaker circuitBreaker = new CircuitBreaker(service)
                .requestThreshold(5)
                .resetIntervalInMilliseconds(6000);
        circuitBreaker.run();
        circuitBreaker.run();
        circuitBreaker.run();
        circuitBreaker.run();
        circuitBreaker.run();

        assertThat(service.noOfCalls, is(5));
    }

    @Test
    public void externalServiceIsNotInvokedIfFailureThresholdIsExceededWithinResetInterval()
            throws InterruptedException {

        HelloWorldService service = new HelloWorldService();
        CircuitBreaker circuitBreaker = new CircuitBreaker(service)
                .requestThreshold(5)
                .resetIntervalInMilliseconds(1500);

        circuitBreaker.run();
        circuitBreaker.run();
        circuitBreaker.run();
        circuitBreaker.run();
        circuitBreaker.run();

        Thread.sleep(1000);

        circuitBreaker.run();

        assertThat(service.noOfCalls, is(5));
    }

    @Test
    public void externalServiceIsInvokedIfResetIntervalHasElapsedAfterExceedingThreshold()
            throws InterruptedException {

        HelloWorldService service = new HelloWorldService();
        CircuitBreaker circuitBreaker = new CircuitBreaker(service)
                .requestThreshold(5)
                .resetIntervalInMilliseconds(2000);

        circuitBreaker.run();
        circuitBreaker.run();
        circuitBreaker.run();
        circuitBreaker.run();
        circuitBreaker.run();

        Thread.sleep(3000);

        circuitBreaker.run();

        assertThat(service.noOfCalls, is(6));
    }
}
