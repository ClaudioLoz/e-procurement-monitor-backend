package com.osce.eprocurementmonitorbackend.repository;

import com.osce.eprocurementmonitorbackend.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

    List<Rating> findAllByEprocurement_Id(Long eProcurementId);
    List<Rating> findAllByEprocurement_IdAndCreatedDateBetween(Long eProcurementId, Date startDate, Date endDate);
}
