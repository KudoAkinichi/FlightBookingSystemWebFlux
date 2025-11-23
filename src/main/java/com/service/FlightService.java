package com.service;

import com.dto.request.FlightSearchRequest;
import com.dto.response.FlightSearchResponse;
import com.model.Flight;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FlightService {
    Flux<FlightSearchResponse> searchFlights(FlightSearchRequest request);
    Mono<Flight> getFlightById(String flightId);
    Mono<Flight> saveFlight(Flight flight);
    Mono<Flight> updateFlightSeats(String flightId, int seatsToBook);
}