package com.validator;

import com.dto.request.InventoryRequest;
import com.exception.InvalidRequestException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class InventoryValidator {

    /**
     * Validate inventory/schedule request
     */
    public void validateInventoryRequest(InventoryRequest request) {
        // Validate arrival time is after departure time
        if (!request.getArrivalDateTime().isAfter(request.getDepartureDateTime())) {
            throw new InvalidRequestException(
                    "Arrival time must be after departure time"
            );
        }

        // Validate departure time is in the future
        if (request.getDepartureDateTime().isBefore(LocalDateTime.now())) {
            throw new InvalidRequestException(
                    "Departure time cannot be in the past"
            );
        }

        // Validate total seats is reasonable
        if (request.getTotalSeats() < 1 || request.getTotalSeats() > 500) {
            throw new InvalidRequestException(
                    "Total seats must be between 1 and 500"
            );
        }

        // Validate base fare
        if (request.getBaseFare() < 0) {
            throw new InvalidRequestException(
                    "Base fare cannot be negative"
            );
        }

        // Validate flight duration is reasonable (max 24 hours)
        long hours = java.time.Duration.between(
                request.getDepartureDateTime(),
                request.getArrivalDateTime()
        ).toHours();

        if (hours > 24) {
            throw new InvalidRequestException(
                    "Flight duration cannot exceed 24 hours"
            );
        }

        // Validate IATA codes format (3 letters)
        if (!isValidIATACode(request.getOrigin())) {
            throw new InvalidRequestException(
                    "Invalid origin IATA code format. Must be 3 letters"
            );
        }

        if (!isValidIATACode(request.getDestination())) {
            throw new InvalidRequestException(
                    "Invalid destination IATA code format. Must be 3 letters"
            );
        }
    }

    /**
     * Validate IATA code format
     */
    private boolean isValidIATACode(String code) {
        return code != null && code.matches("^[A-Z]{3}$");
    }
}