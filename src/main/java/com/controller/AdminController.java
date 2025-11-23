package com.controller;

import com.dto.request.AirlineRequest;
import com.dto.request.AirportRequest;
import com.dto.request.InventoryRequest;
import com.dto.response.ApiResponse;
import com.exception.AirlineNotFoundException;
import com.exception.AirportNotFoundException;
import com.exception.DuplicateResourceException;
import com.model.Airline;
import com.model.Airport;
import com.repository.AirlineRepository;
import com.repository.AirportRepository;
import com.service.InventoryService;
import com.util.Constants;
import com.util.DateTimeUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping(Constants.ADMIN_PATH)
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final InventoryService inventoryService;
    private final AirlineRepository airlineRepository;
    private final AirportRepository airportRepository;

    /**
     * Add flight inventory/schedule
     * POST /api/v1/admin/inventory
     */
    @PostMapping("/inventory")
    public Mono<ResponseEntity<ApiResponse<String>>> addFlightInventory(
            @Valid @RequestBody InventoryRequest request) {

        log.info("Admin: Adding flight inventory for {}", request.getFlightNumber());

        return inventoryService.addFlightInventory(request)
                .map(response -> ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body(response));
    }

    /**
     * Update flight inventory
     * PUT /api/v1/admin/inventory/{inventoryId}
     */
    @PutMapping("/inventory/{inventoryId}")
    public Mono<ResponseEntity<ApiResponse<String>>> updateFlightInventory(
            @PathVariable String inventoryId,
            @Valid @RequestBody InventoryRequest request) {

        log.info("Admin: Updating flight inventory {}", inventoryId);

        return inventoryService.updateFlightInventory(inventoryId, request)
                .map(ResponseEntity::ok);
    }

    /**
     * Create or update airline
     * POST /api/v1/admin/airlines
     */
    @PostMapping("/airlines")
    public Mono<ResponseEntity<ApiResponse<Airline>>> createAirline(
            @Valid @RequestBody AirlineRequest request) {

        log.info("Admin: Creating airline {}", request.getAirlineCode());

        return airlineRepository.existsByAirlineCode(request.getAirlineCode())
                .flatMap(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        return Mono.error(new DuplicateResourceException(
                                "Airline", request.getAirlineCode()
                        ));
                    }

                    Airline airline = Airline.builder()
                            .airlineCode(request.getAirlineCode().toUpperCase())
                            .name(request.getName())
                            .logoUrl(request.getLogoUrl())
                            .contactEmail(request.getContactEmail())
                            .contactPhone(request.getContactPhone())
                            .website(request.getWebsite())
                            .isActive(true)
                            .createdAt(DateTimeUtil.getCurrentTimestamp())
                            .updatedAt(DateTimeUtil.getCurrentTimestamp())
                            .build();

                    return airlineRepository.save(airline)
                            .map(saved -> ResponseEntity
                                    .status(HttpStatus.CREATED)
                                    .body(ApiResponse.success("Airline created successfully", saved)));
                });
    }

    /**
     * Update airline
     * PUT /api/v1/admin/airlines/{airlineCode}
     */
    @PutMapping("/airlines/{airlineCode}")
    public Mono<ResponseEntity<ApiResponse<Airline>>> updateAirline(
            @PathVariable String airlineCode,
            @Valid @RequestBody AirlineRequest request) {

        log.info("Admin: Updating airline {}", airlineCode);

        return airlineRepository.findByAirlineCode(airlineCode.toUpperCase())
                .switchIfEmpty(Mono.error(new AirlineNotFoundException(airlineCode)))
                .flatMap(airline -> {
                    airline.setName(request.getName());
                    airline.setLogoUrl(request.getLogoUrl());
                    airline.setContactEmail(request.getContactEmail());
                    airline.setContactPhone(request.getContactPhone());
                    airline.setWebsite(request.getWebsite());
                    airline.setUpdatedAt(DateTimeUtil.getCurrentTimestamp());

                    return airlineRepository.save(airline)
                            .map(updated -> ResponseEntity.ok(
                                    ApiResponse.success("Airline updated successfully", updated)
                            ));
                });
    }

    /**
     * Get all airlines
     * GET /api/v1/admin/airlines
     */
    @GetMapping("/airlines")
    public Mono<ResponseEntity<ApiResponse<List<Airline>>>> getAllAirlines() {
        log.info("Admin: Fetching all airlines");

        return airlineRepository.findAll()
                .collectList()
                .map(airlines -> ResponseEntity.ok(
                        ApiResponse.success("Airlines retrieved successfully", airlines)
                ));
    }

    /**
     * Get airline by code
     * GET /api/v1/admin/airlines/{airlineCode}
     */
    @GetMapping("/airlines/{airlineCode}")
    public Mono<ResponseEntity<ApiResponse<Airline>>> getAirlineByCode(
            @PathVariable String airlineCode) {

        log.info("Admin: Fetching airline {}", airlineCode);

        return airlineRepository.findByAirlineCode(airlineCode.toUpperCase())
                .map(airline -> ResponseEntity.ok(
                        ApiResponse.success("Airline retrieved successfully", airline)
                ))
                .switchIfEmpty(Mono.error(new AirlineNotFoundException(airlineCode)));
    }

    /**
     * Create airport
     * POST /api/v1/admin/airports
     */
    @PostMapping("/airports")
    public Mono<ResponseEntity<ApiResponse<Airport>>> createAirport(
            @Valid @RequestBody AirportRequest request) {

        log.info("Admin: Creating airport {}", request.getIataCode());

        return airportRepository.existsByIataCode(request.getIataCode())
                .flatMap(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        return Mono.error(new DuplicateResourceException(
                                "Airport", request.getIataCode()
                        ));
                    }

                    Airport airport = Airport.builder()
                            .iataCode(request.getIataCode().toUpperCase())
                            .name(request.getName())
                            .city(request.getCity())
                            .country(request.getCountry())
                            .timezone(request.getTimezone() != null ?
                                    request.getTimezone() : Constants.TIMEZONE_IST)
                            .isActive(true)
                            .createdAt(DateTimeUtil.getCurrentTimestamp())
                            .updatedAt(DateTimeUtil.getCurrentTimestamp())
                            .build();

                    return airportRepository.save(airport)
                            .map(saved -> ResponseEntity
                                    .status(HttpStatus.CREATED)
                                    .body(ApiResponse.success("Airport created successfully", saved)));
                });
    }

    /**
     * Update airport
     * PUT /api/v1/admin/airports/{iataCode}
     */
    @PutMapping("/airports/{iataCode}")
    public Mono<ResponseEntity<ApiResponse<Airport>>> updateAirport(
            @PathVariable String iataCode,
            @Valid @RequestBody AirportRequest request) {

        log.info("Admin: Updating airport {}", iataCode);

        return airportRepository.findByIataCode(iataCode.toUpperCase())
                .switchIfEmpty(Mono.error(new AirportNotFoundException(iataCode)))
                .flatMap(airport -> {
                    airport.setName(request.getName());
                    airport.setCity(request.getCity());
                    airport.setCountry(request.getCountry());
                    airport.setTimezone(request.getTimezone());
                    airport.setUpdatedAt(DateTimeUtil.getCurrentTimestamp());

                    return airportRepository.save(airport)
                            .map(updated -> ResponseEntity.ok(
                                    ApiResponse.success("Airport updated successfully", updated)
                            ));
                });
    }

    /**
     * Get all airports
     * GET /api/v1/admin/airports
     */
    @GetMapping("/airports")
    public Mono<ResponseEntity<ApiResponse<List<Airport>>>> getAllAirports() {
        log.info("Admin: Fetching all airports");

        return airportRepository.findAll()
                .collectList()
                .map(airports -> ResponseEntity.ok(
                        ApiResponse.success("Airports retrieved successfully", airports)
                ));
    }

    /**
     * Get airport by IATA code
     * GET /api/v1/admin/airports/{iataCode}
     */
    @GetMapping("/airports/{iataCode}")
    public Mono<ResponseEntity<ApiResponse<Airport>>> getAirportByCode(
            @PathVariable String iataCode) {

        log.info("Admin: Fetching airport {}", iataCode);

        return airportRepository.findByIataCode(iataCode.toUpperCase())
                .map(airport -> ResponseEntity.ok(
                        ApiResponse.success("Airport retrieved successfully", airport)
                ))
                .switchIfEmpty(Mono.error(new AirportNotFoundException(iataCode)));
    }
}