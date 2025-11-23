package com.exception;

public class BookingNotFoundException extends RuntimeException {

    public BookingNotFoundException(String message) {
        super(message);
    }

    public static BookingNotFoundException forPnr(String pnr) {
        return new BookingNotFoundException(
                String.format("Booking with PNR '%s' not found", pnr)
        );
    }
}
