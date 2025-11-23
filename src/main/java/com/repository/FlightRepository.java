package com.repository;

import com.model.Flight;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Repository
public interface FlightRepository extends ReactiveMongoRepository<Flight, String> {

    Flux<Flight> findByOriginAndDestinationAndDepartureDateTimeBetween(
            String origin,
            String destination,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime
    );

    Mono<Flight> findByFlightNumberAndDepartureDateTime(
            String flightNumber,
            LocalDateTime departureDateTime
    );

    Flux<Flight> findByAirlineCode(String airlineCode);

    Flux<Flight> findByOriginAndDestination(String origin, String destination);

    Mono<Boolean> existsByFlightNumberAndDepartureDateTime(
            String flightNumber,
            LocalDateTime departureDateTime
    );
}