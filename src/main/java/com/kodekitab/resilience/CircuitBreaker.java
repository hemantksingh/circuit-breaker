package com.kodekitab.resilience;

import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Supplier;
import java.util.logging.Logger;

public class CircuitBreaker<T> {

    private static final Logger logger = Logger.getLogger(CircuitBreaker.class.getName());
    private int failureCount;
    private final int threshold;
    private final long resetInterval;

    private CircuitBreaker(int failureCount, int threshold, long closingInterval) {
        this.failureCount = failureCount;
        this.threshold = threshold;
        this.resetInterval = closingInterval;

        closeCircuitPeriodically(this);
    }

    public CircuitBreaker() {
        this(0, 5, 20000);
    }

    private static void closeCircuitPeriodically(CircuitBreaker circuitBreaker) {
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                circuitBreaker.close();
            }
        }, circuitBreaker.resetInterval, circuitBreaker.resetInterval);
    }

    public CircuitBreaker requestThreshold(int threshold) {
        return new CircuitBreaker(failureCount, threshold, resetInterval);
    }

    public CircuitBreaker resetIntervalIn(int milliseconds) {
        return new CircuitBreaker(failureCount, threshold, milliseconds);
    }

    public void invoke(Supplier<T> action) throws CircuitBreakerException {
        if (this.isOpen())
            throw new CircuitBreakerException("The underlying service is not responding.");

        else try {
            action.get();
        } catch (Exception e) {
            failureCount++;
        }
    }

    private boolean isOpen() {
        logger.info("failure count: " + failureCount + " threshold: " + threshold);
        return failureCount >= threshold;
    }

    private void close() {
        logger.info("Closing circuit.");
        this.failureCount = 0;
    }
}
