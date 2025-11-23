package com.service.impl;

import com.dto.request.InventoryRequest;
import com.dto.response.ApiResponse;
import com.exception.AirlineNotFoundException;
import com.exception.DuplicateResourceException;
import com.model.Flight;
import com.model.Seat;
import com.repository.AirlineRepository;
import com.repository.FlightRepository;
import com.service.InventoryService;
import com.util.DateTimeUtil;
import com.util.SeatGenerator;
import com.validator.InventoryValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImpl implements InventoryService {

    private final FlightRepository flightRepository;
    private final AirlineRepository airlineRepository;
    private final InventoryValidator inventoryValidator;

    public Mono<ApiResponse<String>> addFlightInventory(InventoryRequest request) {
        log.info("Adding flight inventory: {} from {} to {}",
                request.getFlightNumber(), request.getOrigin(), request.getDestination());

        // Validate request
        inventoryValidator.validateInventoryRequest(request);

        // Check if airline exists
        return airlineRepository.findByAirlineCode(request.getAirlineCode())
                .switchIfEmpty(Mono.error(new AirlineNotFoundException(request.getAirlineCode())))
                .flatMap(airline -> {
                    // Check for duplicate flight
                    return flightRepository
                            .existsByFlightNumberAndDepartureDateTime(
                                    request.getFlightNumber(),
                                    request.getDepartureDateTime()
                            )
                            .flatMap(exists -> {
                                if (Boolean.TRUE.equals(exists)) {
                                    return Mono.error(new DuplicateResourceException(
                                            "Flight",
                                            request.getFlightNumber() + " on " + request.getDepartureDateTime()
                                    ));
                                }

                                // Build and save flight
                                Flight flight = buildFlight(request, airline.getName(), airline.getLogoUrl());

                                return flightRepository.save(flight)
                                        .map(savedFlight -> ApiResponse.success(
                                                "Flight inventory added successfully",
                                                savedFlight.getId()
                                        ));
                            });
                })
                .doOnSuccess(response -> log.info("Flight inventory added: {}", response.getData()))
                .doOnError(error -> log.error("Error adding flight inventory: {}", error.getMessage()));
    }

    public Mono<ApiResponse<String>> updateFlightInventory(String inventoryId, InventoryRequest request) {
        log.info("Updating flight inventory: {}", inventoryId);

        // Validate request
        inventoryValidator.validateInventoryRequest(request);

        return flightRepository.findById(inventoryId)
                .flatMap(existingFlight ->
                        airlineRepository.findByAirlineCode(request.getAirlineCode())
                                .switchIfEmpty(Mono.error(new AirlineNotFoundException(request.getAirlineCode())))
                                .flatMap(airline -> {
                                    // Update flight details
                                    updateFlightFromRequest(existingFlight, request, airline.getName(), airline.getLogoUrl());

                                    return flightRepository.save(existingFlight)
                                            .map(updatedFlight -> ApiResponse.success(
                                                    "Flight inventory updated successfully",
                                                    updatedFlight.getId()
                                            ));
                                })
                )
                .doOnSuccess(response -> log.info("Flight inventory updated: {}", inventoryId))
                .doOnError(error -> log.error("Error updating flight inventory: {}", error.getMessage()));
    }

    /**
     * Build Flight entity from request
     */
    private Flight buildFlight(InventoryRequest request, String airlineName, String logoUrl) {
        List<Seat> seats = SeatGenerator.generateSeats(request.getTotalSeats());

        return Flight.builder()
                .flightNumber(request.getFlightNumber())
                .airlineCode(request.getAirlineCode())
                .airlineName(airlineName)
                .airlineLogoUrl(logoUrl)
                .origin(request.getOrigin().toUpperCase())
                .destination(request.getDestination().toUpperCase())
                .departureDateTime(request.getDepartureDateTime())
                .arrivalDateTime(request.getArrivalDateTime())
                .aircraftType(request.getAircraftType())
                .totalSeats(request.getTotalSeats())
                .availableSeats(request.getTotalSeats())
                .baseFare(request.getBaseFare())
                .currency(request.getCurrency() != null ? request.getCurrency() : "INR")
                .seats(seats)
                .daysOfWeek(request.getDaysOfWeek())
                .status("SCHEDULED")
                .createdAt(DateTimeUtil.getCurrentTimestamp())
                .updatedAt(DateTimeUtil.getCurrentTimestamp())
                .build();
    }

    /**
     * Update existing flight from request
     */
    private void updateFlightFromRequest(Flight flight, InventoryRequest request, String airlineName, String logoUrl) {
        flight.setFlightNumber(request.getFlightNumber());
        flight.setAirlineCode(request.getAirlineCode());
        flight.setAirlineName(airlineName);
        flight.setAirlineLogoUrl(logoUrl);
        flight.setOrigin(request.getOrigin().toUpperCase());
        flight.setDestination(request.getDestination().toUpperCase());
        flight.setDepartureDateTime(request.getDepartureDateTime());
        flight.setArrivalDateTime(request.getArrivalDateTime());
        flight.setAircraftType(request.getAircraftType());
        flight.setBaseFare(request.getBaseFare());
        flight.setCurrency(request.getCurrency() != null ? request.getCurrency() : "INR");
        flight.setDaysOfWeek(request.getDaysOfWeek());
        flight.setUpdatedAt(DateTimeUtil.getCurrentTimestamp());
    }
}