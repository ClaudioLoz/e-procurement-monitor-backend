package com.osce.eprocurementmonitorbackend.service.impl;

import com.osce.eprocurementmonitorbackend.api.dto.RatingAverageDTO;
import com.osce.eprocurementmonitorbackend.api.dto.RatingDTO;
import com.osce.eprocurementmonitorbackend.api.dto.RatingDistributionOutDTO;
import com.osce.eprocurementmonitorbackend.model.AuthUser;
import com.osce.eprocurementmonitorbackend.model.EProcurement;
import com.osce.eprocurementmonitorbackend.model.Rating;
import com.osce.eprocurementmonitorbackend.repository.AuthUserRepository;
import com.osce.eprocurementmonitorbackend.repository.EProcurementRepository;
import com.osce.eprocurementmonitorbackend.repository.RatingRepository;
import com.osce.eprocurementmonitorbackend.security.services.UserDetailsImpl;
import com.osce.eprocurementmonitorbackend.service.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RatingServiceImpl implements RatingService {

    private final RatingRepository ratingRepository;

    @Autowired
    private EProcurementRepository eProcurementRepository;
    @Autowired
    private AuthUserRepository authUserRepository;

    public RatingServiceImpl(RatingRepository ratingRepository) {
        this.ratingRepository = ratingRepository;
    }

    @Override
    public RatingDTO createRating(RatingDTO ratingDTO) {
        Rating rating = new Rating();

        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        AuthUser authUser = new AuthUser();
        authUser.setId(userDetails.getId());
        rating.setUser(authUser);
        EProcurement foundEProcurement = eProcurementRepository.findById(ratingDTO.getEProcurementId())
                .orElseThrow(() -> new IllegalArgumentException("EProcurement not found with ID: " + ratingDTO.getEProcurementId()));
        rating.setEprocurement(foundEProcurement);

        rating.setJustification(ratingDTO.getJustification());
        rating.setStars(ratingDTO.getStars());
        ratingRepository.save(rating);
        ratingDTO.setId(rating.getId());
        ratingDTO.setAuthUserName(userDetails.getName());
        ratingDTO.setCreatedDate(rating.getCreatedDate());
        return ratingDTO;
    }

    @Override
    public List<RatingDistributionOutDTO> calculateRatingDistributionByEProcurementId(Long eProcurementId) {
        List<Rating> ratings = ratingRepository.findAllByEprocurement_Id(eProcurementId);

        // Calculate the count of each rating
        Map<Integer, Integer> ratingCountMap = new HashMap<>();
        for (Rating rating : ratings) {
            int stars = rating.getStars();
            ratingCountMap.put(stars, ratingCountMap.getOrDefault(stars, 0) + 1);
        }

        // Calculate the total count of ratings
        int totalRatings = ratings.size();

        // Calculate the percentage distribution for each rating
        List<RatingDistributionOutDTO> ratingDistribution = new ArrayList<>();
        for (int stars = 1; stars <= 5; stars++) {
            int count = ratingCountMap.getOrDefault(stars, 0);
            double percentage = 0;
            if (totalRatings != 0) percentage = (count * 100.0) / totalRatings;
            RatingDistributionOutDTO distribution = new RatingDistributionOutDTO();
            distribution.setStars(stars);
            distribution.setPercentage(percentage);
            ratingDistribution.add(distribution);
        }

        return ratingDistribution;
    }

    @Override
    public List<RatingDTO> findAllRatingsByEProcurementId(Long eProcurementId) {
        return ratingRepository.findAllByEprocurement_Id(eProcurementId).stream().map(rating -> {
            RatingDTO ratingDTO = new RatingDTO();
            ratingDTO.setEProcurementId(rating.getEprocurement().getId());
            ratingDTO.setStars(rating.getStars());
            ratingDTO.setJustification(rating.getJustification());
            ratingDTO.setId(rating.getId());
            ratingDTO.setCreatedDate(rating.getCreatedDate());
            AuthUser authUser = authUserRepository.findById(rating.getUser().getId()).orElseThrow();
            ratingDTO.setAuthUserName(authUser.getName());
            return ratingDTO;
        }).collect(Collectors.toList());
    }

    @Override
    public List<RatingAverageDTO> getRatingAveragesByEProcurementIdAndYear(Long eProcurementId, int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Date startDate = calendar.getTime();

        calendar.set(Calendar.MONTH, Calendar.DECEMBER);
        calendar.set(Calendar.DAY_OF_MONTH, 31);
        Date endDate = calendar.getTime();

        List<Rating> ratings = ratingRepository.findAllByEprocurement_IdAndCreatedDateBetween(eProcurementId, startDate, endDate);

        Map<Integer, List<Rating>> ratingsByMonth = groupRatingsByMonth(ratings);
        List<RatingAverageDTO> ratingAverages = calculateAveragesByMonth(ratingsByMonth);

        return ratingAverages;
    }

    @Override
    public Double calculateTotalRatingAverageByEProcurementId(Long eProcurementId) {
        List<Rating> ratings = ratingRepository.findAllByEprocurement_Id(eProcurementId);
        int totalRatings = ratings.size();

        if (totalRatings == 0) {
            return 0.0;
        }

        int sum = ratings.stream()
                .mapToInt(Rating::getStars)
                .sum();

        return (double) sum / totalRatings;
    }


    private Map<Integer, List<Rating>> groupRatingsByMonth(List<Rating> ratings) {
        // Group ratings by month using a Map<Integer, List<Rating>> structure
        Map<Integer, List<Rating>> ratingsByMonth = new HashMap<>();

        for (Rating rating : ratings) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(rating.getCreatedDate());
            int month = calendar.get(Calendar.MONTH) + 1; // Adding 1 because Calendar.MONTH is zero-based

            ratingsByMonth.computeIfAbsent(month, k -> new ArrayList<>()).add(rating);
        }

        return ratingsByMonth;
    }

    private List<RatingAverageDTO> calculateAveragesByMonth(Map<Integer, List<Rating>> ratingsByMonth) {
        // Calculate rating averages for each month
        List<RatingAverageDTO> ratingAverages = new ArrayList<>();

        for (Map.Entry<Integer, List<Rating>> entry : ratingsByMonth.entrySet()) {
            int month = entry.getKey();
            List<Rating> monthRatings = entry.getValue();

            // Calculate average for the month
            double average = calculateAverage(monthRatings);

            RatingAverageDTO ratingAverageDTO = new RatingAverageDTO();
            ratingAverageDTO.setMonth(month);
            ratingAverageDTO.setAverage(average);

            ratingAverages.add(ratingAverageDTO);
        }

        return ratingAverages;
    }

    private double calculateAverage(List<Rating> ratings) {
        if (ratings.isEmpty()) {
            return 0.0;
        }

        int totalStars = 0;
        for (Rating rating : ratings) {
            totalStars += rating.getStars();
        }

        return (double) totalStars / ratings.size();
    }

}
