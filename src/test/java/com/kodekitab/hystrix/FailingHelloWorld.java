package com.kodekitab.hystrix;

import com.kodekitab.resilience.HelloWorld;

public class FailingHelloWorld implements HelloWorld {
    public int noOfCalls = 0;

    @Override
    public String getMessage() {
        noOfCalls++;
        throw new UnsupportedOperationException();
    }
}
