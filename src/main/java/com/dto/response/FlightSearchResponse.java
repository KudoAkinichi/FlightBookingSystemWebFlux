package com.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlightSearchResponse {

    private String flightId;
    private String flightNumber;
    private String airlineCode;
    private String airlineName;
    private String airlineLogoUrl;

    private String origin;
    private String destination;

    private LocalDateTime departureDateTime;
    private LocalDateTime arrivalDateTime;

    private String duration; // Calculated duration

    private Double baseFare;
    private String currency;

    private Integer availableSeats;
    private String aircraftType;
}