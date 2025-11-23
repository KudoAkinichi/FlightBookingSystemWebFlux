package com.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Passenger {

    private String name;
    private String gender; // MALE, FEMALE, OTHER
    private Integer age;
    private String seatNumber;
    private String mealPreference; // VEG, NON_VEG, NONE
}