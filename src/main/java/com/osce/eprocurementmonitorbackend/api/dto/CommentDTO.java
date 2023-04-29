package com.osce.eprocurementmonitorbackend.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;


@Getter
@Setter
public class CommentDTO {
    private Long eProcurementId;
    private String text;
    //out
    private byte[] image;
    private Date createdDate;
    private Long id;
    private String authUserName;
}
