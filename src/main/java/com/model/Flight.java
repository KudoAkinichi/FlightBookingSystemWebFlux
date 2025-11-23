package com.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "flights")
@CompoundIndexes({
        @CompoundIndex(name = "route_date_idx", def = "{'origin': 1, 'destination': 1, 'departureDateTime': 1}"),
        @CompoundIndex(name = "airline_flight_idx", def = "{'airlineCode': 1, 'flightNumber': 1}")
})
public class Flight {

    @Id
    private String id;

    private String flightNumber;
    private String airlineCode;
    private String airlineName;
    private String airlineLogoUrl;

    private String origin;
    private String destination;

    private LocalDateTime departureDateTime;
    private LocalDateTime arrivalDateTime;

    private String aircraftType;
    private Integer totalSeats;
    private Integer availableSeats;

    private Double baseFare;
    private String currency;

    private List<Seat> seats;
    private List<String> daysOfWeek;

    private String status; // SCHEDULED, DEPARTED, CANCELLED

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}