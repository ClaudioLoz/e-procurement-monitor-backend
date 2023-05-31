package com.osce.eprocurementmonitorbackend.repository;

import com.osce.eprocurementmonitorbackend.model.Suggestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SuggestionRepository extends JpaRepository<Suggestion, Long> {

    List<Suggestion> findAllByEprocurement_Id(Long eProcurementId);
}
