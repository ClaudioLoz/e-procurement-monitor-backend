package com.osce.eprocurementmonitorbackend.api.dto;

import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class RatingDistributionOutDTO {
    private int stars;
    private double percentage;
}
