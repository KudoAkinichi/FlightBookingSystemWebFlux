package com.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PassengerDetails {

    @NotBlank(message = "Passenger name is required")
    private String name;

    @NotBlank(message = "Gender is required")
    private String gender; // MALE, FEMALE, OTHER

    @NotNull(message = "Age is required")
    @Min(value = 0, message = "Age must be positive")
    @Max(value = 120, message = "Invalid age")
    private Integer age;

    private String seatNumber;

    @NotBlank(message = "Meal preference is required")
    private String mealPreference; // VEG, NON_VEG, NONE
}