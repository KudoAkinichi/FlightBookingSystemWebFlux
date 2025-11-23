package com.util;

import com.model.Seat;

import java.util.ArrayList;
import java.util.List;

public final class SeatGenerator {

    private SeatGenerator() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    private static final String[] SEAT_LETTERS = {"A", "B", "C", "D", "E", "F"};
    private static final int BUSINESS_ROWS = 3;
    private static final int ECONOMY_ROWS_START = 4;

    /**
     * Generate seat map for an aircraft
     * @param totalSeats Total number of seats
     * @return List of Seat objects
     */
    public static List<Seat> generateSeats(int totalSeats) {
        List<Seat> seats = new ArrayList<>();

        int rows = (int) Math.ceil(totalSeats / 6.0);
        int seatCount = 0;

        for (int row = 1; row <= rows && seatCount < totalSeats; row++) {
            for (String letter : SEAT_LETTERS) {
                if (seatCount >= totalSeats) break;

                String seatNumber = row + letter;
                String seatClass = determineSeatClass(row);
                String seatType = determineSeatType(letter);
                Double extraCharge = calculateExtraCharge(seatClass, seatType);

                Seat seat = Seat.builder()
                        .seatNumber(seatNumber)
                        .seatClass(seatClass)
                        .isAvailable(true)
                        .seatType(seatType)
                        .extraCharge(extraCharge)
                        .build();

                seats.add(seat);
                seatCount++;
            }
        }

        return seats;
    }

    /**
     * Determine seat class based on row number
     */
    private static String determineSeatClass(int row) {
        if (row <= BUSINESS_ROWS) {
            return Constants.SEAT_BUSINESS;
        }
        return Constants.SEAT_ECONOMY;
    }

    /**
     * Determine seat type based on letter
     */
    private static String determineSeatType(String letter) {
        if (letter.equals("A") || letter.equals("F")) {
            return "WINDOW";
        } else if (letter.equals("C") || letter.equals("D")) {
            return "AISLE";
        } else {
            return "MIDDLE";
        }
    }

    /**
     * Calculate extra charge for seat
     */
    private static Double calculateExtraCharge(String seatClass, String seatType) {
        double charge = 0.0;

        // Business class premium
        if (Constants.SEAT_BUSINESS.equals(seatClass)) {
            charge += 2000.0;
        }

        // Window seat premium
        if ("WINDOW".equals(seatType)) {
            charge += 200.0;
        }

        // Aisle seat premium
        if ("AISLE".equals(seatType)) {
            charge += 100.0;
        }

        return charge;
    }

    /**
     * Check if a seat number is valid
     */
    public static boolean isValidSeatNumber(String seatNumber) {
        if (seatNumber == null || seatNumber.isEmpty()) {
            return false;
        }

        return seatNumber.matches("^\\d+[A-F]$");
    }
}