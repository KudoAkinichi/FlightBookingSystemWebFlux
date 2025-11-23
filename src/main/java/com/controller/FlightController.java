package com.controller;

import com.dto.request.FlightSearchRequest;
import com.dto.response.ApiResponse;
import com.dto.response.FlightSearchResponse;
import com.service.FlightService;
import com.util.Constants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping(Constants.FLIGHTS_PATH)
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Flight Operations", description = "APIs for searching flights and seat management")
public class FlightController {

    private final FlightService flightService;

    @PostMapping("/search")
    @Operation(summary = "Search flights", description = "Search for available flights based on origin, destination, and date")
    public Mono<ResponseEntity<ApiResponse<List<FlightSearchResponse>>>> searchFlights(
            @Valid @RequestBody FlightSearchRequest request) {

        log.info("Received flight search request: {} to {}", request.getOrigin(), request.getDestination());

        return flightService.searchFlights(request)
                .collectList()
                .map(flights -> {
                    if (flights.isEmpty()) {
                        return ResponseEntity
                                .status(HttpStatus.NOT_FOUND)
                                .body(ApiResponse.<List<FlightSearchResponse>>builder()
                                        .success(false)
                                        .message("No flights found for the given search criteria")
                                        .data(flights)
                                        .build());
                    }
                    return ResponseEntity.ok(
                            ApiResponse.success("Flights retrieved successfully", flights)
                    );
                });
    }

    @GetMapping("/{flightId}")
    @Operation(summary = "Get flight details", description = "Retrieve detailed information about a specific flight")
    public Mono<ResponseEntity<ApiResponse<Object>>> getFlightById(@PathVariable String flightId) {
        log.info("Fetching flight details for ID: {}", flightId);

        return flightService.getFlightById(flightId)
                .map(flight -> ResponseEntity.ok(
                        ApiResponse.success("Flight details retrieved successfully", flight)
                ));
    }

    @GetMapping("/{flightId}/seats")
    @Operation(summary = "Get seat map", description = "Retrieve the seat map for a specific flight")
    public Mono<ResponseEntity<ApiResponse<Object>>> getSeatMap(@PathVariable String flightId) {
        log.info("Fetching seat map for flight: {}", flightId);

        return flightService.getFlightById(flightId)
                .map(flight -> ResponseEntity.ok(
                        ApiResponse.success("Seat map retrieved successfully", flight.getSeats())
                ));
    }
}