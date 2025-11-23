package com.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public final class DateTimeUtil {

    private DateTimeUtil() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern(Constants.DATE_FORMAT);

    private static final DateTimeFormatter DATETIME_FORMATTER =
            DateTimeFormatter.ofPattern(Constants.DATETIME_FORMAT);

    /**
     * Calculate duration between two LocalDateTime objects
     */
    public static String calculateDuration(LocalDateTime start, LocalDateTime end) {
        Duration duration = Duration.between(start, end);
        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;

        return String.format("%dh %dm", hours, minutes);
    }

    /**
     * Check if a datetime is in the past
     */
    public static boolean isPast(LocalDateTime dateTime) {
        return dateTime.isBefore(LocalDateTime.now());
    }

    /**
     * Check if a datetime is in the future
     */
    public static boolean isFuture(LocalDateTime dateTime) {
        return dateTime.isAfter(LocalDateTime.now());
    }

    /**
     * Get hours remaining until a specific datetime
     */
    public static long getHoursUntil(LocalDateTime dateTime) {
        return Duration.between(LocalDateTime.now(), dateTime).toHours();
    }

    /**
     * Check if cancellation is allowed (24 hours before departure)
     */
    public static boolean isCancellationAllowed(LocalDateTime departureDateTime, int hoursRequired) {
        long hoursRemaining = getHoursUntil(departureDateTime);
        return hoursRemaining >= hoursRequired;
    }

    /**
     * Convert LocalDateTime to specific timezone
     */
    public static ZonedDateTime convertToTimezone(LocalDateTime dateTime, String timezone) {
        ZoneId zoneId = ZoneId.of(timezone);
        return dateTime.atZone(ZoneId.systemDefault()).withZoneSameInstant(zoneId);
    }

    /**
     * Format LocalDateTime to string
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime.format(DATETIME_FORMATTER);
    }

    /**
     * Get current timestamp in IST
     */
    public static LocalDateTime getCurrentTimestamp() {
        return LocalDateTime.now(ZoneId.of(Constants.TIMEZONE_IST));
    }

    /**
     * Check if datetime falls within a range
     */
    public static boolean isWithinRange(LocalDateTime dateTime, LocalDateTime start, LocalDateTime end) {
        return !dateTime.isBefore(start) && !dateTime.isAfter(end);
    }

    /**
     * Get start and end of day for a given datetime
     */
    public static LocalDateTime getStartOfDay(LocalDateTime dateTime) {
        return dateTime.toLocalDate().atStartOfDay();
    }

    public static LocalDateTime getEndOfDay(LocalDateTime dateTime) {
        return dateTime.toLocalDate().atTime(23, 59, 59);
    }
}