package com.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class InventoryRequest {

    @NotBlank(message = "Airline code is required")
    private String airlineCode;

    @NotBlank(message = "Flight number is required")
    private String flightNumber;

    @NotBlank(message = "Origin is required")
    private String origin;

    @NotBlank(message = "Destination is required")
    private String destination;

    @NotNull(message = "Departure date/time is required")
    private LocalDateTime departureDateTime;

    @NotNull(message = "Arrival date/time is required")
    private LocalDateTime arrivalDateTime;

    @NotBlank(message = "Aircraft type is required")
    private String aircraftType;

    @NotNull(message = "Total seats is required")
    @Min(value = 1, message = "At least 1 seat required")
    private Integer totalSeats;

    @NotNull(message = "Base fare is required")
    @Min(value = 0, message = "Fare must be non-negative")
    private Double baseFare;

    private List<String> daysOfWeek; // For recurring schedules

    private String currency;
}