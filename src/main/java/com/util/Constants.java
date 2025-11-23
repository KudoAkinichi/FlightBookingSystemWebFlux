package com.util;

public final class Constants {

    private Constants() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    // API Paths
    public static final String API_BASE_PATH = "/api/v1";
    public static final String FLIGHTS_PATH = API_BASE_PATH + "/flights";
    public static final String BOOKINGS_PATH = API_BASE_PATH + "/bookings";
    public static final String ADMIN_PATH = API_BASE_PATH + "/admin";

    // Date/Time Formats
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String TIMEZONE_IST = "Asia/Kolkata";

    // PNR Configuration
    public static final String PNR_PREFIX = "PNR";
    public static final int PNR_NUMBER_LENGTH = 9;

    // Booking Status
    public static final String STATUS_CONFIRMED = "CONFIRMED";
    public static final String STATUS_CANCELLED = "CANCELLED";
    public static final String STATUS_PENDING = "PENDING";

    // Seat Types
    public static final String SEAT_ECONOMY = "ECONOMY";
    public static final String SEAT_BUSINESS = "BUSINESS";
    public static final String SEAT_FIRST_CLASS = "FIRST_CLASS";

    // Meal Preferences
    public static final String MEAL_VEG = "VEG";
    public static final String MEAL_NON_VEG = "NON_VEG";
    public static final String MEAL_NONE = "NONE";

    // Trip Types
    public static final String TRIP_ONEWAY = "ONEWAY";
    public static final String TRIP_ROUNDTRIP = "ROUNDTRIP";

    // Error Messages
    public static final String ERROR_FLIGHT_NOT_FOUND = "Flight not found";
    public static final String ERROR_BOOKING_NOT_FOUND = "Booking not found";
    public static final String ERROR_SEATS_UNAVAILABLE = "Requested seats are not available";
    public static final String ERROR_INVALID_CANCELLATION = "Cancellation not allowed within 24 hours of departure";
    public static final String ERROR_INVALID_INPUT = "Invalid input provided";
}