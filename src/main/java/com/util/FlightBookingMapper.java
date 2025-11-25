package com.util;

import com.dto.response.BookingDetails;
import com.dto.response.FlightDetails;
import com.model.Booking;
import com.model.Flight;

public class FlightBookingMapper {

    private FlightBookingMapper() {}

    public static FlightDetails mapFlightDetails(Flight flight) {
        return FlightDetails.builder()
                .flightNumber(flight.getFlightNumber())
                .airlineName(flight.getAirlineName())
                .airlineLogoUrl(flight.getAirlineLogoUrl())
                .origin(flight.getOrigin())
                .destination(flight.getDestination())
                .departureDateTime(flight.getDepartureDateTime())
                .arrivalDateTime(flight.getArrivalDateTime())
                .duration(DateTimeUtil.calculateDuration(
                        flight.getDepartureDateTime(),
                        flight.getArrivalDateTime()))
                .aircraftType(flight.getAircraftType())
                .build();
    }

    public static BookingDetails mapBookingDetails(Booking booking) {
        return BookingDetails.builder()
                .contactName(booking.getContactName())
                .contactEmail(booking.getContactEmail())
                .seatNumbers(booking.getSeatNumbers())
                .bookingDateTime(booking.getBookingDateTime())
                .journeyDate(booking.getJourneyDate())
                .build();
    }
}
