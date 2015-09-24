package com.kodekitab.resilience;

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

public class CircuitBreaker {

    private final HelloWorldService service;
    private int threshold;
    private int failureCount = 0;
    Logger logger = Logger.getLogger(this.getClass().getName());
    private long resetInterval = 20000;


    public CircuitBreaker(HelloWorldService service) {

        this.service = service;
        System.out.println("reset Interval = [" + resetInterval + "]");
    }

    public void run() {
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                logger.info("Setting failure to 0");
                failureCount = 0;
            }
        }, this.resetInterval, this.resetInterval);

        logger.info("failure count: " + failureCount + " threshold: " + threshold);
        try {
            if (failureCount < threshold)
                service.getMessage();

        } catch (Exception e) {
            failureCount++;
        }
    }

    public CircuitBreaker requestThreshold(int threshold) {
        this.threshold = threshold;
        return this;
    }

    public CircuitBreaker resetIntervalInMilliseconds(int resetInterval) {
        this.resetInterval = resetInterval;
        return this;
    }
}
