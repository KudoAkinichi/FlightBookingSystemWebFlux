package com.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CancellationResponse {

    private String pnr;
    private String status;
    private String message;

    private Double refundAmount;
    private String currency;

    private LocalDateTime cancellationDateTime;
    private String cancellationReason;
}