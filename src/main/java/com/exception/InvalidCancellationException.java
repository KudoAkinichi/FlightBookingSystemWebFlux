package com.exception;

public class InvalidCancellationException extends RuntimeException {

    public InvalidCancellationException(String message) {
        super(message);
    }

    public InvalidCancellationException(String pnr, long hoursRemaining) {
        super(String.format(
                "Booking with PNR '%s' cannot be cancelled. Only %d hours remaining before departure. " +
                        "Cancellation allowed only 24+ hours before departure.",
                pnr, hoursRemaining));
    }
}