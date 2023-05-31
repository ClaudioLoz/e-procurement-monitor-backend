package com.osce.eprocurementmonitorbackend.service;

import com.osce.eprocurementmonitorbackend.api.dto.CommentDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CommentService {

    CommentDTO createComment(CommentDTO commentDTO, MultipartFile image);

    List<CommentDTO> findAllCommentsByEProcurementId(Long eProcurementId);

    Integer calculateTotalCommentCountByEProcurementId(Long eProcurementId);
}
