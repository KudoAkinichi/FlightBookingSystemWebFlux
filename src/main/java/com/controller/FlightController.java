package com.controller;

import com.dto.request.FlightSearchRequest;
import com.dto.response.ApiResponse;
import com.dto.response.FlightSearchResponse;
import com.service.FlightService;
import com.util.Constants;
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
public class FlightController {

    private final FlightService flightService;

    /**
     * Search for flights
     * POST /api/v1/flights/search
     */
    @PostMapping("/search")
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

    /**
     * Get flight by ID
     * GET /api/v1/flights/{flightId}
     */
    @GetMapping("/{flightId}")
    public Mono<ResponseEntity<ApiResponse<Object>>> getFlightById(@PathVariable String flightId) {
        log.info("Fetching flight details for ID: {}", flightId);

        return flightService.getFlightById(flightId)
                .map(flight -> ResponseEntity.ok(
                        ApiResponse.success("Flight details retrieved successfully", flight)
                ));
    }

    /**
     * Get seat map for a flight
     * GET /api/v1/flights/{flightId}/seats
     */
    @GetMapping("/{flightId}/seats")
    public Mono<ResponseEntity<ApiResponse<Object>>> getSeatMap(@PathVariable String flightId) {
        log.info("Fetching seat map for flight: {}", flightId);

        return flightService.getFlightById(flightId)
                .map(flight -> ResponseEntity.ok(
                        ApiResponse.success("Seat map retrieved successfully", flight.getSeats())
                ));
    }
}