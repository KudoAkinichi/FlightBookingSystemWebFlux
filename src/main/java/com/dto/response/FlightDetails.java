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
public class FlightDetails {

    private String flightNumber;
    private String airlineName;
    private String airlineLogoUrl;

    private String origin;
    private String destination;

    private LocalDateTime departureDateTime;
    private LocalDateTime arrivalDateTime;

    private String duration;
    private String aircraftType;
}