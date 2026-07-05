package com.pb7technologies.razorpay.common.exception;

public class InvalidStateTransitionException extends RuntimeException{

    private final String fromState;
    private final String toEvent;

    public InvalidStateTransitionException(String fromState, String toEvent){
        super("Invalid transition from " + fromState + " with event " + toEvent);
        this.fromState = fromState;
        this.toEvent = toEvent;
    }
}
