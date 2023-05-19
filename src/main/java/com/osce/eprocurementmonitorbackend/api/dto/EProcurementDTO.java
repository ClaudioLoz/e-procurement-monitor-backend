package com.osce.eprocurementmonitorbackend.api.dto;

import com.osce.eprocurementmonitorbackend.model.EProcurement;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;


@Getter
@Setter
public class EProcurementDTO {
    private EProcurement eProcurement;
    private HashMap<Long, String> encryptedFiles;
}
