package com.osce.eprocurementmonitorbackend.api.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Setter
@Getter
public class RatingDTO {
    @NotNull
    private Long eProcurementId;

    private int stars;

    @NotBlank
    private String justification;

    //out
    private Long id;
    private Date createdDate;
    private String authUserName;

}
