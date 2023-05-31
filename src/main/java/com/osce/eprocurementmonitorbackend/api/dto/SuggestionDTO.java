package com.osce.eprocurementmonitorbackend.api.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Setter
@Getter
public class SuggestionDTO {
    @NotNull
    private Long eProcurementId;

    @NotBlank
    private String text;

    //out
    private Long id;
    private Date createdDate;
    private String authUserName;

}
