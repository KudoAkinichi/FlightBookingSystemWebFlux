package com.exception;

public class FlightNotFoundException extends RuntimeException {

    public FlightNotFoundException(String message) {
        super(message);
    }

    public FlightNotFoundException(String flightId, String message) {
        super(String.format("Flight with ID '%s' not found: %s", flightId, message));
    }
}