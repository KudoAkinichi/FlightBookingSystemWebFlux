package com.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AirlineRequest {

    @NotBlank(message = "Airline code is required")
    private String airlineCode;

    @NotBlank(message = "Airline name is required")
    private String name;

    private String logoUrl;

    @Email(message = "Invalid email format")
    private String contactEmail;

    private String contactPhone;

    private String website;
}