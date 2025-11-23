package com.service;

import com.dto.request.BookingRequest;
import com.dto.response.BookingResponse;
import com.dto.response.CancellationResponse;
import com.dto.response.TicketResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BookingService {
    Mono<BookingResponse> createBooking(BookingRequest request);
    Mono<TicketResponse> getBookingByPnr(String pnr);
    Flux<BookingResponse> getBookingHistory(String email);
    Mono<CancellationResponse> cancelBooking(String pnr);
}