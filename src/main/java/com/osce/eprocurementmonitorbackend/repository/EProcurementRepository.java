package com.osce.eprocurementmonitorbackend.repository;

import com.osce.eprocurementmonitorbackend.enums.ProcurementStatus;
import com.osce.eprocurementmonitorbackend.model.EProcurement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EProcurementRepository extends JpaRepository<EProcurement, Long> {
    List<EProcurement> findAllByProcurementStatusIs(ProcurementStatus procurementStatus);
    Iterable<EProcurement> findByContractEndDateLessThanAndProcurementStatus(LocalDate date
                            , ProcurementStatus procurementStatus);
}
