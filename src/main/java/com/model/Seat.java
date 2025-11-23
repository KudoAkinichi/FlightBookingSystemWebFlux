package com.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Seat {

    private String seatNumber;
    private String seatClass; // ECONOMY, BUSINESS, FIRST_CLASS
    private Boolean isAvailable;
    private String seatType; // WINDOW, AISLE, MIDDLE
    private Double extraCharge;
}