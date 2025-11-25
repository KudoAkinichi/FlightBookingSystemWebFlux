package com.validator;

import com.dto.request.FlightSearchRequest;
import com.exception.InvalidRequestException;
import com.util.Constants;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class FlightSearchValidator {

    /**
     * Validate flight search request
     */
    public void validateSearchRequest(FlightSearchRequest request) {
        if (request.getOrigin().equalsIgnoreCase(request.getDestination())) {
            throw new InvalidRequestException(
                    "Origin and destination cannot be the same"
            );
        }

        // Validate departure date is not in the past
        if (request.getDepartureDate().isBefore(LocalDate.now())) {
            throw new InvalidRequestException(
                    "Departure date cannot be in the past"
            );
        }

        // Validate return date for round trip
        if (Constants.TRIP_ROUNDTRIP.equalsIgnoreCase(request.getTripType())) {
            if (request.getReturnDate() == null) {
                throw new InvalidRequestException(
                        "Return date is required for round trip"
                );
            }

            if (request.getReturnDate().isBefore(request.getDepartureDate())) {
                throw new InvalidRequestException(
                        "Return date cannot be before departure date"
                );
            }
        }

        // Validate passenger count
        if (request.getPassengers() < 1 || request.getPassengers() > 9) {
            throw new InvalidRequestException(
                    "Number of passengers must be between 1 and 9"
            );
        }

        // Validate trip type
        if (request.getTripType() != null && !Constants.TRIP_ONEWAY.equalsIgnoreCase(request.getTripType()) &&
                    !Constants.TRIP_ROUNDTRIP.equalsIgnoreCase(request.getTripType())) {
                throw new InvalidRequestException(
                        "Invalid trip type. Must be ONEWAY or ROUNDTRIP"
                );
            }


        // Validate cabin class if provided
        if (request.getCabinClass() != null && !Constants.SEAT_ECONOMY.equalsIgnoreCase(request.getCabinClass()) &&
                    !Constants.SEAT_BUSINESS.equalsIgnoreCase(request.getCabinClass()) &&
                    !Constants.SEAT_FIRST_CLASS.equalsIgnoreCase(request.getCabinClass())) {
                throw new InvalidRequestException(
                        "Invalid cabin class. Must be ECONOMY, BUSINESS, or FIRST_CLASS"
                );
            }

    }
}