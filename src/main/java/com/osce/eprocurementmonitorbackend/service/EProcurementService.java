package com.osce.eprocurementmonitorbackend.service;

import com.osce.eprocurementmonitorbackend.api.dto.EProcurementDTO;
import com.osce.eprocurementmonitorbackend.api.dto.EProcurementDetailOutDTO;
import com.osce.eprocurementmonitorbackend.api.dto.EProcurementOutDTO;
import com.osce.eprocurementmonitorbackend.model.EProcurement;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface EProcurementService {

    EProcurementDTO createEProcurement(EProcurement eProcurement, MultipartFile[] files);

    List<EProcurementOutDTO> findAllEProcurements();

    EProcurementDetailOutDTO findEProcurementById(Long id);
}
