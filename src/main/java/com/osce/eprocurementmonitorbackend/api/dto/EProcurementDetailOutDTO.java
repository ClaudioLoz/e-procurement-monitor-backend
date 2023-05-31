package com.osce.eprocurementmonitorbackend.api.dto;

import com.osce.eprocurementmonitorbackend.model.EProcurement;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class EProcurementDetailOutDTO {
    private EProcurement eProcurement;
    private List<FileInfoOutDTO> fileInfoOutDTOList;
    private Double totalRatingAverage;
}
