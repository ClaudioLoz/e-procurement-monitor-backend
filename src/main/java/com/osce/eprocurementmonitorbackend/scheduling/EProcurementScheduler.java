package com.osce.eprocurementmonitorbackend.scheduling;

import com.osce.eprocurementmonitorbackend.enums.ProcurementStatus;
import com.osce.eprocurementmonitorbackend.model.EProcurement;
import com.osce.eprocurementmonitorbackend.repository.EProcurementRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Collection;

@Component
public class EProcurementScheduler {

    private final EProcurementRepository eProcurementRepository;

    private final Logger logger = LoggerFactory.getLogger(EProcurementScheduler.class);


    public EProcurementScheduler(EProcurementRepository eProcurementRepository) {
        this.eProcurementRepository = eProcurementRepository;
    }

    @Scheduled(cron = "0 0 0 * * *") // Runs every day at midnight
    public void changeStatusOfExpiredEprocurements() {
        logger.info("Scheduler activado. Cambiando estado de los contrataciones con fecha fin de contrato pasado...");

        LocalDate currentDate = LocalDate.now();

        // Update the status of eprocurements whose contract end date has passed
        Iterable<EProcurement> completedEprocurements = eProcurementRepository.findByContractEndDateLessThanAndProcurementStatus(
                currentDate, ProcurementStatus.FOLLOW_UP);

        for (EProcurement eProcurement : completedEprocurements) {
            eProcurement.setProcurementStatus(ProcurementStatus.COMPLETED);
        }

        eProcurementRepository.saveAll(completedEprocurements);

        logger.info("Se ha cambiado el estado para {} contrataciones.", ((Collection<?>) completedEprocurements).size());
    }
}