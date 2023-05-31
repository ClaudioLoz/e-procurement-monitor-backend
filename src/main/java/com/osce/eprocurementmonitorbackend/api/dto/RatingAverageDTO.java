package com.osce.eprocurementmonitorbackend.api.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RatingAverageDTO {
    private int month;
    private double average;

}
