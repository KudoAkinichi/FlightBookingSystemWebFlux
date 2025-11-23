package com.exception;

public class AirportNotFoundException extends RuntimeException {

    public AirportNotFoundException(String message) {
        super(message);
    }

    public static AirportNotFoundException forIataCode(String iataCode) {
        return new AirportNotFoundException(
                String.format("Airport with IATA code '%s' not found", iataCode)
        );
    }
}
