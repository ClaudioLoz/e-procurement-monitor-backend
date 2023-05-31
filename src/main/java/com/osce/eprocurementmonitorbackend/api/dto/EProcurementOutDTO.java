package com.osce.eprocurementmonitorbackend.api.dto;

import com.osce.eprocurementmonitorbackend.enums.ProcurementObject;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class EProcurementOutDTO {
    private Long id;
    private String contractingEntityName;
    private String contractingEntityRuc;
    private String contractorName;
    private String contractorRuc;
    private ProcurementObject procurementObject;
    private Double amount;
    private LocalDate contractStartDate;
    private LocalDate contractEndDate;
    private String department;
    private String username;
    private Double totalRatingAverage;
    private Integer totalCommentCount;
}
