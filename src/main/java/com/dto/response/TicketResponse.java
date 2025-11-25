package com.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketResponse {

    private String pnr;
    private String bookingId;
    private String status;

    private FlightDetails flightDetails;
    private BookingDetails bookingDetails;

    private List<PassengerInfo> passengers;

    private FareBreakdown fareBreakdown;
}