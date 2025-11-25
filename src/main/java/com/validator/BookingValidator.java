package com.validator;

import com.dto.request.BookingRequest;
import com.exception.InvalidRequestException;
import com.model.Flight;
import com.model.Seat;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class BookingValidator {

    /**
     * Validate booking request against flight data
     */
    public void validateBookingRequest(BookingRequest request, Flight flight) {
        // Check passenger count matches seat count
        if (request.getPassengers().size() != request.getSeatNumbers().size()) {
            throw new InvalidRequestException(
                    "Number of passengers must match number of seats selected"
            );
        }

        // Check for duplicate seat selections
        Set<String> uniqueSeats = new HashSet<>(request.getSeatNumbers());
        if (uniqueSeats.size() != request.getSeatNumbers().size()) {
            throw new InvalidRequestException(
                    "Duplicate seat selections are not allowed"
            );
        }

        // Validate seat availability
        validateSeatAvailability(request.getSeatNumbers(), flight.getSeats());

        // Check if enough seats available
        if (flight.getAvailableSeats() < request.getPassengers().size()) {
            throw new InvalidRequestException(
                    String.format("Only %d seats available, but requested %d",
                            flight.getAvailableSeats(),
                            request.getPassengers().size())
            );
        }

        // Validate passenger details
        request.getPassengers().forEach(passenger -> {
            if (passenger.getAge() < 0 || passenger.getAge() > 120) {
                throw new InvalidRequestException(
                        "Invalid age for passenger: " + passenger.getName()
                );
            }
        });
    }

    /**
     * Validate seat availability
     */
    private void validateSeatAvailability(List<String> requestedSeats, List<Seat> flightSeats) {
        List<String> unavailableSeats = requestedSeats.stream()
                .filter(seatNumber -> !isSeatAvailable(seatNumber, flightSeats))
                .toList();

        if (!unavailableSeats.isEmpty()) {
            throw new InvalidRequestException(
                    "The following seats are not available: " + String.join(", ", unavailableSeats)
            );
        }
    }

    /**
     * Check if a specific seat is available
     */
    private boolean isSeatAvailable(String seatNumber, List<Seat> flightSeats) {
        return flightSeats.stream()
                .anyMatch(seat -> seat.getSeatNumber().equals(seatNumber) && seat.getIsAvailable());
    }
}