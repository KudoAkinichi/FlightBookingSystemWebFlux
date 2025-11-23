package com.controller;

import com.dto.request.BookingRequest;
import com.dto.response.ApiResponse;
import com.dto.response.BookingResponse;
import com.dto.response.CancellationResponse;
import com.dto.response.TicketResponse;
import com.service.BookingService;
import com.service.TicketService;
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
@RequestMapping(Constants.BOOKINGS_PATH)
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private final BookingService bookingService;
    private final TicketService ticketService;

    /**
     * Create a new booking
     * POST /api/v1/bookings
     */
    @PostMapping
    public Mono<ResponseEntity<ApiResponse<BookingResponse>>> createBooking(
            @Valid @RequestBody BookingRequest request) {

        log.info("Creating booking for flight: {}", request.getFlightId());

        return bookingService.createBooking(request)
                .map(booking -> ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body(ApiResponse.success("Booking created successfully", booking)));
    }

    /**
     * Get booking by PNR
     * GET /api/v1/bookings/pnr/{pnr}
     */
    @GetMapping("/pnr/{pnr}")
    public Mono<ResponseEntity<ApiResponse<TicketResponse>>> getBookingByPnr(
            @PathVariable String pnr) {

        log.info("Fetching booking for PNR: {}", pnr);

        return bookingService.getBookingByPnr(pnr)
                .map(ticket -> ResponseEntity.ok(
                        ApiResponse.success("Booking retrieved successfully", ticket)
                ));
    }

    /**
     * Get booking history by email
     * GET /api/v1/bookings/user/{email}
     */
    @GetMapping("/user/{email}")
    public Mono<ResponseEntity<ApiResponse<List<BookingResponse>>>> getBookingHistory(
            @PathVariable String email) {

        log.info("Fetching booking history for email: {}", email);

        return bookingService.getBookingHistory(email)
                .collectList()
                .map(bookings -> {
                    if (bookings.isEmpty()) {
                        return ResponseEntity
                                .status(HttpStatus.NOT_FOUND)
                                .body(ApiResponse.<List<BookingResponse>>builder()
                                        .success(false)
                                        .message("No bookings found for this email")
                                        .data(bookings)
                                        .build());
                    }
                    return ResponseEntity.ok(
                            ApiResponse.success("Booking history retrieved successfully", bookings)
                    );
                });
    }

    /**
     * Cancel booking
     * DELETE /api/v1/bookings/{pnr}
     */
    @DeleteMapping("/{pnr}")
    public Mono<ResponseEntity<ApiResponse<CancellationResponse>>> cancelBooking(
            @PathVariable String pnr) {

        log.info("Cancelling booking with PNR: {}", pnr);

        return bookingService.cancelBooking(pnr)
                .map(cancellation -> ResponseEntity.ok(
                        ApiResponse.success("Booking cancelled successfully", cancellation)
                ));
    }

    /**
     * Download ticket PDF
     * GET /api/v1/bookings/{pnr}/download
     */
    @GetMapping("/{pnr}/download")
    public Mono<ResponseEntity<byte[]>> downloadTicket(@PathVariable String pnr) {
        log.info("Downloading ticket for PNR: {}", pnr);

        return ticketService.downloadTicketPdf(pnr)
                .map(pdfBytes -> ResponseEntity
                        .ok()
                        .header("Content-Disposition", "attachment; filename=ticket-" + pnr + ".pdf")
                        .header("Content-Type", "application/pdf")
                        .body(pdfBytes));
    }

    /**
     * Resend booking confirmation email
     * POST /api/v1/bookings/{pnr}/resend-email
     */
    @PostMapping("/{pnr}/resend-email")
    public Mono<ResponseEntity<ApiResponse<String>>> resendEmail(@PathVariable String pnr) {
        log.info("Resending email for PNR: {}", pnr);

        return ticketService.resendTicketEmail(pnr)
                .map(message -> ResponseEntity.ok(
                        ApiResponse.success(message, null)
                ));
    }
}