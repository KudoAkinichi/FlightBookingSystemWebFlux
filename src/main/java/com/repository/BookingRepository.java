package com.repository;

import com.model.Booking;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface BookingRepository extends ReactiveMongoRepository<Booking, String> {

    Mono<Booking> findByPnr(String pnr);

    Flux<Booking> findByContactEmail(String email);

    Flux<Booking> findByContactEmailOrderByBookingDateTimeDesc(String email);

    Flux<Booking> findByFlightId(String flightId);

    Mono<Boolean> existsByPnr(String pnr);

    Flux<Booking> findByStatusAndContactEmail(String status, String email);
}