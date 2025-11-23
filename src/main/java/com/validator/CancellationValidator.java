package com.validator;

import com.exception.InvalidCancellationException;
import com.model.Booking;
import com.util.Constants;
import com.util.DateTimeUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CancellationValidator {

    @Value("${app.booking.cancellation-hours:24}")
    private int cancellationHours;

    /**
     * Validate if booking can be cancelled
     */
    public void validateCancellation(Booking booking) {
        // Check if booking is already cancelled
        if (Constants.STATUS_CANCELLED.equals(booking.getStatus())) {
            throw new InvalidCancellationException(
                    "Booking is already cancelled"
            );
        }

        // Check if journey date is in the past
        if (DateTimeUtil.isPast(booking.getJourneyDate())) {
            throw new InvalidCancellationException(
                    "Cannot cancel booking for past journey"
            );
        }

        // Check if cancellation is within allowed time window
        long hoursRemaining = DateTimeUtil.getHoursUntil(booking.getJourneyDate());

        if (!DateTimeUtil.isCancellationAllowed(booking.getJourneyDate(), cancellationHours)) {
            throw new InvalidCancellationException(booking.getPnr(), hoursRemaining);
        }
    }

    /**
     * Calculate refund amount based on cancellation time
     */
    public double calculateRefundAmount(Booking booking) {
        long hoursRemaining = DateTimeUtil.getHoursUntil(booking.getJourneyDate());

        // Full refund if cancelled more than 48 hours before
        if (hoursRemaining >= 48) {
            return booking.getTotalFare();
        }

        // 75% refund if cancelled between 24-48 hours
        if (hoursRemaining >= 24) {
            return booking.getTotalFare() * 0.75;
        }

        // No refund if less than 24 hours
        return 0.0;
    }
}