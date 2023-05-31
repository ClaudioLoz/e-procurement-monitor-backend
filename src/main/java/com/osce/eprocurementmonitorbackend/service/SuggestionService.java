package com.osce.eprocurementmonitorbackend.service;



import com.osce.eprocurementmonitorbackend.api.dto.SuggestionDTO;

import java.util.List;

public interface SuggestionService {
    SuggestionDTO createSuggestion(SuggestionDTO ratingDTO);
    List<SuggestionDTO> findAllSuggestionsByEProcurementId(Long eProcurementId);

}
