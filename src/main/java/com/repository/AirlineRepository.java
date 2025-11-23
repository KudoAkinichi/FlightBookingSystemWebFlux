package com.repository;

import com.model.Airline;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface AirlineRepository extends ReactiveMongoRepository<Airline, String> {

    Mono<Airline> findByAirlineCode(String airlineCode);

    Flux<Airline> findByIsActive(Boolean isActive);

    Mono<Boolean> existsByAirlineCode(String airlineCode);
}