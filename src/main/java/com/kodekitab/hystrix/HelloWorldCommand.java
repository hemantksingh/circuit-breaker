package com.kodekitab.hystrix;

import com.kodekitab.resilience.HelloWorldService;
import com.netflix.hystrix.HystrixCommand;

public class HelloWorldCommand extends HystrixCommand<String> {

    private final HelloWorldService service;

    public HelloWorldCommand(HelloWorldService service, Setter defaults) {
        super(defaults);
        this.service = service;
    }

    @Override
    protected String getFallback() {
        return "Good Bye";
    }

    @Override
    protected String run() throws Exception {
        Thread.sleep(100);
        return service.getMessage();
    }
}
