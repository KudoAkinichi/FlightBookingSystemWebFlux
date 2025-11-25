package com.service.impl;

import com.dto.request.BookingRequest;
import com.dto.response.*;
import com.exception.BookingNotFoundException;
import com.model.Booking;
import com.model.Flight;
import com.model.Passenger;
import com.model.Seat;
import com.repository.BookingRepository;
import com.repository.FlightRepository;
import com.service.BookingService;
import com.util.Constants;
import com.util.DateTimeUtil;
import com.util.FlightBookingMapper;
import com.util.PNRGenerator;
import com.validator.BookingValidator;
import com.validator.CancellationValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final FlightRepository flightRepository;
    private final BookingValidator bookingValidator;
    private final CancellationValidator cancellationValidator;

    @Override
    public Mono<BookingResponse> createBooking(BookingRequest request) {
        log.info("Creating booking for flight: {}", request.getFlightId());

        return flightRepository.findById(request.getFlightId())
                .flatMap(flight -> {
                    // Validate booking request
                    bookingValidator.validateBookingRequest(request, flight);

                    // Create booking
                    Booking booking = buildBooking(request, flight);

                    // Update seat availability
                    updateSeats(flight, request.getSeatNumbers());

                    // Save flight and booking
                    return flightRepository.save(flight)
                            .then(bookingRepository.save(booking))
                            .map(this::convertToBookingResponse);
                })
                .doOnSuccess(response -> log.info("Booking created successfully with PNR: {}", response.getPnr()))
                .doOnError(error -> log.error("Error creating booking: {}", error.getMessage()));
    }

    @Override
    public Mono<TicketResponse> getBookingByPnr(String pnr) {
        log.info("Fetching booking with PNR: {}", pnr);

        return bookingRepository.findByPnr(pnr.toUpperCase())
                .switchIfEmpty(Mono.error(new BookingNotFoundException(pnr)))
                .flatMap(booking ->
                        flightRepository.findById(booking.getFlightId())
                                .map(flight -> convertToTicketResponse(booking, flight))
                );
    }

    @Override
    public Flux<BookingResponse> getBookingHistory(String email) {
        log.info("Fetching booking history for email: {}", email);

        return bookingRepository.findByContactEmailOrderByBookingDateTimeDesc(email.toLowerCase())
                .flatMap(booking ->
                        flightRepository.findById(booking.getFlightId())
                                .map(flight -> convertToBookingResponse(booking))
                )
                .switchIfEmpty(Flux.empty());
    }

    @Override
    public Mono<CancellationResponse> cancelBooking(String pnr) {
        log.info("Cancelling booking with PNR: {}", pnr);

        return bookingRepository.findByPnr(pnr.toUpperCase())
                .switchIfEmpty(Mono.error(new BookingNotFoundException(pnr)))
                .flatMap(booking -> {
                    // Validate cancellation
                    cancellationValidator.validateCancellation(booking);

                    // Calculate refund
                    double refundAmount = cancellationValidator.calculateRefundAmount(booking);

                    // Update booking status
                    booking.setStatus(Constants.STATUS_CANCELLED);
                    booking.setCancellationDateTime(DateTimeUtil.getCurrentTimestamp());
                    booking.setCancellationReason("Cancelled by user");
                    booking.setRefundAmount(refundAmount);

                    // Release seats
                    return flightRepository.findById(booking.getFlightId())
                            .flatMap(flight -> {
                                releaseSeats(flight, booking.getSeatNumbers());
                                return flightRepository.save(flight);
                            })
                            .then(bookingRepository.save(booking))
                            .map(cancelledBooking -> buildCancellationResponse(cancelledBooking, refundAmount));
                })
                .doOnSuccess(response -> log.info("Booking cancelled successfully: {}", pnr))
                .doOnError(error -> log.error("Error cancelling booking: {}", error.getMessage()));
    }

    /**
     * Build Booking entity from request
     */
    private Booking buildBooking(BookingRequest request, Flight flight) {
        String pnr = generateUniquePNR();

        List<Passenger> passengers = request.getPassengers().stream()
                .map(passengerDto -> Passenger.builder()
                        .name(passengerDto.getName())
                        .gender(passengerDto.getGender())
                        .age(passengerDto.getAge())
                        .seatNumber(passengerDto.getSeatNumber())
                        .mealPreference(passengerDto.getMealPreference())
                        .build())
                .toList();   // SonarQube compliant


        double totalFare = calculateTotalFare(flight, request.getSeatNumbers());

        return Booking.builder()
                .pnr(pnr)
                .flightId(flight.getId())
                .flightNumber(flight.getFlightNumber())
                .route(flight.getOrigin() + "-" + flight.getDestination())
                .contactEmail(request.getContactEmail().toLowerCase())
                .contactName(request.getContactName())
                .passengers(passengers)
                .seatNumbers(request.getSeatNumbers())
                .totalFare(totalFare)
                .currency(flight.getCurrency())
                .status(Constants.STATUS_CONFIRMED)
                .journeyDate(flight.getDepartureDateTime())
                .bookingDateTime(DateTimeUtil.getCurrentTimestamp())
                .build();
    }

    /**
     * Generate unique PNR
     */
    private String generateUniquePNR() {
        String pnr = PNRGenerator.generatePNR();

        // Simple check - if exists, try again (blocking for simplicity)
        int attempts = 0;
        while (Boolean.TRUE.equals(bookingRepository.existsByPnr(pnr).block()) && attempts < 10) {
            pnr = PNRGenerator.generatePNR();
            attempts++;
        }

        return pnr;
    }

    /**
     * Calculate total fare including seat charges
     */
    private double calculateTotalFare(Flight flight, List<String> seatNumbers) {
        double baseFare = flight.getBaseFare() * seatNumbers.size();

        double seatCharges = flight.getSeats().stream()
                .filter(seat -> seatNumbers.contains(seat.getSeatNumber()))
                .mapToDouble(Seat::getExtraCharge)
                .sum();

        return baseFare + seatCharges;
    }

    /**
     * Update seat availability
     */
    private void updateSeats(Flight flight, List<String> seatNumbers) {
        flight.getSeats().forEach(seat -> {
            if (seatNumbers.contains(seat.getSeatNumber())) {
                seat.setIsAvailable(false);
            }
        });

        flight.setAvailableSeats(flight.getAvailableSeats() - seatNumbers.size());
        flight.setUpdatedAt(DateTimeUtil.getCurrentTimestamp());
    }

    /**
     * Release seats after cancellation
     */
    private void releaseSeats(Flight flight, List<String> seatNumbers) {
        flight.getSeats().forEach(seat -> {
            if (seatNumbers.contains(seat.getSeatNumber())) {
                seat.setIsAvailable(true);
            }
        });

        flight.setAvailableSeats(flight.getAvailableSeats() + seatNumbers.size());
        flight.setUpdatedAt(DateTimeUtil.getCurrentTimestamp());
    }

    /**
     * Convert to BookingResponse
     */
    private BookingResponse convertToBookingResponse(Booking booking) {
        List<PassengerInfo> passengerInfos = booking.getPassengers().stream()
                .map(p -> PassengerInfo.builder()
                        .name(p.getName())
                        .gender(p.getGender())
                        .age(p.getAge())
                        .seatNumber(p.getSeatNumber())
                        .mealPreference(p.getMealPreference())
                        .build())
                .toList();

        return BookingResponse.builder()
                .bookingId(booking.getId())
                .pnr(booking.getPnr())
                .status(booking.getStatus())
                .flightNumber(booking.getFlightNumber())
                .route(booking.getRoute())
                .contactName(booking.getContactName())
                .contactEmail(booking.getContactEmail())
                .passengers(passengerInfos)
                .seatNumbers(booking.getSeatNumbers())
                .totalFare(booking.getTotalFare())
                .currency(booking.getCurrency())
                .journeyDate(booking.getJourneyDate())
                .bookingDateTime(booking.getBookingDateTime())
                .message("Booking confirmed successfully")
                .build();
    }

    /**
     * Convert to TicketResponse
     */
    private TicketResponse convertToTicketResponse(Booking booking, Flight flight) {
        FlightDetails flightDetails = FlightBookingMapper.mapFlightDetails(flight);
        BookingDetails bookingDetails = FlightBookingMapper.mapBookingDetails(booking);


        List<PassengerInfo> passengers = booking.getPassengers().stream()
                .map(p -> PassengerInfo.builder()
                        .name(p.getName())
                        .gender(p.getGender())
                        .age(p.getAge())
                        .seatNumber(p.getSeatNumber())
                        .mealPreference(p.getMealPreference())
                        .build())
                .toList();

        FareBreakdown fareBreakdown = FareBreakdown.builder()
                .baseFare(flight.getBaseFare() * booking.getPassengers().size())
                .taxes(0.0)
                .seatCharges(booking.getTotalFare() - (flight.getBaseFare() * booking.getPassengers().size()))
                .mealCharges(0.0)
                .totalFare(booking.getTotalFare())
                .currency(booking.getCurrency())
                .build();

        return TicketResponse.builder()
                .pnr(booking.getPnr())
                .bookingId(booking.getId())
                .status(booking.getStatus())
                .flightDetails(flightDetails)
                .bookingDetails(bookingDetails)
                .passengers(passengers)
                .fareBreakdown(fareBreakdown)
                .build();
    }

    /**
     * Build CancellationResponse
     */
    private CancellationResponse buildCancellationResponse(Booking booking, double refundAmount) {
        return CancellationResponse.builder()
                .pnr(booking.getPnr())
                .status(Constants.STATUS_CANCELLED)
                .message("Booking cancelled successfully")
                .refundAmount(refundAmount)
                .currency(booking.getCurrency())
                .cancellationDateTime(booking.getCancellationDateTime())
                .cancellationReason(booking.getCancellationReason())
                .build();
    }
}