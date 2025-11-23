package com.repository;

import com.model.Airport;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface AirportRepository extends ReactiveMongoRepository<Airport, String> {

    Mono<Airport> findByIataCode(String iataCode);

    Flux<Airport> findByCity(String city);

    Flux<Airport> findByCountry(String country);

    Flux<Airport> findByIsActive(Boolean isActive);

    Mono<Boolean> existsByIataCode(String iataCode);
}