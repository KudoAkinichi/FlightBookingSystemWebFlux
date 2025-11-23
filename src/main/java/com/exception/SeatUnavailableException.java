package com.exception;

import java.util.List;

public class SeatUnavailableException extends RuntimeException {

    public SeatUnavailableException(String message) {
        super(message);
    }

    public SeatUnavailableException(List<String> unavailableSeats) {
        super(String.format("The following seats are not available: %s",
                String.join(", ", unavailableSeats)));
    }
}