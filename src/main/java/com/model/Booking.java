package com.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "bookings")
public class Booking {

    @Id
    private String id;

    @Indexed(unique = true)
    private String pnr;

    private String flightId;
    private String flightNumber;
    private String route; // e.g., "DEL-BOM"

    @Indexed
    private String contactEmail;
    private String contactName;

    private List<Passenger> passengers;
    private List<String> seatNumbers;

    private Double totalFare;
    private String currency;

    private String status; // CONFIRMED, CANCELLED, PENDING

    private LocalDateTime journeyDate;
    private LocalDateTime bookingDateTime;
    private LocalDateTime cancellationDateTime;

    private String cancellationReason;
    private Double refundAmount;
}