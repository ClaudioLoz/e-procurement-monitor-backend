package com.osce.eprocurementmonitorbackend.service.impl;

import com.osce.eprocurementmonitorbackend.api.dto.CommentDTO;
import com.osce.eprocurementmonitorbackend.model.AuthUser;
import com.osce.eprocurementmonitorbackend.model.Comment;
import com.osce.eprocurementmonitorbackend.model.EProcurement;
import com.osce.eprocurementmonitorbackend.repository.AuthUserRepository;
import com.osce.eprocurementmonitorbackend.security.services.UserDetailsImpl;
import com.osce.eprocurementmonitorbackend.repository.CommentRepository;
import com.osce.eprocurementmonitorbackend.repository.EProcurementRepository;
import com.osce.eprocurementmonitorbackend.service.CommentService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final EProcurementRepository eProcurementRepository;

    public CommentServiceImpl(CommentRepository commentRepository, EProcurementRepository eProcurementRepository) {
        this.commentRepository = commentRepository;
        this.eProcurementRepository = eProcurementRepository;
    }

    @Autowired
    private AuthUserRepository authUserRepository;

    @Override
    public CommentDTO createComment(CommentDTO commentDTO, MultipartFile image) {
        Comment comment = new Comment();

        EProcurement eProcurement = eProcurementRepository.findById(commentDTO.getEProcurementId())
                .orElseThrow(() -> new IllegalArgumentException("EProcurement not found with ID: " + commentDTO.getEProcurementId()));
        comment.setEprocurement(eProcurement);

        UserDetailsImpl userDetails =
                (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        AuthUser authUser = new AuthUser();
        authUser.setId(userDetails.getId());
        comment.setUser(authUser);
        try {
            if (StringUtils.isBlank(commentDTO.getText())) comment.setImage(image.getBytes());
            else comment.setText(commentDTO.getText());
            commentRepository.save(comment);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        commentDTO.setImage(comment.getImage());
        commentDTO.setCreatedDate(comment.getCreatedDate());
        commentDTO.setId(comment.getId());
        commentDTO.setAuthUserName(userDetails.getName());
        return commentDTO;
    }

    @Override
    public List<CommentDTO> findAllCommentsByEProcurementId(Long eProcurementId) {
        return commentRepository.findAllByEprocurement_Id(eProcurementId).stream().map(comment -> {
            CommentDTO commentDTO = new CommentDTO();
            commentDTO.setId(comment.getId());
            commentDTO.setEProcurementId(comment.getEprocurement().getId());
            commentDTO.setImage(comment.getImage());
            commentDTO.setText(comment.getText());
            commentDTO.setCreatedDate(comment.getCreatedDate());
            AuthUser authUser = authUserRepository.findById(comment.getUser().getId()).orElseThrow();
            commentDTO.setAuthUserName(authUser.getName());
            return commentDTO;
        }).collect(Collectors.toList());
    }

    @Override
    public Integer calculateTotalCommentCountByEProcurementId(Long eProcurementId) {
        return commentRepository.findAllByEprocurement_Id(eProcurementId).size();
    }

}
