package com.kodekitab.hystrix;

import com.kodekitab.resilience.HelloWorldService;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class HystrixTestClient {

    private final String key = HelloWorldCommand.class.getSimpleName();

    @Test
    public void externalInvocationFails() {
        HystrixCommand.Setter defaultConfig = HystrixCommand.Setter
                .withGroupKey(HystrixCommandGroupKey.Factory.asKey(key))
                .andCommandKey(HystrixCommandKey.Factory.asKey("Failure"))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        .withExecutionTimeoutInMilliseconds(2000)
                        .withCircuitBreakerRequestVolumeThreshold(5));

        FailingHelloWorld service = new FailingHelloWorld();

        for (int i = 0; i < 9; i++) {
            HelloWorldCommand command = new HelloWorldCommand(service, defaultConfig);
            command.execute();
        }

        assertThat(service.noOfCalls, is(5));
    }

    @Test
    public void externalInvocationSucceeds() {

        HystrixCommand.Setter defaultConfig = HystrixCommand.Setter
                .withGroupKey(HystrixCommandGroupKey.Factory.asKey(key))
                .andCommandKey(HystrixCommandKey.Factory.asKey("Success"))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        .withExecutionTimeoutInMilliseconds(2000)
                        .withCircuitBreakerRequestVolumeThreshold(5));

        HelloWorldService service = new HelloWorldService();

        for (int i = 0; i < 9; i++) {
            HelloWorldCommand command = new HelloWorldCommand(service, defaultConfig);
            command.execute();
        }

        assertThat(service.noOfCalls, is(9));
    }
}
