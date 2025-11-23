package com.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PassengerInfo {

    private String name;
    private String gender;
    private Integer age;
    private String seatNumber;
    private String mealPreference;
}