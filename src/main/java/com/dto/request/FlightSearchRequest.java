package com.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlightSearchRequest {

    @NotBlank(message = "Origin is required")
    private String origin;

    @NotBlank(message = "Destination is required")
    private String destination;

    @NotNull(message = "Departure date is required")
    private LocalDate departureDate;

    private LocalDate returnDate; // Optional for round trip

    @NotNull(message = "Number of passengers is required")
    @Min(value = 1, message = "At least 1 passenger required")
    private Integer passengers;

    private String tripType; // ONEWAY or ROUNDTRIP

    private String cabinClass; // ECONOMY, BUSINESS, FIRST_CLASS
}