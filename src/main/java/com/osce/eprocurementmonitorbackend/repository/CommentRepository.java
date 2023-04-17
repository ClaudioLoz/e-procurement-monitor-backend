package com.osce.eprocurementmonitorbackend.repository;

import com.osce.eprocurementmonitorbackend.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByEprocurement_Id(Long eProcurementId);
}
