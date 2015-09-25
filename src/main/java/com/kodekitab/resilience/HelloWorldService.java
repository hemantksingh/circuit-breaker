package com.kodekitab.resilience;

public class HelloWorldService implements HelloWorld {

    public int noOfCalls = 0;

    @Override
    public String getMessage() {
        noOfCalls ++;
        return "Hello World";
    }
}
