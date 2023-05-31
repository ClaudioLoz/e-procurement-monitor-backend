package com.osce.eprocurementmonitorbackend.api.controller.v1;

import com.osce.eprocurementmonitorbackend.api.dto.RatingAverageDTO;
import com.osce.eprocurementmonitorbackend.api.dto.RatingDTO;
import com.osce.eprocurementmonitorbackend.api.dto.RatingDistributionOutDTO;
import com.osce.eprocurementmonitorbackend.service.RatingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/ratings")
public class RatingController {

    private final RatingService ratingService;

    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<RatingDTO> createRating(@Valid @RequestBody RatingDTO ratingDTO) {
        return new ResponseEntity<>(ratingService.createRating(ratingDTO), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<RatingDTO>> findAllRatingsByEProcurementId(@RequestParam Long eProcurementId) {
        return new ResponseEntity<>(ratingService.findAllRatingsByEProcurementId(eProcurementId), HttpStatus.OK);
    }
    @GetMapping("/distribution")
    public ResponseEntity<List<RatingDistributionOutDTO>> getRatingDistributionByEProcurementId(@RequestParam Long eProcurementId) {
        return new ResponseEntity<>(ratingService.calculateRatingDistributionByEProcurementId(eProcurementId), HttpStatus.OK);
    }

    @GetMapping("/averages")
    public ResponseEntity<List<RatingAverageDTO>> getRatingAveragesByEProcurementIdAndYear(@RequestParam Long eProcurementId, @RequestParam int year) {
        return new ResponseEntity<>(ratingService.getRatingAveragesByEProcurementIdAndYear(eProcurementId, year), HttpStatus.OK);
    }


}
