package com.kodekitab.resilience;

public class HelloWorldService {

    public int noOfCalls = 0;

    public String getMessage() {
        noOfCalls ++;
        throw new UnsupportedOperationException();
    }
}
