package com.service.impl;

import com.dto.request.FlightSearchRequest;
import com.dto.response.FlightSearchResponse;
import com.exception.FlightNotFoundException;
import com.model.Flight;
import com.model.Seat;
import com.repository.FlightRepository;
import com.service.FlightService;
import com.util.DateTimeUtil;
import com.validator.FlightSearchValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FlightServiceImpl implements FlightService {

    private final FlightRepository flightRepository;
    private final FlightSearchValidator searchValidator;

    @Override
    public Flux<FlightSearchResponse> searchFlights(FlightSearchRequest request) {
        log.info("Searching flights from {} to {} on {}",
                request.getOrigin(), request.getDestination(), request.getDepartureDate());

        // Validate search request
        searchValidator.validateSearchRequest(request);

        // Convert LocalDate to LocalDateTime range (start and end of day)
        LocalDateTime startOfDay = request.getDepartureDate().atStartOfDay();
        LocalDateTime endOfDay = request.getDepartureDate().atTime(23, 59, 59);

        return flightRepository
                .findByOriginAndDestinationAndDepartureDateTimeBetween(
                        request.getOrigin().toUpperCase(),
                        request.getDestination().toUpperCase(),
                        startOfDay,
                        endOfDay
                )
                .filter(flight -> flight.getAvailableSeats() >= request.getPassengers())
                .map(this::convertToSearchResponse)
                .switchIfEmpty(Flux.defer(() -> {
                    log.warn("No flights found for search criteria");
                    return Flux.empty();
                }));
    }

    @Override
    public Mono<Flight> getFlightById(String flightId) {
        log.info("Fetching flight with ID: {}", flightId);

        return flightRepository.findById(flightId)
                .switchIfEmpty(Mono.error(new FlightNotFoundException(
                        "Flight with ID " + flightId + " not found"
                )));
    }

    @Override
    public Mono<Flight> saveFlight(Flight flight) {
        log.info("Saving flight: {}", flight.getFlightNumber());

        flight.setCreatedAt(DateTimeUtil.getCurrentTimestamp());
        flight.setUpdatedAt(DateTimeUtil.getCurrentTimestamp());

        return flightRepository.save(flight);
    }

    @Override
    public Mono<Flight> updateFlightSeats(String flightId, int seatsToBook) {
        log.info("Updating seats for flight: {}, booking {} seats", flightId, seatsToBook);

        return flightRepository.findById(flightId)
                .flatMap(flight -> {
                    int newAvailableSeats = flight.getAvailableSeats() - seatsToBook;
                    flight.setAvailableSeats(newAvailableSeats);
                    flight.setUpdatedAt(DateTimeUtil.getCurrentTimestamp());
                    return flightRepository.save(flight);
                })
                .switchIfEmpty(Mono.error(new FlightNotFoundException(flightId)));
    }

    /**
     * Convert Flight entity to FlightSearchResponse DTO
     */
    private FlightSearchResponse convertToSearchResponse(Flight flight) {
        String duration = DateTimeUtil.calculateDuration(
                flight.getDepartureDateTime(),
                flight.getArrivalDateTime()
        );

        return FlightSearchResponse.builder()
                .flightId(flight.getId())
                .flightNumber(flight.getFlightNumber())
                .airlineCode(flight.getAirlineCode())
                .airlineName(flight.getAirlineName())
                .airlineLogoUrl(flight.getAirlineLogoUrl())
                .origin(flight.getOrigin())
                .destination(flight.getDestination())
                .departureDateTime(flight.getDepartureDateTime())
                .arrivalDateTime(flight.getArrivalDateTime())
                .duration(duration)
                .baseFare(flight.getBaseFare())
                .currency(flight.getCurrency())
                .availableSeats(flight.getAvailableSeats())
                .aircraftType(flight.getAircraftType())
                .build();
    }
}