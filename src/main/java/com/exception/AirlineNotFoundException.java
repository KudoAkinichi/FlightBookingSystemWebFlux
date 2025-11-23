package com.exception;

public class AirlineNotFoundException extends RuntimeException {

    public AirlineNotFoundException(String message) {
        super(message);
    }

    public static AirlineNotFoundException forCode(String airlineCode) {
        return new AirlineNotFoundException(
                String.format("Airline with code '%s' not found", airlineCode)
        );
    }
}
