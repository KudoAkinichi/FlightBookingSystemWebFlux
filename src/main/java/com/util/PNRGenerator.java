package com.util;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class PNRGenerator {

    private PNRGenerator() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    private static final String ALPHANUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Generate a unique PNR number
     * Format: PNR + 6 random alphanumeric characters
     * Example: PNR7K3M9A
     */
    public static String generatePNR() {
        StringBuilder pnr = new StringBuilder(Constants.PNR_PREFIX);

        for (int i = 0; i < 6; i++) {
            int index = RANDOM.nextInt(ALPHANUMERIC.length());
            pnr.append(ALPHANUMERIC.charAt(index));
        }

        return pnr.toString();
    }

    /**
     * Generate PNR with timestamp component
     * Format: PNR + YYMMDD + 3 random chars
     * Example: PNR231124A3F
     */
    public static String generatePNRWithTimestamp() {
        StringBuilder pnr = new StringBuilder(Constants.PNR_PREFIX);

        // Add date component (YYMMDD)
        String dateComponent = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyMMdd"));
        pnr.append(dateComponent);

        // Add random component
        for (int i = 0; i < 3; i++) {
            int index = RANDOM.nextInt(ALPHANUMERIC.length());
            pnr.append(ALPHANUMERIC.charAt(index));
        }

        return pnr.toString();
    }

    /**
     * Validate PNR format
     */
    public static boolean isValidPNR(String pnr) {
        if (pnr == null || pnr.isEmpty()) {
            return false;
        }

        return pnr.matches("^" + Constants.PNR_PREFIX + "[A-Z0-9]{6,}$");
    }
}