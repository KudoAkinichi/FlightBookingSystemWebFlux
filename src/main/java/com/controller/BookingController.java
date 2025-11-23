package com.controller;

import com.dto.request.BookingRequest;
import com.dto.response.ApiResponse;
import com.dto.response.BookingResponse;
import com.dto.response.CancellationResponse;
import com.dto.response.TicketResponse;
import com.service.BookingService;
import com.service.TicketService;
import com.util.Constants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Booking Operations", description = "APIs for flight booking management")
public class BookingController {

    private final BookingService bookingService;
    private final TicketService ticketService;

    @PostMapping
    @Operation(summary = "Create booking", description = "Create a new flight booking")
    public Mono<ResponseEntity<ApiResponse<BookingResponse>>> createBooking(
            @Valid @RequestBody BookingRequest request) {

        log.info("Creating booking for flight: {}", request.getFlightId());

        return bookingService.createBooking(request)
                .map(booking -> ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body(ApiResponse.success("Booking created successfully", booking)));
    }

    @GetMapping("/pnr/{pnr}")
    @Operation(summary = "Get booking by PNR", description = "Retrieve booking details using PNR number")
    public Mono<ResponseEntity<ApiResponse<TicketResponse>>> getBookingByPnr(
            @PathVariable String pnr) {

        log.info("Fetching booking for PNR: {}", pnr);

        return bookingService.getBookingByPnr(pnr)
                .map(ticket -> ResponseEntity.ok(
                        ApiResponse.success("Booking retrieved successfully", ticket)
                ));
    }

    @GetMapping("/user/{email}")
    @Operation(summary = "Get booking history", description = "Retrieve all bookings for a user by email")
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

    @DeleteMapping("/{pnr}")
    @Operation(summary = "Cancel booking", description = "Cancel a booking using PNR number")
    public Mono<ResponseEntity<ApiResponse<CancellationResponse>>> cancelBooking(
            @PathVariable String pnr) {

        log.info("Cancelling booking with PNR: {}", pnr);

        return bookingService.cancelBooking(pnr)
                .map(cancellation -> ResponseEntity.ok(
                        ApiResponse.success("Booking cancelled successfully", cancellation)
                ));
    }

    @GetMapping("/{pnr}/download")
    @Operation(summary = "Download ticket", description = "Download ticket PDF for a booking")
    public Mono<ResponseEntity<byte[]>> downloadTicket(@PathVariable String pnr) {
        log.info("Downloading ticket for PNR: {}", pnr);

        return ticketService.downloadTicketPdf(pnr)
                .map(pdfBytes -> ResponseEntity
                        .ok()
                        .header("Content-Disposition", "attachment; filename=ticket-" + pnr + ".pdf")
                        .header("Content-Type", "application/pdf")
                        .body(pdfBytes));
    }

    @PostMapping("/{pnr}/resend-email")
    @Operation(summary = "Resend booking email", description = "Resend booking confirmation email")
    public Mono<ResponseEntity<ApiResponse<String>>> resendEmail(@PathVariable String pnr) {
        log.info("Resending email for PNR: {}", pnr);

        return ticketService.resendTicketEmail(pnr)
                .map(message -> ResponseEntity.ok(
                        ApiResponse.success(message, null)
                ));
    }
}