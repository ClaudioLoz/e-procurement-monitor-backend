package com.osce.eprocurementmonitorbackend.repository;

import com.osce.eprocurementmonitorbackend.model.EProcurement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EProcurementRepository extends JpaRepository<EProcurement, Long> {

}
