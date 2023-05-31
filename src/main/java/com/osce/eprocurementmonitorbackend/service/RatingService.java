package com.osce.eprocurementmonitorbackend.service;


import com.osce.eprocurementmonitorbackend.api.dto.RatingAverageDTO;
import com.osce.eprocurementmonitorbackend.api.dto.RatingDTO;
import com.osce.eprocurementmonitorbackend.api.dto.RatingDistributionOutDTO;

import java.util.List;

public interface RatingService {
    RatingDTO createRating(RatingDTO ratingDTO);
    List<RatingDistributionOutDTO> calculateRatingDistributionByEProcurementId(Long eProcurementId);
    List<RatingDTO> findAllRatingsByEProcurementId(Long eProcurementId);
    List<RatingAverageDTO> getRatingAveragesByEProcurementIdAndYear(Long eProcurementId, int year);

    Double calculateTotalRatingAverageByEProcurementId(Long eProcurementId);
}
