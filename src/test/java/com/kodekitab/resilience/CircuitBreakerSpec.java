package com.kodekitab.resilience;

import com.kodekitab.hystrix.FailingHelloWorld;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

public class CircuitBreakerSpec {

    FailingHelloWorld service = new FailingHelloWorld();

    @Test
    public void externalServiceIsInvokedAsManyTimesAsTheSpecifiedThreshold()
            throws CircuitBreakerException {

        CircuitBreaker circuitBreaker = new CircuitBreaker()
                .requestThreshold(5)
                .resetIntervalIn(6000);

        CircuitBreakerException exception = null;
            circuitBreaker.invoke(() -> service.getMessage());
            circuitBreaker.invoke(() -> service.getMessage());
            circuitBreaker.invoke(() -> service.getMessage());
            circuitBreaker.invoke(() -> service.getMessage());
            circuitBreaker.invoke(() -> service.getMessage());
        try {
            circuitBreaker.invoke(() -> service.getMessage());
        } catch (CircuitBreakerException e ){
            exception = e;
        }
        assertThat(service.noOfCalls, is(5));
        assertThat(exception, is(notNullValue()));
    }

    @Test
    public void externalServiceIsNotInvokedIfFailureThresholdIsExceededWithinResetInterval()
            throws InterruptedException, CircuitBreakerException {

        CircuitBreaker circuitBreaker = new CircuitBreaker()
                .requestThreshold(5)
                .resetIntervalIn(1500);

        circuitBreaker.invoke(() -> service.getMessage());
        circuitBreaker.invoke(() -> service.getMessage());
        circuitBreaker.invoke(() -> service.getMessage());
        circuitBreaker.invoke(() -> service.getMessage());
        circuitBreaker.invoke(() -> service.getMessage());

        Thread.sleep(1000);

        CircuitBreakerException exception = null;
        try {
            circuitBreaker.invoke(() -> service.getMessage());
        } catch (CircuitBreakerException e ){
            exception = e;
        }

        assertThat(service.noOfCalls, is(5));
        assertThat(exception, is(notNullValue()));
    }

    @Test
    public void externalServiceIsInvokedIfResetIntervalHasElapsedAfterExceedingThreshold()
            throws InterruptedException, CircuitBreakerException {

        CircuitBreaker circuitBreaker = new CircuitBreaker()
                .requestThreshold(5)
                .resetIntervalIn(2000);

        circuitBreaker.invoke(() -> service.getMessage());
        circuitBreaker.invoke(() -> service.getMessage());
        circuitBreaker.invoke(() -> service.getMessage());
        circuitBreaker.invoke(() -> service.getMessage());
        circuitBreaker.invoke(() -> service.getMessage());

        Thread.sleep(3000);

        circuitBreaker.invoke(() -> service.getMessage());

        assertThat(service.noOfCalls, is(6));
    }
}