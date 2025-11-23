package com.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FareBreakdown {

    private Double baseFare;
    private Double taxes;
    private Double seatCharges;
    private Double mealCharges;
    private Double totalFare;
    private String currency;
}