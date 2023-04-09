package com.osce.eprocurementmonitorbackend.repository;

import com.osce.eprocurementmonitorbackend.model.FileInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileInfoRepository extends JpaRepository<FileInfo, Long> {

    List<FileInfo> findAllByEprocurement_Id(Long eprocurementId);
}
