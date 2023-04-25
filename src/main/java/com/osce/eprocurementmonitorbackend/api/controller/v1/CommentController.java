package com.osce.eprocurementmonitorbackend.api.controller.v1;

import com.osce.eprocurementmonitorbackend.api.dto.CommentDTO;
import com.osce.eprocurementmonitorbackend.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<CommentDTO> createComment(@RequestPart("json") CommentDTO commentDTO
            , @RequestPart("image") MultipartFile image) {
        return new ResponseEntity<>(commentService.createComment(commentDTO, image), HttpStatus.CREATED);
    }


    @GetMapping
    public ResponseEntity<List<CommentDTO>> findAllCommentsByEProcurementId(@RequestParam Long eProcurementId) {
        return new ResponseEntity<>(commentService.findAllCommentsByEProcurementId(eProcurementId), HttpStatus.OK);
    }


}
