package com.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {

    private String bookingId;
    private String pnr;
    private String status;

    private String flightNumber;
    private String route;

    private String contactName;
    private String contactEmail;

    private List<PassengerInfo> passengers;
    private List<String> seatNumbers;

    private Double totalFare;
    private String currency;

    private LocalDateTime journeyDate;
    private LocalDateTime bookingDateTime;

    private String message;
}