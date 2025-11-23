package com.service.impl;

import com.dto.response.*;
import com.exception.BookingNotFoundException;
import com.model.Booking;
import com.model.Flight;
import com.repository.BookingRepository;
import com.repository.FlightRepository;
import com.service.TicketService;
import com.util.DateTimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TicketServiceImpl implements TicketService {

    private final BookingRepository bookingRepository;
    private final FlightRepository flightRepository;

    @Override
    public Mono<TicketResponse> getTicketByPnr(String pnr) {
        log.info("Fetching ticket for PNR: {}", pnr);

        return bookingRepository.findByPnr(pnr.toUpperCase())
                .switchIfEmpty(Mono.error(new BookingNotFoundException(pnr)))
                .flatMap(booking ->
                        flightRepository.findById(booking.getFlightId())
                                .map(flight -> buildTicketResponse(booking, flight))
                )
                .doOnSuccess(ticket -> log.info("Ticket fetched successfully for PNR: {}", pnr))
                .doOnError(error -> log.error("Error fetching ticket: {}", error.getMessage()));
    }

    @Override
    public Mono<byte[]> downloadTicketPdf(String pnr) {
        log.info("Generating PDF for PNR: {}", pnr);

        // TODO: Implement PDF generation using a library like iText or Apache PDFBox
        // For now, returning a placeholder
        return getTicketByPnr(pnr)
                .map(ticket -> {
                    String pdfContent = generatePdfContent(ticket);
                    return pdfContent.getBytes();
                })
                .doOnSuccess(pdf -> log.info("PDF generated for PNR: {}", pnr));
    }

    @Override
    public Mono<String> resendTicketEmail(String pnr) {
        log.info("Resending ticket email for PNR: {}", pnr);

        return getTicketByPnr(pnr)
                .flatMap(ticket -> {
                    // TODO: Implement email sending using Spring Mail
                    // For now, just simulating the email send
                    log.info("Email sent to: {}", ticket.getBookingDetails().getContactEmail());
                    return Mono.just("Ticket email sent successfully to " +
                            ticket.getBookingDetails().getContactEmail());
                })
                .doOnError(error -> log.error("Error sending email: {}", error.getMessage()));
    }

    /**
     * Build TicketResponse from Booking and Flight
     */
    private TicketResponse buildTicketResponse(Booking booking, Flight flight) {
        FlightDetails flightDetails = FlightDetails.builder()
                .flightNumber(flight.getFlightNumber())
                .airlineName(flight.getAirlineName())
                .airlineLogoUrl(flight.getAirlineLogoUrl())
                .origin(flight.getOrigin())
                .destination(flight.getDestination())
                .departureDateTime(flight.getDepartureDateTime())
                .arrivalDateTime(flight.getArrivalDateTime())
                .duration(DateTimeUtil.calculateDuration(
                        flight.getDepartureDateTime(),
                        flight.getArrivalDateTime()))
                .aircraftType(flight.getAircraftType())
                .build();

        BookingDetails bookingDetails = BookingDetails.builder()
                .contactName(booking.getContactName())
                .contactEmail(booking.getContactEmail())
                .seatNumbers(booking.getSeatNumbers())
                .bookingDateTime(booking.getBookingDateTime())
                .journeyDate(booking.getJourneyDate())
                .build();

        List<PassengerInfo> passengers = booking.getPassengers().stream()
                .map(p -> PassengerInfo.builder()
                        .name(p.getName())
                        .gender(p.getGender())
                        .age(p.getAge())
                        .seatNumber(p.getSeatNumber())
                        .mealPreference(p.getMealPreference())
                        .build())
                .collect(Collectors.toList());

        double baseFare = flight.getBaseFare() * booking.getPassengers().size();
        double seatCharges = booking.getTotalFare() - baseFare;

        FareBreakdown fareBreakdown = FareBreakdown.builder()
                .baseFare(baseFare)
                .taxes(0.0)
                .seatCharges(seatCharges)
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
     * Generate PDF content (placeholder implementation)
     */
    private String generatePdfContent(TicketResponse ticket) {
        return String.format(
                "E-TICKET\n\n" +
                        "PNR: %s\n" +
                        "Status: %s\n\n" +
                        "Flight Details:\n" +
                        "Flight Number: %s\n" +
                        "Airline: %s\n" +
                        "Route: %s to %s\n" +
                        "Departure: %s\n" +
                        "Arrival: %s\n\n" +
                        "Passenger Details:\n%s\n\n" +
                        "Total Fare: %s %.2f",
                ticket.getPnr(),
                ticket.getStatus(),
                ticket.getFlightDetails().getFlightNumber(),
                ticket.getFlightDetails().getAirlineName(),
                ticket.getFlightDetails().getOrigin(),
                ticket.getFlightDetails().getDestination(),
                ticket.getFlightDetails().getDepartureDateTime(),
                ticket.getFlightDetails().getArrivalDateTime(),
                ticket.getPassengers().stream()
                        .map(p -> p.getName() + " - Seat: " + p.getSeatNumber())
                        .collect(Collectors.joining("\n")),
                ticket.getFareBreakdown().getCurrency(),
                ticket.getFareBreakdown().getTotalFare()
        );
    }
}