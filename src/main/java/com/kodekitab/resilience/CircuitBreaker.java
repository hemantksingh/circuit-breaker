package com.kodekitab.resilience;

import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Supplier;
import java.util.logging.Logger;

public class CircuitBreaker<T> {

    private static Logger logger = Logger.getLogger(CircuitBreaker.class.getName());
    private int failureCount;
    private final int threshold;
    private final long resetInterval;

    private CircuitBreaker(int failureCount, int threshold, long resetInterval) {
        this.failureCount = failureCount;
        this.threshold = threshold;
        this.resetInterval = resetInterval;

        startTimer(this);
    }

    public CircuitBreaker() {
        this(0, 5, 200);
    }

    private static void  startTimer(CircuitBreaker circuitBreaker) {
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

    public void run(Supplier<T> action) {
        try {
            if (isClosed())
                action.get();
        } catch (Exception e) {
            failureCount++;
        }
    }

    private  boolean isClosed() {
        logger.info("failure count: " + failureCount + " threshold: " + threshold);
        return failureCount < threshold;
    }

    private void close() {
        logger.info("Closing circuit.");
        this.failureCount = 0;
    }
}
