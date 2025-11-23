package com.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingDetails {

    private String contactName;
    private String contactEmail;

    private List<String> seatNumbers;

    private LocalDateTime bookingDateTime;
    private LocalDateTime journeyDate;
}